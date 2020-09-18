package simulators.loconet;

import com.TcpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * LocoNet simulator
 * 
 * @author daniel
 */
public class LocoNetSimulator {
    
    private ClientConnection clientConnection;
    
    public LocoNetSimulator() throws IOException {
        new TcpServer(1234, ClientConnection::new);
    }
    
    public void setVoltage(double voltage) {
        if (clientConnection != null) {
            clientConnection.voltage = voltage;
        }
    }
    
    public void setCurrent(double current) {
        if (clientConnection != null) {
            clientConnection.current = current;
        }
    }
    
    
    private class ClientConnection implements Runnable {
        
        private final BufferedReader in;
        private final PrintStream out;
        private double voltage = 0.0;
        private double current = 0.0;
        
        private ClientConnection(Socket socket) throws IOException {
            System.out.println("LocoNet: Creating input  stream reader for " + socket.getRemoteSocketAddress() );
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
            System.out.println("LocoNet: Creating output stream writer for " + socket.getRemoteSocketAddress() );
            out = new PrintStream(socket.getOutputStream(),true, "UTF8");
            
            out.println("VERSION JMRI_ConnSimulator version 0.1");
            
            LocoNetSimulator.this.clientConnection = this;
            
            Thread thread = new Thread(this);
            thread.start();
        }
        
        @Override
        public void run() {
            String line;
            
            try {
                while ((in != null) && ((line = in.readLine()) != null)) {
                    System.out.format("LocoNet: Input: %s%n", line);
                    
                    if (line.startsWith("SEND ")) {
//                        String command = line.substring("SEND ".length());
                        String command = line.trim();
                        if (command.equals("SEND BB 79 41 7C")) {
                            int[] data = {
                                0xE6,   // Byte 0   // OPC_EXP_RD_SL_DATA
                                0x15,   // Byte 1   // Num bytes = 21 bytes
                                0x01,   // Byte 2   // Slot number = 249
                                0x79,   // Byte 3   // Slot number = 249
                                (int)Math.round(voltage),   // Byte 4   voltage
                                0x00,   // Byte 5
                                (int)Math.round(current),   // Byte 4   voltage
                                0x00,   // Byte 7   max current
                                0x00,   // Byte 8
                                0x00,   // Byte 9
                                0x00,   // Byte 10
                                0x00,   // Byte 11
                                0x00,   // Byte 12
                                0x00,   // Byte 13
                                0x00,   // Byte 14
                                0x00,   // Byte 15
                                0x34,   // Byte 16
                                0x00,   // Byte 17
                                0x00,   // Byte 18
                                0x00,   // Byte 19
                                0x00,   // Byte 20
                            };
                            if (data.length != 21) throw new RuntimeException("Invalid length of data");
                            
                            int checksum = 0xFF;
//                            int checksum = 0x00;
                            for (int b : data) checksum = checksum ^ b;
                            data[20] = checksum;

                            String sequence = "";
                            for (int b : data) sequence += String.format(" %02X", b);
                            out.println("RECEIVE "+sequence);
                            System.out.println("LocoNet: RECEIVE "+sequence);
                        } else {
//                            out.println("RECEIVE "+sequence);
                        }
                        out.println("SENT OK");
                    }
                    
//                    long time = (System.currentTimeMillis() - startTime) / 1000;
//                    Server.get().debug("<--" + line);
//                    System.out.print(time);
//                    System.out.print(": Mobil --> Dator: ");
//                    System.out.print("<--");
//                    System.out.println(line);
//                    client.send(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
