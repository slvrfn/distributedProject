package distributedServer;

import java.io.*;
import java.util.Scanner;

public class TCPServer 
{
	public static void main(String[] args) throws IOException 
	{
		Scanner s = new Scanner(System.in);
		System.out.println("Is this client service running on the same machine as the ServerRouter? \'y\' or \'n\'");
		String choice = s.next();
		boolean RunningOnLocalMachine = choice.equals("y");

		//currently only running original test
		TextThread test = new TextThread(RunningOnLocalMachine);
		test.start();

		System.out.println("Thread Started");
		//readline to suspend this prog (temp)
		choice = s.next();
	}
}
