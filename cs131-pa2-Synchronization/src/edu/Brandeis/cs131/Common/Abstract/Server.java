package edu.Brandeis.cs131.Common.Abstract;

import edu.Brandeis.cs131.Common.Abstract.Log.EventType;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;
import java.util.Objects;

/**
 * An Server is an object which can be used by clients. Clients themselves
 * are responsible for indicating when they want to use a server and when they are
 * ready to leave. Servers are responsible for indicating if it is safe for an
 * Client to enter.
 *
 * When an client wants to enter a server, it calls connect on the server
 * instance. If the client has entered the server successfully, connect
 * returns true. Otherwise, connect returns false. The client simulates the
 * time spent at the server, and then must call disconnect on the same server
 * instance it entered.
 */
public abstract class Server {

    private final String name;
    public static Log DEFAULT_LOG = new Log();
    private final Log log;
    /* DO NOT alter code to add or remove items to the log. The log is used to
     ensure rules are adhered to by the test cases. It does not, should not and
     * must not serve as a mechanism for communication between servers and 
     * clients.*/

    public Server(String name, Log log) {
        this.name = name;
        this.log = log;
    }

    public Server(String name) {
        this(name, Server.DEFAULT_LOG);
    }

    public final boolean connect(Client client) {
        //Do not overwrite this function, you should be overwriting connectInner
        int sig = log.nextLogEventNumber();
        log.addToLog(client, this, EventType.ENTER_ATTEMPT, sig);
        if (this.connectInner(client)) {
            log.addToLog(client, this, EventType.ENTER_SUCCESS, sig);
            return true;
        } else {
            log.addToLog(client, this, EventType.ENTER_FAILED, sig);
            return false;
        }
    }

    /**
     * client tries to make a request to a server.
     *
     * @param client, The client that is attempting to use the server
     * @return true if the client was able to start requesting from the server, false
     * otherwise
     */
    public abstract boolean connectInner(Client client);

    public final void disconnect(Client client) {
        //Do not overwrite this function, you should be overwriting disconnectInner
        int sig = log.nextLogEventNumber();
        this.log.addToLog(client, this, EventType.LEAVE_START, sig);
        this.disconnectInner(client);
        this.log.addToLog(client, this, EventType.LEAVE_END, sig);
    }

    /**
     * client exits the server.
     *
     * @param client, The client that is leaving the server
     */
    public abstract void disconnectInner(Client client);
    
    public Log getLog() {
        return log;
    }

    /**
     * Returns the name of this server
     *
     * @return The name of this server
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s", this.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
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
        final Server other = (Server) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
}
