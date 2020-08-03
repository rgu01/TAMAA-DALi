package edu.collaboration.model.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Path;

public class MapTxtGenerator {
    private String mapTxt = "res/map";
    //private int numOfNodes;
    private List<List<Node>> milestones;
    private List<List<Path>> paths;
    
    public MapTxtGenerator(List<List<Node>> milestones, List<List<Path>> paths) {
    	this.paths = paths;
        this.milestones = milestones;
    }

    public void createSampleMap() {
    	int numOfNodes = 0;
        int[][] map = null;
        Node origin = null, target = null;
        Path thePath = null;
        List<Node> nodes = null;
        List<Path> paths = null;

    	for(int pi = 0; pi < this.paths.size(); pi++)
    	{
    		nodes = this.milestones.get(pi);
    		paths = this.paths.get(pi);
        	numOfNodes = nodes.size();
        	map = new int[numOfNodes][numOfNodes];
        	
        	for (int i = 0; i < numOfNodes; i++) 
        	{
            	origin = nodes.get(i);
                for (int j = 0; j < numOfNodes; j++) 
                {
                    if (i < j) 
                    {
                    	target = nodes.get(j);
                    	for(Path p:paths)
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
                    } 
                    else if (i > j) 
                    {
                        map[i][j] = map[j][i];
                    } 
                    else 
                    {
                        map[i][j] = 0;
                    }
                }
            }
            outputMapTxt(map, pi);
        }
    }

    public void outputMapTxt(int[][] map, int index)
    {
    	int numOfNodes = map.length;
        try 
        {
            File writename = new File(mapTxt + index + ".txt");
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

