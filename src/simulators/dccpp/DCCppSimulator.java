package simulators.dccpp;

import com.TcpServer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * LocoNet simulator
 * 
 * @author daniel
 */
public class DCCppSimulator {
    
    static final String SEND_PREFIX = "<";  // All commands starts with this
    
    private final TcpServer tcpServer;
    private ClientConnection clientConnection;
    
    public DCCppSimulator() throws IOException {
        this.tcpServer = new TcpServer(2560, ClientConnection::new);
    }
    
//    public void setVoltage(double voltage) {
//        clientConnection.voltage = voltage;
//    }
    
    public void setCurrent(int current) {
        clientConnection.current = current;
    }
    
    
    private class ClientConnection implements Runnable {
        
        private final Socket clientSocket;
        private DataInputStream istream = null;
        private BufferedReader istreamReader = null;
//        private OutputStream ostream = null;
//        private final BufferedReader in;
        private final PrintStream out;
//        private double voltage = 0.0;
        private int current = 0;
        
        private ClientConnection(Socket socket) throws IOException {
            this.clientSocket = socket;
            
            System.out.println("DCCpp: Creating input  stream reader for " + socket.getRemoteSocketAddress() );
            istream = new DataInputStream(socket.getInputStream());
            istreamReader = new BufferedReader(new InputStreamReader(istream));
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
            System.out.println("DCCpp: Creating output stream writer for " + socket.getRemoteSocketAddress() );
//            ostream = new DataOutputStream(socket.getOutputStream());
            out = new PrintStream(socket.getOutputStream(),true, "UTF8");
            
//            out.println("VERSION JMRI_ConnSimulator version 0.1");
            
            DCCppSimulator.this.clientConnection = this;
            
            Thread thread = new Thread(this);
            thread.start();
        }
        
        private void handleCommand(String command) {
            switch (command) {
                case "s":   // Read CS status
                    break;
                case "#":   // Read max slots
                    break;
                case "S":   // Sensor command
                    break;
                case "c":   // Read track current
                    out.format("<a%d>", current);
                    out.flush();
                    break;
                default:
                    System.out.format("Unknown DCCpp command: %s%n", command);
            }
        }
        
        @Override
        public void run() {
            String line;
            
            StringBuilder sb = new StringBuilder();
            
            try {
                while (true) {
                    int b = istream.readByte();
//                    System.out.format("Read byte: %d, %c%n", b, b);
                    sb.append(Character.toChars(b));
//                    System.out.format("Buffer: %s%n", sb.toString());
                    
                    if ((sb.length() == 1) && (sb.charAt(0) != '<')) {
                        System.out.format("Error. First char is not <%n");
                        sb.deleteCharAt(0);
                    }
                    
                    if (sb.charAt(sb.length()-1) == '>') {
                        String command = sb.substring(1, sb.length()-1);
                        System.out.format("Command received: %s%n", command);
                        handleCommand(command);
                        sb.delete(0, sb.length());
                    }
                }
                
/*                
                while ((in != null) && ((line = in.readLine()) != null)) {
                    System.out.format("DCCpp: Input: %s%n", line);
                    
                    if (line.startsWith("SEND ")) {
//                        String command = line.substring("SEND ".length());
                        String command = line.trim();
                        if (command.equals("SEND BB 79 41 7C")) {
                            int[] data = {
                                0xE6,   // Byte 0   // OPC_EXP_RD_SL_DATA
                                0x15,   // Byte 1   // Num bytes = 21 bytes
                                0x00,   // Byte 2   // Slot number = 249
//                                0xF9,   // Byte 2   // Slot number = 249
                                0x00,   // Byte 3
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
                            System.out.println("DCCpp: RECEIVE "+sequence);
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
*/
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
