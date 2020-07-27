package edu.collaboration.model.queries;


import mdh.se.dpac.uppawl.structure.UPPAWLMission;
import mdh.se.dpac.uppawl.structure.UPPAWLMissionPlan;
import mdh.se.dpac.uppawl.structure.UPPAWLStaticMap;

public class UPPAWLTaskMatching {
	
	public static void addQueries(UPPAWLMissionPlan plan, UPPAWLUppaalQueries queries, int agentID)
	{
		String sFormula="",sComment="",sMilestones="";
		UPPAWLUppaalFormula formula;
		UPPAWLUppaalComment comment;
		UPPAWLUppaalQuery query;
		
		for(UPPAWLMission m:plan.missions)
		{
			for(int i=0;i<m.milestones.size();i++)
			{
				if(i != m.milestones.size()-1)
				{
					sMilestones += UPPAWLStaticMap.InstanceName + agentID + ".P" + m.milestones.get(i) + "||";
				}
				else
				{
					sMilestones += UPPAWLStaticMap.InstanceName + agentID + ".P" + m.milestones.get(i);
				}
			}
			
			sFormula = "E<> (" + sMilestones + ") imply " + UPPAWLMissionPlan.InstanceName + agentID + ".T" + m.id;
			sComment = "Task Matching for Task" + m.id;
			formula = new UPPAWLUppaalFormula(sFormula);
			comment = new UPPAWLUppaalComment(sComment);
			query = new UPPAWLUppaalQuery();
			query.setFormula(formula);
			query.setComment(comment);
			queries.addQuery(query);
			
			sFormula = "A[] " + UPPAWLMissionPlan.InstanceName + agentID + ".T" + m.id + " imply (" + sMilestones + ")";
			sComment = "Task Matching for Task" + m.id;
			formula = new UPPAWLUppaalFormula(sFormula);
			comment = new UPPAWLUppaalComment(sComment);
			query = new UPPAWLUppaalQuery();
			query.setFormula(formula);
			query.setComment(comment);
			queries.addQuery(query);
			
			sMilestones = "";
		}
	}
}
