package part1.ServerRouter;

import logWriter.LogWriter;

import java.io.*;
import java.net.*;

//Generic base test thread. This is used to perform the basic message wiring
//while derived classes perform the actual tests
public abstract class ServerRouterBaseThread extends Thread
{
	private Object [][] RTable; // routing table
	private PrintWriter out; // writers (for writing back to the machine and to destination)
    private BufferedReader in; // reader (for reading from the machine connected to)
	private String destination; // communication string
	private Socket outSocket, toClient; // socket for communicating with a destination
	//private int ind; // indext in the routing table

	LogWriter logWriter;
	protected volatile Boolean isRunning = true;

	// Constructor
	ServerRouterBaseThread(Object [][] Table, Socket socket, int index, LogWriter writer)
	{
		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			ERROR("Error creating In/Out streams");
		}

		RTable = Table;
		String addr = socket.getInetAddress().getHostAddress();
		addr += ":"+socket.getPort();
		System.out.println(addr + " added to routing table");
		RTable[index][0] = addr; // IP addresses
		RTable[index][1] = socket; // sockets for communication
		//ind = index;
		logWriter = writer;
		toClient = socket;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		try
		{
			// Initial sends/receives
			destination = in.readLine(); // initial read (the destination for writing)
			PRINT("Forwarding to " + destination);
			out.println("Connected to the router."); // confirmation of connection
			
			// waits 10 seconds to let the routing table fill with all machines' information
			try
			{
				Thread.currentThread().sleep(10000);
			}
			catch(InterruptedException ie)
			{
				ERROR("Thread interrupted");
			}

			long startTime, endTime, lookupTime;
			startTime = System.nanoTime();
			//should always get a value set
			endTime = 0;

			// loops through the routing table to find the destination
			for ( int i=0; i<RTable.length; i++)
			{
				if (destination.equals((String) RTable[i][0]))
				{
					outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
					PRINT("Found destination: " + destination);
					endTime = System.nanoTime();
					break;
				}
			}
			lookupTime = endTime-startTime;

			int itemsInRoutingTable = 0;

			for (int i = 0; i<RTable.length; i++)
			{
				if (RTable[i][0] != null)
					itemsInRoutingTable++;
				else
					//if null was reached the items that matter have already been counted
					break;
			}

			String toWrite = String.format("Lookup Time: %s nanoseconds Items in Routing Table: %s", lookupTime, itemsInRoutingTable);
			logWriter.WriteToFile(toWrite);

			RunTest(toClient, outSocket);
		}
		catch (IOException e) 
		{
			ERROR("Could not listen to socket.");
		}

		out.close();
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			ERROR("Error when closing socket");
		}
	}

	//the test to be performed
	//you are provided a socket, you can perform whatever options over it
	//clean up writers/readers you create
	//DO NOT clean up socket provided
	abstract protected void RunTest(Socket client, Socket oSocket);

	public void TerminateThread()
	{

		isRunning = false;
	}

	//method to help identify which thread has an error
	protected void ERROR(String message)
	{
		//allow children to modify output
		PRINT(message);
		System.exit(1);
	}

	//allow children to modify output before it is printed
	protected void PRINT(String message)
	{
		System.out.println(message);
	}
}