package edu.collaboration.pathplanning;

import java.util.List;

public interface PathPlanningAlgorithm {
	public List<Node> calculate(Node start, Node destination);
}
