package edu.collaboration.model.structure;

import java.util.ArrayList;
import java.util.List;

public class UPPAgentMission {
	public int id;
	public int bcet;
	public int wcet;
	public boolean regularTask;
	public List<Integer> milestones;
	private List<Integer> events_trigger; //The event triggering this task
	//private List<Integer> events_reset;
	private String precondition;
	private UPPAgentEventMonitor monitor;
	
	public UPPAgentMission(int id, UPPAgentEventMonitor ev)
	{
		this.id = id;
		bcet = 0;
		wcet = 0;
		regularTask = true;
		milestones = new ArrayList<Integer>();
		events_trigger = new ArrayList<Integer>();
		//events_reset = new ArrayList<Integer>();
		precondition = "";
		monitor = ev;
	}
	
	public void setPrecondition(String value)
	{
		boolean tag = false;
		if(!value.equals("-"))
		{
			precondition = value.replaceAll("&&", "&amp;&amp;");
			if(regularTask)
			{
				for(UPPAgentEvent e:monitor.events)
				{
					precondition += "&amp;&amp;!ev[id][" + e.id + "]";
				}
			}
			else
			{
				for(UPPAgentEvent e:monitor.events)
				{
					for(int t:events_trigger)
					{
						if(t==e.id)
						{
							precondition += "&amp;&amp;ev[id][" + e.id + "]";
							tag = true;
							break;
						}
					}
					if(!tag)
					{
						precondition += "&amp;&amp;!ev[id][" + e.id + "]";
					}
					tag = false;
				}
			}
		}
		else
		{
			if(!regularTask)
			{
				precondition = "";
				for(int t:events_trigger)
				{
					precondition += "ev[id][" + t + "]";
				}
			}
			else
			{
				for(UPPAgentEvent e:monitor.events)
				{
					precondition += "!ev[id][" + e.id + "]";
				}
			}
		}
	}
	
	public String getPrecondition()
	{
		return this.precondition;
	}
	
	public void addEventsTrigger(int event)
	{
		if(event != -1)
		{
			regularTask = false;
			events_trigger.add(event);
		}
	}
	
	public List<Integer> getEventsTrigger()
	{
		return events_trigger;
	}
	
	//public void addEventsToReset(int event)
	//{
	//	if(event != -1)
	//	{
	//		events_reset.add(event);
	//	}
	//}
	
	//public List<Integer> getEventsToReset()
	//{
	//	return events_reset;
	//}
}
