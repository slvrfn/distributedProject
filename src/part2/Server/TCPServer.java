package part2.Server;



import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPServer 
{
	public static void main(String[] args)
	{
		Scanner s = new Scanner(System.in);
		PRINT("Is this client service running on the same machine as the ServerRouter? \'y\' or \'n\'. Or \'-1\' to exit");
		String choice = s.next();
		boolean RunningOnLocalMachine = choice.equals("y");

		List<BaseServerThread> currentThreads = new ArrayList<BaseServerThread>();

		while (!choice.equals("-1"))
		{
			PrintChoices();
			choice = s.next();

			BaseServerThread test;
			switch (choice)
			{
				case "1":

					test = new ServerTwoWayTextThread("someRouterIP", RunningOnLocalMachine, choice);
					choice = "ServerTwoWayTextThread";
					break;
				case "2":
					test = new ServerOneWayTextThread("someRouterIP", RunningOnLocalMachine, choice);
					choice = "ServerOneWayTextThread";
					break;
				case "3":
					test = new ServerMessageSizeTextThread("someRouterIP", RunningOnLocalMachine, choice);
					choice = "ServerMessageSizeTextThread";
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
		for (BaseServerThread test: currentThreads)
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
		PRINT("3) Message Size Text Stream Test");
	}

	private static void PRINT(String message)
	{
		System.out.println(message);
	}
}
