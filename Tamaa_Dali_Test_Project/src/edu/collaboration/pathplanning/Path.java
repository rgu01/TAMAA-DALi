package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rgu01
 * A path is a set of path segments that shows the way of going from one node to another
 */
public class Path {
	public Node start;
	public Node end;
	public List<Node> segments;
	
	public Path(Node s, Node e)
	{
		this.start = s;
		this.end = e;
		this.segments = new ArrayList<Node>();
	}

	public Path(Node s, Node e, List<Node> segs)
	{
		this.start = s;
		this.end = e;
		this.segments = segs;
	}
	
	public double length()
	{
		double len = 0.0;
		Node n1 = null, n2 = null;
		PathSegment ps = null;
		
		if(this.segments.size() > 1)
		{
			n1 = this.segments.get(0);
			for(int i = 1; i < this.segments.size(); i++)
			{
				n2 = this.segments.get(i);
				ps = new PathSegment(n1, n2);
				len += ps.directLength();
				n1 = n2;
			}
		}
		
		return len;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean result = false;
		// If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Path or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Path)) { 
            return false; 
        } 
          
        // typecast o to Path so that we can compare data members  
        Path p = (Path) o; 
        
        if(this.start.equals(p.start) && this.end.equals(p.end))
        {
        	result = true;
        }
        else if(this.start.equals(p.end) && this.end.equals(p.start))
        {
        	result = true;
        }
		
		return result;
	}
}
