package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class NavigationArea {
	public static double threshold = 5;
	public List<Node> boundry;
	public List<Node> milestones;
	public List<Obstacle> obstacles;
	
	/*public NavigationArea()
	{
		this.boundry = new ArrayList<Node>();
		this.milestones = new ArrayList<Node>();
		this.obstacles = new ArrayList<Obstacle>();
	}*/	
	
	//A square navigation area
	public NavigationArea(Node topLeft, Node botLeft, Node botRight, Node topRight)
	{
		int granularity = 100;
		double lon_dif = topLeft.lon - botRight.lon;
		double lat_dif = topLeft.lat - botRight.lat;
		double total_dif = lon_dif > lat_dif ? lon_dif : lat_dif;
		NavigationArea.threshold = total_dif/granularity;
		this.boundry = new ArrayList<Node>();
		this.milestones = new ArrayList<Node>();
		this.obstacles = new ArrayList<Obstacle>();
		boundry.add(topLeft);
		boundry.add(botLeft);
		boundry.add(botRight);
		boundry.add(topRight);
	}
	
	/*public NavigationArea(List<Node> boundry, List<Node> milestones, List<Obstacle> obstacles)
	{
		this.boundry = boundry;
		this.milestones = milestones;
		this.obstacles = obstacles;
	}*/

	public boolean contains(Node n)
	{
		Node topRight = this.boundry.get(3);
		Node botLeft = this.boundry.get(1);
		if(n.lon > topRight.lon || n.lat > topRight.lat)
		{
			return false;
		}
		if(n.lon < botLeft.lon || n.lat < botLeft.lat)
		{
			return false;
		}
		
		return true;
	}
	
}
