package edu.collaboration.model.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UPPAgentFleet {
	private static String filePath = "res/agents.txt"; 
	public List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
	//public List<Integer> agents = new ArrayList<Integer>();
	//public List<Integer> speed = new ArrayList<Integer>();
	
	public UPPAgentFleet()
	{
		try 
		{ 
			File filename = new File(filePath); 
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); 
			Scanner sc = new Scanner(reader);
			int id, speed;
			if(sc != null && sc.hasNextLine())
			{
				sc.nextLine();
				while(sc.hasNextInt())
				{
					//id
					id = sc.nextInt();
					//agents.add(sc.nextInt());
					//speed
					speed = sc.nextInt();
					//speed.add(sc.nextInt());
					agents.add(new UPPAgentVehicle(id, speed));
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
