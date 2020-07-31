package edu.collaboration.model.generation;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import edu.collaboration.model.queries.*;
import edu.collaboration.model.structure.*;


public class UPPAgentGenerator {
	public static String outputXML = "./model/local.xml";
	private static String templateXML = "empty_template.xml"; //under the bin folder
	private static String gResetString = "int steps = 0;\r\nvoid gReset()\r\n" +
    		"{\r\n" +
    		"   int i,j,k;\r\n" +
    		"   for(i=0;i<AgentNum;i++)\r\n" +
    		"   {\r\n" +
    		"       tf[i][0]=true;\r\n" +
    		"       ts[i][0]=true;\r\n" +
    		"       for(j=1;j<TaskNum;j++)\r\n" +
    		"        {\r\n" +
    		"           ts[i][j]=false;\r\n" +
    		"           tf[i][j]=false;\r\n" +
    		"        }\r\n" +
    		"       for(k=0;k<MilestoneNum;k++)\r\n" +
    		"       {\r\n" +
    		"           position[i][k]=false;\r\n" +
    		"           visited[i][k]=false;\r\n" +
    		"       }\r\n" +
    		"       iteration[i]=0;\r\n" +
    		"    }\r\n" +
    		"    steps=0;\r\n" +
    		"    globalTime=0;\r\n" +
    		"} \r\n";
	private static String updateIterationString="\r\n";
	
	public static void run() {
	    String show = "";
	    String tempatePath = "";
		UppaalDocument doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    try {
	    	tempatePath = new File(".").getCanonicalPath() + "\\res\\" + templateXML;
			DocumentBuilder builder = factory.newDocumentBuilder();
			//InputStream is = UPPAgentGenerator.class.getClassLoader().getResourceAsStream(tempatePath); 
			InputStream is = new FileInputStream(new File(tempatePath));
			if(is != null)
			{
				Document templatedoc =  builder.parse(is);
				templatedoc.getDocumentElement().normalize();

				Element root = templatedoc.getDocumentElement();
				doc = new UppaalDocument(root);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Template is missing.", "Error", JOptionPane.PLAIN_MESSAGE);
			}
		} catch (ParserConfigurationException ex) {

	    	show = ex.getMessage();
	    	JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		} catch (SAXException ex) {

	    	show = ex.getMessage();
	    	JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		} catch (IOException ex) {

	    	show = ex.getMessage();
	    	JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		}
	    
	    int mapScale = 0, taskNum = 0, eventNum = 0, agentNum = 0;
	    UPPAgentFleet fleet = new UPPAgentFleet();
	    UPPAgentMission m = null;
    	UPPAgentUppaalQueries queries = new UPPAgentUppaalQueries();
	    String system_declaration = "";
	    String global_declaration = "// Place global declarations here.\r\n";
	    String constBcet = "const int BCET[AgentNum][TaskNum]={";
	    String constWcet = "const int WCET[AgentNum][TaskNum]={";
	    String constRegularTask = "const int RegularTaskNum[AgentNum] = {";
    	String iterationString = "int iteration[AgentNum] = {";
    	String channelString = "chan move[AgentNum], initialize;\r\n";
	    
	    agentNum = fleet.agents.size();
	    UPPAgentEventMonitor monitor=null;
	    
	    //for(int agentID:fleet.agents)
	    for(UPPAgentVehicle agent:fleet.agents)
	    {
	    	monitor = new UPPAgentEventMonitor(agent,0);
	    	UPPAgentMissionPlan missionPlan = new UPPAgentMissionPlan(agent, monitor,taskNum);
		    if(taskNum < missionPlan.missions.size())
		    {
		    	taskNum = missionPlan.missions.size();
		    }
		    if(eventNum < monitor.events.size())
		    {
		    	eventNum = monitor.events.size();
		    }
	    }
	    
	    UPPAgentTaskCoverage tcq = new UPPAgentTaskCoverage(fleet);
	    queries.addQuery(tcq);  
	    
	    //for(int agentID:fleet.agents)
	    for(UPPAgentVehicle agent:fleet.agents)
	    {
	    	UPPAgentStaticMap map = new UPPAgentStaticMap(agent);
	    	//UPPAgentTaskCoverage tcq = new UPPAgentTaskCoverage(agentID);
	    	monitor = new UPPAgentEventMonitor(agent, eventNum);
	    	UPPAgentMissionPlan missionPlan = new UPPAgentMissionPlan(agent, monitor,taskNum);
	    
		    //queries.addQuery(tcq);  
		    
		    monitor.setDeclaration();
		    mapScale = map.Scale;
		    
		    for(int i = 0; i < taskNum; i++)
		    {
		    	if(i<missionPlan.missions.size())
		    	{
		    		m = missionPlan.missions.get(i);
		    	}
	    		if(i == 0)
	    		{
		    		constBcet += "{0,";
		    		constWcet += "{0,";
	    		}
		    	if(i<missionPlan.missions.size())
		    	{
		    		constBcet += m.bcet;
		    		constWcet += m.wcet;
		    	}
		    	else
		    	{
		    		constBcet += 0;
		    		constWcet += 0;
		    	}
	    		if(i != taskNum-1)
	    		{
		    		constBcet += ",";
		    		constWcet += ",";
	    		}
	    		else
	    		{
		    		constBcet += "}";
		    		constWcet += "}";
	    		}
		    }
		    
		    constRegularTask += missionPlan.regularTasksNum;
		    iterationString += 0;
		    if(agent.id != fleet.agents.size() - 1)
		    {
		    	constRegularTask += ",";
		    	iterationString += ",";
	    		constBcet += ",";
	    		constWcet += ",";
		    	
		    }
		    else
		    {
		    	constRegularTask += "};\r\n";
		    	iterationString += "};\r\n";
	    		constBcet += "};\r\n";
	    		constWcet += "};\r\n";
		    }
		    
		    for(UPPAgentEvent e:monitor.events)
		    {
		    	system_declaration += UPPAgentEventMonitor.InstanceName + agent.id + e.id + " = " + UPPAgentEventMonitor.SystemName + "(" + agent.id + "," + e.id + ");\r\n";
		    }
		    system_declaration += UPPAgentStaticMap.InstanceName + agent.id + " = " + UPPAgentStaticMap.SystemName + agent.id + "(" + agent.id + ");\r\n";
		    system_declaration += UPPAgentMissionPlan.InstanceName + agent.id + " = " + UPPAgentMissionPlan.SystemName + agent.id + "(" + agent.id + ");\r\n";
		    
		    doc.addAutomaton(map);
		    doc.addAutomaton(missionPlan);
	    }
	    
	    
	    
	    if(monitor!=null)
	    {
	    	monitor.initialize();
	    	if(eventNum != 0)
	    	{
	    		doc.addAutomaton(monitor);
	    		channelString += "chan done[AgentNum][EventNum];\r\n";
	    	}
	    }
	    system_declaration += "\r\nsystem ";
	    for(UPPAgentVehicle agent:fleet.agents)
	    {
	    	monitor = new UPPAgentEventMonitor(agent,eventNum);
		    system_declaration += UPPAgentStaticMap.InstanceName + agent.id + ", " + UPPAgentMissionPlan.InstanceName + agent.id + ", ";	
		    
		    for(UPPAgentEvent e:monitor.events)
		    {
		    	system_declaration += UPPAgentEventMonitor.InstanceName + agent.id + e.id + ", ";
		    }
	    }
	    system_declaration = system_declaration.substring(0, system_declaration.lastIndexOf(",")) + ";";
	    try
	    {
	    	gResetString = "";
	    	global_declaration += addDeclaration(mapScale, taskNum, eventNum, agentNum) + 
	    		constBcet + constWcet + constRegularTask + channelString + 
	    		"\r\n" +
	    		iterationString + 
	    		"\r\n" + 
	    		"clock globalTime;\r\n" +
	    		gResetString + updateIterationString;
	    }
	    catch(Exception ex)
	    {
	    	show = ex.getMessage();
	    	JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
	    }
	    doc.setDeclaration(global_declaration);
	    doc.setSystem(system_declaration);    
		doc.addOrReplaceChildElement(queries);    
	    doc.saveToFile(outputXML);
	
	    show = "Model for " + fleet.agents.size() + " agents has built! MapScale: " + 
	    	    mapScale + ", taskNum: " + taskNum + ", eventNum: " + eventNum + ".";
	    
	    JOptionPane.showMessageDialog(null, show, "Done", JOptionPane.PLAIN_MESSAGE);
	}
	
	private static String addDeclaration(int mapScale, int taskNum, int eventNum, int agentNum) throws Exception {

	    String constAgentsNum = "const int AgentNum = "+ agentNum + 
	    		";\r\ntypedef int[0,AgentNum-1] AgentScale;\r\n";
	    String constMilestone = "const int MilestoneNum = " + mapScale + ";\r\n";
	    String constNum = "const int TaskNum = " + (taskNum+1) + ";\r\n" +
	    "const int EventNum = " + eventNum + ";\r\n";
	    String constMax = "const int MaxIteration = 2;";
	    
	    //String finish = "",start = "",visit = "";
	    String events = "",position = "",declaration = "";
	    
	    if(agentNum != 0)
	    {
	    	if(taskNum != 0)
	    	{
	    	    //finish = "bool tf[AgentNum][TaskNum]={";
	    	    //start = "bool ts[AgentNum][TaskNum]={";
	    	}
	    	else
	    	{
	    		throw new Exception("Agents number must be larger than 0!");
	    	}
	    	if(eventNum != 0)
	    	{
	    		events = "bool ev[AgentNum][EventNum]={";
	    	}
	    	if(mapScale != 0)
	    	{
	    	    position = "bool position[AgentNum][MilestoneNum]={";
	    	    //visit = "bool visited[AgentNum][MilestoneNum]={";
	    	}
	    	else
	    	{
	    		throw new Exception("Map size must be larger than 0!");
	    	}
	    }
	    else
	    {
	    	throw new Exception("At least one agent is required!");
	    }
	    

	    for(int i = 0; i < agentNum; i++)
	    {
		    for(int m = 0; m < mapScale; m++)
		    {
		    	if(m == 0)
		    	{
	    			position += "{";
	    			//visit += "{";
		    	}
    			position += "false";
    			//visit += "false";
	    		if(m != mapScale-1)
	    		{
	    			position += ",";
	    			//visit += ",";
	    		}
	    		else
	    		{
	    			position += "}";
	    			//visit += "}";
	    		}
		    }
	    	for(int j = 0; j < taskNum; j++)
	    	{
	    		if(j == 0)
	    		{
	    			//finish += "{true,";
	    			//start += "{true,";
	    		}
    			//finish += "false";
    			//start += "false";
    			if(j != taskNum-1)
	    		{
	    			//finish += ",";
	    			//start += ",";
	    		}
	    		else
	    		{
	    			//finish += "}";
	    			//start += "}";
	    		}
	    	}
	    	for(int k = 0; k < eventNum; k++)
	    	{
	    		if(k == 0)
	    		{
	    			events += "{";
	    		}
	    		events += "false";
	    		if(k != eventNum-1)
	    		{
	    			events += ",";
	    		}
	    		else
	    		{
	    			events += "}";
	    		}
	    	}
	    	if(i == agentNum-1)
	    	{
	    		position += "};\r\n";
	    		//visit += "};\r\n";
	    		//finish += "};\r\n";
	    		//start += "};\r\n";
	    		if(events != "")
	    		{
	    			events += "};\r\n";
	    		}
	    	}
	    	else
	    	{
	    		position += ",";
	    		//visit += ",";
	    		//finish += ",";
	    		//start += ",";
	    		if(events != "")
	    		{
	    			events += ",";
	    		}
	    	}
	    }
	    
	    declaration = constAgentsNum + constNum + constMilestone + constMax +
	    		"\r\n" + 
	    		"\r\n" + 
	    		events + position + 
	    		//visit + 
	    		//finish + start+
	    		"\r\n";
	    
		return declaration;
	}
}
