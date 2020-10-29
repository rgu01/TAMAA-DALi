package edu.collaboration.tamaa;

import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import com.afarcloud.thrift.*;

import edu.collaboration.taskscheduling.TaskScheduleParser;
import edu.collaboration.taskscheduling.TaskSchedulePlan;

//try

public class UPlanner {
	private String configPath = "res/config.txt";
	private String mmtAddress;
	private int mmtPort;
	private int tamaaPort;

	public void StartServer(PlannerService.Processor<PlannerServiceHandler> processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(this.tamaaPort);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new
			// TThreadPoolServer.Args(serverTransport).processor(processor));

			// System.out.println(UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath());
			// JOptionPane.showMessageDialog(null,
			// UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath(),
			// "DEBUG", JOptionPane.PLAIN_MESSAGE);
			// InputStream is = new InputStream("resources/empty_template.xml");
			System.out.println("Starting the simple server...");
			server.serve();
			System.out.println("Server started...");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void logtoText() {
		String dirName = "./results/";
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String fileName = "Log " + format.format(date) + ".txt";
		File file = new File(dirName + fileName);

		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readConfig() {
		try {
			File filename = new File(this.configPath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			Scanner sc = new Scanner(reader);
			if (sc != null && sc.hasNextLine()) {
				this.mmtAddress = sc.nextLine().replace("MMT Address: ", "");
				this.mmtPort = Integer.parseInt(sc.nextLine().replace("MMT Port: ", ""));
				this.tamaaPort = Integer.parseInt(sc.nextLine().replace("Planner port: ", ""));
				sc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		UPlanner planner = new UPlanner();
		planner.readConfig();
		planner.logtoText();
		System.out.println("============" + new Date().toString() + "============");
		planner.StartServer(new PlannerService.Processor<PlannerServiceHandler>(
				new PlannerServiceHandler(planner.mmtAddress, planner.mmtPort)));
	}
}
