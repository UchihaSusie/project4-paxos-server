package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TCPHandler implements the core functionality of the distributed key-value store server.
 * This class:
 * 1. Implements the Paxos consensus algorithm for distributed agreement
 * 2. Manages the key-value store operations
 * 3. Handles server-to-server communication
 * 4. Implements failure simulation for testing purposes
 */
public class TCPHandler extends UnicastRemoteObject implements SendMessage {

    /**
     * The highest proposal number that this server has promised to accept.
     * In Paxos, this ensures that servers don't accept proposals with lower numbers
     * than what they've already promised to accept.
     */
    private int promisedProposalNumber = -1;

    /**
     * The proposal number of the last accepted proposal.
     * This helps in maintaining consistency across the distributed system
     * by tracking which proposal was actually accepted.
     */
    private int acceptedProposalNumber = -1;

    /**
     * The value of the last accepted proposal.
     * This stores the actual data that was agreed upon in the Paxos protocol.
     */
    private String acceptedValue = null;

    /**
     * The value that has been learned and committed by this server.
     * This represents the final agreed-upon value that can be safely
     * used by the application layer.
     */
    private String learnedValue = null;

    /** Lock for thread-safe operations on the key-value store */
    private final ReentrantLock lock = new ReentrantLock();

    /** The underlying key-value store implementation */
    private final KeyValue keyValueStore;

    /** List of other servers in the distributed system */
    private List<SendMessage> otherServers;

    /** Flag indicating if the server is currently running */
    private volatile boolean isRunning = true;

    /** Scheduler for simulating server failures */
    private ScheduledExecutorService scheduler;

    /**
     * Constructor initializes the key-value store, server list, and failure simulation.
     * Sets up a scheduled task to randomly simulate server failures.
     *
     * @throws RemoteException if RMI-related errors occur
     */
    protected TCPHandler() throws RemoteException {
        super();
        this.keyValueStore = new KeyValue();
        this.otherServers = new ArrayList<>();

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (Math.random() < 0.3) {
                simulateFailure();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Simulates a server failure by:
     * 1. Setting the server to non-running state
     * 2. Sleeping for a random duration
     * 3. Restoring the server to running state
     */
    private void simulateFailure() {
        lock.lock();
        try {
            isRunning = false;
            System.out.println("Server is failed");
            Thread.sleep((int) (Math.random() * 5000 + 3000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            isRunning = true;
            System.out.println("Server has been restarted.");
            lock.unlock();
        }
    }

    /**
     * Adds a reference to another server in the distributed system.
     *
     * @param server the server to add to the list of known servers
     */
    public void addServer(SendMessage server) {
        otherServers.add(server);
    }

    /**
     * Handles incoming client requests by initiating the Paxos consensus process.
     *
     * @param message the client request message
     * @return the result of the operation
     * @throws RemoteException if RMI-related errors occur
     */
    @Override
    public String sendMessage(String message) throws RemoteException {
        try {
            return propose(message);
        } catch (ServerNotActiveException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Implements the Paxos proposer role by:
     * 1. Generating a unique proposal number
     * 2. Conducting the prepare phase
     * 3. Conducting the accept phase
     * 4. Broadcasting the learned value
     *
     * @param value the value to be proposed
     * @return the result of the consensus process
     * @throws RemoteException if RMI-related errors occur
     * @throws ServerNotActiveException if the server is not active
     */
    public String propose(String value) throws RemoteException, ServerNotActiveException {
        String str[] = value.split(" ");
        Response fail = new Response(str[0],"FAIL","Proposal failed: no majority");
        int proposalNumber = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        List<SendMessage> agreeServer = new ArrayList<>();
        boolean majority = false;

        // Prepare Phase: Collect promises from acceptors
        for (SendMessage server : otherServers) {
            try {
                if (server.prepare(proposalNumber)) {
                    agreeServer.add(server);
                }
            } catch (RemoteException ignored) {
            } catch (Exception e) {
                ServerLogger.logError("Prepare failed on server", e);
            }
        }
        try {
            if (this.prepare(proposalNumber))
                agreeServer.add(this);
        } catch (RemoteException ignored) {
        } catch (Exception e) {
            ServerLogger.logError("Prepare failed on server", e);
        }

        majority = agreeServer.size() > otherServers.size() / 2;

        if (!majority) {
            return fail.toString();
        }

        // Accept Phase: Get acceptances from servers that promised
        for (SendMessage server : agreeServer) {
            try {
                if (!server.accept(proposalNumber, value)) {
                    return fail.toString();
                }
            } catch (Exception e) {
                ServerLogger.logWarning("Accept failed on server");
            }
        }

        // Learn Phase: Broadcast the chosen value to all servers
        for (SendMessage server : otherServers) {
            try {
                server.learn(value);
            } catch (Exception e) {
                ServerLogger.logError("Learn failed on server: " + server, e);
            }
        }

        return this.learn(value);
    }

    /**
     * Implements the Paxos prepare phase for acceptors.
     * Promises not to accept proposals with numbers less than the given proposal number.
     *
     * @param proposalNumber the proposal number to check
     * @return true if the server promises to not accept lower-numbered proposals
     * @throws RemoteException if the server is not running
     */
    @Override
    public synchronized boolean prepare(int proposalNumber) throws RemoteException {
        if (!isRunning) {
            throw new RemoteException("Server is currently down.");
        }

        if (proposalNumber > promisedProposalNumber) {
            promisedProposalNumber = proposalNumber;
            return true;
        }
        return false;
    }

    /**
     * Implements the Paxos accept phase for acceptors.
     * Accepts the proposal if the server hasn't promised to not accept it.
     *
     * @param proposalNumber the proposal number
     * @param value the proposed value
     * @return true if the proposal is accepted
     * @throws RemoteException if the server is not running
     */
    @Override
    public synchronized boolean accept(int proposalNumber, String value) throws RemoteException {
        if (!isRunning) {
            throw new RemoteException("Server is currently down.");
        }

        if (proposalNumber >= promisedProposalNumber) {
            promisedProposalNumber = proposalNumber;
            acceptedProposalNumber = proposalNumber;
            acceptedValue = value;
            return true;
        }
        return false;
    }

    /**
     * Implements the Paxos learn phase.
     * Processes the learned value and applies it to the key-value store.
     *
     * @param value the value to be learned
     * @return the result of processing the learned value
     * @throws RemoteException if RMI-related errors occur
     */
    @Override
    public synchronized String learn(String value) throws RemoteException {
        lock.lock();
        try {
            learnedValue = value;
            return handleRequest(value, RemoteServer.getClientHost());
        } catch (ServerNotActiveException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Processes the client request by:
     * 1. Logging the received message
     * 2. Handling the message through the key-value store
     * 3. Returning the appropriate response
     *
     * @param message the client request message
     * @param clientHost the client's host address
     * @return the result of the operation
     */
    private String handleRequest(String message, String clientHost) {
        Response res;
        ServerLogger.logInfo("Received from client: " + message, clientHost, Thread.currentThread().threadId());
        res = handleMessage(message, clientHost);

        if (res != null) {
            return res.toString();
        } else {
            return null;
        }
    }

    /**
     * Handles specific operations (PUT, GET, DELETE) on the key-value store.
     * Validates the request format and delegates to the appropriate key-value store operation.
     *
     * @param message the client request message
     * @param clientHost the client's host address
     * @return a Response object containing the operation result
     */
    private Response handleMessage(String message, String clientHost) {
        Response res = null;
        String[] parts = message.split(" ");
        switch (parts[0]) {
            case "PUT":
                if(parts.length != 3){
                    ServerLogger.logWarning("received PUT request, incorrect number of arguments", clientHost);
                    return null;
                } else {
                    res = keyValueStore.put(parts[1], parts[2]);
                }
                break;
            case "GET":
                if(parts.length != 2){
                    ServerLogger.logWarning("received GET request, incorrect number of arguments", clientHost);
                    return null;
                } else {
                    res = keyValueStore.get(parts[1]);
                }
                break;
            case "DELETE":
                if(parts.length != 2){
                    ServerLogger.logWarning("received DELETE request, incorrect number of arguments", clientHost);
                    return null;
                } else {
                    res = keyValueStore.delete(parts[1]);
                }
                break;
            default:
                ServerLogger.logWarning("received malformed request of length ", clientHost);
                break;
        }
        return res;
    }
}

