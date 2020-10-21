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
import edu.collaboration.model.structure.UPPAgentGenerator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.Dali;
import edu.collaboration.pathplanning.xstar.*;
import edu.collaboration.taskscheduling.*;

public class PlannerServiceHandler implements PlannerService.Iface {
	// Mission corePlan = null;
	private static int InitialTaskID = 20;
	// public static final String SERVER_IP = "192.168.0.109";
	public static final String SERVER_IP = "192.168.56.1";
	// public static final String SERVER_IP = "127.0.0.1";
	public static final int SERVER_PORT = 9779;

	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		// boolean pathExist = false;
		// Communication with MMT
		TTransport transport = null;
		TProtocol protocol = null;
		MmtService.Client client = null;
		PathPlanningAlgorithm as = null;
		double task_lat, task_lon;
		// Node start = null, end = null;
		// List<Position> milestones = new ArrayList<Position>();
		Position origin = null, goal = null;
		// Map<List<Node>, List<Node>> paths = new HashMap<List<Node>, List<Node>>();
		// List<List<Node>> nodesForDifferentAgents = new ArrayList<List<Node>>();
		// List<List<Path>> pathsForDifferentAgents = new ArrayList<List<Path>>();
		List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0,
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		Node top_right = null, bot_left = null, top_left = null, bot_right = null;
		NavigationArea nArea = null;
		SphericalMercator sphericalMercator = new SphericalMercator();

		try {
			transport = new TSocket("127.0.0.1", 9096);
			transport.open();

			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());

			// Navigation Area
			top_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(3).longitude);
			top_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(3).latitude);
			top_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
			top_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
			bot_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(1).longitude);
			bot_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(1).latitude);
			bot_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
			bot_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
			top_right = new Node(top_right_lat, top_right_lon);
			bot_left = new Node(bot_left_lat, bot_left_lon);
			top_left = new Node(top_left_lat, top_left_lon);
			bot_right = new Node(bot_right_lat, bot_right_lon);
			nArea = new NavigationArea(top_left, bot_left, bot_right, top_right);
			/*
			 * nArea.boundry.add(top_left); nArea.boundry.add(bot_left);
			 * nArea.boundry.add(bot_right); nArea.boundry.add(top_right);
			 */
			// Obstacles
			for (Region forbidden : plan.getForbiddenArea()) {
				Obstacle obs = new Obstacle();

				top_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
				top_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
				top_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
				top_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
				bot_right_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
				bot_right_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
				bot_left_lon = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
				bot_left_lat = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
				top_right = new Node(top_right_lat, top_right_lon);
				bot_left = new Node(bot_left_lat, bot_left_lon);
				top_left = new Node(top_left_lat, top_left_lon);
				bot_right = new Node(bot_right_lat, bot_right_lon);
				obs.vertices.add(top_left);
				obs.vertices.add(bot_left);
				obs.vertices.add(bot_right);
				obs.vertices.add(top_right);

				nArea.obstacles.add(obs);
			}

			as = new AStar(nArea);
			// as = new Dali(nArea);
			// vehicle_lon =
			// sphericalMercator.xAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().longitude);
			// vehicle_lat =
			// sphericalMercator.yAxisProjection(plan.getVehicles().get(0).stateVector.getPosition().latitude);
			//
			for (Vehicle v : plan.getVehicles()) {
				int agentID = 0, milestoneID = 1;// 0 is for the starting position
				List<Node> milestones = new ArrayList<Node>();
				UPPAgentVehicle agent = new UPPAgentVehicle(agentID++);
				agent.vehicle = v;
				milestones.add(agent.getStartNode());
				for (Task task : plan.tasks) {
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
			UPPAgentGenerator.run(agents);
			// call UPPAAL in the server side to synthesize a mission plan
			TransferFile trans = new TransferFile(PlannerServiceHandler.SERVER_IP, PlannerServiceHandler.SERVER_PORT);
			trans.sendFile(UPPAgentGenerator.outputXML);
			trans.close();
			trans = new TransferFile(PlannerServiceHandler.SERVER_IP, PlannerServiceHandler.SERVER_PORT);
			trans.receiveFile(TaskScheduleParser.planPath);
			trans.close();

			// parse the result xml
			TaskSchedulePlan taskPlan = TaskScheduleParser.parse();
			TaskScheduleState states;
			AgentState agentState;
			TaskScheduleAction action;
			Task movement = null, execution = null;
			List<Command> segments = null;
			int startTime = 0;
			Node currentNode, targetNode;
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
								if (task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& task.getArea().area.get(0).equals(currentNode.getPosition())) {
									task.assignedVehicleId = agent.vehicle.id;
									task.startTime = startTime;
									movement.parentTaskId = task.id;
									execution = task;
									//add movement going to this task's milestone
									plan.addToTasks(movement);
									for(Command segment:segments)
									{
										plan.addToCommands(segment);
									}
								}
							}
						}
						// finish an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskFinish)) {
							currentNode = agent.getMilestone(agentState.currentPosition);
							execution.endTime = startTime + (int)action.time;
							plan.addToTasks(execution);
						}
					}
				}
			}
			/******************************************************************
			 * end of the task scheduling code
			 */

			client.sendPlan(requestId, plan);
			System.out.println("Mission Plan Sent!");
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
		}
	}

	@Override
	public String ping() throws TException {
		// TODO Auto-generated method stub
		return "SOME TEST";
	}

	private Task startMove(Node node, UPPAgentVehicle agent, long startTime) {
		int taskID = InitialTaskID + 1;
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

	private List<Command> finishMove(Task transit, UPPAgentVehicle agent, Node endPoint, int duration) {
		Position startPoint = transit.area.area.get(0);
		Path path = agent.findPath(startPoint, endPoint.getPosition());
		Node start = path.start;
		List<Command> movement = new ArrayList<Command>();
		transit.area.area.add(endPoint.getPosition());
		transit.endTime = transit.startTime + duration;
		for (Node end : path.segments) {
			if (!end.equals(start)) {
				Command move = new PathSegment(start, end).createNewMove();
				move.setRelatedTask(transit);
				movement.add(move);
				start = end;
			}
		}

		return movement;
	}
}