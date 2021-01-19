package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.afarcloud.thrift.RegionType;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.Path;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;

public class Dali implements PathPlanningAlgorithm {

	HashMap<Integer, DaliNode> nodes = new HashMap<Integer, DaliNode>();
	HashMap<CoordinatesTuple, DaliNode> loc2node = new HashMap<CoordinatesTuple, DaliNode>();
	HashMap<Integer, DaliEdge> edges = new HashMap<Integer, DaliEdge>();
	
	List<DaliAnomaly> anomalies = new ArrayList<DaliAnomaly>();
	List<Obstacle> permanentObstacles;
	HashMap<Integer, Double> heatNodes = new HashMap<Integer, Double>();
	
	public double vehicleSpeed = 1;
	//double startTime =0;
	boolean checkAnomalies = false;
	
	public Dali(NavigationArea nArea) {
		generateGraph(nArea);
		updateHeatMap();
	}
	
	public Dali(NavigationArea nArea, List<DaliRegionConstraint> regionPreferences) {
		generateGraph(nArea, regionPreferences);
		updateHeatMap();
	}
	
//	public Dali(NavigationArea nArea, List<DaliConstraint> constraints) {
//		generateGraph(nArea);
//		for (DaliConstraint c : constraints) {
//			DaliNode center = findNearestNode(c.lat, c.lon);
//			if (center != null) 
//				c.addConstaint(center);
//		}
//	}
	
///////////////////////////////////////////////////////////
// Creation of a graph
///////////////////////////////////////////////////////////
	int nid;
	int eid;
	List<DaliNode> processing = new ArrayList<DaliNode>();
	
	//TODO move to Navigation area 
	//TODO assume square area
	//TODO assume order of corners: tl, bl, br, tr
	boolean isInNavigationArea(NavigationArea nArea, CoordinatesTuple coordinates) {
		Node topLeft = nArea.boundry.get(0);
		Node botRight = nArea.boundry.get(2);
		if (!Utils.isInside(coordinates.lat, coordinates.lon, topLeft, botRight)) {
			return false;
		}
		for (Obstacle obs : permanentObstacles) {
			Node obsTL =  obs.vertices.get(0);
			Node obsBR = obs.vertices.get(2);
			if (Utils.isInside(coordinates.lat, coordinates.lon, obsTL, obsBR)) {
				return false;
			}
		}
		return true;
	}
	
	DaliNode addNode(CoordinatesTuple coordinates, DaliNode prev) {
		DaliNode newNode = findNode(coordinates);
		if (newNode == null) {
			newNode = new DaliNode(nid, coordinates.lat, coordinates.lon);
			nid++;
			processing.add(newNode);
			nodes.put(nid, newNode);
			loc2node.put(coordinates, newNode);
			addToAnomalies(newNode);
		}
		DaliEdge e = prev.createEdge(newNode, eid);
		edges.put(eid, e);
		eid++;
		return newNode;
	}
	
	DaliNode findNode(double x, double y) {
		return loc2node.getOrDefault(new CoordinatesTuple(x, y), null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNode(CoordinatesTuple coordinates) {
		return loc2node.getOrDefault(coordinates, null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNearestNode(double x, double y) {
		DaliNode tl = this.nodes.get(0);
		double xPos = tl.lat - NavigationArea.threshold *(Math.round((tl.lat -x)/ NavigationArea.threshold));
		double yPos = tl.lon + NavigationArea.threshold *(Math.round((y- tl.lon)/ NavigationArea.threshold));
		return findNode(xPos, yPos);
		//return nodes.values().stream().filter(n -> Math.abs(n.lat-x)< NavigationArea.threshold / 2 
		//										&& Math.abs(n.lon-y)< NavigationArea.threshold / 2).findAny().orElse(null);
	}
	
	void generateGraph(NavigationArea nArea) {
		generateGraph(nArea, null);
	}
		
	void generateGraph(NavigationArea nArea, List<DaliRegionConstraint> regionPreferences) {
		//processAnomalies(nArea);
		anomalies = nArea.obstacles.stream().filter(obs -> (obs.endTime >0)).
														map(obs -> new DaliAnomaly(obs)).collect(Collectors.toList());
		permanentObstacles = nArea.obstacles.stream().filter(obs -> (obs.endTime <=0)).collect(Collectors.toList());
		DaliNode topLeft = new DaliNode(0, nArea.boundry.get(0).lat - NavigationArea.threshold / 2 , 
											nArea.boundry.get(0).lon + NavigationArea.threshold / 2 );
		addToAnomalies(topLeft);
		processing.add(topLeft);
		nodes.put(0, topLeft);
		loc2node.put(new CoordinatesTuple(nArea.boundry.get(0).lat, nArea.boundry.get(0).lon), topLeft);
		nid = 1;
		eid = 0;
		while (!processing.isEmpty()) {
			DaliNode currentDaliNode = processing.remove(0);
			for (CoordinatesTuple neighbour : Utils.neighbourLocations(currentDaliNode.lat, currentDaliNode.lon)) {
				if (isInNavigationArea(nArea, neighbour)) {
					DaliNode newNode = addNode(neighbour, currentDaliNode);
					processPriorityRegion(regionPreferences, newNode);
				}				
			}
		}
	}

	private void processPriorityRegion(List<DaliRegionConstraint> regionPreferences, DaliNode newNode) {
		if (regionPreferences != null) {
			for(DaliRegionConstraint rc : regionPreferences) {
				if (Utils.isInside(newNode.lat, newNode.lon, rc.topLeft, rc.bottomRight)) {
					if (rc.regionType == RegionType.HEAT_REGION) {
						if (heatNodes.containsKey(newNode.id)) {
							heatNodes.put(newNode.id, Double.max(heatNodes.get(newNode.id), rc.priority));
						}
						else {
							heatNodes.put(newNode.id, rc.priority);
						}
					}
					else { 
						double localIntencity = 1 + (rc.priority -1)/ newNode.distanceToPoint(rc.center.lat, rc.center.lon);
						if (newNode.regionIntensity < localIntencity) {
							newNode.regionIntensity = localIntencity;
							newNode.isDesirable = rc.regionType == RegionType.PREFERRED;
						}
					}
				}
			}
		}
	}
	
	private void addToAnomalies(Node node) {
		for (DaliAnomaly anomaly : anomalies) {
			if (anomaly.isInside(node)) {
				anomaly.addNode(node);
			}
		}
	}
	
	private boolean blockedByAnomalies(int node_id, double time) {
		for (DaliAnomaly anomaly : anomalies) {
			if (anomaly.isBlocking(node_id , time)) {
				return true;
			}
		}
		return false;
	}

////////////////////////////////////////////
// Methods for updates for heat and anomalies. 
///////////////////////////////////////////
	
	//Heat in interval [0,1] with 0 - unoccupied and 1 - unpassable
	public void updateHeatMap() {
		for (DaliEdge edge : edges.values()) {
			if (heatNodes.containsKey(edge.dest.id)) {
				edge.heat = heatNodes.get(edge.dest.id);
			}
		}
	}
	
	//public void updateAnomalies(List<DaliAnomaly> newAnomalies, double passedTime) {
	//	anomalies.entrySet().removeIf(e -> e.getValue().timeToLive < passedTime);
	//	anomalies.entrySet().forEach(e -> e.getValue().timeToLive -= passedTime);
	//	newAnomalies.forEach(e -> anomalies.put(e.edgeID, e));
	//}
	
	//public void setStartTime(double time) {
	//	this.startTime = time;
	//}
	
	public void setCheckAnomalies(boolean val) {
		this.checkAnomalies = val;
	}

///////////////////////////////////////////	
	
	@Override
	public Path calculate(Node start, Node destination, double vehicleSpeed) {
		return calculate(start, destination, vehicleSpeed, 0);
	}
	
	void clearNodes() {
		for (DaliNode n : this.nodes.values()) {
			n.currentDistance = Double.POSITIVE_INFINITY;
		}
	}
	
	private Boolean sameDirection(DaliNode next, DaliNode cur) {
		DaliNode prev = cur.previous;
		return (next.lat - cur.lat == cur.lat -prev.lat) && (next.lon - cur.lon == cur.lon -prev.lon);
	}
	
	public Path calculate(Node start, Node destination, double vehicleSpeed, double startTime) {
		DaliNode target = findNearestNode(destination.lat, destination.lon);
		DaliNode source = findNearestNode(start.lat, start.lon);
		if (target == null) {
			System.out.println("Target node  not found");
			return null;
		}
		if (source == null) {
			System.out.println("Source node  not found");
			return null;
		}
		PriorityQueue<DaliNode> processing = new PriorityQueue<DaliNode>(new Comparator<DaliNode>() { 
			@Override
		    public int compare(DaliNode a, DaliNode b) {
				return a.currentDistance < b.currentDistance ? -1 : a.currentDistance == b.currentDistance ? 0 : 1;
			}
		});
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		Path p_result = new Path(start, destination);
		source.currentDistance = 0;
		processing.add(source);
		while(!processing.isEmpty() || !distances.containsKey(target)) {
			DaliNode current = processing.remove();
			double currentDistance = current.currentDistance;
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (checkAnomalies && blockedByAnomalies(e.dest.id, currentDistance / vehicleSpeed + startTime)) continue;
				double priorityCoeff = e.dest.isDesirable ? 1/e.dest.regionIntensity : e.dest.regionIntensity;
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat) ;
				if (!processing.contains(e.dest) || e.dest.currentDistance > edist || 
						(current != source && e.dest.currentDistance == edist && sameDirection(e.dest, current))) {
					processing.remove(e.dest);
					e.dest.currentDistance = edist;
					e.dest.previous = current;
					processing.add(e.dest);					
				}			
			}
			distances.put(current, currentDistance);
		}
		clearNodes();
		/* old version
		HashMap<DaliNode, Double> processing = new HashMap<DaliNode, Double>();
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		Path p_result = new Path(start, destination);
		processing.put(source, 0.0);
		while(!processing.isEmpty() || !distances.containsKey(target)) {
			DaliNode current = processing.entrySet().stream().min(Entry.comparingByValue()).get().getKey();
			double currentDistance = processing.get(current);
			processing.remove(current);
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (checkAnomalies && blockedByAnomalies(e.dest.id, currentDistance / vehicleSpeed + startTime)) continue;
				double priorityCoeff = e.dest.isDesirable ? 1/e.dest.regionIntensity : e.dest.regionIntensity;
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat) ;
				if (!processing.containsKey(e.dest) || processing.get(e.dest) > edist) {
					processing.put(e.dest, edist);
					e.dest.previous = current;
				}			
			}
			distances.put(current, currentDistance);
		}
		*/
		if (distances.containsKey(target)) {
			double totalLength = 0;
			List<Node> path = new ArrayList<Node>();
			DaliNode current = target;
			path.add(target);
			while (current.previous != source) {
				DaliEdge e = current.previous.findOutEdge(current);
				totalLength += e.length/(1-e.heat); 
				current = current.previous;
				path.add(0, current);
			}
			DaliEdge e = current.previous.findOutEdge(current);
			totalLength += e.length/(1-e.heat);
			path.add(0, source);
			p_result.segments = path;
			p_result.setLength(totalLength);
			System.out.println("Done");
			return p_result;
		}
		else
		{
			System.out.println("No path found");
			return null;
		}
	}
	
	public boolean pathEntersAnomaly(Path path, double startTime, double speed) {
		int i =0;
		double time = startTime;
		while (i!= path.segments.size() -1) {
			DaliNode cur = (DaliNode)path.segments.get(i);
			DaliNode nxt = (DaliNode)path.segments.get(i+1);
			DaliEdge edge = cur.edges.stream().filter(e -> e.dest == nxt).findFirst().orElse(null);
			if (edge == null) {
				return true;
			}
			time = time + edge.length / (speed * (1-edge.heat));
			if (blockedByAnomalies(nxt.id, time)) {
				return true;
			}
			i++;
		}
		return false;
	}

}
