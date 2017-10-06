package distributedClient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//Generic base thread. This is used to perform the basic message wiring
//while derived classes perform the actual tests
public abstract class BaseClientThread extends Thread
{
    boolean RunningOnLocalMachine;
    String routerName; // ServerRouter host name
    String destinationIpAddress; // destination IP (Server)
    String testChoice;

    protected volatile Boolean isRunning = true;

    BaseClientThread(String _routerName, String destinationIp, boolean onLocalMachine, String choice) throws IOException
    {
        RunningOnLocalMachine = onLocalMachine;
        routerName = _routerName;
        destinationIpAddress = destinationIp;
        testChoice = choice;
    }

    // Run method (will run for each machine that connects to the ServerRouter)
    public void run()
    {
        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        //still need text writers for the initial send/receive for wiring up the client/server
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        String host = null; // Client machine's IP
        int SockNum = 5555; // port number

        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            ERROR("Don't know about router: " + routerName);
        }

        // Tries to connect to the ServerRouter
        try
        {
            if (RunningOnLocalMachine)
            {
                routerName = "127.0.0.1";
                destinationIpAddress ="127.0.0.1:4321";
            }
            Socket = new Socket(routerName, SockNum, InetAddress.getByName(null),PortToRunOn());
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O for the connection to: " + routerName);
        }

        String fromServer; // messages received from ServerRouter

        // Communication process (initial sends/receives
        out.println(testChoice); //initial send test choice selection (MUST)
        PRINT("Test choice \'" + testChoice + "\' was sent to the server router");
        out.println(destinationIpAddress);//  send (IP of the destination Server)
        try
        {
            fromServer = in.readLine();//initial receive from router (verification of connection)
            PRINT("ServerRouter: " + fromServer);
        }
        catch (IOException e)
        {
            ERROR("Couldn't read from Server");
        }

        if (RunningOnLocalMachine)
            host = "127.0.0.1";
        out.println(host + ":" + PortToRunOn());// Client sends the IP of its machine as initial send

        //since connection now set up, actually run test
        RunTest(Socket);

        // closing connections
        out.close();
        try
        {
            in.close();
            Socket.close();
        }
        catch (IOException e)
        {
            ERROR("Error when closing socket");
        }
        PRINT("Thread Closed");
    }

    //the test to be performed
    //you are provided a socket, you can perform whatever options over it
    //clean up writers/readers you create
    //DO NOT clean up socket provided
    abstract protected void RunTest(Socket s);

    //necessary so each test can specify which port it wants to run on
    abstract protected int PortToRunOn();

    public void TerminateThread()
    {
        isRunning = false;
    }

    //method to help identify which thread has an error
    protected void ERROR(String message)
    {
        //allow children to modify output
        PRINT(message);
        System.exit(1);
    }

    //allow children to modify output before it is printed
    protected void PRINT(String message)
    {
        System.out.println(message);
    }
}