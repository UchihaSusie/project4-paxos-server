package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Interface defining the communication protocol for the distributed key-value store.
 * This interface extends Remote to support RMI (Remote Method Invocation) functionality.
 * It implements the Paxos consensus algorithm for distributed agreement.
 */
public interface SendMessage extends Remote {
    /**
     * Handles incoming client requests and initiates the Paxos consensus process.
     * This is the main entry point for client operations (PUT, GET, DELETE).
     *
     * @param message The client request message containing the operation and parameters
     * @return The response to the client request
     * @throws RemoteException If there is a communication error
     */
    String sendMessage(String message) throws RemoteException;

    /**
     * Paxos Prepare Phase: Proposer asks acceptors to promise not to accept proposals
     * with numbers less than the given proposal number.
     *
     * @param proposalNumber The unique identifier for this proposal
     * @return true if the server promises to not accept proposals with lower numbers
     * @throws RemoteException If there is a communication error
     */
    boolean prepare(int proposalNumber) throws RemoteException;

    /**
     * Paxos Accept Phase: Proposer asks acceptors to accept a proposal if they haven't
     * promised to not accept it.
     *
     * @param proposalNumber The unique identifier for this proposal
     * @param value The proposed value to be accepted
     * @return true if the proposal is accepted
     * @throws RemoteException If there is a communication error
     */
    boolean accept(int proposalNumber, String value) throws RemoteException;

    /**
     * Paxos Learn Phase: Once a value is chosen, it is broadcast to all servers
     * to ensure consistency across the distributed system.
     *
     * @param value The chosen value to be learned by all servers
     * @return The result of processing the learned value
     * @throws RemoteException If there is a communication error
     * @throws ServerNotActiveException If the server is not currently active
     */
    String learn(String value) throws RemoteException, ServerNotActiveException;
}
