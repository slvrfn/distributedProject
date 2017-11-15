package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Generic base test thread. This is used to perform the basic message wiring
//while derived classes perform the actual tests
public abstract class UDPBaseThread extends Thread
{
	protected ConcurrentHashMap<String,String> lookupTable; // routing table
	protected ConcurrentHashMap<String,String> serverRouters;

	LogWriter logWriter;
	protected volatile Boolean isRunning = true;

	// Constructor
	UDPBaseThread(ConcurrentHashMap<String,String> routers, ConcurrentHashMap map, LogWriter writer)
	{
		logWriter = writer;
		lookupTable = map;
		serverRouters = routers;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		byte[] buf = new byte[12];
		DatagramPacket dgp = new DatagramPacket(buf, buf.length);
		DatagramSocket sk = null;

		try
		{
			sk = new DatagramSocket(GetPort());
			System.out.println("Server started");
		}
		catch (IOException e) 
		{
			ERROR("Could not listen to socket.");
		}

		try
		{
			while (isRunning) {
				sk.receive(dgp);

				PerformAction(sk, dgp);
			}
		}
		catch (IOException e)
		{
			ERROR("Error receiving on socket");
		}


		if (sk != null)
			sk.close();
	}

	//the action to be performed
	//you are provided a DatagramPacket, you can perform whatever actions with it
	abstract protected void PerformAction(DatagramSocket s, DatagramPacket p);

	//gets the port this udp request is supposed to occur on
	abstract protected int GetPort();

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