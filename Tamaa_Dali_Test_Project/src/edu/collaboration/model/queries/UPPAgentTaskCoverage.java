package edu.collaboration.model.queries;

public class UPPAgentTaskCoverage extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAgentTaskCoverage(int agentID)
	{
		super();
		agent = agentID;
		//String sFormula = "E<> forall(i:int[0," + plan.regularTasksNum + "]) TF[i]";
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=1";
		String sFormula = "E<>  iteration[" + agent + "]>=1";
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
