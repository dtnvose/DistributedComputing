import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

    public class TCPClient {
    	
    	private final static int TEXT = 1;
    	private final static int IMAGE = 2;
    	private final static int VIDEO = 3;
    	private final static int MUSIC = 4;
    	private final static int[] INPUTS = {TEXT, IMAGE, VIDEO, MUSIC};
    	
    	private final static String ROUTER_ADDRESS = "192.168.1.9";
    	private final static String SERVER_ADDRESS = "192.168.1.9";
    	private final static String CLIENT_ADDRESS = "192.168.1.35";
    	private final static int PORT = 5555;	
    	
        public static void main(String[] args) throws IOException {
        	
        	int fileType = inputType();			
         
			if(fileType == TEXT){
	    	
				sendTextFile(); 
			}
			
			else if(fileType == IMAGE){
				
				sendImageFile();
			}
			
			else if(fileType == VIDEO){
				
				sendVideoFile();
			}
			
			else if(fileType == MUSIC){
				sendMusicFile();
			}
        
		
			// closing connections

       }
        
       private static int inputType(){
  		   int fileType = 0;
  		   boolean correctInput = false;
  		   Scanner in = new Scanner(System.in);
  		   while(!correctInput){
  			   System.out.println("Client \n----------------------\n1) Text File \n2) Image File \n3) Video File \n4) Music File");
  		   
  			   if(in.hasNextInt()){
  				  fileType = in.nextInt();
  				  if(fileType > 0 && fileType < INPUTS.length){
  					  correctInput = true;
  				  }
  			   }
  		   }
  		   return fileType;
  	   }	

       private static void sendTextFile() throws IOException{
    	   Scanner sin = new Scanner(System.in);
    	   System.out.print("Name of Text file: ");
    	   String textFile = sin.nextLine();
    	   sin.close();
    	   Reader reader = new FileReader("src/"+textFile); 
    	   BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
       	
    	   // Variables for setting up connection and communication
           Socket Socket = null; // socket to connect with ServerRouter
           DataOutputStream out = null; // for writing to ServerRouter
           DataInputStream in = null; // for reading from ServerRouter
           String routerName = ROUTER_ADDRESS; // ServerRouter host name
           int SockNum = PORT; // port number
  			
          // Tries to connect to the ServerRouter
           try {
              Socket = new Socket(routerName, SockNum);
              out = new DataOutputStream(Socket.getOutputStream());
              in = new DataInputStream(Socket.getInputStream());
           } 
               catch (UnknownHostException e) {
                 System.err.println("Don't know about router: " + routerName);
                 System.exit(1);
              } 
               catch (IOException e) {
                 System.err.println("Couldn't get I/O for the connection to: " + routerName);
                 System.exit(1);
              }
    	   // Variables for message passing	 
    	   String fromServer; // messages received from ServerRouter
           String fromUser; // messages sent to ServerRouter
           String address = SERVER_ADDRESS; // destination IP (Server)
           //long t0, t1, t;
         	
           //ROUTER COMMUNICATION
   		   // Communication process (initial sends/receives
           out.writeUTF(address);// initial send (IP of the destination Server)
           out.flush();
           fromServer = in.readUTF();//initial receive from router (verification of connection)
           System.out.println("ServerRouter: " + fromServer);
           
           //CLIENT TO SERVER
           //out.println(CLIENT_ADDRESS); // Client sends the IP of its machine as initial send
           out.writeUTF(Integer.toString(TEXT)); //Sends the file type to server
           out.flush();
           
           //t0 = System.currentTimeMillis();
    	   
    	   while ((fromServer = in.readUTF()) != null) {
    		   System.out.println("Server: " + fromServer);
				//t1 = System.currentTimeMillis();
				if (fromServer.equals("BYE.")) // exit statement
					break;
				//t = t1 - t0;
				//System.out.println("Cycle time: " + t + "ms");
          
			
            fromUser = fromFile.readLine(); // reading strings from a file
            if (fromUser != null) {
               System.out.println("Client: " + fromUser);
               out.writeUTF(fromUser); // sending the strings to the Server via ServerRouter
               out.flush();
					//t0 = System.currentTimeMillis();
            }
         }
    	   	fromFile.close();
			out.close();
			in.close();
			Socket.close();
       }
       private static void sendImageFile(){}
       private static void sendVideoFile() throws IOException{
    	  
		   Scanner sin = new Scanner(System.in);
			System.out.println("Name of Video file: ");
			 String videoFile = sin.nextLine();
			 sin.close();
			 Path filePath = Paths.get("src/",videoFile);
			byte[] bytes = Files.readAllBytes(filePath);
       	
    	   // Variables for setting up connection and communication
           Socket Socket = null; // socket to connect with ServerRouter
           DataOutputStream out = null; // for writing to ServerRouter
           DataInputStream in = null; // for reading from ServerRouter
           String routerName = ROUTER_ADDRESS; // ServerRouter host name
           int SockNum = PORT; // port number
  			
          // Tries to connect to the ServerRouter
           try {
              Socket = new Socket(routerName, SockNum);
              out = new DataOutputStream(Socket.getOutputStream());
              in = new DataInputStream(Socket.getInputStream());
           } 
               catch (UnknownHostException e) {
                 System.err.println("Don't know about router: " + routerName);
                 System.exit(1);
              } 
               catch (IOException e) {
                 System.err.println("Couldn't get I/O for the connection to: " + routerName);
                 System.exit(1);
              }
    	   // Variables for message passing	 
    	   String fromServer; // messages received from ServerRouter
           String fromUser; // messages sent to ServerRouter
           String address = SERVER_ADDRESS; // destination IP (Server)
           //long t0, t1, t;
         	
           //ROUTER COMMUNICATION
   		   // Communication process (initial sends/receives
           out.writeUTF(address);
           out.flush();// initial send (IP of the destination Server)
           fromServer = in.readUTF();//initial receive from router (verification of connection)
           System.out.println("ServerRouter: " + fromServer);
           
           //CLIENT TO SERVER
           //out.println(CLIENT_ADDRESS); // Client sends the IP of its machine as initial send
           out.writeInt(VIDEO); //Sends the file type to server
           out.flush();
           //t0 = System.currentTimeMillis();
    	   
           int currentPacket = 1;
           int packetIndex = 0;
    	   int packetSize = 100;
    	   byte packet[] = new byte[packetSize];
    	   int count = -1;
    	   boolean done = false;
    	   byte receieved[] = new byte[packetSize];
    	   while ((count = in.read(receieved)) != -1) {
    		   if(done){
    			   Socket.getOutputStream().flush();
    			   packet = "FILE DONE".getBytes();
    			   out.writeUTF("FILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONEFILE DONE");
    			   System.out.println("FILE DONE");
    			   break;
    		   }
    		   fromServer = new String(receieved, "UTF-8");
    		   System.out.println("Server: " + fromServer);
    		   
    		   if(packetIndex+packetSize > bytes.length){
    			 byte[] lastPacket = Arrays.copyOfRange(bytes, packetIndex, bytes.length-1);
    			 System.out.println(new String(lastPacket, "UTF-8"));
    			 System.out.println(Arrays.toString(lastPacket));	
    			 out.write(lastPacket);
    			 done = true;
    			}
    		   else{
    						packet = Arrays.copyOfRange(bytes, packetIndex, currentPacket*packetSize);
    						currentPacket++;
    						packetIndex+=packetSize;
    						
    						System.out.println(Arrays.toString(packet));
    						out.write(packet);
    					
    		   }
         }
			out.close();
			in.close();
			Socket.close();
       }
       private static void sendMusicFile(){}
    }
