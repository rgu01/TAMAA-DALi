package edu.collaboration.model.queries;

public class UPPAWLTaskIteration extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAWLTaskIteration(int agentID)
	{
		super();
		agent = agentID;
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=MaxIteration-1";
		String sFormula = "E<> iteration[" + agent + "]>=MaxIteration-1";
		UPPAWLUppaalFormula formula = new UPPAWLUppaalFormula(sFormula);
		String sComment = "Task Iteration";
		UPPAWLUppaalComment comment = new UPPAWLUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
