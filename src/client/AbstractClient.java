package client;

/**
 * AbstractClient provides a base class for clients in the system.
 * It includes common properties such as hostname and port, as well as
 * a method for processing and validating server responses.
 */
public abstract class AbstractClient {
    // server information for logging
    protected String hostname;
    // port information for logging
    protected int port;

    /**
     * Constructor to initialize the AbstractClient with hostname and port.
     *
     * @param hostname the server's hostname or IP address
     * @param port     the server's port number
     */
    public AbstractClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Getter for the hostname.
     *
     * @return the hostname of the server
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Getter for the port number.
     *
     * @return the port number of the server
     */
    public int getPort() {
        return port;
    }

    /**
     * Validates and logs the server's response based on the message sent.
     *
     * @param response the response received from the server
     * @param msg      the original message sent to the server, split into parts
     */
    public void responseCheck(String response, String[] msg) {
        if(response != null){
            // Split the response into parts to analyze its content
            String[] parts = response.split(" ", 3);
            // Check if the response matches the original message's operation
            if(parts[0].equals(msg[0])){
                if (parts[1].equals("FAIL")) {
                    ClientLogger.logWarning("Received from "+ hostname +":"+port + " : Status: " + parts[1]
                            + ". Message: "+parts[2]);
                }else{
                    ClientLogger.logInfo("Received from "+ hostname +":"+port + " : Status: " + parts[1]
                            + ". Message: "+parts[2]);

                }
            }else{
                ClientLogger.logWarning("Received unsolicited response " +
                        "acknowledging unknown packet");
            }
        }
    }
}
