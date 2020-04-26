package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public static double threshould = 0.1;
	public double lat;
	public double lon;
	public Node parent;
	
	public Node(double x, double y)
	{
		this.lat = x;
		this.lon = y;
		this.parent = null;
	}
	
	public List<Node> neighbors()
	{
		List<Node> ns = new ArrayList<Node>();
		
		ns.add(new Node(this.lat + Node.threshould, this.lon));
		ns.add(new Node(this.lat + Node.threshould, this.lon - Node.threshould));
		ns.add(new Node(this.lat, this.lon - Node.threshould));
		ns.add(new Node(this.lat - Node.threshould, this.lon - Node.threshould));
		ns.add(new Node(this.lat - Node.threshould, this.lon));
		ns.add(new Node(this.lat - Node.threshould, this.lon + Node.threshould));
		ns.add(new Node(this.lat, this.lon + Node.threshould));
		ns.add(new Node(this.lat + Node.threshould, this.lon + Node.threshould));
		
		return ns;
	}
	
	public boolean equals(Node ns)
	{
		return this.lat == ns.lat && this.lon == ns.lon;
	}
	
	public boolean near(Node ns)
	{
		PathSegment ps = new PathSegment(this, ns);
		
		return ps.weight <= 2*Node.threshould;
	}
}
