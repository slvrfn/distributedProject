package distributedClient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient 
{
	public static void main(String[] args) throws IOException 
	{
		Scanner s = new Scanner(System.in);
		System.out.println("Is this client service running on the same machine as the ServerRouter? \'y\' or \'n\'");
		String choice = s.next();
		boolean RunningOnLocalMachine = choice.equals("y");

		// Variables for setting up connection and communication
		Socket Socket = null; // socket to connect with ServerRouter
		PrintWriter out = null; // for writing to ServerRouter
		BufferedReader in = null; // for reading form ServerRouter
		InetAddress addr = InetAddress.getLocalHost();
		String host = addr.getHostAddress(); // Client machine's IP
		int SockNum = 5555; // port number
		String routerName = "someRouterIP"; // ServerRouter host name
		String destinationIpAddress ="someDestinationIP"; // destination IP (Server)


		
		// Tries to connect to the ServerRouter
		try
		{
			if (RunningOnLocalMachine)
			{
				routerName = "127.0.0.1";
				destinationIpAddress ="127.0.0.1:4321";
				Socket = new Socket(routerName, SockNum, InetAddress.getByName(null),1234);
			}
			else
				Socket = new Socket(routerName, SockNum);
			out = new PrintWriter(Socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
		}
		catch (UnknownHostException e) 
		{
		   System.err.println("C Don't know about router: " + routerName);
		   System.exit(1);
		}
		catch (IOException e) 
		{
			System.err.println("Couldn't get I/O for the connection to: " + routerName);
			System.exit(1);
		}
		
		// Variables for message passing
		Reader reader = new FileReader("src/file.txt"); 
		BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
		String fromServer; // messages received from ServerRouter
		String fromUser; // messages sent to ServerRouter
		long t0, t1, t;
		
		// Communication process (initial sends/receives
		out.println(destinationIpAddress);// initial send (IP of the destination Server)
		fromServer = in.readLine();//initial receive from router (verification of connection)
		System.out.println("ServerRouter: " + fromServer);

		if (RunningOnLocalMachine)
			out.println("127.0.0.1:1234"); // Client sends local IP with desired port as initial send
		else
			out.println(host); // Client sends the IP of its machine as initial send

		t0 = System.currentTimeMillis();
	
		// Communication while loop
		while ((fromServer = in.readLine()) != null) 
		{
			System.out.println("Server: " + fromServer);
			t1 = System.currentTimeMillis();
			if (fromServer.equals("Bye.")) // exit statement
				break;
			t = t1 - t0;
			System.out.println("Cycle time: " + t);
			
			fromUser = fromFile.readLine(); // reading strings from a file
			if (fromUser != null) 
			{
				System.out.println("Client: " + fromUser);
				out.println(fromUser); // sending the strings to the Server via ServerRouter
				t0 = System.currentTimeMillis();
			}
		}
		
		// closing connections
		out.close();
		in.close();
		Socket.close();
	}
}
