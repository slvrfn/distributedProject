package distributedServer;



import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPServer 
{
	public static void main(String[] args) throws IOException 
	{
		Scanner s = new Scanner(System.in);
		PRINT("Is this client service running on the same machine as the ServerRouter? \'y\' or \'n\'. Or \'-1\' to exit");
		String choice = s.next();
		boolean RunningOnLocalMachine = choice.equals("y");

		List<BaseTestThread> currentThreads = new ArrayList<BaseTestThread>();

		while (!choice.equals("-1"))
		{
			PrintChoices();
			choice = s.next();

			BaseTestThread test;
			switch (choice)
			{
				case "1":

					test = new TextThread(RunningOnLocalMachine);
					choice = "TextThread";
					break;
				default:
					PRINT("Invalid Input");
					//loop starts over instead of continuing
					continue;
			}

			currentThreads.add(test);
			test.start();

			PRINT(choice + " Thread Started!");
		}

		//stop any current threads if they are running
		for (BaseTestThread test: currentThreads)
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
		PRINT("1) Text Stream Test");
	}

	private static void PRINT(String message)
	{
		System.out.println(message);
	}
}
