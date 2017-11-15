package part2.ServerRouter;

import logWriter.LogWriter;

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;

public class UDPNotify extends UDPBaseThread {

    public UDPNotify(ConcurrentHashMap map, LogWriter writer) {
        super(map, writer);
    }

    @Override
    protected void PerformAction(DatagramPacket p) {
        String received = new String(p.getData(), 0, p.getLength());

        int separatorIndex = received.indexOf(":");
        if (separatorIndex == -1)
            ERROR("Improperly Formatted Request");

        String symbolicName = received.substring(0,separatorIndex);
        String code = received.substring(separatorIndex+1);

        if (code.equals("Join"))
        {
            String addr = String.valueOf(p.getAddress());
            lookupTable.put(symbolicName, addr);
            PRINT(symbolicName + ":" + addr + " was added to the lookup table");
        }
        else if (code.equals("Leave")){
            lookupTable.remove(symbolicName);
            PRINT(symbolicName + " was removed from the lookup table");
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
