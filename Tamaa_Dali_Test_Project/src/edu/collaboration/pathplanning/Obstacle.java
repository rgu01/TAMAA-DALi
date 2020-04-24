package edu.collaboration.pathplanning;

import java.util.List;

public class Obstacle {
	public List<Coordinates> vertices;

	public boolean lineOfSight(Coordinates start, Coordinates goal)
	{
		PathSegment line = new PathSegment(start, goal), sideOfObstacle;
		
		for(int i = 0; i < vertices.size()-1; i++)
		{
			sideOfObstacle = new PathSegment(vertices.get(i), vertices.get(i+1));
			if(sideOfObstacle.isIntersect(line))
			{
				return false;
			}
		}
		
		return true;
	}
}
