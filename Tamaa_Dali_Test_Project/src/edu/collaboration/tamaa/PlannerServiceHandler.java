package edu.collaboration.tamaa;

import MercatoerProjection.SphericalMercator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;  
import org.apache.thrift.protocol.TProtocol;  
import org.apache.thrift.transport.TSocket;  
import org.apache.thrift.transport.TTransport;  
import org.apache.thrift.transport.TTransportException;  
import com.afarcloud.thrift.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.collaboration.communication.TransferFile;
import edu.collaboration.model.generation.MapTxtGenerator;
import edu.collaboration.model.generation.UPPAgentGenerator;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.xstar.*;

public class PlannerServiceHandler implements PlannerService.Iface {
	Mission corePlan = null;
	public static final String SERVER_IP = "192.168.0.109"; 
    public static final int SERVER_PORT = 9779; 
    
	@Override
	public void computePlan(int requestId, Mission plan) throws TException 
	{
		//Communication with MMT
		TTransport transport = null; 	
		TProtocol protocol = null;
		MmtService.Client client = null;
		AStar as = null;
		//List<Node> milestones = new ArrayList<Node>();
		double task_lat, task_lon;
		Node start = null, end = null;
		List<Position> milestones = new ArrayList<Position>();
		List<Node> nodes = new ArrayList<Node>();
		Position origin = null, goal = null;
		//Map<List<Node>, List<Node>> paths = new HashMap<List<Node>, List<Node>>();
		List<Path> paths = new ArrayList<Path>();
		int taskID = 21;
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0, 
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		Node top_right  = null, bot_left = null, top_left  = null, bot_right = null;
		NavigationArea nArea = null;
		SphericalMercator sphericalMercator = new SphericalMercator();
		String resultFile = "./results/plan.xml";
		
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
			//vehicle_lon = sphericalMercator.xAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().longitude);
			//vehicle_lat = sphericalMercator.yAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().latitude);
			origin = plan.getVehicles().get(0).stateVector.getPosition();
			milestones.add(origin);
			for(int i = 0; i < plan.tasks.size(); i++)
			{
				milestones.add(plan.getTasks().get(i).area.area.get(0));
			}
			for(Position p_origin:milestones)
			{
				task_lon = sphericalMercator.xAxisProjection(p_origin.longitude);
				task_lat = sphericalMercator.yAxisProjection(p_origin.latitude);
				start = new Node(task_lat, task_lon);
				if(!nodes.contains(start))
				{
					nodes.add(start);
				}
				for(Position p_target:milestones)
				{
					task_lon = sphericalMercator.xAxisProjection(p_target.longitude);
					task_lat = sphericalMercator.yAxisProjection(p_target.latitude);
					end = new Node(task_lat, task_lon);
					if(!start.equals(end))
					{
						Path path = as.calculate(start, end);
						List<Node> key = new ArrayList<Node>();
						List<Node> key_temp = new ArrayList<Node>();
						key.add(start);
						key.add(end);
						key_temp.add(end);
						key_temp.add(start);
						if(!paths.contains(path))
						{
							paths.add(path);
						}
						/*if(!paths.containsKey(key) && !paths.containsKey(key_temp))
						{
							Collections.reverse(path);
							paths.put(key, path);
						}*/
					}
				}
			}
			MapTxtGenerator mapGen = new MapTxtGenerator(nodes, paths);
			mapGen.createSampleMap();
			UPPAgentGenerator.run();
			
			//call UPPAAL in the server side to synthesize a mission plan
			TransferFile trans = new TransferFile(PlannerServiceHandler.SERVER_IP, PlannerServiceHandler.SERVER_PORT);
			trans.sendFile(UPPAgentGenerator.outputXML);
			trans.close();
			trans = new TransferFile(PlannerServiceHandler.SERVER_IP, PlannerServiceHandler.SERVER_PORT);
			trans.receiveFile(resultFile);
			trans.close();
			
			//draw the paths in MMT
			//Iterator<Entry<List<Node>, List<Node>>> iter = paths.entrySet().iterator();
			Iterator<Path> iter_paths = paths.iterator();
			Iterator<Node> iter_path = null;
			Path a_path;
			Node a_node;
			while (iter_paths.hasNext()) {
			    //Map.Entry<List<Node>, List<Node>> entry = (Map.Entry<List<Node>, List<Node>>) iter.next();
			    //List<Node> val = (List<Node>)entry.getValue();
				a_path = iter_paths.next();
				iter_path = a_path.segments.iterator();
				a_node = iter_path.next();
				origin = new Position(sphericalMercator.x2lon(a_node.lon),sphericalMercator.y2lat(a_node.lat), 0.0);
				while(iter_path.hasNext())
				{
					a_node = iter_path.next();
					goal = new Position(sphericalMercator.x2lon(a_node.lon),sphericalMercator.y2lat(a_node.lat), 0.0);
					plan.tasks.add(newTransitAction(taskID, origin, goal, plan.vehicles.get(0), 0));
					origin = goal;
					taskID++;
				}
			}

			//client.sendPlan(requestId, plan);
			//System.out.println("Mission Plan Sent!");
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
		catch(Exception ex)
		{
			System.out.println("final xerror");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			transport.close();
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