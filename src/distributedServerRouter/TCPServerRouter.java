package distributedServerRouter;

import logWriter.LogWriter;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPServerRouter 
{
	public static void main(String[] args)
	{
		List<ServerRouterBaseThread> currentThreads = new ArrayList<>();
		List<Socket> openSockets = new ArrayList<>();
		BufferedReader in; // reader (for reading from the machine connected to)

		Socket clientSocket = null; // socket for the thread
		Object [][] RoutingTable = new Object [30][2]; // routing table
		int SockNum = 5555; // port number
		Boolean Running = true;
		int ind = 0; // index in the routing table

		String logFolderSaveLocation = "src/distributedServerRouter";
		LogWriter routingTableLookupLogWriter = new LogWriter(logFolderSaveLocation, "RoutingTableLookup");
		
		//Accepting connections
		ServerSocket serverSocket = null; // server socket for accepting connections
		try
		{
			serverSocket = new ServerSocket(SockNum);
			PRINT("ServerRouter is Listening on port: " + SockNum);
		}
		catch (IOException e)
		{
			ERROR("Could not listen on port: " + SockNum);
		}
		
		// Creating threads with accepted connections
		while (Running == true)
		{
			try
			{
				clientSocket = serverSocket.accept();
				openSockets.add(clientSocket);

				ServerRouterBaseThread t = null; // creates a thread with a specified port
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String testChoice = in.readLine();
				PRINT("Test choice \'" + testChoice + "\' was received");
				String choice = "";
				switch (testChoice)
				{
					case "1":
						t = new ServerRouterTextThread(RoutingTable, clientSocket, ind, routingTableLookupLogWriter);
						choice = "ServerRouterTextThread";
						break;
					case "2":
						t = new ServerRouterTextThread(RoutingTable, clientSocket, ind, routingTableLookupLogWriter);
						choice = "ServerRouterTextThread";
						break;
					case "3":
						t = new ServerRouterTextThread(RoutingTable, clientSocket, ind, routingTableLookupLogWriter);
						choice = "ServerRouterTextThread";
						break;
					default:
						ERROR("Test not available");
						break;
				}
				currentThreads.add(t);
				t.start(); // starts the thread
				PRINT(choice + " started");

				ind++; // increments the index
				PRINT("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
			}
			catch (IOException e) 
			{
				ERROR("Client/Server failed to connect.");
			}
		}//end while

		//stop any current threads if they are running
		for (ServerRouterBaseThread test: currentThreads)
		{
			if (test.isAlive())
			{
				test.TerminateThread();
			}
		}

		try
		{
			//closing connections
			for (Socket sock: openSockets)
			{
				try
				{
					//closing sockets
					sock.close();
				}
				catch (IOException e)
				{
					PRINT("Error closing opened socket");
				}
			}
			serverSocket.close();
		}
		catch (IOException e)
		{
			PRINT("Error closing server socket");
		}
	}

	private static void PRINT(String message)
	{
		System.out.println(message);
	}

	private static void ERROR(String message)
	{
		//allow children to modify output
		PRINT(message);
		System.exit(1);
	}
}