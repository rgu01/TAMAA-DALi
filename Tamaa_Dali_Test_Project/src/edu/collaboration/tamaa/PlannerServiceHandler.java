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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.xstar.*;

public class PlannerServiceHandler implements PlannerService.Iface {
	Mission corePlan = null;
	@Override
	public void computePlan(int requestId, Mission plan) throws TException 
	{
		//Communication with MMT
		TTransport transport = null; 	
		TProtocol protocol = null;
		MmtService.Client client = null;
		AStar as = null;
		//List<Node> milestones = new ArrayList<Node>();
		double vehicle_lat, vehicle_lon, task_lat, task_lon;
		Node start = null, end = null;
		List<Position> milestones = new ArrayList<Position>();
		Position origin = null, goal = null, taskPosition = null;
		Map<List<Node>, List<Node>> paths = new HashMap<List<Node>, List<Node>>();
		int taskID = 21;
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0, 
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		Node top_right  = null, bot_left = null, top_left  = null, bot_right = null;
		NavigationArea nArea = null;
		SphericalMercator sphericalMercator = new SphericalMercator();
		
		try {
			transport = new TSocket("127.0.0.1", 9096);
			transport.open();

			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());

			//Delete all current transit tasks in the map
			List<Task> found = new ArrayList<Task>();
			for (Task t: plan.getTasks()){
				if(t.taskTemplate.taskType == TaskType.TRANSIT){
					found.add(t);
				}
			}
			plan.tasks.removeAll(found);
			
			//Navigation Area
			top_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(3).longitude);
			top_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(3).latitude);
			top_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
			top_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
			bot_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(1).longitude);
			bot_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(1).latitude);
			bot_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
			bot_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
			top_right  = new Node(top_right_lat,top_right_lon);
			bot_left = new Node(bot_left_lat,bot_left_lon);
			top_left  = new Node(top_left_lat,top_left_lon);
			bot_right = new Node(bot_right_lat,bot_right_lon);
			nArea = new NavigationArea(top_left, bot_left, bot_right, top_right);
			/*nArea.boundry.add(top_left);
			nArea.boundry.add(bot_left);
			nArea.boundry.add(bot_right);
			nArea.boundry.add(top_right);*/
			// Obstacles
			for (Region forbidden : plan.getForbiddenArea()) 
			{
				Obstacle obs = new Obstacle();
				
				top_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
				top_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
				top_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
				top_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
				bot_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
				bot_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
				bot_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
				bot_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
				top_right  = new Node(top_right_lat,top_right_lon);
				bot_left = new Node(bot_left_lat,bot_left_lon);
				top_left  = new Node(top_left_lat,top_left_lon);
				bot_right = new Node(bot_right_lat,bot_right_lon);
				obs.vertices.add(top_left);
				obs.vertices.add(bot_left);
				obs.vertices.add(bot_right);
				obs.vertices.add(top_right);
				
				nArea.obstacles.add(obs);
			}
			
			as = new AStar(nArea);
			vehicle_lon = sphericalMercator.xAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().longitude);
			vehicle_lat = sphericalMercator.yAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().latitude);
			start = new Node(vehicle_lat, vehicle_lon);
			origin = plan.getVehicles().get(0).stateVector.getPosition();
			milestones.add(origin);
			for(int i = 0; i < plan.tasks.size(); i++)
			{
				milestones.add(plan.getTasks().get(i).area.area.get(0));
			}
			for(Position p_origin:milestones)
			{
				for(Position p_target:milestones)
				{
					if(!p_origin.equals(p_target))
					{
						task_lon = sphericalMercator.xAxisProjection(p_target.longitude);
						task_lat = sphericalMercator.yAxisProjection(p_target.latitude);
						end = new Node(task_lat, task_lon);
						List<Node> path = as.calculate(start, end);
						List<Node> key = new ArrayList<Node>();
						List<Node> key_temp = new ArrayList<Node>();
						key.add(start);
						key.add(end);
						key_temp.add(end);
						key_temp.add(start);
						if(!paths.containsKey(key) && !paths.containsKey(key_temp))
						{
							Collections.reverse(path);
							paths.put(key, path);
						}
					}
				}
			}
			//draw the paths in MMT
			Iterator<Entry<List<Node>, List<Node>>> iter = paths.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<List<Node>, List<Node>> entry = (Map.Entry<List<Node>, List<Node>>) iter.next();
			    List<Node> key = (List<Node>)entry.getKey();
			    List<Node> val = (List<Node>)entry.getValue();
			    origin = new Position(sphericalMercator.x2lon(val.get(0).lon),sphericalMercator.y2lat(val.get(0).lat), 0.0);
				for (int i = 1; i < val.size()-1; i++) {
					goal = new Position(sphericalMercator.x2lon(val.get(i).lon),sphericalMercator.y2lat(val.get(i).lat), 0.0);
					plan.tasks.add(newTransitAction(taskID, origin, goal, plan.vehicles.get(0), 0));
					origin = goal;
					taskID++;
				}
			}
			/*taskID = 21;
			for(Task task:tasks)
			{
				taskPosition = task.area.area.get(0);
				task_lon = sphericalMercator.xAxisProjection(taskPosition.longitude);
				task_lat = sphericalMercator.yAxisProjection(taskPosition.latitude);
				end = new Node(task_lat, task_lon);
				//calculate path
				path = as.calculate(start, end);
				Collections.reverse(path);
				//create transits in MMT
				for (int i = 1; i < path.size()-1; i++) {
					goal = new Position(sphericalMercator.x2lon(path.get(i).lon),sphericalMercator.y2lat(path.get(i).lat), 0.0);
					plan.tasks.add(newTransitAction(taskID, origin, goal, plan.vehicles.get(0), 0));
					origin = goal;
					taskID++;
				}
				//plan.tasks.add(newTransitAction(taskID, origin, taskPosition, plan.vehicles.get(0), 0));
				start = end;
			}*/	
		} 
		catch (TTransportException e) 
		{
			System.out.println("final error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (TException x) 
		{
			System.out.println("final xerror");
			System.out.println(x.getMessage());
			x.printStackTrace();
		}
		finally
		{
			client.sendPlan(requestId, plan);
			transport.close();
			System.out.println("Mission Plan Sent!");
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