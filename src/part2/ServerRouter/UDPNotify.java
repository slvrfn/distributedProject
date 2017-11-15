package part2.ServerRouter;

import logWriter.LogWriter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class UDPNotify extends UDPBaseThread {

    public UDPNotify(ConcurrentHashMap<String,String> SRs, ConcurrentHashMap map, LogWriter writer) {
        super(SRs, map, writer);
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
        String addr = String.valueOf(p.getAddress());

        if (type.equals("P"))
        {
            ModifyHashMap(lookupTable, symbolicName, code, addr);
        }
        else if (type.equals("SR")){
            ModifyHashMap(serverRouters, symbolicName, code, addr);
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
}
