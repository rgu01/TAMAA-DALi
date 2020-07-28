package edu.collaboration.communication;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.io.File;

public class TransferFile extends Socket {
    private Socket client;
    private FileInputStream fis;
    private DataOutputStream dos;
	//public static final String SERVER_IP = "192.168.0.109"; 
    //public static final int SERVER_PORT = 9779; 
 

    public TransferFile(String ip, int port) throws Exception {
        super(ip, port);
        this.client = this;
        System.out.println("Cliect[port:" + client.getLocalPort() + "] connect to the Server.");
    }
 

    public void sendFile(String path) throws Exception {
        try {
            File file = new File(path);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());
 
                // File name and length
                //dos.writeUTF(file.getName());
                //dos.flush();
                //dos.writeLong(file.length());
                //dos.flush();
 
                // start to transfer
                System.out.println("======== Start to send files ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100*progress/file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== Files tranferred successfully ========");
            }
            else
            {
                System.out.println("File does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null)
                fis.close();
            if(dos != null)
                dos.close();
            client.close();
        }
    }

}
