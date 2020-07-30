package edu.collaboration.tamaa;

import org.apache.thrift.transport.TServerSocket;  
import org.apache.thrift.transport.TServerTransport;
import javax.swing.JOptionPane;
import org.apache.thrift.server.TServer;  
import org.apache.thrift.server.TServer.Args;  
import org.apache.thrift.server.TSimpleServer; 
import com.afarcloud.thrift.*;

import edu.collaboration.taskscheduling.TaskScheduleParser;
import edu.collaboration.taskscheduling.TaskSchedulePlan;

//try

public class UPlanner {
	public static void StartServer(PlannerService.Processor<PlannerServiceHandler> processor) {
		  try {  
			  TServerTransport serverTransport = new TServerSocket(9097);
			  TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
		  
			  // Use this for a multithreaded server  
			  // TServer server = new TThreadPoolServer(new  
			  // TThreadPoolServer.Args(serverTransport).processor(processor));  

			  //System.out.println(UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath());
			  //JOptionPane.showMessageDialog(null, UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath(), "DEBUG", JOptionPane.PLAIN_MESSAGE);
			  //InputStream is = new InputStream("resources/empty_template.xml"); 
			  System.out.println("Starting the simple server...");  
			  server.serve();
			  System.out.println("Server started...");
		  } catch (Exception e) {
		  	System.out.println(e.getMessage());
			  e.printStackTrace();  
			  JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE);
		  }  
	}  
		   
	public static void main(String[] args) {  
		StartServer(new PlannerService.Processor<PlannerServiceHandler>(new PlannerServiceHandler()));  
	}  
}
