package part2.Client;

import logWriter.LogWriter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPClient
{
	public static void main(String[] args) throws IOException 
	{
		Scanner s = new Scanner(System.in);
		System.out.println("Is this client service running on the same machine as the ServerRouter? \'y\' or \'n\'");
		String choice = s.next();
		boolean RunningOnLocalMachine = choice.equals("y");

		List<BaseClientThread> currentThreads = new ArrayList<BaseClientThread>();

		String logFolderSaveLocation = "src/part2.Client";
		LogWriter twoWayTextLogWriter = new LogWriter(logFolderSaveLocation, "TwoWayText");
		LogWriter oneWayTextLogWriter = new LogWriter(logFolderSaveLocation, "OneWayText");
		LogWriter messageSizeTextLogWriter = new LogWriter(logFolderSaveLocation, "MessageSizeText");

		DatagramSocket sock = null;
		DatagramSocket responseSocket = null;
		ServerSocket connectionSocket = null;
		try {
			sock = new DatagramSocket();
			responseSocket = new DatagramSocket(1234);
			connectionSocket = new ServerSocket(9876);
		} catch (SocketException e) {
			ERROR("Could not get datagram socket");
		}

		//notify SR
		NotifyServerRouter("A", "Join", sock, "127.0.0.1");
		//make request to SR and get response
		String address = RequestFromRouter("A", sock, responseSocket, "127.0.0.1");

//region hide
		while (!choice.equals("-1"))
		{
			PrintChoices();
			choice = s.next();

			BaseClientThread test;
			switch (choice)
			{
				case "1":
					test = new ClientTwoWayTextThread("someRouterIP","someDestinationIp", RunningOnLocalMachine, choice, twoWayTextLogWriter);
					choice = "ClientTwoWayTextThread";
					break;
				case "2":
					test = new ClientOneWayTextThread("someRouterIP","someDestinationIp", RunningOnLocalMachine, choice, oneWayTextLogWriter);
					choice = "ClientOneWayTextThread";
					break;
				case "3":
					test = new ClientMessageSizeTextThread("someRouterIP","someDestinationIp", RunningOnLocalMachine, choice, messageSizeTextLogWriter);
					choice = "ClientMessageSizeTextThread";
					break;
				default:
					PRINT("Invalid Input");
					//loop starts over instead of continuing
					continue;
			}

			currentThreads.add(test);
			test.start();

			PRINT(choice + " Thread Started!\n");
		}

		//stop any current threads if they are running
		for (BaseClientThread test: currentThreads)
		{
			if (test.isAlive())
			{
				test.TerminateThread();
			}
		}

		PRINT("Server Closing");
		//endregion
	}

	private static void PrintChoices()
	{
		PRINT("What Type of Test Would you like to run?");
		PRINT("Enter number before test");
		PRINT("Enter \'-1\' to exit");
		PRINT("1) Two Way Text Stream Test");
		PRINT("2) One Way Text Stream Test");
		PRINT("3) Message Size Text Stream Test");
	}

	private static void PRINT(String message)
	{
		System.out.println(message);
	}

	protected static void ERROR(String message)
	{
		//allow children to modify output
		PRINT(message);
		System.exit(1);
	}

	private static void NotifyServerRouter(String name, String action, DatagramSocket sock, String addr){
		String message = name+ ":" + action + ":P";
		byte[] response = message.getBytes();
		try {
			DatagramPacket out = new DatagramPacket(response, response.length, InetAddress.getByName(addr), 22222);
			sock.send(out);
		}
		catch (IOException e){
			ERROR("Error sending response");
		}

	}

	private static String RequestFromRouter(String name, DatagramSocket outSocket, DatagramSocket responseSocket, String addr){
		byte[] request = name.getBytes();
		try {
			DatagramPacket out = new DatagramPacket(request, request.length, InetAddress.getByName(addr), 11111);
			outSocket.send(out);
		}
		catch (IOException e){
			ERROR("Error sending request");
		}

		byte[] buf = new byte[1000];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);

		try {
			responseSocket.receive(dp);
		} catch (IOException e) {
			ERROR("Error receiving request from server router");
		}

		return new String(dp.getData(), 0, dp.getLength());
	}
}
