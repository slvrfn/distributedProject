package distributedClient;

import logWriter.LogWriter;

import java.io.IOException;
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

		String logFolderSaveLocation = "src/distributedClient";
		LogWriter twoWayTextLogWriter = new LogWriter(logFolderSaveLocation, "TwoWayText");
		LogWriter oneWayTextLogWriter = new LogWriter(logFolderSaveLocation, "OneWayText");

		while (!choice.equals("-1"))
		{
			PrintChoices();
			choice = s.next();

			BaseClientThread test;
			switch (choice)
			{
				case "1":
					test = new ClientTwoWayTextThread("someRouterIP","someDestinationIp", RunningOnLocalMachine, choice, twoWayTextLogWriter);
					choice = "ServerTwoWayTextThread";
					break;
				case "2":
					test = new ClientOneWayTextThread("someRouterIP","someDestinationIp", RunningOnLocalMachine, choice, oneWayTextLogWriter);
					choice = "ServerOneWayTextThread";
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
	}

	private static void PrintChoices()
	{
		PRINT("What Type of Test Would you like to run?");
		PRINT("Enter number before test");
		PRINT("Enter \'-1\' to exit");
		PRINT("1) Two Way Text Stream Test");
		PRINT("2) One Way Text Stream Test");
	}

	private static void PRINT(String message)
	{
		System.out.println(message);
	}
}
