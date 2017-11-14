package part1.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//class to run some network test
public class ServerTwoWayTextThread extends BaseServerThread
{
    public ServerTwoWayTextThread(String _routerName, boolean onLocalMachine, String choice) {
        super(_routerName, onLocalMachine, choice);
    }

    @Override
    protected int PortToRunOn() {
        return 4321;
    }

    //the test to be performed
    //you are provided a socket, you can perform whatever options over it
    //clean up writers/readers you create
    //DO NOT clean up socket provided
    @Override
    protected void RunTest(Socket socket)
    {
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter

        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O for the connection");
        }

        // Variables for message passing
        String fromServer = ""; // messages sent to ServerRouter
        String fromClient; // messages received from ServerRouter

        try
        {
            // Communication while loop
            //loop will stop if the thread needs to be closed, or if client has stopped sending things
            while (isRunning && (fromClient = in.readLine()) != null)
            {
                PRINT("Client said: " + fromClient);
                if (fromClient.equals("Bye.")) // exit statement
                {
                    break;
                }
                if (fromClient.equals("Finished.")) // exit statement
                {
                    String finishTime = String.valueOf(System.currentTimeMillis());
                    PRINT("Test finished at: " + finishTime);
                    out.println(finishTime);
                    break;
                }
                fromServer = fromClient;
                PRINT("Server said: " + fromServer);
                out.println(fromServer); // sending the converted message back to the Client via ServerRouter
            }
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O from the client");
        }

        // closing connections
        out.close();

        try
        {
            in.close();
        }
        catch (IOException e)
        {
            ERROR("Error when closing socket");
        }
    }

    @Override
    protected void PRINT(String message) {
        super.PRINT("ServerTwoWayTextThread " + message);
    }
}
