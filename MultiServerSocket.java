/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiserversocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MultiServerSocket implements Runnable{

    protected int serverPort;
    protected ServerSocket serverSocket = null;
    boolean isServerRunning = true;
    protected Thread currentThread= null;

    public MultiServerSocket(int port) throws IOException{
        this.serverPort = port;
        Socket clientSocket = null;
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
        while(isServerRunning){
            System.out.println("Waiting for client connections");
            try {
                clientSocket = this.serverSocket.accept();
                new Thread(new WorkerRunnable(clientSocket)).start();
                System.out.println("Connected to client");
                String s1 = br1.readLine();
                if(s1.equalsIgnoreCase("quit")){
                    isServerRunning = false;
                    clientSocket.close();
                }
                
            } catch (IOException e) {
                if(!isServerRunning)
                    System.out.println("Server Stopped...") ;
                else
                    System.out.println("Error found!");
                return;
            }
        }
        try { 
         serverSocket.close(); 
         System.out.println("Server Stopped"); 
      } catch(Exception ioe) { 
         System.out.println("Error Found stopping server socket"); 
         System.exit(-1); 
      }
    }

    public void run(){
        synchronized(this){
            this.currentThread = Thread.currentThread();
        }
    }

    public synchronized void stopServer(){
        this.isServerRunning = false;
        try {
            this.serverSocket.close();
            System.out.println("Server stopping!");
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
    public static void main(String[] args) throws IOException {
        int portNum = new Integer(args[0]);
        System.out.println("Port Number : " + portNum);
        MultiServerSocket server = new MultiServerSocket(portNum);
    }

class WorkerRunnable implements Runnable{
    private Socket clientSocket = null;
    
    public WorkerRunnable(Socket cltSocket) {
        this.clientSocket = cltSocket;
    }

    public void run() {
        InputStream is  = null; //clientSocket.getInputStream();
        OutputStream os = null;//clientSocket.getOutputStream();
        BufferedReader in = null;//new BufferedReader(new InputStreamReader(is));
        PrintWriter out = null;
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        try {
            is  = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(is));
            out = new PrintWriter(os,true);
            
            String line = in.readLine();
            String tokens[] = line.split(" ");
            String fname = tokens[1].replace("/", "");

            if(line.contains("GET")){
                File f = new File(fname);
                if (f.exists()) {
                    out.print("HTTP/1.1 200 OK\r\n");
                    out.print("\r\n");
                    out.flush();

                    byte[] mybytearray = new byte [(int)f.length()];
                    InputStream fis = new FileInputStream(f);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);

                    os = clientSocket.getOutputStream();
                    os.write(mybytearray);
                }
                else{
                    out.print("HTTP/1.1 404 NOT FOUND\r\n");
                    out.print("\r\n");
                }
                out.flush();
            }
            else if(line.contains("PUT"))
            {
                byte[] mybytearray  = new byte[100];
                FileOutputStream fos = new FileOutputStream(fname);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead;

                is = clientSocket.getInputStream();
                
                while ((bytesRead = is.read(mybytearray)) >= 0)
                {
                    System.out.println("Writing file");
                    bos.write(mybytearray, 0, bytesRead);
                }
                bos.flush();
                bos.close();
                
                if(bytesRead != 0 ){
                    out.print("HTTP/1.1 200 OK File Created\r\n");
                    out.print("\r\n");
                    out.flush();
                }
            }
            clientSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally { 
            try { 
               in.close(); 
               out.close(); 
               //clientSocket.close(); 
               //System.out.println("...Stopped"); 
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
         }
    }
}
}