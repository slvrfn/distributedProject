package part2.ServerRouter;

import logWriter.LogWriter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class UDPNotify extends UDPBaseThread {

    private String serverName;

    public UDPNotify(ConcurrentHashMap<String,String> SRs, ConcurrentHashMap map, String sName, LogWriter writer) {
        super(SRs, map, writer);
        serverName = sName;
    }

    @Override
    protected void PerformAction(DatagramSocket s, DatagramPacket p) {
        String received = new String(p.getData(), 0, p.getLength());

        String[] parts = received.split(":");
        if (parts.length !=3)
            ERROR("Improperly Formatted Request");

        String symbolicName = parts[0];
        String code = parts[1];
        String type = parts[2];
        String addr = String.valueOf(p.getAddress()).substring(1);

        if (type.equals("P"))
        {
            ModifyHashMap(lookupTable, symbolicName, code, addr);
        }
        else if (type.equals("SR")){
            ModifyHashMap(serverRouters, symbolicName, code, addr);
            //notify the server that sent the message of this SR's presence
            NotifyServerRouter(serverName, "Join", s, addr);
        }
        else {
            ERROR("Improperly formatted notify type");
        }
    }

    private void ModifyHashMap(ConcurrentHashMap<String,String> map, String symbolic, String code, String addr){
        if (code.equals("Join"))
        {
            map.put(symbolic, addr);
            PRINT(symbolic + ":" + addr + " was added to the lookup table");
        }
        else if (code.equals("Leave")){
            map.remove(symbolic);
            PRINT(symbolic + " was removed from the lookup table");
        }
        else {
            ERROR("Improperly formatted notify code");
        }
    }

    @Override
    protected int GetPort() {
        return 22222;
    }

    private void NotifyServerRouter(String name, String action, DatagramSocket sock, String addr){
        String message = name+ ":" + action + ":SR";
        byte[] response = message.getBytes();
        try {
            DatagramPacket out = new DatagramPacket(response, response.length, InetAddress.getByName(addr), 22222);
            sock.send(out);
        }
        catch (IOException e){
            ERROR("Error sending response");
        }
    }
}
