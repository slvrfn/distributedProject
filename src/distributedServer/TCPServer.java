package distributedServer;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPServer 
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
		String routerName = "someRouterIP"; // ServerRouter host name
		int SockNum = 5555; // port number
		
		// Tries to connect to the ServerRouter
		try 
		{
			if (RunningOnLocalMachine)
			{
				routerName = "127.0.0.1";
				Socket = new Socket(routerName, SockNum, InetAddress.getByName(null),4321);
			}
			else
				Socket = new Socket(routerName, SockNum);
			out = new PrintWriter(Socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
		}
		catch (UnknownHostException e) 
		{
			System.err.println("S Don't know about router: " + routerName);
			System.exit(1);
		} 
		catch (IOException e) 
		{
			System.err.println("Couldn't get I/O for the connection to: " + routerName);
			System.exit(1);
		}
		
		// Variables for message passing
		String fromServer; // messages sent to ServerRouter
		String fromClient; // messages received from ServerRouter
		
		// Communication process (initial sends/receives)
		
		fromClient = in.readLine();// initial receive from router (verification of connection)
		out.println(fromClient);// initial send (IP of the destination Client)
		System.out.println("ServerRouter: " + fromClient);
				 
		// Communication while loop
		while ((fromClient = in.readLine()) != null) 
		{
			System.out.println("Client said: " + fromClient);
			if (fromClient.equals("Bye.")) // exit statement
				break;
			fromServer = fromClient.toUpperCase(); // converting received message to upper case
			System.out.println("Server said: " + fromServer);
			out.println(fromServer); // sending the converted message back to the Client via ServerRouter
		}
		
		// closing connections
		out.close();
		in.close();
		Socket.close();
	}
}
