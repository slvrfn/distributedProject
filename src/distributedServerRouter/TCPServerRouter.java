package distributedServerRouter;

import logWriter.LogWriter;

import java.net.*;
import java.io.*;

public class TCPServerRouter 
{
	public static void main(String[] args) throws IOException
	{
		Socket clientSocket = null; // socket for the thread
		Object [][] RoutingTable = new Object [30][2]; // routing table
		int SockNum = 5555; // port number
		Boolean Running = true;
		int ind = 0; // index in the routing table

		String logFolderSaveLocation = "src/distributedServerRouter";
		
		//Accepting connections
		ServerSocket serverSocket = null; // server socket for accepting connections
		try
		{
			serverSocket = new ServerSocket(SockNum);
			System.out.println("ServerRouter is Listening on port: " + SockNum);
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + SockNum);
			System.exit(1);
		}
		
		// Creating threads with accepted connections
		while (Running == true)
		{
			try
			{
				clientSocket = serverSocket.accept();
				SThread t = new SThread(RoutingTable, clientSocket, ind, new LogWriter(logFolderSaveLocation)); // creates a thread with a random port
				t.start(); // starts the thread
				ind++; // increments the index
				System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
			}
			catch (IOException e) 
			{
				System.err.println("Client/Server failed to connect.");
				System.exit(1);
			}
		}//end while
		
		//closing connections
		clientSocket.close();
		serverSocket.close();
	}
}