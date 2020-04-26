package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
	public List<Node> vertices;
	
	public Obstacle()
	{
		this.vertices = new ArrayList<Node>();
	}

	public boolean block(Node start, Node goal)
	{
		PathSegment line = new PathSegment(start, goal), sideOfObstacle;
		
		for(int i = 0; i < vertices.size()-1; i++)
		{
			sideOfObstacle = new PathSegment(vertices.get(i), vertices.get(i+1));
			if(sideOfObstacle.isIntersect(line))
			{
				return true;
			}
		}
		
		return false;
	}
}
