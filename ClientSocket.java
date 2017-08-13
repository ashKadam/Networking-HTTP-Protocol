
package clientsocket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {

    public static void main(String[] args) {
        
        String hostName = args[0];
        int portNum = new Integer(args[1]); 
        String command = args[2];
        String fileName = args[3]; 
        startSender client = new startSender(
                hostName, portNum, command, fileName);
        client.run();
    }
     public static class startSender{
        
        public String hostName;
        public int portNum;
        public String command;
        public String fileName;
        
        startSender(String hName, int pNum, String cmd, String fName)
        {
            hostName = hName;
            portNum = pNum;
            command = cmd;
            fileName = fName;
        }
        
        public void run()
        {
            Socket socket= null;
            OutputStream os = null;
                PrintWriter out = null;
                BufferedReader in = null;
                
            try  {
                socket = new Socket(hostName, portNum);
                os = socket.getOutputStream();
                out = new PrintWriter(os);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                
                if(command.equalsIgnoreCase("GET")){
                    out.write("GET /" + fileName + " HTTP/1.1\r\n");
                    out.println("");
                    out.flush();
                    socket.shutdownOutput();
                    
                    try{
                        BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                        String line = br.readLine();
                        if(line.contains("404"))
                            System.out.println("File not found");
                        else{
                            System.out.println("File Received!");
                            while((line = br.readLine()) != null)
                                System.out.println(line);
                        }
                        
                    }
                    catch(Exception e){}
                }
                else if(command.equalsIgnoreCase("PUT")){
                    out.print("PUT /" + fileName + " HTTP/1.1\r\n");
                    out.println("");
                    out.flush();
                    
                    File myFile = new File (fileName);
                    if(!myFile.exists()){
                        System.out.println("File to be transferred is not found");
                    }
                    else{
                        System.out.println("Transferring File " + myFile);
                        byte[] mybytearray = new byte [(int)myFile.length()];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        fis.read(mybytearray,0,mybytearray.length);
                        os = socket.getOutputStream();
                        os.write(mybytearray);
                        out.flush();
                        
                        fis.close();
                        socket.shutdownOutput();
                    }
                    
                    String line = in.readLine();
                    if(line != null && line.contains("200")){
                        System.out.println(line);
                        System.out.println("File stored at Server location");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally { 
            try { 
               in.close(); 
               out.close(); 
               socket.close(); 
               System.out.println("Client Socket Closed"); 
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
         }
        }
    }
}
