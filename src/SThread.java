import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.lang.Exception;


	
public class SThread extends Thread 
{
	private Object [][] RTable; // routing table
	private DataOutputStream out, outTo; // writers (for writing back to the machine and to destination)
   private DataInputStream in; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, destination, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // indext in the routing table

	// Constructor
	SThread(Object [][] Table, Socket toClient, int index) throws IOException
	{
			out = new DataOutputStream(toClient.getOutputStream());
			in = new DataInputStream(toClient.getInputStream());
			RTable = Table;
			addr = toClient.getInetAddress().getHostAddress();
			RTable[index][0] = addr; // IP addresses 
			RTable[index][1] = toClient; // sockets for communication
			ind = index;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		try
		{
		// Initial sends/receives
		destination = in.readUTF(); // initial read (the destination for writing)
		System.out.println("Forwarding to " + destination);
		out.writeUTF("Connected to the router.");
		// confirmation of connection
		
		// waits 10 seconds to let the routing table fill with all machines' information
		try{
    		Thread.currentThread().sleep(10000); 
	   }
		catch(InterruptedException ie){
		System.out.println("Thread interrupted");
		}
		
		// loops through the routing table to find the destination
		for ( int i=0; i<10; i++) 
				{
					if (destination.equals((String) RTable[i][0])){
						outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
						System.out.println("Found destination: " + destination);
						outTo = new DataOutputStream(outSocket.getOutputStream()); // assigns a writer
				}}
		
		// Communication loop
		byte[] data = new byte[1000000];
		int count = -1;
		while ((count = in.read(data)) != -1 && count != 3) {
				if (outSocket != null){
					System.out.println(count);
					outTo.write(data,0,count);
				}
       }// end while
		System.out.println("Client/Server Done");
		 }// end try
			catch (IOException e) {
               System.err.println("Could not listen to socket.");
               System.exit(1);
         }
	}
}