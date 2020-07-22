package edu.collaboration.tamaa;

import MercatoerProjection.SphericalMercator;
//import edu.collaboration.tamaa.UppaalMapGenerator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;  
import org.apache.thrift.protocol.TProtocol;  
import org.apache.thrift.transport.TSocket;  
import org.apache.thrift.transport.TTransport;  
import org.apache.thrift.transport.TTransportException;  
import com.afarcloud.thrift.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.xstar.*;
import com.afarcloud.thrift.*;
import pathFinding.ThetaStar;
import quadTree.Obstacle;
import quadTree.Point;
import quadTree.QuadNode;
import quadTree.QuadTree;

public class PlannerServiceHandler implements PlannerService.Iface {
 	Mission corePlan = null;
 	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		try {
			TTransport transport;
			transport = new TSocket("127.0.0.1", 9096);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			MmtService.Client client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());

			//System.out.println(plan);
			//forbidden.area.set( index, positionOnMap);
			//allforbiddenAreas.add(forbidden);
			//Getting forbidden areas
//			List<Region> allforbiddenAreas = new ArrayList<Region>();
			System.out.println("OLD- "+plan.tasks.size());
			//Delete all transit tasks
			List<Task> found = new ArrayList<Task>();
			for (Task t: plan.getTasks()){
				if(t.taskTemplate.taskType == TaskType.TRANSIT){
					found.add(t);
				}
			}
			plan.tasks.removeAll(found);
			System.out.println("NEW- "+plan.tasks.size());
			SphericalMercator sphericalMercator = new SphericalMercator();

			// Region
			double top_right_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
			double top_right_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
			double bot_left_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
			double bot_left_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
			Point top_right  = new Point(top_right_x,top_right_y);
			Point bot_left = new Point(bot_left_x,bot_left_y);
			// Obstacles
			ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
			for (Region forbidden : plan.getForbiddenArea()) {
				int index = 0;
				double top_obstacle_x = 0;
				double top_obstacle_y = 0;
				double bot_obstacle_x = 0;
				double bot_obstacle_y = 0;
				for (Position positionOnMap : forbidden.area){
					top_obstacle_x = sphericalMercator.xAxisProjection(plan.getForbiddenArea().get(0).getArea().get(2).longitude);
					top_obstacle_y = sphericalMercator.yAxisProjection(plan.getForbiddenArea().get(0).getArea().get(2).latitude);
					bot_obstacle_x = sphericalMercator.xAxisProjection(plan.getForbiddenArea().get(0).getArea().get(0).longitude);
					bot_obstacle_y = sphericalMercator.yAxisProjection(plan.getForbiddenArea().get(0).getArea().get(0).latitude);
					index++;
				}
				obstacles.add(new Obstacle( new Point(bot_obstacle_x,bot_obstacle_y),new Point(top_obstacle_x,top_obstacle_y)));
			}
			System.out.println(obstacles.size());

			// Quad Tree
			QuadTree quadtree = new QuadTree(obstacles, 15, bot_left, top_right, 4);

			double vehicle_x = sphericalMercator.xAxisProjection( plan.getVehicles().get(0).stateVector.getPosition().longitude);
			double vehicle_y = sphericalMercator.yAxisProjection( plan.getVehicles().get(0).stateVector.getPosition().latitude);

			double task_x = sphericalMercator.xAxisProjection(plan.getTasks().get(0).area.area.get(0).longitude);
			double task_y = sphericalMercator.yAxisProjection(plan.getTasks().get(0).area.area.get(0).latitude);

			QuadNode q00 = quadtree.find_node(new Point(vehicle_x,vehicle_y));
			QuadNode q10 = quadtree.find_node(new Point(task_x,task_y));

			ThetaStar thetaStar = new ThetaStar(quadtree);
			List<QuadNode> q = thetaStar.findPath(q00,q10);

			Position positon1 = plan.getVehicles().get(0).stateVector.getPosition();
			int indexId = 21;
			Collections.reverse(q);
			for (int i = 1; i < q.size()-1; i++) {
				Point origine = q.get(i).origine();
				Position position2 = new Position(sphericalMercator.x2lon(q.get(i).getBottom_left().x),sphericalMercator.y2lat(q.get(i).getBottom_left().y), 0.0);
				plan.tasks.add(newTransitAction(indexId, positon1, position2, plan.vehicles.get(0), 0));
				positon1 = position2;
				indexId++;
			}
			Position task1Position = plan.getTasks().get(0).area.area.get(0);
			plan.tasks.add(newTransitAction(indexId, positon1, task1Position, plan.vehicles.get(0), 0));

			// Static layer
			UppaalMapGenerator mapGenerator = new UppaalMapGenerator(3);
			mapGenerator.creteSampleMap();

			//Sample for task transition.
			// From current vehicle position to first task.
//			Position vehicleStartPosition = plan.getVehicles().get(0).stateVector.getPosition();
//			Position task1Position = plan.getTasks().get(0).area.area.get(0);
			//plan.tasks.add(newTransitAction(plan.vehicles.get(plan.getVehicles().size()-1).id+3, vehicleStartPosition, task1Position, plan.vehicles.get(0), 0));
			// From first task position to second task.
//			Position task1StartPosition = plan.getTasks().get(0).area.area.get(0);
//			Position task2Position = plan.getTasks().get(1).area.area.get(0);
			// check the output - System.out.println("Position1:"+task1StartPosition.toString()+", Position2:"+task2Position.toString());
//			plan.tasks.add(newTransitAction(plan.getTasks().get(plan.getTasks().size()-1).parentTaskId+1, task1StartPosition, task2Position, plan.vehicles.get(0), 0));

			
			client.sendPlan(requestId, plan);
			transport.close();
		} catch (TTransportException e) {
			System.out.println("final error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (TException x) {
			System.out.println("final xerror");
			System.out.println(x.getMessage());
			x.printStackTrace();
		}

	}

	@Override
	public String ping() throws TException {
		// TODO Auto-generated method stub
		return "SOME TEST";
	}
	
	private Task newTransitAction(int actionID, Position startPosition, Position endPosition, Vehicle vehicleUsed, int startTime)
	{
		//System.out.println(vehicleUsed);
		Task transit = new Task();

		Orientation bearing = new Orientation();
		int endTime = 0, distance;

		distance = (int)(100*Math.pow(startPosition.getLatitude()-endPosition.getLatitude(), 2));//fix number for now
		distance = distance + (int)(100*Math.pow(startPosition.getLongitude()-endPosition.getLongitude(), 2));//fix number for now
		distance = (int)Math.sqrt(distance);

		transit.altitude = 0;
		transit.assignedVehicleId = vehicleUsed.id;
		bearing.roll = 0; bearing.pitch = 0; bearing.yaw = 0;
		transit.bearing = bearing;

		endTime = (int) (startTime + distance/vehicleUsed.getMaxSpeed());

		transit.area = new Region();
		transit.area.area = new java.util.ArrayList<Position>();
		transit.area.area.add(startPosition);
		transit.area.area.add(endPosition);

		transit.startTime = startTime; transit.endTime = endTime;
		transit.range = 0;
		transit.id = actionID;

		List<EquipmentType> requiredTypes = new java.util.ArrayList<EquipmentType>();
		requiredTypes.add(EquipmentType.COLLISION_AVOIDANCE); // we have to fix this;
		transit.setTaskTemplate(new TaskTemplate(TaskType.TRANSIT, "Transit", TaskRegionType.Column,requiredTypes));

		transit.timeLapse = 0;
		transit.speed = vehicleUsed.maxSpeed;
		transit.taskStatus = TaskCommandStatus.NotStarted;

		return transit;
	}



}
