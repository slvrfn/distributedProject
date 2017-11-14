package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerRouterTextThread extends ServerRouterBaseThread
{
    private String inputLine, outputLine; // communication strings
    private PrintWriter outTo; // writer (for writing back to destination)
    private BufferedReader in; // reader (for reading from the machine connected to)

    public ServerRouterTextThread(Object[][] Table, Socket toClient, int index, LogWriter writer) throws IOException
    {
        super(Table, toClient, index, writer);
    }

    @Override
    protected void RunTest(Socket toClient, Socket outSocket)
    {
        try
        {
            outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
            in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O for the connection");
        }

        try
        {
            // Communication loop
            while (isRunning && (inputLine = in.readLine()) != null)
            {
                PRINT("Client/Server said: " + inputLine);

                outputLine = inputLine; // passes the input from the machine to the output string for the destination
                if ( outSocket != null)
                {
                    outTo.println(outputLine); // writes to the destination
                }
                if (inputLine.equals("Bye.")) // exit statement
                    break;
            }
        }
        catch (IOException e)
        {
            ERROR("Couldn't read from Client");
        }
    }

    @Override
    protected void PRINT(String message) {
        super.PRINT("ServerRouterTextThread " + message);
    }
}
