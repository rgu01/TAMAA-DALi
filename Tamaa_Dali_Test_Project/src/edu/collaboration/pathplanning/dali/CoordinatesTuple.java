package edu.collaboration.pathplanning.dali;

public class CoordinatesTuple {
	double x;
	double y;
	
	public CoordinatesTuple(double lat, double lon) {
		x = lat;
		y = lon;		
	}
	
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof CoordinatesTuple)) return false;
	    CoordinatesTuple co = (CoordinatesTuple) o;
	    return this.x == co.x && this.y == co.y;
	}
}
