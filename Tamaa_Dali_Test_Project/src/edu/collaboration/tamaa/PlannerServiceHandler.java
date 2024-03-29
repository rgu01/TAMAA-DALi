package edu.collaboration.tamaa;

import MercatoerProjection.SphericalMercator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import com.afarcloud.thrift.*;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import edu.collaboration.communication.TransferFile;
import edu.collaboration.model.structure.UPPAgentGenerator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.AStar2;
import edu.collaboration.pathplanning.dali.Dali;
import edu.collaboration.pathplanning.dali.DaliRegionConstraint;
import edu.collaboration.pathplanning.dali.DaliStar;
import edu.collaboration.pathplanning.xstar.*;
import edu.collaboration.tamaa.PlannerServiceHandlerTestVersion.Algo;
import edu.collaboration.taskscheduling.*;

public class PlannerServiceHandler implements PlannerService.Iface {
	// Mission corePlan = null;
	private static int InitialTaskID = 21;
	// public static final String SERVER_IP = "192.168.0.109";
	// public static final String SERVER_IP = "192.168.56.1";
	// public static final String SERVER_IP = "127.0.0.1";
	// public static final int SERVER_PORT = 9779;
	// public String mmtAddress = "192.168.1.2";
	public String mmtAddress = "127.0.0.1";
	public int mmtPort = 9096;
	private String uppaalAddress;
	private int uppaalPort;
	private NavigationArea nArea = null;
	private List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();

	public static String timeLogFile = "./results/time.log";
	public static String logFileDali = "./results/dali.log";
	public static String logFileDaliStar = "./results/dalistar.log";
	private ArrayList<Long> execTimes = new ArrayList<Long>();
	private Boolean UseMultiTargetPathPlanning = false;
	private boolean ownModel;
	private String ownModelAddress;

	public enum Algo {
		AStar, Dali, DaliStar, AStar2
	}

	public Algo algo = Algo.DaliStar;
	// public Algo algo = Algo.AStar;

	private int NbRecomputeUnsuccess = 1;
	private int NbRecomputeTimedAnomalies = 5;

	public PlannerServiceHandler() {
	}

	public PlannerServiceHandler(String mmtAddress, int mmtPort, String uppaalAddress, int uppaalPort, boolean ownModel,
			String ownModelAddress) {
		this.mmtAddress = mmtAddress;
		this.mmtPort = mmtPort;
		this.uppaalAddress = uppaalAddress;
		this.uppaalPort = uppaalPort;
		this.ownModel = ownModel;
		this.ownModelAddress = ownModelAddress;
	}

	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		// Communication with MMT
		TTransport transport = null;
		TProtocol protocol = null;
		MmtService.Client client = null;
		PathPlanningAlgorithm as = null;
		double lat[] = { 0.0, 0.0, 0.0, 0.0 }, lon[] = { 0.0, 0.0, 0.0, 0.0 };
		SphericalMercator sphericalMercator = new SphericalMercator();

		try {
			// initialize the connection to MMT
			transport = new TSocket(this.mmtAddress, this.mmtPort);
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());
			// The information from MMT is stored in "plan".
			// Obtain the navigation area
			nArea = new NavigationArea(plan);
			// Get the number of tasks
			int NTasks = plan.getTasksSize();
			// Obtain the coordinates of the verdices of special areas, such as forbidden areas.
			List<Node> obsVertices = new ArrayList<Node>();
			// Obtain the special areas that Dali can cope with
			List<DaliRegionConstraint> regionPreferences = new ArrayList<DaliRegionConstraint>();
			// Iterate the list of forbidden areas, which actually includes all kinds of special areas
			for (Region forbidden : plan.getForbiddenArea()) {
				lon[3] = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
				lat[3] = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
				lon[2] = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
				lat[2] = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
				lon[1] = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
				lat[1] = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
				lon[0] = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
				lat[0] = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
				obsVertices.add(new Node(lat[0], lon[0]));
				obsVertices.add(new Node(lat[1], lon[1]));
				obsVertices.add(new Node(lat[2], lon[2]));
				obsVertices.add(new Node(lat[3], lon[3]));
				// Put each of the special areas into the corresponding list.
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

			// Choosing a pathfinding algorithm
			switch (this.algo) {
			case AStar:
				as = new AStar(nArea);
				break;
			case Dali:
				as = new Dali(nArea, regionPreferences);
				break;
			case DaliStar:
				as = new DaliStar(nArea, regionPreferences);
				break;
			case AStar2:
				as = new AStar2(nArea);
				break;
			}
			// The the start time of the mission
			long startTime = System.nanoTime();
			// Compute the paths by calling the chosen pathfinding algorithm
			computePaths(plan, as);

			/*****************************************************************
			 * If no server is running, and only path planning is needed, please comment the
			 * code below
			 *****************************************************************/
			List<Path> passAnomalyPaths = new ArrayList<Path>();
			List<Integer> passAnomalyPathsTime = new ArrayList<Integer>();
			// Calculating a mission plan that includes paths and task schedule
			boolean success = generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			// If the calculation fails, we recompute by considering the temporary obstacles (a.k.a., anomalies) and 
			// the users' preference (a.k.a., preferred locations)
			while ((!success && NbRecomputeUnsuccess > 0)
					|| (passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies > 0)) {
				if (passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies > 0)
					success = recomputePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
				else
					success = recomputeWithoutPreferedLocations(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			}
			
			//-------------------------------------------------------------------------------------------------------------
			// Start to write logs. This is only for experiments
			if (success && passAnomalyPaths.size() == 0) {
				long stopTime = System.nanoTime();

				try {
					FileWriter fw = new FileWriter(timeLogFile, true);
					String log = String.valueOf((stopTime - startTime) / 1000000) + ' ' + algo.toString()
							+ " Threshold " + String.valueOf(NavigationArea.threshold) + " Tasks "
							+ String.valueOf(NTasks) + " Forbidden " + String.valueOf(plan.getForbiddenArea().size())
							+ "\n";
					fw.write(log);
					fw.close();
				} catch (Exception e) {
				}

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
				}
				System.out.println(commandsLog);
				//End of writing logs. This is only for experiments
				//-------------------------------------------------------------------------------------------------------------

				// Send the mission plan to MMT
				client.sendPlan(requestId, plan);
			} else {
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
			this.agents.clear();
		}
	}

	private boolean recomputeWithoutPreferedLocations(Mission plan, PathPlanningAlgorithm as,
			List<Path> passAnomalyPaths, List<Integer> passAnomalyPathsTime) throws Exception {
		NbRecomputeUnsuccess--;
		NbRecomputeTimedAnomalies = 5;
		if (this.algo == Algo.AStar || this.algo == Algo.AStar2) {
			return false;
		}
		Dali dali = (Dali) as;
		dali.setUsePreferedAreas(false);
		for (UPPAgentVehicle agent : this.agents) {
			for (int i = 0; i < agent.paths.size(); i++) {
				Path path = agent.paths.get(i);
				long startTime1 = System.nanoTime();
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, 0);
				execTimes.add(System.nanoTime() - startTime1);
				agent.paths.set(i, newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}

	private boolean recomputePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,
			List<Integer> passAnomalyPathsTime) throws Exception {
		NbRecomputeTimedAnomalies--;
		if (this.algo == Algo.AStar || this.algo == Algo.AStar2) {
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
				long startTime1 = System.nanoTime();
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, startTime);
				execTimes.add(System.nanoTime() - startTime1);
				agent.paths.add(newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}

	
	private boolean generatePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,
			List<Integer> passAnomalyPathsTime) throws Exception {
		// boolean success = true;
		// Call uppaal to generate mission plans
		boolean success = callUppaal();
		if (!success) {
			// String show = "Time out: server does not respond!";
			// JOptionPane.showMessageDialog(null, show, "Error: Time Out",
			// JOptionPane.PLAIN_MESSAGE);
		} else {
			// A mission plan is successfully generated and stored in an XML file
			passAnomalyPaths.clear();
			passAnomalyPathsTime.clear();
			// Parse the XML file of the mission plan
			success = parseXML(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			if (!success) {
				// String show = "No mission plan is found!";
				// JOptionPane.showMessageDialog(null, show, "Warning: Dissatisfied",
				// JOptionPane.PLAIN_MESSAGE);
			}
		}
		return success;
	}
	

	private void cleanPlan(Mission plan) {
		plan.commands.clear();
		plan.tasks.removeIf(x -> x.getTaskTemplate().getTaskType().name() == "TRANSIT");
	}
	

	private int newTaskID(Mission plan) {
		int maxID = 0;

		for (Task task : plan.tasks) {
			if (maxID < task.id) {
				maxID = task.id;
			}
		}

		return maxID + 1;
	}
	
	/**
	 * This function parses the resulting XML file of the mission plan. 
	 * @param plan
	 * @param as
	 * @param passAnomalyPaths
	 * @param passAnomalyPathsTime
	 * @return
	 */

	private boolean parseXML(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,
			List<Integer> passAnomalyPathsTime) {
		TaskSchedulePlan taskPlan = TaskScheduleParser.parse();
		TaskScheduleState states;
		AgentState agentState;
		TaskScheduleAction action;
		// multiple vehicles
		Task[] movement = new Task[this.agents.size()];
		// Task[] execution = new Task[this.agents.size()];
		List<Command>[] segments = new ArrayList[this.agents.size()];
		Integer[] startTime = new Integer[this.agents.size()];
		Node[] currentNode = new Node[this.agents.size()];
		Node[] targetNode = new Node[this.agents.size()];
		Node[] waypoint = new Node[this.agents.size()];
		Node[] lastPosition = new Node[this.agents.size()];
		Integer[] lastPostionTime = new Integer[this.agents.size()];

		if (taskPlan.satisfied) {
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
							if (as instanceof Dali) {
								Path currentPath = agent.findPath(lastPosition[agent.ID].getPosition(),
										targetNode[agent.ID].getPosition());
								if (((Dali) as).pathEntersAnomaly(currentPath, lastPostionTime[agent.ID],
										agent.vehicle.maxSpeed)) {
									passAnomalyPaths.add(currentPath);
									passAnomalyPathsTime.add(lastPostionTime[agent.ID]);
								}

							}
							segments[agent.ID] = this.finishMove(movement[agent.ID], agent, targetNode[agent.ID],
									(int) action.time, as);
							startTime[agent.ID] += (int) action.time;
						}
						// start an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskStart)) {
							currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
							Task newTask = null;
							for (Task task : plan.tasks) {
								waypoint[agent.ID] = new Node(task.getArea().area.get(0));
								if (task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint[agent.ID].equals(currentNode[agent.ID])) {
									if (task.endTime == 0) {
										// this task is not assigned yet
										task.assignedVehicleId = agent.vehicle.id;
										task.startTime = startTime[agent.ID];
										movement[agent.ID].parentTaskId = task.id;
										// execution[agent.ID] = task;
										break;
									} else {
										newTask = task.deepCopy();
										newTask.id = this.newTaskID(plan);
										newTask.assignedVehicleId = agent.vehicle.id;
										newTask.startTime = startTime[agent.ID];
										newTask.endTime = 0;
										movement[agent.ID].parentTaskId = newTask.id;
										break;
									}
								}
							}
							if (newTask != null) {
								plan.addToTasks(newTask);
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
							// execution[agent.ID].assignedVehicleId = agent.vehicle.id;
							// execution[agent.ID].endTime = startTime[agent.ID] + (int) action.time;
							// newly added
							for (Task task : plan.tasks) {
								waypoint[agent.ID] = new Node(task.getArea().area.get(0));
								if (task.assignedVehicleId == agent.vehicle.id
										&& task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint[agent.ID].equals(currentNode[agent.ID]) && task.endTime == 0) {
									task.endTime = startTime[agent.ID] + (int) action.time;
									startTime[agent.ID] = (int) task.endTime;
									break;
								}
							}
						}
					}
				}
			}
		}

		return taskPlan.satisfied;
	}

	
	private boolean callUppaal() throws Exception {
		boolean result = true;
		// Generate the UPPAAL model for generating mission plans
		// @Claire, this function will be replaced by your function of generating the new UPPAAL models
		UPPAgentGenerator.run(this.agents);
		// Send the UPPAAL model to the server
		TransferFile trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
		if (!this.ownModel) {
			trans.sendFile(UPPAgentGenerator.outputXML);
		} else {
			trans.sendFile(this.ownModelAddress);
			// trans.sendFile("./model/special use case - no monitors.xml");
		}
		// The model is transferred
		trans.close();
		// Receive the result of mission planning from the server
		trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
		trans.receiveFile(TaskScheduleParser.planPath);
		// An XML file that stores resulting mission plan is received
		if (trans.isClosed() && trans.timeOut) {
			result = false;
		}
		trans.close();
		return result;
	}
	

	private void computePaths(Mission plan, PathPlanningAlgorithm as) {
		if (this.UseMultiTargetPathPlanning && ((this.algo == Algo.Dali || this.algo == Algo.DaliStar))) {
			computePathsMultiTarget(plan, as);
		} else {
			computePathsSingleTarget(plan, as);
		}
	}
	

	private void computePathsSingleTarget(Mission plan, PathPlanningAlgorithm as) {
		int agentID = 0, milestoneID = 1;// 0 is for the starting position
		HashMap<Node, HashMap<Node, Path>> computedPaths = new HashMap<Node, HashMap<Node, Path>>();
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
							if (computedPaths.containsKey(n1) && computedPaths.get(n1).containsKey(n2)) {
								path = computedPaths.get(n1).get(n2);
							} else {
								long startTime1 = System.nanoTime();
								path = as.calculate(n1, n2, v.maxSpeed);
								execTimes.add(System.nanoTime() - startTime1);
								if (!computedPaths.containsKey(n1)) {
									computedPaths.put(n1, new HashMap<Node, Path>());
								}
								computedPaths.get(n1).put(n2, path);
							}
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
	

	private void computePathsMultiTarget(Mission plan, PathPlanningAlgorithm as) {
		int agentID = 0, milestoneID = 1;// 0 is for the starting position
		HashMap<Node, HashMap<Node, Path>> computedPaths = new HashMap<Node, HashMap<Node, Path>>();
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
				List<Node> destinations = new ArrayList<Node>();
				for (Node n2 : milestones) {
					if (!n1.equals(n2)) {
						Path path = new Path(n1, n2);
						if (!agent.isPathExist(path)) {
							if (computedPaths.containsKey(n1) && computedPaths.get(n1).containsKey(n2)) {
								path = computedPaths.get(n1).get(n2);
								if (path != null && !paths.contains(path)) {
									paths.add(path);
								}
							} else {
								destinations.add(n2);
							}
						}
					}
				}
				if (destinations.size() != 0) {
					long startTime1 = System.nanoTime();
					List<Path> pathsFromN = ((Dali) as).calculateSingleSource(n1, destinations, v.maxSpeed);
					execTimes.add(System.nanoTime() - startTime1);
					if (!computedPaths.containsKey(n1)) {
						computedPaths.put(n1, new HashMap<Node, Path>());
					}
					for (Path p : pathsFromN) {
						computedPaths.get(n1).put(p.end, p);
						if (p != null && !paths.contains(p)) {
							paths.add(p);
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
		if (taskID == 25) {
			taskID = 25;
		}
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

	private List<Command> finishMove(Task transit, UPPAgentVehicle agent, Node endPoint, int duration,
			PathPlanningAlgorithm as) {
		Position startPoint = transit.area.area.get(0);
		Path path = agent.findPath(startPoint, endPoint.getPosition());
		Node start = path.start;
		long startTime = transit.startTime;
		List<Command> movement = new ArrayList<Command>();
		List<Node> segments = path.segments;
		if (this.algo == Algo.Dali || this.algo == Algo.DaliStar) {
			segments = ((Dali) as).pathStraightener(path, transit.startTime, agent.vehicle.maxSpeed);
		}
		transit.area.area.add(endPoint.getPosition());
		transit.endTime = transit.startTime + duration;
		for (Node end : segments) {
			// for (Node end : path.segments) {
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
