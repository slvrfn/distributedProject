package part2.Peer;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {
    private String tag;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    ServerThread(String tag, Socket clientSocket) {
        this.tag = tag;
        this.clientSocket = clientSocket;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            ERROR("Couldn't get I/O for connection");
        }
    }

    public void run() {
        try {
            clientName = in.readLine(); // Initial read from client of client's name
        } catch (IOException e) {
            ERROR("Could not get initial read from client");
        }
        clientName += " (" + clientSocket.getRemoteSocketAddress() + ")";
        out.println(tag); // Initial send to client of server's name
        PRINT("Connected with " + clientName);
        try {
            String test = in.readLine();
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
            }
        } catch (IOException e) {
            PRINT("Could not listen to socket");
        }
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            ERROR("Error when closing BufferedReader");
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem Closing Socket");
        }
    }

    // Echo Test
    private void Test1() {
        out.println("Test1 Started");
        String inputLine, outputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                outputLine = inputLine;
                if (clientSocket != null)
                    out.println(outputLine);
                if (inputLine.equals("Bye."))
                    break;
            }
        } catch (IOException e) {
            ERROR("Could not get input from " + clientName);
        }
    }

    // File Transfer Test
    private void Test2() {
        File file = new File("src/file.txt");
        byte[] bytes = new byte[16 * 1024];

        InputStream inFile = null;
        OutputStream outFile = null;
        try {
            inFile = new FileInputStream(file);
            outFile = clientSocket.getOutputStream();
        } catch (FileNotFoundException e) {
            ERROR("Could not find file");
        } catch (IOException e) {
            ERROR("Could not get OutputStream for clientSocket");
        }

        int count;
        try {
            while ((count = inFile.read(bytes)) > 0) {
                outFile.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not convert file to bytes or bytes could not be sent");
        }

        try {
            outFile.close();
            inFile.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
    }

    // Image Transfer Test
    private void Test3() {
        File image = new File("src/image.jpg");
        byte[] bytes = new byte[16 * 1024];

        InputStream inImg = null;
        OutputStream outImg = null;
        try {
            inImg = new FileInputStream(image);
            outImg = clientSocket.getOutputStream();
        } catch (FileNotFoundException e) {
            ERROR("Could not find file");
        } catch (IOException e) {
            ERROR("Could not get OutputStream for clientSocket");
        }

        int count;
        try {
            while ((count = inImg.read(bytes)) > 0) {
                outImg.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not convert image to bytes or bytes could not be sent");
        }

        try {
            outImg.close();
            inImg.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
    }

    // Video Transfer Test
    private void Test4() {
        File video = new File("src/video.mp4");
        byte[] bytes = new byte[16 * 1024];

        InputStream inVid = null;
        OutputStream outVid = null;
        try {
            inVid = new FileInputStream(video);
            outVid = clientSocket.getOutputStream();
        } catch (FileNotFoundException e) {
            ERROR("Could not find file");
        } catch (IOException e) {
            ERROR("Could not get OutputStream for clientSocket");
        }

        int count;
        try {
            while ((count = inVid.read(bytes)) > 0) {
                outVid.write(bytes, 0, count);
            }
        } catch (IOException e) {
            ERROR("Could not convert video to bytes or bytes could not be sent");
        }

        try {
            outVid.close();
            inVid.close();
        } catch (IOException e) {
            ERROR("Problem closing Output and Input Streams");
        }
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
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Could not close socket");
        }
    }
}
