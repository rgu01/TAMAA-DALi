package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;

public class Utils {

	public static List<CoordinatesTuple> neighbourLocations(double lat, double lon) {
		ArrayList<CoordinatesTuple> res = new ArrayList<CoordinatesTuple>();
		res.add(new CoordinatesTuple(lat, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon ));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon + NavigationArea.threshold));
		return res;
	}
	
	public static boolean isInside(double lat, double lon, Node topLeft, Node bottomRight) {
		return ( lat < topLeft.lat && lat > bottomRight.lat &&
				lon > topLeft.lon && lon < bottomRight.lon);
	}
	
	public static boolean isInside(double lat, double lon, CoordinatesTuple topLeft, CoordinatesTuple bottomRight) {
		return ( lat < topLeft.lat && lat > bottomRight.lat &&
				lon > topLeft.lon && lon < bottomRight.lon);
	}
}
