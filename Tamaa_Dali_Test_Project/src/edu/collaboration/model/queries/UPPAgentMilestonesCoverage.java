package edu.collaboration.model.queries;

import mdh.se.dpac.uppawl.structure.UPPAWLStaticMap;

public class UPPAgentMilestonesCoverage extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAgentMilestonesCoverage(int agentID, int scale)
	{
		super();
		agent = agentID;
		String sFormula = "E<> forall(i:int[0," + (scale-1) + "]) visited[" + agent + "][i]";
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Milestone Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}

}
