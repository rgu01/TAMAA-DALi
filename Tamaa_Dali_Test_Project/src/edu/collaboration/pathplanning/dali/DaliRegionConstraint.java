package edu.collaboration.pathplanning.dali;

import java.util.HashMap;
import java.util.Map.Entry;

import java.util.List;

import edu.collaboration.pathplanning.Node;


public class DaliRegionConstraint {

	CoordinatesTuple topLeft;
	CoordinatesTuple bottomRight;
	double priority;
	boolean isDesirable;
	
	public DaliRegionConstraint(List<Node> corners, double priority) {
		double maxLat = corners.get(0).lat, minLat = corners.get(0).lat, 
				   maxLon = corners.get(0).lon, minLon = corners.get(0).lon;

		Node temp;
		for (int i = 1; i < corners.size(); i++) {
			temp = corners.get(i);
			if (maxLat < temp.lat) {
				maxLat = temp.lat;
			} else if (minLat > temp.lat) {
				minLat = temp.lat;
			}
			if (maxLon < temp.lon) {
				maxLon = temp.lon;
			} else if (minLon > temp.lon) {
				minLon = temp.lon;
			}
		}

		
		topLeft = new CoordinatesTuple(maxLat, minLon);
		bottomRight = new CoordinatesTuple(minLat, maxLon);
		this.priority = priority;
		isDesirable = priority > 1;	
	}

}