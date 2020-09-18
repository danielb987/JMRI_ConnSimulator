package simulators.dccpp;

import com.TcpServer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * LocoNet simulator
 * 
 * @author daniel
 */
public class DCCppSimulator {
    
    static final String SEND_PREFIX = "<";  // All commands starts with this
    
    private ClientConnection clientConnection;
    
    public DCCppSimulator() throws IOException {
        new TcpServer(2560, ClientConnection::new);
    }
    
    public void setCurrent(int current) {
        if (clientConnection != null) {
            clientConnection.current = current;
        }
    }
    
    
    private class ClientConnection implements Runnable {
        
        private DataInputStream istream = null;
        private final PrintStream out;
        private int current = 0;
        
        private ClientConnection(Socket socket) throws IOException {
            System.out.println("DCCpp: Creating input  stream reader for " + socket.getRemoteSocketAddress() );
            istream = new DataInputStream(socket.getInputStream());
            System.out.println("DCCpp: Creating output stream writer for " + socket.getRemoteSocketAddress() );
            out = new PrintStream(socket.getOutputStream(),true, "UTF8");
            
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
            StringBuilder sb = new StringBuilder();
            
            try {
                while (true) {
                    int b = istream.readByte();
                    sb.append(Character.toChars(b));
                    
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
