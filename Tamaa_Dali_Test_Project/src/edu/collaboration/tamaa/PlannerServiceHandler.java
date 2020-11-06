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

import javax.swing.JOptionPane;

import edu.collaboration.communication.TransferFile;
import edu.collaboration.model.structure.UPPAgentGenerator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.Dali;
import edu.collaboration.pathplanning.xstar.*;
import edu.collaboration.taskscheduling.*;

public class PlannerServiceHandler implements PlannerService.Iface {
	// Mission corePlan = null;
	private static int InitialTaskID = 21;
	// public static final String SERVER_IP = "192.168.0.109";
	// public static final String SERVER_IP = "192.168.56.1";
	// public static final String SERVER_IP = "127.0.0.1";
	// public static final int SERVER_PORT = 9779;
	public String mmtAddress = "127.0.0.1";
	public int mmtPort = 9096;
	private String uppaalAddress;
	private int uppaalPort;

	public PlannerServiceHandler() {
	}

	public PlannerServiceHandler(String mmtAddress, int mmtPort, String uppaalAddress, int uppaalPort) {
		this.mmtAddress = mmtAddress;
		this.mmtPort = mmtPort;
		this.uppaalAddress = uppaalAddress;
		this.uppaalPort = uppaalPort;
	}

	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		// boolean pathExist = false;
		// Communication with MMT
		TTransport transport = null;
		TProtocol protocol = null;
		MmtService.Client client = null;
		PathPlanningAlgorithm as = null;
		List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0,
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		double minSpeed = Double.MAX_VALUE;
		Node vertices[] = new Node[4];
		NavigationArea nArea = null;
		SphericalMercator sphericalMercator = new SphericalMercator();

		try {
			transport = new TSocket(this.mmtAddress, this.mmtPort);
			transport.open();

			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());
			
			for (Vehicle v : plan.getVehicles()) {
				if(v.getMaxSpeed() < minSpeed)
				{
					minSpeed = v.getMaxSpeed();
				}
			}
			// Navigation Area
			top_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(3).longitude);
			top_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(3).latitude);
			top_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
			top_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
			bot_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(1).longitude);
			bot_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(1).latitude);
			bot_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
			bot_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
			vertices[0] = new Node(top_right_lat, top_right_lon);
			vertices[1] = new Node(bot_left_lat, bot_left_lon);
			vertices[2] = new Node(top_left_lat, top_left_lon);
			vertices[3] = new Node(bot_right_lat, bot_right_lon);
			nArea = new NavigationArea(vertices, 4, minSpeed);
			/*
			 * nArea.boundry.add(top_left); nArea.boundry.add(bot_left);
			 * nArea.boundry.add(bot_right); nArea.boundry.add(top_right);
			 */
			// Obstacles
			List<Node> obsVertices = new ArrayList<Node>();
			for (Region forbidden : plan.getForbiddenArea()) {
				top_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
				top_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
				top_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
				top_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
				bot_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
				bot_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
				bot_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
				bot_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
				obsVertices.add(new Node(top_right_lat, top_right_lon));
				obsVertices.add(new Node(bot_left_lat, bot_left_lon));
				obsVertices.add(new Node(top_left_lat, top_left_lon));
				obsVertices.add(new Node(bot_right_lat, bot_right_lon));

				Obstacle obs = new Obstacle(obsVertices);
				nArea.obstacles.add(obs);
				obsVertices.clear();
			}

			as = new AStar(nArea);
			//as = new Dali(nArea);
			for (Vehicle v : plan.getVehicles()) {
				int agentID = 0, milestoneID = 1;// 0 is for the starting position
				List<Node> milestones = new ArrayList<Node>();
				UPPAgentVehicle agent = new UPPAgentVehicle(agentID++);
				agent.vehicle = v;
				milestones.add(agent.getStartNode());
				for (Task task : plan.tasks) {
					// one vehicle assumed begin
					task.missionId = (int) task.altitude;
					//
					if (task.assignedVehicleId == 0) {
						task.assignedVehicleId = v.id;
					}
					// one vehicle assumed over
					if (agent.canDoTask(task)) {
						Node milestone = new Node(milestoneID++, task);
						milestones.add(milestone);
						agent.addTask(milestone);
					}
				}
				List<Path> paths = new ArrayList<Path>();
				for (Node n1 : milestones) {
					for (Node n2 : milestones) {
						if (!n1.equals(n2)) {
							Path path = new Path(n1, n2);
							if (!agent.isPathExist(path)) {
								path = as.calculate(n1, n2);
							}

							if (!paths.contains(path)) {
								paths.add(path);
							}
						}
					}
				}
				agent.paths = paths;
				agents.add(agent);
			}

			/*****************************************************************
			 * If no server is running, and only path planning is needed, please comment the
			 * code below
			 */
			 UPPAgentGenerator.run(agents); // call UPPAAL in the server side to synthesize a mission plan 
			 TransferFile trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
			 trans.sendFile(UPPAgentGenerator.outputXML); 
			 trans.close(); 
			 trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
			 trans.receiveFile(TaskScheduleParser.planPath); 
			 trans.close();
			 /****************************************************************** end of
			 * the task scheduling code
			 */

			// parse the result xml
			TaskSchedulePlan taskPlan = TaskScheduleParser.parse();
			TaskScheduleState states;
			AgentState agentState;
			TaskScheduleAction action;
			Task movement = null, execution = null;
			List<Command> segments = null;
			int startTime = 0;
			Node currentNode, targetNode, waypoint;
			for (int index = 0; index < taskPlan.length(); index++) {
				states = taskPlan.states.get(index);
				action = taskPlan.actions.get(index);
				for (UPPAgentVehicle agent : agents) {
					if (agent.ID == action.agentID) {
						agentState = states.getAgentState(agent.ID);
						// start to move
						if (action.type.equals(TaskScheduleAction.StrMoveStart)) {
							if (!agentState.currentPosition.equals("initial")) {
								currentNode = agent.getMilestone(agentState.currentPosition);
								movement = this.startMove(currentNode, agent, startTime);
							}
						}
						// finish a move
						else if (action.type.equals(TaskScheduleAction.StrMoveFinish)) {
							targetNode = agent.getMilestone(action.target);
							segments = this.finishMove(movement, agent, targetNode, (int) action.time);
							startTime += (int) action.time;
						}
						// start an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskStart)) {
							currentNode = agent.getMilestone(agentState.currentPosition);
							for (Task task : plan.tasks) {
								waypoint = new Node(task.getArea().area.get(0));
								if (task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint.equals(currentNode)) {
									task.assignedVehicleId = agent.vehicle.id;
									task.startTime = startTime;
									movement.parentTaskId = task.id;
									execution = task;
									break;
								}
							}
							// add movement going to this task's milestone
							plan.addToTasks(movement);
							for (Command segment : segments) {
								plan.addToCommands(segment);
							}
						}
						// finish an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskFinish)) {
							currentNode = agent.getMilestone(agentState.currentPosition);
							execution.assignedVehicleId = agent.vehicle.id;
							execution.endTime = startTime + (int) action.time;
							// plan.addToTasks(execution);
						}
					}
				}
			}
			
			String commandsLog = "";
			Position ppp1 = new Position(), ppp2 = new Position();
			Node s1,s2;
			for(int i = 0; i < plan.getCommands().size()-1;i++)
			{
				ppp1.latitude = plan.getCommands().get(i).params.get(4);
				ppp1.longitude = plan.getCommands().get(i).params.get(5);
				ppp2.latitude = plan.getCommands().get(i+1).params.get(4);
				ppp2.longitude = plan.getCommands().get(i+1).params.get(5);
				s1 = new Node(ppp1);
				s2 = new Node(ppp2);
				if(i == 58)
				{
					i = i+0;
				}
				if(nArea.collide(s1, s2))
				{
					i = i+0;
				}
				commandsLog += plan.getCommands().get(i).endTime + ": " + new Node(ppp1).toString() + "\r\n";
				//commandsLog += plan.getCommands().get(i+1).endTime + ": " + new Node(ppp2).toString() + "\r\n";
			}
			System.out.println(commandsLog);

			client.sendPlan(requestId, plan);
			// System.out.println("Mission Plan Sent!");
			String show = "A plan with " + plan.tasks.size() + " tasks, and " + plan.commands.size()
					+ " commands has been sent!";
			JOptionPane.showMessageDialog(null, show, "Done", JOptionPane.PLAIN_MESSAGE);
		} catch (TTransportException e) {
			System.out.println("final error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (TException x) {
			System.out.println("final xerror");
			System.out.println(x.getMessage());
			x.printStackTrace();
		} catch (Exception ex) {
			System.out.println("final xerror");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} finally {
			transport.close();
			System.exit(0);
		}
	}

	@Override
	public String ping() throws TException {
		// TODO Auto-generated method stub
		return "SOME TEST";
	}

	private Task startMove(Node node, UPPAgentVehicle agent, long startTime) {
		int taskID = InitialTaskID++;
		Task transit = new Task();
		Orientation bearing = new Orientation();
		List<EquipmentType> requiredTypes = new java.util.ArrayList<EquipmentType>();
		Position start = node.getPosition();

		transit.setAssignedVehicleId(agent.vehicle.id);
		transit.altitude = 0;
		transit.assignedVehicleId = agent.vehicle.id;
		bearing.roll = 0;
		bearing.pitch = 0;
		bearing.yaw = 0;
		transit.bearing = bearing;

		transit.area = new Region();
		transit.area.area = new java.util.ArrayList<Position>();
		transit.area.area.add(start);

		transit.startTime = startTime;
		transit.range = 0;
		transit.id = taskID;

		transit.setTaskTemplate(new TaskTemplate(TaskType.TRANSIT, "", TaskRegionType.Column, requiredTypes));
		transit.speed = agent.vehicle.maxSpeed;
		transit.startTime = startTime;
		transit.taskStatus = TaskCommandStatus.Running;
		transit.taskTemplate.description = "Start to move";

		return transit;
	}

	private static int moveID = 0;
	private List<Command> finishMove(Task transit, UPPAgentVehicle agent, Node endPoint, int duration) {
		Position startPoint = transit.area.area.get(0);
		Path path = agent.findPath(startPoint, endPoint.getPosition());
		Node start = path.start;
		long startTime = transit.startTime;
		List<Command> movement = new ArrayList<Command>();

		transit.area.area.add(endPoint.getPosition());
		transit.endTime = transit.startTime + duration;
		for (Node end : path.segments) {
			if (!end.equals(start)) {
				Command move = new PathSegment(start, end).createNewMove(moveID++, agent, startTime);
				move.setRelatedTask(transit);
				movement.add(move);
				start = end;
				startTime = move.endTime;
			}
		}

		return movement;
	}
}