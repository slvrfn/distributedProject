package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LookupServerRouter
{
	public static void main(String[] args)
	{
		String logFolderSaveLocation = "src/part2.ServerRouter";

		ConcurrentHashMap<String,String> lookupTable = new ConcurrentHashMap<>();

		//start request thread
		LogWriter requestLogWriter = new LogWriter(logFolderSaveLocation, "requestLogWriter");
		UDPRequest requestListener = new UDPRequest(lookupTable, null);
		requestListener.start();

		//start notify thread
		LogWriter notifyLogWriter = new LogWriter(logFolderSaveLocation, "notifyLogWriter");
		UDPNotify notifyListener = new UDPNotify(lookupTable, null);
		notifyListener.start();

		//start connect thread
		//add thread to listeningThreads
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
}