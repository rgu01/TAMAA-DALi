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

public class PlannerServiceHandler implements PlannerService.Iface {
 	Mission corePlan = null;
	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		try {
			//Communication with MMT
			TTransport transport;
			transport = new TSocket("127.0.0.1", 9096);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			MmtService.Client client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());

			//Delete all current transit tasks in the map
			List<Task> found = new ArrayList<Task>();
			for (Task t: plan.getTasks()){
				if(t.taskTemplate.taskType == TaskType.TRANSIT){
					found.add(t);
				}
			}
			plan.tasks.removeAll(found);
			
			SphericalMercator sphericalMercator = new SphericalMercator();
			//Navigation Area
			double top_left_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(1).longitude);
			double top_left_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(1).latitude);
			double top_right_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
			double top_right_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
			double bot_right_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(3).longitude);
			double bot_right_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(3).latitude);
			double bot_left_x = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
			double bot_left_y = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
			Node top_right  = new Node(top_right_x,top_right_y);
			Node bot_left = new Node(bot_left_x,bot_left_y);
			Node top_left  = new Node(top_left_x,top_left_y);
			Node bot_right = new Node(bot_right_x,bot_right_y);
			NavigationArea nArea = new NavigationArea();
			nArea.boundry.add(top_left);
			nArea.boundry.add(bot_left);
			nArea.boundry.add(bot_right);
			nArea.boundry.add(top_right);
			// Obstacles
			for (Region forbidden : plan.getForbiddenArea()) 
			{
				Obstacle obs = new Obstacle();
				
				top_left_x = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
				top_left_y = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
				top_right_x = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
				top_right_y = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
				bot_right_x = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
				bot_right_y = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
				bot_left_x = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
				bot_left_y = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
				top_right  = new Node(top_right_x,top_right_y);
				bot_left = new Node(bot_left_x,bot_left_y);
				top_left  = new Node(top_left_x,top_left_y);
				bot_right = new Node(bot_right_x,bot_right_y);
				obs.vertices.add(top_left);
				obs.vertices.add(bot_left);
				obs.vertices.add(bot_right);
				obs.vertices.add(top_right);
				
				nArea.obstacles.add(obs);
			}
			
			AStar as = new AStar(nArea);
			double vehicle_x = sphericalMercator.xAxisProjection( plan.getVehicles().get(0).stateVector.getPosition().longitude);
			double vehicle_y = sphericalMercator.yAxisProjection( plan.getVehicles().get(0).stateVector.getPosition().latitude);
			double task_x = sphericalMercator.xAxisProjection(plan.getTasks().get(0).area.area.get(0).longitude);
			double task_y = sphericalMercator.yAxisProjection(plan.getTasks().get(0).area.area.get(0).latitude);
			//calculate path
			List<Node> path = as.calculate(new Node(vehicle_x, vehicle_y), new Node(task_x, task_y));
			Position origin = plan.getVehicles().get(0).stateVector.getPosition();
			Position goal = null;
			Collections.reverse(path);
			//create transits in MMT
			int taskID = 21;
			for (int i = 1; i < path.size()-1; i++) {
				goal = new Position(sphericalMercator.x2lon(path.get(i).lat),sphericalMercator.y2lat(path.get(i).lon), 0.0);
				plan.tasks.add(newTransitAction(i, origin, goal, plan.vehicles.get(0), 0));
				origin = goal;
				taskID++;
			}
			Position task1Position = plan.getTasks().get(0).area.area.get(0);
			plan.tasks.add(newTransitAction(taskID, origin, task1Position, plan.vehicles.get(0), 0));
			
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
		//requiredTypes.add(EquipmentType.COLLISION_AVOIDANCE); // we have to fix this;
		//requiredTypes.add(EquipmentType.WIFI); // we have to fix this;
		transit.setTaskTemplate(new TaskTemplate(TaskType.INSPECT, "", TaskRegionType.Column, requiredTypes));

		transit.timeLapse = 0;
		transit.speed = vehicleUsed.maxSpeed;
		transit.taskStatus = TaskCommandStatus.NotStarted;
		
		transit.taskTemplate.description = "";

		return transit;
	}

}