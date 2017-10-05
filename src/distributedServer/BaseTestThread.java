package distributedServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//Generic base thread. This is used to perform the basic message wiring
//while derived classes perform the actual tests
public abstract class BaseTestThread extends Thread
{
	boolean RunningOnLocalMachine;

	BaseTestThread(boolean onLocalMachine) throws IOException
	{
		RunningOnLocalMachine = onLocalMachine;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		// Variables for setting up connection and communication
		Socket socket = null; // socket to connect with ServerRouter

		String routerName = "someRouterIP"; // ServerRouter host name
		int SockNum = 5555; // port number

		// Tries to connect to the ServerRouter
		try
		{
			if (RunningOnLocalMachine)
			{
				routerName = "127.0.0.1";
				socket = new Socket(routerName, SockNum, InetAddress.getByName(null),4321);
			}
			else
				socket = new Socket(routerName, SockNum);
		}
		catch (UnknownHostException e)
		{
			ERROR("Don't know about router: " + routerName);
		}
		catch (IOException e)
		{
			ERROR("Couldn't get I/O for the connection to: " + routerName);
		}

		//since connection now set up, actually run test
		RunTest(socket);

		//clean up socket
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			ERROR("Error when closing socket");
		}
	}

	abstract protected void RunTest(Socket s);

	//method to help identify which thread has an error
	protected void ERROR(String message)
	{
		System.err.println(message);
		System.exit(1);
	}
}