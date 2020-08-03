package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private static int ID = 0;
	public int id;
	public double lat;
	public double lon;
	public Node parent;
	//
	public Node(double lat, double lon)
	{
		this.lon = lon;
		this.lat = lat;
		this.parent = null;
		id = Node.ID;
		Node.ID = Node.ID + 1;
	}
	
	public List<Node> neighbors()
	{
		List<Node> ns = new ArrayList<Node>();
		
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon));
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon + NavigationArea.threshold));
		ns.add(new Node(this.lat, this.lon + NavigationArea.threshold));
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon + NavigationArea.threshold));
		
		return ns;
	}
	

@Override
	public int hashCode() {
		int result = 17;
		result = (int) (result * 31 + this.lat);
		result = (int) (result * 31 + this.lon);
		
		return result;
	}
	
	@Override
	public boolean equals(Object o)
	{
		// If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Node or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Node)) { 
            return false; 
        } 
          
        // typecast o to Node so that we can compare data members  
        Node ns = (Node) o; 
		
		return this.lat == ns.lat && this.lon == ns.lon;
	}
	
	public String toString()
	{
		return "Node "+ this.id +":(" + this.lat + ", " + this.lon + ")";
	}
	
	public boolean near(Node ns)
	{
		PathSegment ps = new PathSegment(this, ns);
		
		return ps.directLength() <= 2*NavigationArea.threshold;
	}
	
	@Override
	public Node clone()
	{
		Node copy = new Node(this.lat, this.lon);
		copy.parent = this.parent.clone();
		
		return copy;
	}
}
