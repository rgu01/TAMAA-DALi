package edu.collaboration.model.queries;

import mdh.se.dpac.uppawl.structure.UPPAWLStaticMap;

public class UPPAWLMilestonesCoverage extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAWLMilestonesCoverage(int agentID, int scale)
	{
		super();
		agent = agentID;
		String sFormula = "E<> forall(i:int[0," + (scale-1) + "]) visited[" + agent + "][i]";
		UPPAWLUppaalFormula formula = new UPPAWLUppaalFormula(sFormula);
		String sComment = "Milestone Coverage";
		UPPAWLUppaalComment comment = new UPPAWLUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}

}
