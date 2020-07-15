package edu.collaboration.pathplanning.xstar;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.Node;

public class ANode extends Node {
	public double gValue;
	private double hValue;
	private Node destination;
	
	public ANode(double x, double y, Node destination)
	{
		super(x, y);
		this.gValue = 0;
		this.hValue = this.estimatedCost(destination);
		this.destination = destination;
	}
	
	private double estimatedCost(Node goal)
	{
		return Math.abs(this.lat - goal.lat) + Math.abs(this.lon - goal.lon); 
	}
	
	public double openValue()
	{
		return this.gValue + this.hValue;
	}
	
	@Override
	public List<Node> neighbors()
	{
		List<Node> ns = new ArrayList<Node>();
		
		ns.add(new ANode(this.lat + Node.threshold, this.lon, this.destination));
		ns.add(new ANode(this.lat + Node.threshold, this.lon - Node.threshold, this.destination));
		ns.add(new ANode(this.lat, this.lon - Node.threshold, this.destination));
		ns.add(new ANode(this.lat - Node.threshold, this.lon - Node.threshold, this.destination));
		ns.add(new ANode(this.lat - Node.threshold, this.lon, this.destination));
		ns.add(new ANode(this.lat - Node.threshold, this.lon + Node.threshold, this.destination));
		ns.add(new ANode(this.lat, this.lon + Node.threshold, this.destination));
		ns.add(new ANode(this.lat + Node.threshold, this.lon + Node.threshold, this.destination));
		
		return ns;
	}

}
