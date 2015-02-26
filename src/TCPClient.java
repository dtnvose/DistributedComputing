import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class TCPClient {
    	 	
    	private final static String ROUTER_ADDRESS = "192.168.1.9";
    	private final static String SERVER_ADDRESS = "192.168.1.9";
    	private final static String CLIENT_ADDRESS = "192.168.1.35";
    	private final static int PORT = 5555;	
    	
        public static void main(String[] args) throws IOException { 	
        	Scanner sin = new Scanner(System.in);
 		   	System.out.println("Name of file: ");
 		   	String videoFile = sin.nextLine();
 		   	sin.close();
 		   	Path filePath = Paths.get("src/",videoFile);
 		   	byte[] bytes = Files.readAllBytes(filePath);
        	
 		   	// Variables for setting up connection and communication
            Socket Socket = null; // socket to connect with ServerRouter
            DataOutputStream out = null; // for writing to ServerRouter
            DataInputStream in = null; // for reading from ServerRouter
   			
            // Tries to connect to the ServerRouter
            try {
               Socket = new Socket(ROUTER_ADDRESS, PORT);
               out = new DataOutputStream(Socket.getOutputStream());
               in = new DataInputStream(Socket.getInputStream());
            } 
            catch (UnknownHostException e) {
               System.err.println("Don't know about router: " + ROUTER_ADDRESS);
               System.exit(1);
            } 
            catch (IOException e) {
               System.err.println("Couldn't get I/O for the connection to: " + ROUTER_ADDRESS);
               System.exit(1);
            }
     	   	
            // Variables for message passing	 
     	   	String fromServer; // messages received from ServerRouter

            //ROUTER COMMUNICATION
            out.writeUTF(SERVER_ADDRESS);
            // initial send (IP of the destination Server)
            fromServer = in.readUTF();//initial receive from router (verification of connection)
            System.out.println("ServerRouter: " + fromServer);
            
            //CLIENT TO SERVER
            byte[] complete = new byte[3];//flag that file is finished
            complete = "done".getBytes();
            out.writeUTF(videoFile.substring(videoFile.lastIndexOf(".")+1, videoFile.length())); //Sends the file extension to server
            out.flush();
            out.writeInt(bytes.length);
            System.out.println(bytes.length);
            System.out.println("Server: " + in.readUTF());
     	   	out.write(bytes,0,bytes.length);
     	   	out.flush();
     	   	out.write(complete,0,3);
     	   	out.flush();
     	   	out.close();
     	   	in.close();
     	   	Socket.close();
        }

       }
