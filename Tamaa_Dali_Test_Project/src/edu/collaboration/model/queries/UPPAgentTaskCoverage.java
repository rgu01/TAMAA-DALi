package edu.collaboration.model.queries;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.model.structure.UPPAgentFleet;

public class UPPAgentTaskCoverage extends UPPAgentUppaalQuery {
	private int agent;
	private List<Integer> listAgentID;
	
	public UPPAgentTaskCoverage(int agentID)
	{
		super();
		this.agent = agentID;
		this.listAgentID = new ArrayList<Integer>();
		this.listAgentID.add(agent);
		//String sFormula = "E<> forall(i:int[0," + plan.regularTasksNum + "]) TF[i]";
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=1";
		String sFormula = "E<>  iteration[" + agent + "]>=1";
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
	
	public UPPAgentTaskCoverage(UPPAgentFleet fleet)
	{
		super();
		this.agent = -1;
		this.listAgentID = fleet.agents;
		String sFormula = "E<>  iteration[" + this.listAgentID.get(0) + "]>=1";
		for(int i = 1; i < this.listAgentID.size(); i++)
		{
			sFormula += "&& iteration[" + this.listAgentID.get(i) + "]>=1";
		}
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
