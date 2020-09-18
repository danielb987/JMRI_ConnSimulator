package simulators.cbus;

import com.TcpServer;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * LocoNet simulator
 * 
 * @author daniel
 */
public class CbusSimulator {
    
    private final TcpServer tcpServer;
    private ClientConnection clientConnection;
    
    public CbusSimulator() throws IOException {
        this.tcpServer = new TcpServer(5550, ClientConnection::new);
    }
    
    public void setVoltage(int voltage) {
        if (clientConnection != null) {
            clientConnection.out.format(":SBFE0ND0FFFE0002%04X;%n", voltage);
        }
    }
    
    public void setCurrent(int current) {
        if (clientConnection != null) {
            clientConnection.out.format(":SBFE0ND0FFFE0001%04X;%n", current);
        }
    }
    
    
    private class ClientConnection {
        
        private final PrintStream out;
        
        private ClientConnection(Socket socket) throws IOException {
            System.out.println("Cbus: Creating output stream writer for " + socket.getRemoteSocketAddress() );
            out = new PrintStream(socket.getOutputStream(),true, "UTF8");
            
            CbusSimulator.this.clientConnection = this;
        }
    }
    
}
