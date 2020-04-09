package edu.collaboration.tamaa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UppaalMapGenerator {
    private String mapTxt = "res/map.txt";
    private int scale;

    public UppaalMapGenerator(int scale) {
        this.scale = scale;
    }

    public void creteSampleMap() {
        int[][] map = new int[scale][scale];
        int ran = 0, i = 0, j = 0;

        for (i = 0; i < scale; i++) {
            for (j = 0; j < scale; j++) {
                if (i < j) {
                    ran = (int) (10 * Math.random());
                    //20% - disconnected
                    if (ran >= 8) {
                        map[i][j] = -1;
                    } else {
                        map[i][j] = ran;
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

        public void outputMapTxt(int[][] map){
            try {
                File writename = new File(mapTxt);
                writename.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(writename));
                out.write(scale+"\r\n");
                for(int i = 0; i < scale; i++)
                {
                    for( int j = 0; j < scale; j++)
                    {
                        out.write(map[i][j]+"	");
                    }
                    out.write("\r\n");
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

