package distributedServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//class to run some network test
public class TextThread extends BaseTestThread
{
    public TextThread(boolean onLocalMachine) throws IOException
    {
        super(onLocalMachine);
    }

    //the test to be performed
    //you are provided a socket, you can perform whatever options over it
    //clean up writers/readers you create
    //DO NOT clean up socket provided
    @Override
    protected void RunTest(Socket socket) {


        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter

        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            this.ERROR("Couldn't get I/O for the connection to: ");
        }

        // Variables for message passing
        String fromServer; // messages sent to ServerRouter
        String fromClient; // messages received from ServerRouter

        // Communication process (initial sends/receives)

        try
        {
            fromClient = in.readLine();// initial receive from router (verification of connection)
            out.println(fromClient);// initial send (IP of the destination Client)
            System.out.println("ServerRouter: " + fromClient);

            // Communication while loop
            while ((fromClient = in.readLine()) != null)
            {
                System.out.println("Client said: " + fromClient);
                if (fromClient.equals("Bye.")) // exit statement
                    break;
                fromServer = fromClient.toUpperCase(); // converting received message to upper case
                System.out.println("Server said: " + fromServer);
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
    protected void ERROR(String message) {
        super.ERROR("TextThread " + message);
    }
}
