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

import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import edu.collaboration.communication.TransferFile;
import edu.collaboration.model.structure.UPPAgentGenerator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.Dali;
import edu.collaboration.pathplanning.dali.DaliRegionConstraint;
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
	private NavigationArea nArea = null;
	private List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
	
	private int NbRecomputeUnsuccess = 1;
	private int NbRecomputeTimedAnomalies =5;
	
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
		boolean useDali = true;
		// boolean pathExist = false;
		// Communication with MMT
		TTransport transport = null;
		TProtocol protocol = null;
		MmtService.Client client = null;
		PathPlanningAlgorithm as = null;
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0,
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		SphericalMercator sphericalMercator = new SphericalMercator();

		try {
			transport = new TSocket(this.mmtAddress, this.mmtPort);
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());
			nArea = new NavigationArea(plan);
			
			/*
			 * nArea.boundry.add(top_left); nArea.boundry.add(bot_left);
			 * nArea.boundry.add(bot_right); nArea.boundry.add(top_right);
			 */
			// Obstacles
			List<Node> obsVertices = new ArrayList<Node>();
			List<DaliRegionConstraint> regionPreferences = new ArrayList<DaliRegionConstraint>();
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
				switch (forbidden.regionType) {
				case FORBIDDEN:
					nArea.obstacles
							.add(new Obstacle(obsVertices, (double) forbidden.startTime, (double) forbidden.endTime));
					break;
				case PREFERRED:
					regionPreferences
							.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.PREFERRED));
					break;
				case LESS_PREFERRED:
					regionPreferences
							.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.LESS_PREFERRED));
					break;
				case HEAT_REGION:
					regionPreferences
							.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.HEAT_REGION));
					break;
				default:
				}
				obsVertices.clear();
			}

			as = useDali ? new Dali(nArea, regionPreferences) : new AStar(nArea);
			computePaths(plan, as);

			/*****************************************************************
			 * If no server is running, and only path planning is needed, please comment the
			 * code below
			 */
			List<Path> passAnomalyPaths = new ArrayList<Path>();
			List<Integer> passAnomalyPathsTime = new ArrayList<Integer>();
			boolean success = generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			
			while ((!success && NbRecomputeUnsuccess >0)  || 
					(passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies >0)) {
				if 	(passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies >0) 			
					success = recomputePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
				else 
					success = recomputeWithoutPreferedLocations(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			}
	
			if (success && passAnomalyPaths.size() ==0) {
				String commandsLog = "";
				Position ppp1 = new Position(), ppp2 = new Position();
				Node s1, s2;
				for (int i = 0; i < plan.getCommands().size() - 1; i++) {
					ppp1.latitude = plan.getCommands().get(i).params.get(4);
					ppp1.longitude = plan.getCommands().get(i).params.get(5);
					ppp2.latitude = plan.getCommands().get(i + 1).params.get(4);
					ppp2.longitude = plan.getCommands().get(i + 1).params.get(5);
					s1 = new Node(ppp1);
					s2 = new Node(ppp2);
					if (i == 58) {
						i = i + 0;
					}
					if (nArea.collide(s1, s2)) {
						i = i + 0;
					}
					commandsLog += plan.getCommands().get(i).endTime + ": " + new Node(ppp1).toString() + "\r\n";
					// commandsLog += plan.getCommands().get(i+1).endTime + ": " + new
					// Node(ppp2).toString() + "\r\n";
				}
				System.out.println(commandsLog);

				client.sendPlan(requestId, plan);
				// System.out.println("Mission Plan Sent!");
				String show = "A plan with " + plan.tasks.size() + " tasks, and " + plan.commands.size()
						+ " commands has been sent!";
				JOptionPane.showMessageDialog(null, show, "Done", JOptionPane.PLAIN_MESSAGE);
			}
			else {
				String show = "No mission plan is found! Recomputation limit is reached";
				JOptionPane.showMessageDialog(null, show, "Warning: Dissatisfied", JOptionPane.PLAIN_MESSAGE);
			}
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

	private boolean recomputeWithoutPreferedLocations(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,
			List<Integer> passAnomalyPathsTime) throws Exception {
		NbRecomputeUnsuccess--;
		NbRecomputeTimedAnomalies = 5;
		if (!(as instanceof Dali)) {
			return false;
		}
		Dali dali = (Dali) as;
		dali.setUsePreferedAreas(false);
		for (UPPAgentVehicle agent : this.agents) {
			for (int i = 0; i < agent.paths.size(); i++) {
				Path path = agent.paths.get(i);
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, 0);
				agent.paths.set(i, newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}
	
	private boolean recomputePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths, 
			List<Integer> passAnomalyPathsTime) throws Exception {
		NbRecomputeTimedAnomalies--;
		if (!(as instanceof Dali)) {
			passAnomalyPaths.clear();
			return false;
		}
		Dali dali = (Dali) as;
		dali.setCheckAnomalies(true);
		cleanPlan(plan);
		for (UPPAgentVehicle agent : this.agents) {
			for (int i = 0; i < passAnomalyPaths.size(); i++) {
				Path path = passAnomalyPaths.get(i);
				int startTime = passAnomalyPathsTime.get(i);
				agent.missionTimeLimit = nArea.missionTimeLimit;
				agent.paths.removeIf(oldpath -> oldpath.start == path.start && oldpath.end == path.end);
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, startTime);
				agent.paths.add(newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}

	private boolean generatePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,
			List<Integer> passAnomalyPathsTime) throws Exception {
	    boolean success = callUppaal();
		if (!success) {
			String show = "Time out: server does not respond!";
			//JOptionPane.showMessageDialog(null, show, "Error: Time Out", JOptionPane.PLAIN_MESSAGE);
		}
		else
		{
			passAnomalyPaths.clear();
			passAnomalyPathsTime.clear();
			success = parseXML(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			if (!success) {
				String show = "No mission plan is found!";
				//JOptionPane.showMessageDialog(null, show, "Warning: Dissatisfied", JOptionPane.PLAIN_MESSAGE);
			}
		}
		return success;
	}
	


	private void cleanPlan(Mission plan) {
		plan.commands.clear();
		plan.tasks.removeIf(x -> x.getTaskTemplate().getTaskType().name() == "TRANSIT");
	}

	private boolean parseXML(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths, List<Integer> passAnomalyPathsTime) {
		TaskSchedulePlan taskPlan = TaskScheduleParser.parse();
		TaskScheduleState states;
		AgentState agentState;
		TaskScheduleAction action;
		// multiple vehicles
		Task[] movement = new Task[this.agents.size()];
		Task[] execution = new Task[this.agents.size()];
		List<Command>[] segments = new ArrayList[this.agents.size()];
		Integer[] startTime = new Integer[this.agents.size()];
		Node[] currentNode = new Node[this.agents.size()];
		Node[] targetNode = new Node[this.agents.size()];
		Node[] waypoint = new Node[this.agents.size()];
		Node[] lastPosition = new Node[this.agents.size()];
		Integer[] lastPostionTime = new Integer[this.agents.size()];
		
		if(taskPlan.satisfied)
		{
			for (int index = 0; index < taskPlan.length(); index++) {
				states = taskPlan.states.get(index);
				action = taskPlan.actions.get(index);
				for (UPPAgentVehicle agent : this.agents) {
					if (agent.ID == action.agentID) {
						agentState = states.getAgentState(agent.ID);
						// start to move
						if (action.type.equals(TaskScheduleAction.StrMoveStart)) {
							if (!agentState.currentPosition.equals("initial")) {
								currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
								if (startTime[agent.ID] == null) {
									startTime[agent.ID] = 0;
								}
								movement[agent.ID] = this.startMove(currentNode[agent.ID], agent, startTime[agent.ID]);
								if (as instanceof Dali) {
									lastPosition[agent.ID] = currentNode[agent.ID];
									lastPostionTime[agent.ID] = startTime[agent.ID];
								}
							}
						}
						// finish a move
						else if (action.type.equals(TaskScheduleAction.StrMoveFinish)) {
							targetNode[agent.ID] = agent.getMilestone(action.target);
							segments[agent.ID] = this.finishMove(movement[agent.ID], agent, targetNode[agent.ID],
									(int) action.time);
							if (as instanceof Dali) {
								Path currentPath = agent.findPath(lastPosition[agent.ID].getPosition(),
										targetNode[agent.ID].getPosition());
								if (((Dali) as).pathEntersAnomaly(currentPath, lastPostionTime[agent.ID],
										agent.vehicle.maxSpeed)) {
									passAnomalyPaths.add(currentPath);
									passAnomalyPathsTime.add(lastPostionTime[agent.ID]);
								}
							}
							startTime[agent.ID] += (int) action.time;
						}
						// start an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskStart)) {
							currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
							for (Task task : plan.tasks) {
								waypoint[agent.ID] = new Node(task.getArea().area.get(0));
								if (task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint[agent.ID].equals(currentNode[agent.ID])) {
									task.assignedVehicleId = agent.vehicle.id;
									task.startTime = startTime[agent.ID];
									movement[agent.ID].parentTaskId = task.id;
									execution[agent.ID] = task;
									break;
								}
							}
							// add movement going to this task's milestone
							plan.addToTasks(movement[agent.ID]);
							for (Command segment : segments[agent.ID]) {
								plan.addToCommands(segment);
							}
						}
						// finish an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskFinish)) {
							currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
							execution[agent.ID].assignedVehicleId = agent.vehicle.id;
							execution[agent.ID].endTime = startTime[agent.ID] + (int) action.time;
							// plan.addToTasks(execution);
						}
					}
				}
			}
		}

		return taskPlan.satisfied;
	}

	private boolean callUppaal() throws Exception {
		boolean result = true;
		UPPAgentGenerator.run(this.agents); // call UPPAAL in the server side to synthesize a mission plan
		TransferFile trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
		trans.sendFile(UPPAgentGenerator.outputXML);
		trans.close();
		trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
		trans.receiveFile(TaskScheduleParser.planPath);
		if (trans.isClosed() && trans.timeOut) {
			result = false;
		}
		trans.close();
		return result;
	}

	private void computePaths(Mission plan, PathPlanningAlgorithm as) {
		int agentID = 0, milestoneID = 1;// 0 is for the starting position
		for (Vehicle v : plan.getVehicles()) {
			milestoneID = 1;
			List<Node> milestones = new ArrayList<Node>();
			UPPAgentVehicle agent = new UPPAgentVehicle(agentID++);
			agent.missionTimeLimit = nArea.missionTimeLimit;
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
							path = as.calculate(n1, n2, v.maxSpeed);
						}

						if (path != null && !paths.contains(path)) {
							paths.add(path);
						}
					}
				}
			}
			agent.paths = paths;
			this.agents.add(agent);
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