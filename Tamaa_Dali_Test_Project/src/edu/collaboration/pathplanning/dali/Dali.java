package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.Path;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;

public class Dali implements PathPlanningAlgorithm {

	HashMap<Integer, DaliNode> nodes = new HashMap<Integer, DaliNode>();
	HashMap<CoordinatesTuple, DaliNode> loc2node = new HashMap<CoordinatesTuple, DaliNode>();
	HashMap<Integer, DaliEdge> edges = new HashMap<Integer, DaliEdge>();
	
	HashMap<Integer, DaliAnomaly> anomalies = new HashMap<Integer, DaliAnomaly>();
	
	public double vehicleSpeed = 1;
	
	public Dali(NavigationArea nArea) {
		generateGraph(nArea);
	}
	
	public Dali(NavigationArea nArea, List<DaliConstraint> constraints) {
		generateGraph(nArea);
		for (DaliConstraint c : constraints) {
			DaliNode center = findNearestNode(c.lat, c.lon);
			if (center != null) 
				c.addConstaint(center);
		}
	}
	
///////////////////////////////////////////////////////////
// Creation of a graph
///////////////////////////////////////////////////////////
	int nid;
	int eid;
	List<DaliNode> processing = new ArrayList<DaliNode>();
	
	//TODO move to Navigation area 
	//TODO assume square area
	//TODO assume order of corners: tl, bl, br, tr
	boolean isInArea(NavigationArea nArea, CoordinatesTuple coordinates) {
		Node topLeft = nArea.boundry.get(0);
		Node botRight = nArea.boundry.get(2);
		if( coordinates.x <= topLeft.lat || coordinates.x >= botRight.lat || coordinates.y >= topLeft.lon || coordinates.y <= botRight.lon) {

			return false;
		}
		for (Obstacle obs : nArea.obstacles) {
			Node obsTL =  obs.vertices.get(0);
			Node obsBR = obs.vertices.get(2);
			if( coordinates.x > obsTL.lat && coordinates.x < obsBR.lat && coordinates.y < obsTL.lon && coordinates.y > obsBR.lon) {
				return false;
			}
		}
		return true;
	}
	
	List<CoordinatesTuple> neighbourLocations(double lat, double lon) {
		ArrayList<CoordinatesTuple> res = new ArrayList<CoordinatesTuple>();
		res.add(new CoordinatesTuple(lat, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon ));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon + NavigationArea.threshold));
		return res;
	}
	
	void addNode(CoordinatesTuple coordinates, DaliNode prev) {
		DaliNode newNode = findNode(coordinates);
		if (newNode == null) {
			newNode = new DaliNode(nid, coordinates.x, coordinates.y);
			nid++;
			processing.add(newNode);
			nodes.put(nid, newNode);
			loc2node.put(coordinates, newNode);
		}
		DaliEdge e = prev.createEdge(newNode, eid);
		edges.put(eid, e);
		eid++;
	}
	
	DaliNode findNode(double x, double y) {
		return loc2node.getOrDefault(new CoordinatesTuple(x, y), null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNode(CoordinatesTuple coordinates) {
		return loc2node.getOrDefault(coordinates, null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNearestNode(double x, double y) {
		return nodes.values().stream().filter(n -> Math.abs(n.lat-x)< NavigationArea.threshold / 2 
												&& Math.abs(n.lon-y)< NavigationArea.threshold / 2).findAny().orElse(null);
	}
	
	void generateGraph(NavigationArea nArea) {
		DaliNode topLeft = new DaliNode(0, nArea.boundry.get(0).lat, nArea.boundry.get(0).lon);
		processing.add(topLeft);
		nodes.put(0, topLeft);
		loc2node.put(new CoordinatesTuple(nArea.boundry.get(0).lat, nArea.boundry.get(0).lon), topLeft);
		nid = 1;
		eid = 0;
		while (!processing.isEmpty()) {
			DaliNode currentDaliNode = processing.remove(0);
			for (CoordinatesTuple neighbour : neighbourLocations(currentDaliNode.lat, currentDaliNode.lon)) {
				if (isInArea(nArea, neighbour)) {
					addNode(neighbour, currentDaliNode);
				}				
			}
		}
	}

////////////////////////////////////////////
// Methods for updates for heat and anomalies. 
///////////////////////////////////////////
	
	//Heat in interval [0,1] with 0 - unoccupied and 1 - unpassable
	public void updateHeatMap(HashMap<Integer, Double> heat) {
		for (Entry<Integer, Double> entry : heat.entrySet()) {
			edges.get(entry.getKey()).heat = entry.getValue();
		}
	}
	
	public void updateAnomalies(List<DaliAnomaly> newAnomalies, double passedTime) {
		anomalies.entrySet().removeIf(e -> e.getValue().timeToLive < passedTime);
		anomalies.entrySet().forEach(e -> e.getValue().timeToLive -= passedTime);
		newAnomalies.forEach(e -> anomalies.put(e.edgeID, e));
	}

///////////////////////////////////////////	
	
	@Override
	public Path calculate(Node start, Node destination) {
		DaliNode target = findNearestNode(destination.lat, destination.lon);
		DaliNode source = findNearestNode(start.lat, start.lon);
		HashMap<DaliNode, Double> processing = new HashMap<DaliNode, Double>();
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		Path p_result = new Path(source, destination);
		processing.put(source, 0.0);
		while(!processing.isEmpty() || !distances.containsKey(target)) {
			DaliNode current = processing.entrySet().stream().min(Entry.comparingByValue()).get().getKey();
			double currentDistance = processing.get(current);
			processing.remove(current);
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (anomalies.containsKey(e.id) && anomalies.get(e.id).timeToLive > currentDistance / vehicleSpeed) continue;
				double edist = currentDistance + (e.dest.isDesirable ? 1/e.dest.intensity : e.dest.intensity) * e.length / (1-e.heat);
				if (!processing.containsKey(e.dest) || processing.get(e.dest) > edist) {
					processing.put(e.dest, edist);
					e.dest.previous = current;
				}			
			}
			distances.put(current, currentDistance);
		}
		if (distances.containsKey(target)) {
			List<Node> path = new ArrayList<Node>();
			DaliNode current = target;
			path.add(target);
			while (current.previous != source) {
				current = current.previous;
				path.add(0, current);
			}	
			path.add(0, source);
			p_result.segments = path;
			System.out.println("Done");
			return p_result;
		}
		else
		{
			System.out.println("No path found");
			return null;
		}
	}

}
