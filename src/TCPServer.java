import java.io.*;
import java.net.*;
public class TCPServer {
	
	private static int TEXT = 1;
	private static int IMAGE = 2;
	private static int VIDEO = 3;
	private static int MUSIC = 4;
	
	private static String ROUTER_ADDRESS = "192.168.1.35";
	private static String SERVER_ADDRESS = "192.168.1.35";
	private static String CLIENT_ADDRESS = "192.168.1.20";
	private static int PORT = 5555;
	
	public static void main(String[] args) throws IOException {
      	
		// Variables for setting up connection and communication
         Socket Socket = null; // socket to connect with ServerRouter
         PrintWriter out = null; // for writing to ServerRouter
         BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostAddress(); // Server machine's IP			
			String routerName = ROUTER_ADDRESS; // ServerRouter host name
			int SockNum = 5555; // port number
			
			// Tries to connect to the ServerRouter
         try {
            Socket = new Socket(routerName, SockNum);
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
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
        String fromServer; // messages sent to ServerRouter
        String fromClient; // messages received from ServerRouter      
 		String address = CLIENT_ADDRESS; // destination IP (Client)
		
 		//ROUTER COMMUNICATION
		// Communication process (initial sends/receives)
		out.println(address);// initial send (IP of the destination Client)
		fromClient = in.readLine();// initial receive from router (verification of connection)
		System.out.println("ServerRouter: " + fromClient);
		
		//SERVER TO CLIENT COMMUNICATION
		//retrieves file type
		fromClient = in.readLine();
		int fileType = Integer.parseInt(fromClient);

		if(fileType == TEXT){
			fromServer = "Retrieving Text File";
			System.out.println(fromServer + "\n---------------------");
			out.println(fromServer);
			// Communication while loop
			while ((fromClient = in.readLine()) != null){		
				System.out.println("Client said: " + fromClient);
					fromServer = fromClient.toUpperCase(); // converting received message to upper case
					System.out.println("Server said: " + fromServer);
					out.println(fromServer); // sending the converted message back to the Client via ServerRouter
					
					if (fromClient.equals("bye.")) // exit statement
						break;
			}
		}
			
			// closing connections
         out.close();
         in.close();
         Socket.close();
      }
   }
