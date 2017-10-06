package distributedServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//Generic base test thread. This is used to perform the basic message wiring
//while derived classes perform the actual tests
public abstract class BaseServerThread extends Thread
{
	boolean RunningOnLocalMachine;
	String routerName; // ServerRouter host name
	String testChoice;
	PrintWriter out;
	BufferedReader in;

	protected volatile Boolean isRunning = true;

	BaseServerThread(String _routerName, boolean onLocalMachine, String choice)
	{
		RunningOnLocalMachine = onLocalMachine;
		testChoice = choice;
		routerName = _routerName;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		// Variables for setting up connection and communication
		Socket socket = null; // socket to connect with ServerRouter

		int SockNum = 5555; // port number

		// Tries to connect to the ServerRouter
		try
		{
			if (RunningOnLocalMachine)
				routerName = "127.0.0.1";
			socket = new Socket(routerName, SockNum, InetAddress.getByName(null), PortToRunOn());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ERROR("Couldn't get I/O for the connection to: " + routerName);
		}

		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(testChoice); //initial send test choice (MUST)
			PRINT("Test choice \'" + testChoice + "\' was sent to the server router");
			String fromClient = in.readLine();// initial receive from router (verification of connection)
			out.println(fromClient);// initial send (IP of the destination Client)
			PRINT("ServerRouter: " + fromClient);
		}
		catch (IOException e)
		{
			ERROR("Couldn't get I/O for the connection");
		}

		//since connection now set up, actually run test
		RunTest(socket);

		//clean up socket
		try
		{
			in.close();
			out.close();
			socket.close();
		}
		catch (IOException e)
		{
			ERROR("Error when closing socket");
		}
		PRINT("Thread Closed");
	}

	//the test to be performed
	//you are provided a socket, you can perform whatever options over it
	//clean up writers/readers you create
	//DO NOT clean up socket provided
	abstract protected void RunTest(Socket s);

	//necessary so each test can specify which port it wants to run on
	abstract protected int PortToRunOn();

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