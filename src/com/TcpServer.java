package com;

// import common.StringUtils;
import common.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP server
 * @author daniel
 */
public class TcpServer implements Runnable {
    
    private final int _tcpPort;
    private final ServerSocket serverSocket;
    private final CreateConnection createConnection;
	private volatile PrintWriter debugWriter;	// Use volatile to make sure the close() method does what it is intended to do
    
    public TcpServer(int tcpPort, CreateConnection createConnection) throws IOException {
        this._tcpPort = tcpPort;
        this.createConnection = createConnection;
        this.serverSocket = new ServerSocket(_tcpPort);
        
		Path path = Paths.get("debug.txt");
        debugWriter = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));
		
		new Thread(this).start();
    }
    
	public void debug(String line) {
		debugWriter.println(line);
		debugWriter.flush();
	}
	
	
	@Override
	public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("Client connected.");
                createConnection.create(clientSocket);
            } catch (IOException e) {
                System.out.println(StringUtils.getStackTrace(e));
            }
            
//            connectionList.add(connection);
//            Thread thread = new Thread(connection);
//            thread.start();
        }
	}
    
    
    public interface CreateConnection {
        
        public void create(Socket clientSocket) throws IOException;
    }
    
}
