package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class NavigationArea {
	public static double threshold;
	public List<Node> boundry;
	public List<Node> milestones;
	public List<Obstacle> obstacles;

	/*
	 * public NavigationArea() { this.boundry = new ArrayList<Node>();
	 * this.milestones = new ArrayList<Node>(); this.obstacles = new
	 * ArrayList<Obstacle>(); }
	 */

	// A square navigation area
	public NavigationArea(Node n[], int number, double speed) {

		double maxLat = n[0].lat, minLat = n[0].lat, maxLon = n[0].lon, minLon = n[0].lon;

		for (int i = 0; i < number; i++) {
			if (maxLat < n[i].lat) {
				maxLat = n[i].lat;
			} else if (minLat > n[i].lat) {
				minLat = n[i].lat;
			}
			if (maxLon < n[i].lon) {
				maxLon = n[i].lon;
			} else if (minLon > n[i].lon) {
				minLon = n[i].lon;
			}
		}
		
		Node topLeft = new Node(maxLat, minLon), topRight = new Node(maxLat, maxLon), 
			 botLeft = new Node(minLat, minLon), botRight = new Node(minLat, maxLon);
		double lon_dif = topLeft.lon - botRight.lon;
		double lat_dif = topLeft.lat - botRight.lat;
		double total_dif = lon_dif > lat_dif ? lon_dif : lat_dif;
		double total_time = total_dif/speed;
		
		NavigationArea.threshold = total_dif / total_time;
		this.boundry = new ArrayList<Node>();
		this.milestones = new ArrayList<Node>();
		this.obstacles = new ArrayList<Obstacle>();
		boundry.add(topLeft);// top left
		boundry.add(botLeft);// bottom left
		boundry.add(botRight);// bottom right
		boundry.add(topRight);// top right
	}

	/*
	 * public NavigationArea(List<Node> boundry, List<Node> milestones,
	 * List<Obstacle> obstacles) { this.boundry = boundry; this.milestones =
	 * milestones; this.obstacles = obstacles; }
	 */

	public boolean contains(Node n) {
		Node topRight = this.boundry.get(3);
		Node botLeft = this.boundry.get(1);
		if (n.lon > topRight.lon || n.lat > topRight.lat) {
			return false;
		}
		if (n.lon < botLeft.lon || n.lat < botLeft.lat) {
			return false;
		}

		return true;
	}

}
