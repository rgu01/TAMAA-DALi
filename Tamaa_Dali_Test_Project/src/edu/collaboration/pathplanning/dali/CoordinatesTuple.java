package edu.collaboration.pathplanning.dali;

public class CoordinatesTuple {
	public final double x;
	public final double y;
	
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
	
	@Override
	public int hashCode() {
		int result = 17;
		result = (int) (result * 31 + this.x);
		result = (int) (result * 31 + this.y);		
		return result;
	}
}
