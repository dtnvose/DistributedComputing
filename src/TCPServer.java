import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
public class TCPServer {
	
	private final static int END_OF_FILE = 12345;
	private final static String ROUTER_ADDRESS = "192.168.1.9";
	private final static String SERVER_ADDRESS = "192.168.1.9";
	private final static String CLIENT_ADDRESS = "192.168.1.35";
	private final static int PORT = 5555;
	
	public static void main(String[] args) throws IOException {
      	
		// Variables for setting up connection and communication
         Socket Socket = null; // socket to connect with ServerRouter
         DataOutputStream out = null; // for writing to ServerRouter
         DataInputStream in = null; // for reading form ServerRouter		
			
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
         
        String fromClient; //messages received from ServerRouter
        String extension; //extension of the file being transfered
		
 		//ROUTER COMMUNICATION
		// Communication process (initial sends/receives)
		out.writeUTF(CLIENT_ADDRESS);// initial send (IP of the destination Client)
		fromClient = in.readUTF();// initial receive from router (verification of connection)
		System.out.println("ServerRouter: " + fromClient);
		
		//SERVER TO CLIENT COMMUNICATION		
		extension = in.readUTF();
		int fileSize = 0;
		Date date = new Date();
		FileOutputStream fos = new FileOutputStream("TEST_FILE" + date.getTime() + "." + extension);
		fileSize = in.readInt();
		System.out.println("File Size: " + fileSize);
			
		out.writeUTF("Ready for file transfer");
		byte[] buffer = new byte[fileSize];
		int n = 0;
		int total = 0;
		while((n = in.read(buffer)) != -1 && n!= 3){
			total+=n;
			System.out.println(total + " = " + fileSize);
			fos.write(buffer,0,n);
			fos.flush();
			if(total >= fileSize)
				break;
		}
		byte[] complete = new byte[3];
		complete = "done".getBytes();
		out.write(complete,0,3);
		System.out.println("SUCCESS");
			
		// closing connections
		fos.close();
        out.close();
        in.close();
        Socket.close();
      }
   }
