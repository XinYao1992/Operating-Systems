package edu.Brandeis.cs131.Common.Abstract;

import edu.Brandeis.cs131.Common.Abstract.Log.EventType;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;

import java.util.*;

/**
 * An Client is a Runnable which uses servers. You must subclass Client to
 * customize its behavior (e.g., PremiumClient and BasicClient).
 *
 * When you start a thread which runs an Client, the Client will immediately
 * begin trying to use the server or servers passed into its constructor by
 * calling tryToJoin on each Server instance. As long as tryToJoin returns
 * false (indicating that the Client did not use that Server), the Client
 * will keep trying. This is called busy-waiting.
 *
 */
public abstract class Client implements Runnable {

    private final String name;
    private final Industry industry;
    private Collection<Server> servers;
    private final int speed;
    private int requestLevel;
    private Log log;
    /* DO NOT alter code to add or remove items to the log. The log is used to
     ensure rules are adhered to by the test cases. It does not, should not and
     * must not serve as a mechanism for communication between servers and 
     * clients.*/

    public Client(String name, Industry industry, int speed, int requestLevel, Log log) {
        this.name = name;
        this.industry = industry;
        this.servers = new ArrayList<Server>();
        this.speed = speed;
        this.requestLevel = requestLevel;
        this.log = log;
        
        if (this.speed < 0 || this.speed > 9) {
            throw new RuntimeException("Client has invalid speed");
        }
    }
    
    public Client(String name, Industry industry, int speed, int requestLevel) {
        this(name, industry, speed, requestLevel, Server.DEFAULT_LOG);
    }

    public final String getName() {
        return name;
    }

    public final Industry getIndustry() {
        return industry;
    }

    public final int getRequestLevel() {
        return requestLevel;
    }

    public final int getSpeed() {
        return speed;
    }

 
    @Override
    public String toString() {
        return String.format("%s CLIENT %s", this.industry, this.name);
    }

    public final void addServer(Server newServer) {
        this.servers.add(newServer);
    }

    public final void addServer(Collection<Server> newHill) {
        this.servers.addAll(newHill);
    }

    /**
     * Find and use one of the servers.
     *
     * When a thread is run, it keeps looping through its collection of
     * available servers until it succeeds in starting to use one of them.
     * Then, it will call doWhileAtServer (to simulate doing some work of
     * requesting, i.e., that it takes time to get a response), then leave that
     * server.
     * This process repeats till the client has no requests left to make.
     */
    public final void run() {
        while (this.requestLevel>0) {
            for (Server server : servers) {
                if (server.connect(this)) {
                    doWhileAtServer();
                    server.disconnect(this);
                    break;
                }
            }
        }
        this.log.addToLog(this, EventType.COMPLETE);
    }

    /**
     * This is what your Client does while inside the server to simulate taking
     * time to make a request. The faster your Client is, the less time this will
     * take.
     */
    public void doWhileAtServer() {
        try {
            Thread.sleep((10 - speed) * 100);
            this.requestLevel--;
        } catch (InterruptedException e) {
            System.err.println("Interrupted Client " + toString());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.industry);
        hash = 23 * hash + this.speed;
        hash = 23 * hash + this.requestLevel;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.industry != other.industry) {
            return false;
        }
        if (this.speed != other.speed) {
            return false;
        }
        if (this.requestLevel != other.requestLevel) {
            return false;
        }
        return true;
    }
    
    
}
