package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class LookupServerRouter
{
	public static void main(String[] args)
	{
		String logFolderSaveLocation = "src/part2.ServerRouter";
		String serverName = "SR1";

		ConcurrentHashMap<String,String> lookupTable = new ConcurrentHashMap<>();
		ConcurrentHashMap<String,String> serverRouterTable = new ConcurrentHashMap<>();

		//start request thread
		LogWriter requestLogWriter = new LogWriter(logFolderSaveLocation, "requestLogWriter");
		UDPRequest requestListener = new UDPRequest(serverRouterTable, lookupTable, requestLogWriter);
		requestListener.start();

		//start notify thread
		LogWriter notifyLogWriter = new LogWriter(logFolderSaveLocation, "notifyLogWriter");
		UDPNotify notifyListener = new UDPNotify(serverRouterTable, lookupTable, notifyLogWriter);
		notifyListener.start();

		//start connect thread
		LogWriter connectLogWriter = new LogWriter(logFolderSaveLocation, "connectLogWriter");
		TCPConnectThread connectListener = new TCPConnectThread(lookupTable, connectLogWriter);
		connectListener.start();

		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			ERROR("Could not get datagram socket");
		}

		//add other server routers of interest ot this list
		ArrayList<String> otherServerRouters = new ArrayList<String>();
		//otherServerRouters.add("");
		//need to notify other server routers about this routers existence
		NotifyServerRouters(serverName,"Join", sock, otherServerRouters);

		//user enter if they want the server router to stop

		Scanner s = new Scanner(System.in);
		System.out.println("Threads are running.\nPlease enter '-1' to stop the server router");
		String choice = s.next();

		while (!choice.equals("-1"))
		{
			PRINT("Please enter '-1' to stop the server router\"");
		}

		NotifyServerRouters(serverName,"Leave", sock, otherServerRouters);

		//clean up socket
		if (sock != null)
			sock.close();

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

	private static void NotifyServerRouters(String name, String action, DatagramSocket sock, List<String> addrs){
		for (String addr : addrs) {
			String message = name+ ":" + action + ":SR";
			byte[] response = message.getBytes();
			try {
				DatagramPacket out = new DatagramPacket(response, response.length, InetAddress.getByName(addr), 22222);
				sock.send(out);
			}
			catch (IOException e){
				ERROR("Error sending response");
			}
		}

	}
}