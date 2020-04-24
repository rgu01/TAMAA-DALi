package edu.collaboration.pathplanning;

public class PathSegment {
	public Coordinates origin;
	public Coordinates end;
	
	public PathSegment(double x1, double y1, double x2, double y2)
	{
		this.origin = new Coordinates(x1,y1);
		this.end = new Coordinates(x2,y2);
	}
	
	public PathSegment(Coordinates o, Coordinates e)
	{
		this.origin = o;
		this.end = e;
	}
	
	public boolean isIntersect(PathSegment ps)
	{
		double l1x1 = this.origin.lon;
		double l1y1 = this.origin.lat;
		double l1x2 = this.end.lon;
		double l1y2 = this.end.lat;
		double l2x1 = ps.origin.lon;
		double l2y1 = ps.origin.lat;
		double l2x2 = ps.end.lon;
		double l2y2 = ps.end.lat;

        if (Math.max(l1x1,l1x2) < Math.min(l2x1 ,l2x2)
            || Math.max(l1y1,l1y2) < Math.min(l2y1,l2y2)
            || Math.max(l2x1,l2x2) < Math.min(l1x1,l1x2)
            || Math.max(l2y1,l2y2) < Math.min(l1y1,l1y2))
        {
            return false;
        }

        if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1)) 
              * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
            || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1)) 
              * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0)
        {
            return false;
        }
        return true;
	}

}
