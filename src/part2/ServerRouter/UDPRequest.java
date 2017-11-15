package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UDPRequest extends UDPBaseThread {

    public UDPRequest(ConcurrentHashMap routers, ConcurrentHashMap map, LogWriter writer) {
        super(routers, map, writer);
    }

    @Override
    protected void PerformAction(DatagramSocket s, DatagramPacket p) {
        LookupThread t = new LookupThread(s, p, lookupTable);
        t.start();
        //necessary so any created threads can be cleaned up
    }

    @Override
    protected int GetPort() {
        return 11111;
    }

    public class LookupThread extends Thread{

        ConcurrentHashMap<String, String> _lookupTable;
        String SymbolicName = null;
        DatagramPacket packet;
        DatagramSocket sock;

        public LookupThread(DatagramSocket s, DatagramPacket p, ConcurrentHashMap<String, String> m) {
            packet = p;
            _lookupTable = m;
            String received = new String(p.getData(), 0, p.getLength());
            SymbolicName = received;
            sock = s;
        }

        public void run() {
            if (_lookupTable.containsKey(SymbolicName)){
                //return address to requester
                RespondToRequester(_lookupTable.get(SymbolicName));
            }
            else {
                //start tcp conn

                // Variables for setting up connection and communication
                Socket Socket = null; // socket to connect with ServerRouter
                //still need text writers for the initial send/receive for wiring up the client/server
                PrintWriter out = null; // for writing to ServerRouter
                BufferedReader in = null; // for reading form ServerRouter
                String host = null; // Client machine's IP
                int SockNum = 33333; // port number

                boolean destinationFound = false;


                //need to potentially ask more than one server router
                for ( Map.Entry<String, String>  entry: serverRouters.entrySet()) {
                    String routerIP = entry.getValue();
                    try
                    {
                        Socket = new Socket(routerIP, SockNum);
                        out = new PrintWriter(Socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
                    }
                    catch (IOException e)
                    {
                        ERROR("Couldn't get I/O for the connection to: " + entry.getKey());
                    }

                    String fromServer = ""; // messages received from ServerRouter

                    // Communication process (initial sends/receives
                    out.println("Hello.");//  send (verification on connection)
                    try
                    {
                        fromServer = in.readLine();//initial receive from router (verification of connection)
                        PRINT("ServerRouter: " + fromServer);
                    }
                    catch (IOException e)
                    {
                        ERROR("Couldn't read from Server");
                    }

                    out.println(SymbolicName);// Client forwards the request to the other server router

                    try
                    {
                        fromServer = in.readLine();//receive response from router (requested address or error)
                        PRINT("ServerRouter: " + fromServer);
                    }
                    catch (IOException e)
                    {
                        ERROR("Couldn't read from Server");
                    }

                    //clean up connections first

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

                    //first character will be either Number (requested ip) or some character (error response)
                    if (Character.isDigit(fromServer.charAt(0)))
                    {
                        RespondToRequester(fromServer);
                        destinationFound = true;
                        //break foreach loop because destination was found
                        break;
                    }
                    //else continue asking other server routers
                }

                if (!destinationFound){
                    RespondToRequester("ERR 404");
                }

            }
        }

        private void RespondToRequester(String m){
            byte[] response = m.getBytes();
            DatagramPacket out = new DatagramPacket(response, response.length, packet.getAddress(), 1234);
            try {
                sock.send(out);
            }
            catch (IOException e){
                ERROR("Error sending response");
            }
        }
    }
}


