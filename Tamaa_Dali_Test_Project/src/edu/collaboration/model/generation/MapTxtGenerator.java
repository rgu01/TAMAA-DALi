package edu.collaboration.model.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Path;

public class MapTxtGenerator {
    private String mapTxt = "res/map.txt";
    //private int numOfNodes;
    private List<Node> milestones;
    private List<Path> paths;
    
    public MapTxtGenerator(List<Node> milestones, List<Path> paths) {
    	this.paths = paths;
        this.milestones = milestones;
    }

    public void createSampleMap() {
    	int numOfNodes = this.milestones.size();
        int[][] map = new int[numOfNodes][numOfNodes];
        int i = 0, j = 0;
        Node origin = null, target = null;
        Path thePath = null;

        for (i = 0; i < numOfNodes; i++) {
        	origin = this.milestones.get(i);
            for (j = 0; j < numOfNodes; j++) {
                if (i < j) {
                	target = this.milestones.get(j);
                	for(Path p:this.paths)
                	{
                		if(p.isThePath(origin, target))
                		{
                			thePath = p;
                			break;
                		}
                	}
                	if(thePath != null)
                	{
                		map[i][j] = (int)thePath.length();
                	}
                	else
                	{
                		map[i][j] = -1;
                	}
                } else if (i > j) {
                    map[i][j] = map[j][i];
                } else {
                    map[i][j] = 0;
                }
            }
        }
        outputMapTxt(map);
    }

    public void outputMapTxt(int[][] map)
    {
    	int numOfNodes = this.milestones.size();
        try 
        {
            File writename = new File(mapTxt);
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(numOfNodes+"\r\n");
            for(int i = 0; i < numOfNodes; i++)
            {
                for( int j = 0; j < numOfNodes; j++)
                {
                    out.write(map[i][j]+"	");
                }
                out.write("\r\n");
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

