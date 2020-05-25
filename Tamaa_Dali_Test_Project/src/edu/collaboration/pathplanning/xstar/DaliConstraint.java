package edu.collaboration.pathplanning.xstar;

import java.util.HashMap;
import java.util.Map.Entry;


import edu.collaboration.pathplanning.Node;

public class DaliConstraint {
	
	double lon;
	double lat;
	double intensity;
	double radius;
	boolean isDesirable;
	
	public DaliConstraint(double x, double y, double inten, double dist) {
		lon =x;
		lat =y;
		intensity = inten;
		radius = dist;
		isDesirable = (intensity > 0);
	}
	
	DaliNode findNode(HashMap<Integer,DaliNode> nodes, double x, double y) {
		double dist = Node.threshould * 2;
		DaliNode nearest = null;
		for (DaliNode n : nodes.values()) {
			double newDist = n.distanceToPoint(x, y);
			if (newDist < dist) {
				dist = newDist;
				nearest = n;
			}
		}
		return nearest;
	}
	
	public void addConstaint(HashMap<Integer,DaliNode> nodes) {
		DaliNode center = findNode(nodes, lon, lat);
		HashMap<DaliNode, Double> processing = new HashMap<DaliNode, Double>();
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		processing.put(center,0.0);
		while(!processing.isEmpty()) {
			DaliNode current = processing.entrySet().stream().min(Entry.comparingByValue()).get().getKey();
			double currentDistance = processing.get(current);
			processing.remove(current);
			for (DaliEdge e : current.edges) {
				if (!distances.containsKey(e.dest) && (currentDistance + e.length < radius) &&
						(!processing.containsKey(e.dest) || processing.get(e.dest) > currentDistance + e.length)) {
					processing.put(e.dest, currentDistance + e.length);
				}
			}
			distances.put(current, currentDistance);
		}
		for (Entry<DaliNode, Double> entry : distances.entrySet()) {
			double effect = (entry.getValue())*intensity/radius;
			if (effect > entry.getKey().intensity) {
				entry.getKey().isDesirable = isDesirable;
				entry.getKey().intensity = effect;
			}
		}
	}

}
 