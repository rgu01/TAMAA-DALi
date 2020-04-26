package edu.collaboration.pathplanning.xstar;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;
import edu.collaboration.pathplanning.PathSegment;

public class AStar implements PathPlanningAlgorithm {
	protected List<ANode> open;
	protected List<ANode> closed;
	protected List<Obstacle> obstacles;
	
	
	public AStar(List<Obstacle> obs)
	{
		this.open = new ArrayList<ANode>();
		this.closed = new ArrayList<ANode>();
		this.obstacles = obs;
	}

	@Override
	public List<PathSegment> calculate(Node start, Node destination) {
		ANode temp, aStart, aDestination;
		
		aStart = new ANode(start.lat, start.lon, destination);
		aDestination = new ANode(destination.lat, destination.lon, destination);
		
		aStart.parent = aStart;
		open.add(aStart);
		
		while(open.size() != 0)
		{
			temp = this.pop(this.open);
			if(temp.near(aDestination))
			{
				break;
			}
			closed.add(temp);
			for(Node nb : temp.neighbors())
			{
				if(this.lineOfSight(temp, nb))
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
		
		return null;
	}
	
	protected boolean lineOfSight(Node start, Node goal)
	{
		for(Obstacle obs : this.obstacles)
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
		
		if(s.gValue + c < sp.gValue)
		{
			sp.parent = s;
			sp.gValue = s.gValue + c;
		}
	}
	
	protected ANode pop(List<ANode> set)
	{
		double max = 0;
		ANode result = null;
		
		for(int i = 0; i < set.size(); i++)
		{
			if(max <= set.get(i).openValue())
			{
				result = set.get(i);
				max = result.openValue();
			}
		}
		
		return result;
	}
	
}
