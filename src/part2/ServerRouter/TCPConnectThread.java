package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TCPConnectThread extends Thread
{

	//Accepting connections
	ServerSocket serverSocket = null; // server socket for accepting connections
	volatile Boolean Running = true;

	ConcurrentHashMap<String,String> lookupTable;
	LogWriter logWriter;

	private PrintWriter outTo; // writer (for writing back to destination)
	private BufferedReader in; // reader (for reading from the machine connected to)

	public TCPConnectThread(ConcurrentHashMap<String,String> lookupTable, LogWriter logwriter) {
		this.lookupTable = lookupTable;
		this.logWriter = logwriter;
	}

	public void run()
	{
		Socket clientSocket = null; // socket for the thread
		int SockNum = 5555; // port number


		String logFolderSaveLocation = "src/part1.ServerRouter";
		LogWriter routingTableLookupLogWriter = new LogWriter(logFolderSaveLocation, "RoutingTableLookup");
		

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
		while (Running)
		{
			try
			{
				clientSocket = serverSocket.accept();

				RequestConsumer consumer = new RequestConsumer(clientSocket);

				//currentThreads.add(consumer);
				consumer.start(); // starts the thread
				PRINT("TCP consumer started");

				PRINT("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
			}
			catch (IOException e) 
			{
				ERROR("Client/Server failed to connect.");
			}
		}//end while

		TerminateThread();
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

	public void TerminateThread()
	{
		Running = false;
		try
		{
			//closing connections
			if (serverSocket.isBound())
				serverSocket.close();
		}
		catch (IOException e)
		{
			PRINT("Error closing server socket");
		}
	}

	public class RequestConsumer extends Thread{

		Socket sock;

		public RequestConsumer(Socket s) {
			sock = s;
		}

		public void run(){

			try
			{
				outTo = new PrintWriter(sock.getOutputStream(), true); // assigns a writer
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			}
			catch (IOException e)
			{
				ERROR("Couldn't get I/O for the connection");
			}

			String fromServer = "";

			try
			{
				fromServer = in.readLine();//initial receive from other server router (verification of connection)
				PRINT("Received: " + fromServer);
			}
			catch (IOException e)
			{
				ERROR("Couldn't read from Server");
			}
			outTo.println("Hello.");//  send (verification on connection)

			try
			{
				fromServer = in.readLine();//request from server
				PRINT("Received: " + fromServer);
			}
			catch (IOException e)
			{
				ERROR("Couldn't read from Server");
			}

			if (lookupTable.containsKey(fromServer)){
				outTo.println(lookupTable.get(fromServer));
			}
			else {
				outTo.println("Not found.");
			}

			// closing connections
			outTo.close();
			try
			{
				in.close();
				sock.close();
			}
			catch (IOException e)
			{
				ERROR("Error when closing socket");
			}

		}
	}
}