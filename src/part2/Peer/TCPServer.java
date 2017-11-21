package part2.Peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TCPServer implements Runnable{
    private String tag;
    private int port;
    private CountDownLatch startSignal;
    private List<Thread> serverThreads = new ArrayList<>();

    TCPServer(String tag, int port, CountDownLatch startSignal) {
        this.tag = tag;
        this.port = port;
        this.startSignal = startSignal;
    }

    public void run() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        PRINT("Starting Server...");
        // Create ServerSocket
        try {
            serverSocket = new ServerSocket(this.port);
            PRINT("Listening on port " + port);
            startSignal.countDown();
        } catch (IOException e) {
            ERROR("Could not listen on port " + port);
        }

        while (isRunning()) {
            try {
                clientSocket = serverSocket.accept();
                Thread t = new Thread(new ServerThread(tag, clientSocket));
                serverThreads.add(t);
                t.start();
            } catch (IOException e) {
                ERROR("Client failed to connect");
            }
        }

        PRINT("Closing...");
        for (Thread t: serverThreads) {
            if (t.isAlive())
                t.interrupt();
        }
        PRINT("Closed");
    }

    private boolean isRunning() {
        if (Thread.currentThread().isInterrupted())
            return false;
        else
            return true;
    }

    private void PRINT(String message) {
        System.out.println(message);
    }

    private void ERROR(String message) {
        System.err.println(message);
        System.exit(1);
    }

}
