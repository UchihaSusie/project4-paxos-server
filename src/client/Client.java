package client;

public class Client {
    // Default RMI service port number
    private static final int PORT = 1099;
    public static void main(String[] args) throws InterruptedException {
        // The argument should include server name
        // should only take Server1 Server2 Server3 Server4 Server5
        if (args.length != 1 || !args[0].matches("Server[1-5]")) {
            ClientLogger.logWarning("IllegalArgumentException: " +
                    "Parameter(s): <server-name>; " +
                    "should only take Server1 Server2 Server3 Server4 Server5");
            System.exit(1);
        }

        // get the server name from arguments
        String servername = args[0];
        // Create a client instance to connect to the rmi server and port
        TCPClient client = new TCPClient(servername, PORT);
        Thread.sleep(2000);
        client.send("GET 1");
        client.send("GET 3");
        client.send("PUT 6 6");
        client.send("GET 6");
        client.send("PUT 6 7");
        Thread.sleep(4000);
        client.send("GET 6");
        client.send("DELETE 6");
        client.send("GET 6");
        Thread.sleep(4000);

        client.send("GET Good");
        client.send("PUT Good Bye");
        Thread.sleep(4000);

        client.send("GET Good");
        client.send("DELETE 7");
        client.send("PUT 7 7");
        Thread.sleep(4000);

        client.send("PUT 7 Happy");
        client.send("DELETE 6");
        Thread.sleep(4000);

        client.send("GET 5");
        client.send("DELETE 5");
        client.send("DELETE 4");

    }

}
