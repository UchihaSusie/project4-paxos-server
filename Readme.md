# Running on local machine with Docker Compose

Server will run on port 1099.  
Running command under "src" directory:

- `docker-compose up --build`

## Connect to other servers

In Docker compose file, it is default to connect to server1.  
If you want to connect to other servers, modify line 50 in `docker-compose.yml`

```
    command: ["java", "client.Client", "<modify at here>"]
```

There are 5 Server options: Server1, Server2, Server3, Server4, Server5.  
You can change `<modify at here>` to one of the options. Other input will get an error.

PS:
The Client will send message to the server, the message may fail if more than half of the servers stopped.

# Executive summary

## Assignment Overview

This project builds on my distributed key-value store from Project 3. In that project, I used Two- Phase Commit (2PC) to ensure consistency across five replicated servers. However, 2PC is not fault-tolerant. In this project, I replaced 2PC with the Paxos consensus algorithm to make the system more reliable, even when some servers fail.
The system includes five servers that work together using Paxos. Clients can send PUT, GET, and DELETE requests to any server. I implemented the three roles of Paxos: Proposer, Acceptor, and Learner. I also added random failure simulation for the acceptors to test fault tolerance. Each role runs as a separate thread, and the servers communicate using Java RMI. The goal was to build a fault-tolerant, multi-threaded, and distributed key-value store that can handle concurrent client requests and continue working even if some servers crash and restart.

## Technical Impression

1. Learning and Implementing Paxos
   The first challenge was understanding how Paxos works. The class materials explained the basic steps, but I still didn’t understand why it helps with fault tolerance. I searched online and found a website from Rutgers that gave more details. After writing the code and testing failures, I finally understood how Paxos could help all servers agree on a value even when some of them crash.

2. Simulating Server Failures and Recovery
   To simulate failure, I created a scheduler that randomly makes an acceptor stop responding for a few seconds. I used an isRunning flag to check if the server should accept requests. If it’s
   false, the server ignores messages. After a short delay, the acceptor restarts. This helped me test how the system handles failure and recovery. I also had to make sure the restarted thread

3. Thread Safety and Proposal Number Management
   Since this project used multiple threads, managing shared state was a big problem. I used ReentrantLock and volatileto avoid race conditions. Another issue was generating
   unique proposal numbers. I used timestamps with System.currentTimeMillis(), but I had to make sure no two threads created the same number at the same time. Debugging these problems was hard, so I added detailed logs to track every step in the Paxos process.

## Final Summary

Overall, this project helped me better understand distributed systems and fault tolerance. I learned how Paxos can keep the system running even if some servers fail. I also gained experience in writing multi-threaded code, handling server crashes, and debugging distributed systems. These skills will be useful in future projects that require strong consistency and reliability.
