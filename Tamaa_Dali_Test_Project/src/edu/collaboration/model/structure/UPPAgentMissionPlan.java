package edu.collaboration.model.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.fmaes.j2uppaal.builders.UppaalXMLSorter;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalAutomaton;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLabel;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLocation;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalTransition;

public class UPPAgentMissionPlan extends UppaalAutomaton {
	public static String SystemName="TaskExecution";
	public static String InstanceName="taskExe";
	public static String MissionPath = "res/tasks"; 
	private static int x = -400, y = -200;
	public List<UPPAgentMission> missions = new ArrayList<UPPAgentMission>();
	public int regularTasksNum=0;
	public int eventTasksNum=0;
	private int agent;
	private int taskNum;
	//private int Limit=4; //limit for re-initialization

	public UPPAgentMissionPlan(int agentID, UPPAgentEventMonitor ev, int tn)
	{
		super();
		agent = agentID;
		taskNum = tn;
		createMission(ev);
		initialize();
	}
	
	/*private void randomTasks(UPPAgentEventMonitor ev)
	{
		int i,j;
		UPPAgentMission m = null;
		taskNum = 2;
		for(i=1;i<=taskNum;i++)
		{
			m = new UPPAgentMission(i,ev);
			//best case execution time
			m.bcet = (int) (10*Math.random());
			//worst case execution time
			m.wcet = m.bcet + (int) (10*Math.random());
			//milestones
			m.milestones.add((int) (taskNum*Math.random()));
	        //event trigger this task
	        m.addEventsTrigger(-1);
	        this.regularTasksNum++;
	        if(i>1)
	        {
	        	m.setPrecondition("tf["+(i-1)+"]");
	        }
	        else
	        {
	        	m.setPrecondition("-");
	        }
	        missions.add(m);
		}
	}*/
	
	private void createMission(UPPAgentEventMonitor ev)
	{
		int id = 0;
		//randomTasks(ev);
		try 
		{ 
			File filename = new File(MissionPath+agent+".txt"); 
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); 
			Scanner sc = new Scanner(reader);
			if(sc != null && sc.hasNextLine())
			{
				sc.nextLine();
				while(sc.hasNextInt())
				{
					//id
					id = sc.nextInt();
					UPPAgentMission m = null;
					if(id == 0)
					{
						sc.close();
						throw new Exception("0 is a reserved id for idle task, please change your configuration of tasks");
					}
					else
					{
						m = new UPPAgentMission(id,ev);
					}
					//best case execution time
					m.bcet = sc.nextInt();
					//worst case execution time
					m.wcet = sc.nextInt();
					//milestones
			        for (String retval: sc.next().split(","))
			        {
			            m.milestones.add(Integer.parseInt(retval));
			        }
			        //event trigger this task
			        m.addEventsTrigger(sc.nextInt());
			        if(m.regularTask)
			        {
			        	this.regularTasksNum++;
			        }
			        else
			        {
			        	this.eventTasksNum++;
			        }
	        		//events.add(Integer.parseInt(retval));
			        //event to reset
			        //for (String retval: sc.next().split(","))
			        //{
			        //	if(retval != "-1")
			        //	{
			        //		m.addEventsToReset(Integer.parseInt(retval));
			        //	}
			        //}
					//precondition
			        m.setPrecondition(sc.next());
			        missions.add(m);
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initialize()
	{
		UppaalLocation m,source,target;
		UppaalLabel label;
		UppaalTransition transition;
		UPPAgentMission um;
		int tx=0,ty=0,sx=0,sy=0,nx=0,ny=0,k=0,j=0;
		String mParameters = "";
		String mDeclaration = "";
		
		m = new UppaalLocation();
		m.setName("T0");
		m.setId("T0");
		setLocationCorrdinates(m, x, y);
		
		this.addOrReplaceLocation(m);	
		this.setInitialLocation("T0");
		
		for(int i=0, id=1; i<missions.size();i++,id++)
		{
			um = missions.get(i);
			k=i%4;
			switch (k)
			{
			case 0:
				tx = x-300;
				ty = y-j*200;
				break;
			case 1:
				tx = x+j*200;
				ty = y-300;
				break;
			case 2:
				tx = x+300;
				ty = y+j*200;
				break;
			case 3:
				tx = x+j*200;
				ty = y+300;
				j++;
				break;
			}
				
			m = new UppaalLocation();
			m.setName("T"+id);
			m.setId("T"+id);
			setLocationCorrdinates(m, tx, ty);
			label = new UppaalLabel();
			label.setKind("invariant");
			//label.setValue("t<=" + "WCET[id]["+id+"]");
			label.setValue("t<=" + um.wcet);
			m.addOrReplaceLabel(label);
			this.addOrReplaceLocation(m);
		}
		
		transition = new UppaalTransition();
		transition.setSourceLocationId("T0");
		transition.setTargetLocationId("T0");
		label = new UppaalLabel();
		label.setKind("synchronisation");
		label.setValue("move[id]!");
		transition.addOrReplaceLabel(label);
		//reinitialize
		/*label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("steps++,t=0");
		transition.addOrReplaceLabel(label);*/
		label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("t=0");
		transition.addOrReplaceLabel(label);
		this.addOrReplaceTransition(transition);
		
		String guardOfPositions = "";
		//String assignmentOfEvents = "";
		for(int i=0, id=1; i<missions.size();i++,id++)
		{
			um = missions.get(i);
			transition = new UppaalTransition();
			transition.setSourceLocationId("T0");
			transition.setTargetLocationId("T"+id);
			label = new UppaalLabel();
			label.setKind("assignment");
			//for(j=0; j<um.getEventsToTrigger().size();j++)
			//{
			//	assignmentOfEvents += ",E[" + um.getEventsToTrigger().get(j) + "]=true";
			//}
			//label.setValue("t=0,MS["+id+"]=true,MF["+id+"]=false" + assignmentOfEvents);
			label.setValue("t=0,ts["+id+"]=true,tf["+id+"]=false");
			//label.setValue("t=0,tf["+id+"]=false");
			transition.addOrReplaceLabel(label);
			label = new UppaalLabel();
			label.setKind("guard");
			for(j=0; j<um.milestones.size();j++)
			{
				if(j != um.milestones.size()-1)
				{
					guardOfPositions += "position[id][" + um.milestones.get(j) + "]||";
				}
				else
				{
					guardOfPositions += "position[id][" + um.milestones.get(j) + "]";
				}
			}
			if(um.getPrecondition().equals(""))
			{
				label.setValue(guardOfPositions);
			}
			else
			{
				label.setValue(guardOfPositions + "&amp;&amp;(" + um.getPrecondition()+")");
			}
			transition.addOrReplaceLabel(label);
			this.addOrReplaceTransition(transition);
			
			guardOfPositions = "";
			//assignmentOfEvents = "";
			
			transition = new UppaalTransition();
			transition.setSourceLocationId("T"+id);
			transition.setTargetLocationId("T0");
			
			source = (UppaalLocation) this.getLocationById("T"+id);
			target = (UppaalLocation) this.getLocationById("T0");
			sx=Integer.parseInt(source.getCoordinate("x"));
			sy=Integer.parseInt(source.getCoordinate("y"));
			tx=Integer.parseInt(target.getCoordinate("x"));
			ty=Integer.parseInt(target.getCoordinate("y"));
			if(sx==tx && sy==ty)
			{
				nx = sx - 100;
			}
			else if(sx!=tx)
			{
				nx = sx + (tx - sx) / 2;
			}
			else if(sy!=ty)
			{
				ny = sy + (ty - sy) / 2;
			}
			
			label = new UppaalLabel();
			label.setKind("assignment");
			//for(j=0; j<um.getEventsToReset().size();j++)
			//{
			//	assignmentOfEvents += ",E[" + um.getEventsToReset().get(j) + "]=false";
			//}
			//label.setValue("t=0,MS["+id+"]=false,MF["+id+"]=true" + assignmentOfEvents);
			label.setValue("t=0,ts["+id+"]=false,tf["+id+"]=true,\r\nupdateIteration()");
			//label.setValue("t=0,tf["+id+"]=true,\r\nupdateIteration()");
			label.setCoordinate("x", nx+30+"");
			label.setCoordinate("y", ny+30+"");
			transition.addOrReplaceLabel(label);
			label = new UppaalLabel();
			label.setKind("guard");
			//label.setValue("t>=" + "BCET[id]["+id+"]");
			label.setValue("t>=" + um.bcet);
			label.setCoordinate("x", nx+"");
			label.setCoordinate("y", ny+"");
			transition.addOrReplaceLabel(label);
			if(!um.regularTask)
			{
				label = new UppaalLabel();
				label.setKind("synchronisation");
				label.setValue("done[id][" + um.getEventsTrigger().get(0) + "]!");
				label.setCoordinate("x", nx-30+"");
				label.setCoordinate("y", ny-30+"");
				transition.addOrReplaceLabel(label);
			}
			transition.addNail(nx+"", ny+"");
			this.addOrReplaceTransition(transition);
			
			guardOfPositions = "";
			//assignmentOfEvents = "";
		}
		
		//sel-loop for initialization
		/*transition = new UppaalTransition();
		transition.setSourceLocationId("T0");
		transition.setTargetLocationId("T0");
		label = new UppaalLabel();
		label.setKind("guard");
		label.setValue("steps >= " + Limit);
		transition.addOrReplaceLabel(label);
		label = new UppaalLabel();
		label.setKind("synchronisation");
		label.setValue("initialize!");
		transition.addOrReplaceLabel(label);
		label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("reset()");
		transition.addOrReplaceLabel(label);
		sx = Integer.parseInt(((UppaalLocation)this.getLocationById("T0")).getCoordinate("x")) + 20;
		sy = Integer.parseInt(((UppaalLocation)this.getLocationById("T0")).getCoordinate("y")) + 50;
		transition.addNail(sx+"", sy+"");
		sx = Integer.parseInt(((UppaalLocation)this.getLocationById("T0")).getCoordinate("x")) + 50;
		sy = Integer.parseInt(((UppaalLocation)this.getLocationById("T0")).getCoordinate("y")) + 20;
		transition.addNail(sx+"", sy+"");
		this.addTransition(transition);*/
		
		childrenUppaalElements.sort(new UppaalXMLSorter()); 

		this.setName(SystemName+agent);
		mParameters = "const AgentScale id";
		String tfString = "";
		String tsString = "";
		for(int i = 0; i < taskNum; i++)
		{
			if(i==0)
			{
				tfString += "bool tf[TaskNum]={true,";
				tsString += "bool ts[TaskNum]={true,";
			}
			tfString += "false";
			tsString += "false";
			if(i!=taskNum-1)
			{
				tfString += ",";
				tsString += ",";
			}
			else
			{
				tfString += "};\r\n";
				tsString += "};\r\n";
			}
			
		}
		mDeclaration += "clock t;\r\n" + 
				tfString + tsString + "\r\n" + 
				"void updateIteration()\r\n" +
				"{\r\n" +
				"	int i=0;\r\n" +
				"	bool finish = true;\r\n" +
				"	for(i=1;i<=RegularTaskNum[id];i++)\r\n" +
				"	{\r\n" +
				"		finish &amp;= tf[i];\r\n" +
    			"	}\r\n" +
    			"	if(finish)\r\n" +
    			"	{\r\n" +
    			"		for(i=1;i<=RegularTaskNum[id];i++)\r\n" +
    			"		{\r\n" +
    			"			tf[i]=false;\r\n" +
        		"		}\r\n" +
    			"		\r\n" +
    			"		iteration[id]+=1;\r\n" +
    			"		if(iteration[id]==MaxIteration)\r\n" +
    			"		{\r\n" +
    			"			iteration[id]=0;\r\n" +
        		"		}\r\n" +
    			"	}\r\n" + 
    			"	else\r\n" +
    			"	{\r\n" +
    			"    	finish = true;\r\n" +
    			"	}\r\n" + 
    			"}\r\n\r\n";
    			/*"void reset()\r\n" +
    			"{\r\n" +
    			"   t=0;\r\n" +
    			"   gReset();\r\n" +
    			"}\r\n";*/
		this.setParameter(mParameters);
		this.setDeclaration(mDeclaration);
	}

	private void setLocationCorrdinates(UppaalLocation loc, int x, int y) 
	{
		loc.setCoordinate("x", Integer.toString(x));
		loc.setCoordinate("y", Integer.toString(y));
	}
}
