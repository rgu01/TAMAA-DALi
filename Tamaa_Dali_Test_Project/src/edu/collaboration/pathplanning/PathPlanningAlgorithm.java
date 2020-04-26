package edu.collaboration.pathplanning;

import java.util.List;

public interface PathPlanningAlgorithm {
	public List<PathSegment> calculate(Node start, Node destination);
}
