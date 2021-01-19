package edu.collaboration.model.structure;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Equipment;
import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Task;
import com.afarcloud.thrift.TaskType;
import com.afarcloud.thrift.Vehicle;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Path;

public class UPPAgentVehicle {
	public int ID;
	public int missionTimeLimit;
	public Vehicle vehicle;
	public List<Path> paths = new ArrayList<Path>();
	public List<UPPAgentMission> missions = new ArrayList<UPPAgentMission>();

	public UPPAgentVehicle(int id) {
		this.ID = id;
	}

	public boolean isPathExist(Path p) {
		for (Path path : this.paths) {
			if (path.equals(p)) {
				return true;
			}
		}

		return false;
	}

	public Node getStartNode() {
		Position origin = this.vehicle.stateVector.position;
		Node start = new Node(origin);
		// p0 is the starting point of the vehicle
		start.id = 0;
		return start;
	}

	public boolean canDoTask(Task task) {
		for (TaskType type : this.vehicle.getCapabilities()) {
			if (task.taskTemplate.taskType == type) {
				for (Equipment equipment : this.vehicle.equipments) {
					if (task.getTaskTemplate().getRequiredTypes().get(0).equals(equipment.getType())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public Path findPath(Node start, Node end) {
		for (Path path : this.paths) {
			if ((path.start.equals(start) && path.end.equals(end))
					|| (path.start.equals(end) && path.end.equals(start))) {
				return path;
			}
		}

		return null;
	}

	public Path findPath(Position start, Position end) {
		Node startNode = new Node(start);
		Node endNode = new Node(end);

		return this.findPath(startNode, endNode);
	}

	public void addTask(Node milestone) {
		boolean tag = true;
		if (!this.missions.isEmpty()) {
			for (UPPAgentMission mission : this.missions) {
				// if the tasks' types and their required equipment are the same
				// they are grouped in the same group, but only located at different positions
				if ((mission.task.taskTemplate.equals(milestone.task.taskTemplate))
						&& (mission.task.getTaskTemplate().requiredTypes.get(0)
								.equals(milestone.task.getTaskTemplate().requiredTypes.get(0)))) {
					mission.addMilestone(milestone);
					tag = false;
					break;
				}
			}
		}
		if (tag) {
			UPPAgentMission newOne = new UPPAgentMission(milestone.task.missionId, milestone);
			this.missions.add(newOne);
		}
	}

	public Node getMilestone(String posiStr) {
		int id = -1;
		Node node = null;

		if (posiStr.contains("P")) {
			id = Integer.parseInt(posiStr.substring(1, 2));
			if (id == 0) {
				node = this.getStartNode();
			} else {
				for (UPPAgentMission m : this.missions) {
					for (Node n : m.getMilestones()) {
						if (n.id == id) {
							node = n;
							break;
						}
					}
				}
			}
		}

		return node;
	}
}
