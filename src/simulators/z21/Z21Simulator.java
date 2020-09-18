package simulators.z21;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * LocoNet simulator
 * 
 * @author daniel
 */
public class Z21Simulator {
    
    private final Z21_Server z21Server;
    private InetAddress addr;
    private int udpPort;
    private int voltage = 0;
    private int current = 0;
    
    public Z21Simulator() throws IOException {
        z21Server = new Z21_Server(21105);
    }
    
    public void setVoltage(int voltage) {
        this.voltage = voltage;
        sendPacket();
    }
    
    public void setCurrent(int current) {
        this.current = current;
        sendPacket();
    }
    
    public void sendPacket() {
        if (addr == null) return;
        try {
            byte[] buf = new byte[20];
            buf[0] = 0x14;  // 20 bytes
            buf[1] = 0x00;  // 20 bytes
            buf[2] = (byte) 0x84;  // LAN_SYSTEMSTATE_DATACHANGED
            buf[3] = 0x00;  // LAN_SYSTEMSTATE_DATACHANGED
            buf[4] = (byte)(current & 0xFF);    // Track current
            buf[5] = (byte)(current >> 8);      // Track current
            buf[6] = 0x00;  // Prog current
            buf[7] = 0x00;  // Prog current
            buf[8] = 0x00;  // Filtered main current
            buf[9] = 0x00;  // Filtered main current
            buf[10] = 0x00; // Temperature
            buf[11] = 0x00; // Temperature
            buf[12] = 0x00; // SupplyVoltage
            buf[13] = 0x00; // SupplyVoltage
            buf[14] = (byte)(voltage & 0xFF);   // Track voltage
            buf[15] = (byte)(voltage >> 8);     // Track voltage
            buf[16] = 0x00; // Central state
            buf[17] = 0x00; // Central state
            buf[18] = 0x00; // Central state ex
            buf[19] = 0x00; // Central state ex
            DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, udpPort);
            System.out.format("Send packet: ");
            for (int i=0; i < packet.getLength(); i++) {
                System.out.format("%02X ", buf[i]);
            }
            System.out.format("%n");
            z21Server.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private class Z21_Server implements Runnable {
        
        private final DatagramSocket socket;
        
        private final byte[] buf = new byte[256];
        
        
        
//        private final BufferedReader in;
//        private final PrintStream out;
        private double voltage = 0.0;
        private double current = 0.0;
        
        private Z21_Server(int udpPort) throws IOException {
            socket = new DatagramSocket(udpPort);
            
/*            
            System.out.println("LocoNet: Creating input  stream reader for " + socket.getRemoteSocketAddress() );
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
            System.out.println("LocoNet: Creating output stream writer for " + socket.getRemoteSocketAddress() );
            out = new PrintStream(socket.getOutputStream(),true, "UTF8");
            
            out.println("VERSION JMRI_ConnSimulator version 0.1");
            
            Z21Simulator.this.z21Server = this;
*/            
            Thread thread = new Thread(this);
            thread.start();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.format("Wait on Z21 packet%n");
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    System.out.format("Z21 packet received%n");

                    addr = packet.getAddress();
                    udpPort = packet.getPort();
                    for (int i=0; i < packet.getLength(); i++) {
                        System.out.format("%02X ", buf[i]);
                    }
                    System.out.format("%n");
                    
//                    packet = new DatagramPacket(buf, buf.length, address, port);
//                    String received = new String(packet.getData(), 0, packet.getLength());
//                    socket.send(packet);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
