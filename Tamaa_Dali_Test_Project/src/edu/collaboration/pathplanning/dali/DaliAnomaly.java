package edu.collaboration.pathplanning.dali;

import java.util.HashSet;

import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;

public class DaliAnomaly {

	private HashSet<Integer> nodeIDs = new HashSet<Integer>();
	public final double startTime;
	public final double endTime;
	private final Node topLeft;
	private final Node bottomRight;
	
	public DaliAnomaly(Obstacle obs) {
		this.topLeft = obs.vertices.get(0);
		this.bottomRight = obs.vertices.get(2);
		this.startTime = obs.startTime;
		this.endTime = obs.endTime;
	}
	
	public void addNode(Node node) {
		nodeIDs.add(node.id);
	}
	
	public boolean isInside(Node node) {
		return Utils.isInside(node.lat, node.lon, topLeft, bottomRight);
	}
	
	public boolean isBlocking(int id, double currentTime) {
		return nodeIDs.contains(id) && startTime < currentTime && currentTime < endTime;
	}
}
