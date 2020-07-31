package edu.collaboration.model.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.afarcloud.thrift.Vehicle;

public class AgentTxtGenerator {
    private String agentTxt = "res/agents.txt";
    private List<Vehicle> agents;

    public AgentTxtGenerator(List<Vehicle> listOfAgents)
    {
    	this.agents = listOfAgents;
    }
    
    public void outputAgentTxt()
    {
    	int numOfAgents = this.agents.size();
    	Vehicle v = null;
    	
        try 
        {
            File writename = new File(agentTxt);
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write("id	speed\r\n");
            for(int i = 0; i < numOfAgents; i++)
            {
            	v = this.agents.get(i);
                out.write(i + "	" + (int)v.maxSpeed + "\r\n");
            }
            out.flush();
            out.close();
         } 
         catch (IOException e) 
         {
            e.printStackTrace();
         }
    }
}
