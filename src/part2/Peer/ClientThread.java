package part2.Peer;

import logWriter.LogWriter;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread implements Runnable {
    private String tag;
    private String peer;
    private String test;
    private PrintWriter out;
    private BufferedReader in;
    private String serverName;
    private Socket socket = null;
    private LogWriter logWriter = null;

    ClientThread(String tag, String peer, String test, LogWriter logWriter) {
        this.tag = tag;
        this.peer = peer;
        this.test = test;
        this.logWriter = logWriter;
    }

    public void run() {
        int serverPort = 8888;
        try {
            socket = new Socket(peer, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            ERROR("Could not connect to: " + peer);
        } catch (IOException e) {
            ERROR("Could not get I/O for the connection to: " + peer);
        }

        out.println(tag); //Initial send of client name
        try {
            serverName = in.readLine(); //Initial receive of server's name
        } catch (IOException e) {
            e.printStackTrace();
        }
        PRINT("Client connected to " + serverName);
        switch (test) {
            case "1":
                Test1();
                break;
            case "2":
                Test2();
                break;
            case "3":
                Test3();
                break;
            case "4":
                Test4();
                break;
            default:
                out.println("Test Not Available: " + test);

                out.close();
                try {
                    in.close();
                } catch (IOException e) {
                    ERROR("Could not close BufferedReader");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Could not close socket");
                }
        }
    }

        // Echo Test

    public void Test1() {
        out.println("1");
        System.out.println("Test 1 Started");
        long start, end, total;
        start = System.currentTimeMillis();
        Reader reader = null;
        try {
            reader = new FileReader("src/file.txt");
        } catch (FileNotFoundException e) {
            ERROR("Could not find File");
        }
        BufferedReader fromFile = new BufferedReader(reader);
        String fromPeer;
        String fromUser;

        try {
            while ((fromPeer = in.readLine()) != null) {
                System.out.println(fromPeer);
                if (fromPeer.equals("Bye."))
                    break;
                if ((fromUser = fromFile.readLine()) != null)
                    out.println(fromUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        total = end - start;
        logWriter.WriteToFile(String.valueOf("Client: " + tag + " Server: " + serverName + " Time: " + total));
    }

    // File Transfer Test
    public void Test2() {
        out.println("2");
        long start, end, total;
        start = System.currentTimeMillis();
        InputStream inFile = null;
        OutputStream outFile = null;
        try {
            inFile = socket.getInputStream();
        } catch (IOException ex) {
            ERROR("Can't get socket input stream. ");
        }

        try {
            String filename = "src/Downloads/" + serverName + "-file-" + Long.toString(start) + ".txt";
            outFile = new FileOutputStream(filename);
        } catch (FileNotFoundException ex) {
            ERROR("File not found. ");
        }

        byte[] bytes = new byte[16 * 1024];

        int count;
        try {
            while ((count = inFile.read(bytes)) > 0) {
                outFile.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not receive bytes.");
        }

        try {
            outFile.close();
            inFile.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
        end = System.currentTimeMillis();
        total = end - start;
        logWriter.WriteToFile(String.valueOf("Client: " + tag + " Server: " + serverName + " Time: " + total));
    }

    // Image Transfer Test
    public void Test3() {
        out.println("3");
        long start, end, total;
        start = System.currentTimeMillis();
        InputStream inImg = null;
        OutputStream outImg = null;
        try {
            inImg = socket.getInputStream();
        } catch (IOException ex) {
            ERROR("Can't get socket input stream. ");
        }

        try {
            String filename = "src/Downloads/" + serverName + "-Image-" + start + ".jpg";
            outImg = new FileOutputStream(filename);
        } catch (FileNotFoundException ex) {
            ERROR("File not found. ");
        }

        byte[] bytes = new byte[16 * 1024];

        int count;
        try {
            while ((count = inImg.read(bytes)) > 0) {
                outImg.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not receive bytes.");
        }

        try {
            outImg.close();
            inImg.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
        end = System.currentTimeMillis();
        total = end - start;
        logWriter.WriteToFile(String.valueOf("Client: " + tag + " Server: " + serverName + " Time: " + total));
    }

    // Video Transfer Test
    public void Test4() {
        out.println("4");
        long start, end, total;
        start = System.currentTimeMillis();
        InputStream inVid = null;
        OutputStream outVid = null;
        try {
            inVid = socket.getInputStream();
        } catch (IOException ex) {
            ERROR("Can't get socket input stream. ");
        }

        try {
            String filename = "src/Downloads/" + serverName + "-Video-" + start + ".mp4";
            outVid = new FileOutputStream(filename);
        } catch (FileNotFoundException ex) {
            ERROR("File not found. ");
        }

        byte[] bytes = new byte[16 * 1024];

        int count;
        try {
            while ((count = inVid.read(bytes)) > 0) {
                outVid.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not receive bytes.");
        }

        try {
            outVid.close();
            inVid.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
        end = System.currentTimeMillis();
        total = end - start;
        logWriter.WriteToFile(String.valueOf("Client: " + tag + " Server: " + serverName + " Time: " + total));
    }

    private void PRINT(String message) {
        System.out.println(message);
    }

    private void ERROR(String message) {
        System.err.println(message);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Could not close socket");
        }
    }
}
