package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;

public class Dali implements PathPlanningAlgorithm {

	HashMap<Integer, DaliNode> nodes;
	HashMap<CoordinatesTuple, DaliNode> loc2node;
	HashMap<Integer, DaliEdge> edges;
	
	HashMap<Integer, DaliAnomaly> anomalies;
	
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
	boolean isInArea(NavigationArea nArea, double x, double y) {
		Node topLeft = nArea.boundry.get(0);
		Node botRight = nArea.boundry.get(2);
		if( x < topLeft.lat || x > botRight.lat || y < topLeft.lon || y > botRight.lon) {
			return false;
		}
		for (Obstacle obs : nArea.obstacles) {
			Node obsTL =  obs.vertices.get(0);
			Node obsBR = obs.vertices.get(2);
			if( x > obsTL.lat && x < obsBR.lat && y > obsTL.lon && y < obsBR.lon) {
				return false;
			}
		}
		return true;
	}
	
	void addNode(double x, double y, DaliNode prev) {
		DaliNode newNode = findNode(x, y);
		if (newNode == null) {
			newNode = new DaliNode(nid, x, y);
			nid++;
			processing.add(newNode);
			nodes.put(nid, newNode);
			loc2node.put(new CoordinatesTuple(x, y), newNode);
		}
		DaliEdge e1 = prev.createEdge(newNode, eid);
		DaliEdge e2 = newNode.createEdge(prev, eid+1);
		edges.put(eid, e1);
		edges.put(eid + 1, e2);
		eid +=2;
	}
	
	DaliNode findNode(double x, double y) {
		return loc2node.getOrDefault(new CoordinatesTuple(x, y), null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}
	
	DaliNode findNearestNode(double x, double y) {
		return nodes.values().stream().filter(n -> Math.abs(n.lat-x)< Node.threshold / 2 && Math.abs(n.lon-y)< Node.threshold / 2).findAny().orElse(null);
	}
	
	void generateGraph(NavigationArea nArea) {
		//TODO guess it is top-left 
		DaliNode topLeft = new DaliNode(0, nArea.boundry.get(0).lat, nArea.boundry.get(0).lon);
		processing.add(topLeft);
		nodes.put(0, topLeft);
		loc2node.put(new CoordinatesTuple(nArea.boundry.get(0).lat, nArea.boundry.get(0).lon), topLeft);
		nid =1;
		eid = 0;
		
		while (!processing.isEmpty()) {
			DaliNode currentDaliNode = processing.remove(0);
			if (isInArea(nArea, currentDaliNode.lat + Node.threshold, currentDaliNode.lon)) {
				addNode(currentDaliNode.lat + Node.threshold, currentDaliNode.lon, currentDaliNode);
			}
			if (isInArea(nArea, currentDaliNode.lat, currentDaliNode.lon + Node.threshold)) {
				addNode(currentDaliNode.lat, currentDaliNode.lon + Node.threshold, currentDaliNode);
			}
			if (isInArea(nArea, currentDaliNode.lat + Node.threshold, currentDaliNode.lon - Node.threshold)) {
				addNode(currentDaliNode.lat + Node.threshold, currentDaliNode.lon + Node.threshold, currentDaliNode);
			}
			if (isInArea(nArea, currentDaliNode.lat + Node.threshold, currentDaliNode.lon - Node.threshold)) {
				addNode(currentDaliNode.lat + Node.threshold, currentDaliNode.lon + Node.threshold, currentDaliNode);
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
	public List<Node> calculate(Node start, Node destination) {
		DaliNode target = (DaliNode)destination;
		HashMap<DaliNode, Double> processing = new HashMap<DaliNode, Double>();
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		processing.put((DaliNode)start, 0.0);
		while(!processing.isEmpty() || !distances.containsKey(target)) {
			DaliNode current = processing.entrySet().stream().min(Entry.comparingByValue()).get().getKey();
			double currentDistance = processing.get(current);
			processing.remove(current);
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (anomalies.containsKey(e.id) && anomalies.get(e.id).timeToLive > currentDistance / vehicleSpeed) continue;
				double edist = distances.get(current) + (e.dest.isDesirable ? 1/e.dest.intensity : e.dest.intensity) * e.length / (1-e.heat);
				if (!processing.containsKey(e.dest) || processing.get(e.dest) > edist) {
					processing.put(e.dest, edist);
					e.dest.previous = current;
				}			
			}
			distances.put(current, currentDistance);
		}
		if (distances.containsKey(target)) {
			List<Node> result = new ArrayList<Node>();
			DaliNode current = target;
			result.add(target);
			while (current.previous != null) {
				current = current.previous;
				result.add(0, current);
			}			
			return result;
		}
		else
		{
			System.out.println("No path found");
			return null;
		}
	}

}
