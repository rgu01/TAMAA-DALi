package edu.collaboration.model.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.fmaes.j2uppaal.builders.UppaalXMLSorter;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalAutomaton;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLabel;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLocation;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalTransition;

public class UPPAgentStaticMap extends UppaalAutomaton {
	public static String SystemName="Movement";
	public static String InstanceName="movement";
	public static String MapPath = "res/map.txt"; 
	public String mDeclaration = "clock t;\r\n";
	/*public String mDeclaration = "clock t;\r\n" + 
			   					 "\r\n" + 
			   					 "void reset()\r\n" +
			   					 "{\r\n" +
			   					 "	t=0;\r\n" + 
			   					 "}\r\n";*/
	public String mParameters = "const AgentScale id";
	private static int x = -400, y = -200;
	public	int Scale = 50;
	public int map[][];
	private int agent;
	
	public UPPAgentStaticMap(int agentID)
	{
		super();
		agent = agentID;
		createMap();
		Initialize();
	}
	
	private void randomMap()
	{
		int ran = 0, i = 0, j = 0;
		map = new int[Scale][Scale];
		
		for(i = 0; i < Scale; i++)
		{
			for(j = 0; j < Scale; j++)
			{
				if(i < j)
				{
					ran = (int) (10*Math.random());
					//20% - disconnected
					if(ran >= 8)
					{
						map[i][j] = -1;
					}
					else
					{
						map[i][j] = ran;
					}
				}
				else if (i > j)
				{
					map[i][j] = map[j][i];
				}
				else
				{
					map[i][j] = 0;
				}
			}
		}
	}

	
	private void createMap()
	{
		try 
		{ 
			File filename = new File(MapPath); 
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); 
			Scanner sc = new Scanner(reader);
			this.Scale = sc.nextInt();
			map = new int[Scale][Scale];
			for(int i = 0; i < Scale; i++)
			{
				for(int j = 0; j < Scale; j++)
				{
					if(sc.hasNextInt())
					{
						map[i][j] = sc.nextInt();
					}
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//randomMap();  
	}

	private void Initialize() {
		UppaalLocation p;
		UppaalLabel label;
		UppaalTransition transition;
		
		UppaalLocation initial = new UppaalLocation();
		initial.setName("initial");
		initial.setId("initial");
		initial.setUrgent();
		setLocationCorrdinates(initial, x-100, y-100);
		
		this.addOrReplaceLocation(initial);	
		this.setInitialLocation("initial");
		
		for(int i=0;i<Scale;i++)
		{
			p = new UppaalLocation();
			p.setName("P"+i);
			p.setId("P"+i);
			setLocationCorrdinates(p, x+i*150, y+i*150);
			this.addOrReplaceLocation(p);
			
			for(int j=0;j<Scale;j++)
			{				
				if(map[i][j] > 0)
				{					
					p = new UppaalLocation();
					p.setName("F"+i+"T"+j);
					p.setId("F"+i+"T"+j);
					setLocationCorrdinates(p, x+100+j*150, y+100+i*150);
					label = new UppaalLabel();
					label.setKind("invariant");
					label.setValue("t<=" + map[i][j]);
					p.addOrReplaceLabel(label);
					this.addOrReplaceLocation(p);
				}
			}
		}
		
		/*for(int i=0;i<Scale;i++)
		{
			transition = new UppaalTransition();
			transition.setSourceLocationId("P"+i);
			transition.setTargetLocationId("P"+i);
			label = new UppaalLabel();
			label.setKind("synchronisation");
			label.setValue("done?");
			transition.addOrReplaceLabel(label);
			this.addOrReplaceTransition(transition);
		}*/
		
		transition = new UppaalTransition();
		transition.setSourceLocationId("initial");
		transition.setTargetLocationId("P"+0);
		label = new UppaalLabel();
		label.setKind("assignment");
		//label.setValue("position[id][0]=true,\r\nvisited[id][0]=true");
		label.setValue("position[id][0]=true");
		transition.addOrReplaceLabel(label);
		this.addOrReplaceTransition(transition);
		
		for(int i=0;i<Scale;i++)
		{
			for(int j=0;j<Scale;j++)
			{				
				if(map[i][j] > 0)
				{			
					transition = new UppaalTransition();
					transition.setSourceLocationId("P"+i);
					transition.setTargetLocationId("F"+i+"T"+j);
					label = new UppaalLabel();
					label.setKind("assignment");
					label.setValue("t=0,position[id]["+i+"]=false");
					transition.addOrReplaceLabel(label);
					label = new UppaalLabel();
					label.setKind("synchronisation");
					label.setValue("move[id]?");
					transition.addOrReplaceLabel(label);
					this.addOrReplaceTransition(transition);
					
					transition = new UppaalTransition();
					transition.setSourceLocationId("F"+i+"T"+j);
					transition.setTargetLocationId("P"+j);
					label = new UppaalLabel();
					label.setKind("assignment");
					//label.setValue("t=0,position[id]["+j+"]=true,\r\nvisited[id]["+j+"]=true");
					label.setValue("t=0,position[id]["+j+"]=true");
					transition.addOrReplaceLabel(label);
					label = new UppaalLabel();
					label.setKind("guard");
					label.setValue("t>="+map[i][j]);
					transition.addOrReplaceLabel(label);
					this.addOrReplaceTransition(transition);
				}
			}
		}
		
		//transitions back to initial state
		/*String locationName="";
		for(int i=0;i<Scale;i++)
		{
			locationName="P"+i;
			transition = new UppaalTransition();
			transition.setSourceLocationId(locationName);
			transition.setTargetLocationId("initial");
			label = new UppaalLabel();
			label.setKind("assignment");
			label.setValue("reset()");
			transition.addOrReplaceLabel(label);
			label = new UppaalLabel();
			label.setKind("synchronisation");
			label.setValue("initialize?");
			transition.addOrReplaceLabel(label);
			this.addOrReplaceTransition(transition);
			
			for(int j=0;j<Scale;j++)
			{				
				if(map[i][j] > 0)
				{		
					locationName="T"+i+j;
					transition = new UppaalTransition();
					transition.setSourceLocationId(locationName);
					transition.setTargetLocationId("initial");
					label = new UppaalLabel();
					label.setKind("assignment");
					label.setValue("reset()");
					transition.addOrReplaceLabel(label);
					label = new UppaalLabel();
					label.setKind("synchronisation");
					label.setValue("initialize?");
					transition.addOrReplaceLabel(label);
					this.addOrReplaceTransition(transition);
				}
			}
		}*/
		
		childrenUppaalElements.sort(new UppaalXMLSorter()); 

		this.setName(SystemName+agent);
		this.setParameter(mParameters);
		this.setDeclaration(mDeclaration);
	}
	
	private void setLocationCorrdinates(UppaalLocation loc, int x, int y) {

		loc.setCoordinate("x", Integer.toString(x));
		loc.setCoordinate("y", Integer.toString(y));
	}
}