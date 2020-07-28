package edu.collaboration.model.queries;

public class UPPAgentTaskIteration extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAgentTaskIteration(int agentID)
	{
		super();
		agent = agentID;
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=MaxIteration-1";
		String sFormula = "E<> iteration[" + agent + "]>=MaxIteration-1";
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Iteration";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
