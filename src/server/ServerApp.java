package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * ServerApp is the entry point for starting the RMI server in the distributed key-value store system.
 * This class handles:
 * 1. Initialization of the RMI registry
 * 2. Binding the server's message handling service
 * 3. Establishing connections with other servers in the distributed system
 * 
 * The server supports a cluster of up to 5 servers (Server1 through Server5),
 * each running on the default RMI port 1099.
 */
public class ServerApp {
    /** Default RMI port number used for server communication */
    private static final int PORT = 1099;

    /**
     * Main entry point for starting the server application.
     * 
     * The method performs the following steps:
     * 1. Creates a new TCPHandler instance for message processing
     * 2. Initializes the RMI registry on the default port
     * 3. Binds the server's SendMessage service to the registry
     * 4. Validates the server name argument
     * 5. Establishes connections with other servers in the cluster
     *
     * @param args Command line arguments:
     *             - args[0]: Server name (must be in format "Server[1-5]")
     * @throws Exception If any error occurs during server initialization or connection setup
     */
    public static void main(String[] args) {
        try {
            // Initialize the server's handler for incoming messages
            TCPHandler server = new TCPHandler();
            // Create an RMI registry on the default port 1099
            Registry registry = LocateRegistry.createRegistry(PORT);
            // Bind the server's SendMessage service to the RMI registry
            registry.rebind("SendMessage", server);

            // Validate server name argument
            if (args.length != 1 || !args[0].matches("Server[1-5]")) {
                ServerLogger.logWarning("Error: Please provide a valid server name (Server1-Server5).");
                System.exit(1);
            }

            // Connect to other servers in the distributed system
            for (int i = 1; i <= 5; i++) {
                // Skip connecting to itself based on the server name in the arguments
                if (!args[0].equals("Server" + i)) {
                    // Look up other servers using RMI naming service
                    SendMessage otherServer = (SendMessage) Naming.lookup("rmi://Server" + i + ":" + PORT + "/SendMessage");
                    // Add the other server to the current server's list for distributed communication
                    server.addServer(otherServer);
                }
            }

        } catch (Exception e) {
            ServerLogger.logError("Server creation error: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
