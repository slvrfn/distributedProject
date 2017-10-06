package distributedClient;

import distributedServer.BaseServerThread;

import java.io.*;
import java.net.Socket;

//class to run some network test
public class ClientTextThread extends BaseClientThread
{
    public ClientTextThread(String _routerName, String destinationIp, boolean onLocalMachine, String choice) throws IOException {
        super(_routerName, destinationIp, onLocalMachine, choice);
    }

    @Override
    protected int PortToRunOn()
    {
        return 1234;
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
        String fromUser;
        long startTime, currentTime, timeDifference;

        // Variables for message passing
        Reader reader = null;
        try
        {
            reader = new FileReader("src/file.txt");
        }
        catch (FileNotFoundException e)
        {
            ERROR("Could Not find file to send");
        }
        BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
        String fromServer; // messages received from ServerRouter

        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O for the connection to: ");
        }

        try
        {
            startTime = System.currentTimeMillis();

            // Communication while loop
            while (isRunning && (fromServer = in.readLine()) != null)
            {
                PRINT("Server: " + fromServer);
                currentTime = System.currentTimeMillis();
                if (fromServer.equals("Bye.")) // exit statement
                    break;
                timeDifference = currentTime - startTime;
                PRINT("Cycle time: " + timeDifference);

                fromUser = fromFile.readLine(); // reading strings from a file
                if (fromUser != null)
                {
                    PRINT("Client: " + fromUser);
                    out.println(fromUser); // sending the strings to the Server via ServerRouter
                    startTime = System.currentTimeMillis();
                }
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
    protected void PRINT(String message)
    {

        super.PRINT("ClientTextThread " + message);
    }
}


