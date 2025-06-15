package client;
import server.SendMessage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * TCPClient class handles client-server communication via RMI.
 * It extends AbstractClient and connects to the specified server to send messages.
 */
public class TCPClient extends AbstractClient{
    // Remote server object implementing SendMessage interface
    private SendMessage server;

    /**
     * Constructor for TCPClient with the server name and port.
     *
     * @param servername the name or IP address of the target server
     * @param port       the RMI registry port
     */
    public TCPClient(String servername, int port) {
        // Call the AbstractClient constructor to set up common client properties
        super(servername,port);
        try {
            // connect to server
            Registry registry = LocateRegistry.getRegistry(servername, port);
            server = (SendMessage) registry.lookup("SendMessage");
        } catch (Exception e) {
            ClientLogger.logError("Failed to connect to RMI "+servername+": " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server and retrieves the server's response.
     *
     * @param message the message to be sent to the server
     * @return the response from the server
     */
    public String send(String message) {
        // Split the message into parts for message protocol
        // msg for save the message operation such as PUT GET DELETE
        String[] msg = message.split(" ");
        String response = null;
        try {
            // get the response from server
            response = server.sendMessage(message);
            ClientLogger.logInfo("Message sent: " + message);
        } catch (Exception  e) {
            ClientLogger.logError("TCPClient exception: " + e.getMessage(), e);
        }
        // protocol to check msg
        responseCheck(response, msg);

        return response;
    }

}
