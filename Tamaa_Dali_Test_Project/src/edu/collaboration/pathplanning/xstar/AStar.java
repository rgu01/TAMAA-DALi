package edu.collaboration.pathplanning.xstar;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;
import edu.collaboration.pathplanning.PathSegment;

public class AStar implements PathPlanningAlgorithm {
	protected List<ANode> open;
	protected List<ANode> closed;
	protected NavigationArea map;
	
	
	public AStar(NavigationArea na)
	{
		this.open = new ArrayList<ANode>();
		this.closed = new ArrayList<ANode>();
		this.map = na;
	}

	@Override
	public List<Node> calculate(Node start, Node destination) {
		ANode temp = null, aStart = null, aDestination = null;
		List<Node> result = new ArrayList<Node>();
		
		aStart = new ANode(start.lat, start.lon, destination);
		aDestination = new ANode(destination.lat, destination.lon, destination);
		
		aStart.parent = aStart;
		open.add(aStart);
		
		while(open.size() != 0)
		{
			temp = this.pop(this.open);
			if(temp == null || temp.near(aDestination))
			{
				break;
			}
			closed.add(temp);
			for(Node nb : temp.neighbors())
			{
				if(map.contains(nb))
				{
					if(!closed.contains(nb))
					{
						if(!open.contains(nb))
						{
							((ANode)nb).gValue = Double.MAX_VALUE;
							nb.parent = null;
						}
						this.update(temp, (ANode)nb);
					}
				}
			}
		}
		
		while(temp != null && !temp.equals(start))
		{
			result.add(temp);
			temp = (ANode)temp.parent;
		}
		
		return result;
	}
	
	protected boolean lineOfSight(Node start, Node goal)
	{
		for(Obstacle obs : this.map.obstacles)
		{
			if(obs.block(start, goal))
			{
				return false;
			}
		}
		
		return true;
	}
	
	protected void update(ANode s, ANode sp)
	{
		double valueOld = sp.gValue;
		
		this.computeCost(s, sp);
		if(sp.gValue < valueOld)
		{
			if(open.contains(sp))
			{
				open.remove(sp);
			}
			open.add(sp);
		}
		
	}
	
	protected void computeCost(ANode s, ANode sp)
	{
		//Path 1
		PathSegment ps = new PathSegment(s, sp);
		double c = ps.cost();
		
		if(this.lineOfSight(s, sp))
		{
			if(s.gValue + c < sp.gValue)
			{
				sp.parent = s;
				sp.gValue = s.gValue + c;
			}
			else
			{
				// no code
			}
		}
		else
		{
			//no code
		}
	}
	
	protected ANode pop(List<ANode> set)
	{
		double min = Double.MAX_VALUE;
		ANode result = null;
		
		for(int i = 0; i < set.size(); i++)
		{
			if(min >= set.get(i).openValue())
			{
				result = set.get(i);
				min = result.openValue();
			}
		}
		
		if(result != null)
		{
			set.remove(result);
		}
		
		return result;
	}
	
}
