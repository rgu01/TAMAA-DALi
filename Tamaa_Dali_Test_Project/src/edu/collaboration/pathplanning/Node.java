package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public static double threshould = 1;
	public double lat;
	public double lon;
	public Node parent;
	//
	public Node(double lat, double lon)
	{
		this.lon = lon;
		this.lat = lat;
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
	
	@Override
	public boolean equals(Object o)
	{
		// If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Node)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        Node ns = (Node) o; 
		
		return this.lat == ns.lat && this.lon == ns.lon;
	}
	
	public String toString()
	{
		return "(" + this.lat + ", " + this.lon + ")";
	}
	
	public boolean near(Node ns)
	{
		PathSegment ps = new PathSegment(this, ns);
		
		return ps.cost() <= 2*Node.threshould;
	}
}
