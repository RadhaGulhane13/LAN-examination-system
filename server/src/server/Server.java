
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main server class for the LAN Examination System.
 * Handles client connections and manages the examination process.
 * 
 * @author RADHA
 */
public class Server {
    
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int DEFAULT_PORT = 5555;
    
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        
        try {
            int port = DEFAULT_PORT;
            serverSocket = new ServerSocket(port);
            LOGGER.info("LAN Examination Server started on port " + port);
            
            // Keep accepting client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("New client connected from: " + clientSocket.getRemoteSocketAddress());
                
                // Handle client in a separate thread to avoid blocking
                new Thread(() -> handleClient(clientSocket)).start();
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server error: " + e.getMessage(), e);
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    LOGGER.info("Server socket closed");
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing server socket: " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Handles individual client connection and authentication.
     * 
     * @param clientSocket the connected client socket
     */
    private static void handleClient(Socket clientSocket) {
        DataInputStream in = null;
        DataOutputStream out = null;
        Database db = null;
        
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            
            // Read authentication credentials
            String username = in.readUTF();
            String password = in.readUTF();
            
            LOGGER.info("Authentication attempt for username: " + username);
            
            db = new Database();
            int userId = db.isvaliduser(username, password);
            
            if (userId != 0) {
                LOGGER.info("Authentication successful for user: " + username + " (ID: " + userId + ")");
                out.writeUTF("START");
                out.flush();
                
                // Create dedicated worker thread for this client
                ServerWorker worker = new ServerWorker(clientSocket, db, userId);
                worker.start();
                
                // Worker thread takes ownership of resources
                in = null;
                out = null;
                db = null;
                clientSocket = null;
                
            } else {
                LOGGER.warning("Authentication failed for user: " + username);
                out.writeUTF("ABORT");
                out.flush();
            }
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database driver not found: " + e.getMessage(), e);
            sendErrorResponse(out, "ABORT");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during authentication: " + e.getMessage(), e);
            sendErrorResponse(out, "ABORT");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Client communication error: " + e.getMessage(), e);
        } finally {
            // Clean up resources if they weren't transferred to worker
            closeResources(in, out, clientSocket, db);
        }
    }
    
    /**
     * Safely sends an error response to the client.
     * 
     * @param out the output stream
     * @param message the error message to send
     */
    private static void sendErrorResponse(DataOutputStream out, String message) {
        if (out != null) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to send error response: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Safely closes all resources.
     * 
     * @param in input stream
     * @param out output stream  
     * @param socket client socket
     * @param db database connection
     */
    private static void closeResources(DataInputStream in, DataOutputStream out, Socket socket, Database db) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing input stream: " + e.getMessage(), e);
            }
        }
        
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing output stream: " + e.getMessage(), e);
            }
        }
        
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing client socket: " + e.getMessage(), e);
            }
        }
        
        if (db != null) {
            db.close();
        }
    }
}
