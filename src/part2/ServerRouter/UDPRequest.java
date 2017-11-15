package part2.ServerRouter;

import logWriter.LogWriter;

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;

public class UDPRequest extends UDPBaseThread {

    public UDPRequest(ConcurrentHashMap map, LogWriter writer) {
        super(map, writer);
    }

    @Override
    protected void PerformAction(DatagramPacket p) {


        LookupThread t = new LookupThread(p, lookupTable);
        t.start();
        //necessary so any created threads can be cleaned up
        RegisterThread(t);
    }

    @Override
    protected int GetPort() {
        return 11111;
    }

    public class LookupThread extends Thread{

        ConcurrentHashMap<String, String> _lookupTable;
        String SymbolicName = null;
        DatagramPacket packet;

        public LookupThread(DatagramPacket p, ConcurrentHashMap<String, String> m) {
            packet = p;
            _lookupTable = m;
            String received = new String(p.getData(), 0, p.getLength());
            SymbolicName = received;
        }

        public void run() {
            if (_lookupTable.containsKey(SymbolicName)){
                //return address to requester
                RespondToRequester(_lookupTable.get(SymbolicName));
            }
            else {
                //start tcp conn
            }
        }

        private void RespondToRequester(String m){
            byte[] response = m.getBytes();
            DatagramPacket out = new DatagramPacket(response, response.length, packet.getAddress(), 1234);
        }
    }
}


