package edu.collaboration.pathplanning.xstar;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.Node;

public class DaliNode extends Node {
	
	int id;
	List<DaliEdge> edges= new ArrayList<DaliEdge>();
	
	boolean isDesirable = true;
	double intensity =1;
	
	DaliNode previous = null; //for backtracking

	public DaliNode(int id, double x, double y) {
		super(x, y);
		this.id = id;
	}
	
	public DaliEdge createEdge(DaliNode dest, int edgeID) {
		DaliEdge newEdge = new DaliEdge(edgeID, this, dest);
		this.edges.add(newEdge);
		return newEdge;
	}
	
	public double distanceToOther(Node other) {
		return distanceToPoint(other.lat, other.lon);
	}
	
	public double distanceToPoint(double x, double y) {
		return Math.sqrt((this.lat - x)*(this.lat - x) + (this.lon - y)*(this.lon - y));
	}
}
