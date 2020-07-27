package edu.collaboration.model.queries;

public class UPPAWLTaskCoverage extends UPPAWLUppaalQuery {
	private int agent;
	
	public UPPAWLTaskCoverage(int agentID)
	{
		super();
		agent = agentID;
		//String sFormula = "E<> forall(i:int[0," + plan.regularTasksNum + "]) TF[i]";
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=1";
		String sFormula = "E<>  iteration[" + agent + "]>=1";
		UPPAWLUppaalFormula formula = new UPPAWLUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAWLUppaalComment comment = new UPPAWLUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
