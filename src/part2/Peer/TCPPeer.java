package part2.Peer;

import logWriter.LogWriter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class TCPPeer {
    private static String tag = "";

    public static void main(String[] args) {
        int serverPort = 8888;
        String serverRouter = "10.100.78.53";
        List<Thread> clientThreads = new ArrayList<>();
        int numClients = 0;

        Scanner s = new Scanner(System.in);
        System.out.println("What would you like to call your peer?");
        String choice = s.next();
        tag = choice;

        CountDownLatch startSignal = new CountDownLatch(1);
        Thread server = new Thread(new TCPServer(tag, serverPort, startSignal));
        server.start();
        try {
            startSignal.await();
        } catch (InterruptedException e) {
            ERROR("Could not be notified of Server start");
        }


        DatagramSocket sock = null;
        DatagramSocket responseSocket = null;
        try {
            sock = new DatagramSocket();
            responseSocket = new DatagramSocket(1234);
        } catch (SocketException e) {
            ERROR("Could not get datagram socket");
        }


        //notify SR
        NotifyServerRouter(tag, "Join", sock, serverRouter);

        int numTests = 1;

        String saveLocation = "src/Logs";
        LogWriter peerLookup = new LogWriter(saveLocation, tag, "PeerLookup");
        // Get all Peers to connect
        while (!choice.equals("-1")) {
            System.out.println("What peer would you like to connect to?");
            choice = s.next();
            if (!choice.equals("-1")) {
                long start = System.currentTimeMillis();
                //make request to SR and get response
                String peerAddr = RequestFromRouter(choice, sock, responseSocket, serverRouter);
                long end = System.currentTimeMillis();
                long total = end - start;
                peerLookup.WriteToFile("Lookup Time: " + Long.toString(total));
                LogWriter test1LogWriter = new LogWriter(saveLocation, tag, "EchoTest");
                LogWriter test2LogWriter = new LogWriter(saveLocation, tag, "FileTest");
                LogWriter test3LogWriter = new LogWriter(saveLocation, tag, "ImageTest");
                LogWriter test4LogWriter = new LogWriter(saveLocation, tag, "VideoTest");
                Thread t = null;
                for (int i = 0; i < numTests; i++) {
                    t = new Thread(new ClientThread(tag, peerAddr, "1", test1LogWriter));
                    clientThreads.add(t);
                    t = new Thread(new ClientThread(tag, peerAddr, "2", test2LogWriter));
                    clientThreads.add(t);
                    t = new Thread(new ClientThread(tag, peerAddr, "3", test3LogWriter));
                    clientThreads.add(t);
                    t = new Thread(new ClientThread(tag, peerAddr, "4", test4LogWriter));
                    clientThreads.add(t);
                }
            }
        }

        // Start all ClientThreads
        for (Thread t : clientThreads) {
            t.start();
        }

        // Wait for all clients to complete
        for (Thread t : clientThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                ERROR("Could not join thread");
            }
        }


        NotifyServerRouter(tag, "Leave", sock, serverRouter);

        PRINT("Closing...");

        for (Thread t : clientThreads) {
            if (t.isAlive())
                t.interrupt();
        }
        server.interrupt();

        PRINT("Closed");
    }


    private static void NotifyServerRouter(String name, String action, DatagramSocket sock, String addr){
        String message = name+ ":" + action + ":P";
        byte[] response = message.getBytes();
        try {
            DatagramPacket out = new DatagramPacket(response, response.length, InetAddress.getByName(addr), 22222);
            sock.send(out);
        }
        catch (IOException e){
            ERROR("Error sending response");
        }

    }

    private static String RequestFromRouter(String name, DatagramSocket outSocket, DatagramSocket responseSocket, String addr){
        byte[] request = name.getBytes();
        try {
            DatagramPacket out = new DatagramPacket(request, request.length, InetAddress.getByName(addr), 11111);
            outSocket.send(out);
        }
        catch (IOException e){
            ERROR("Error sending request");
        }

        byte[] buf = new byte[1000];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        try {
            responseSocket.receive(dp);
        } catch (IOException e) {
            ERROR("Error receiving request from server router");
        }

        return new String(dp.getData(), 0, dp.getLength());
    }

    private static void PRINT(String message) {
        System.out.println(message);
    }

    private static void ERROR(String message) {
        System.err.println(message);
        System.exit(1);
    }

}
