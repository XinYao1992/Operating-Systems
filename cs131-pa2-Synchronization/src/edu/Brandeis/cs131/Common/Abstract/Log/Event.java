package edu.Brandeis.cs131.Common.Abstract.Log;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Server;

public class Event {

    private final Client client;
    private final Server server;
    private final EventType event;
    private final int signifier;
    //there is no guarantee the signifier is 100% unique
    //but given the size of our tests vs the possible values for the signifier we forsee little clashing

    public Event(Client client, Server server, EventType event, int signifier) {
        this.client = client;
        this.server = server;
        this.event = event;
        this.signifier = signifier;
    }

    public Event(Client client, Server server, EventType event) {
        this(client, server, event, (int) System.currentTimeMillis());
    }

    public Event(Client client, EventType event) {
        this(client, null, event);
    }

    public Event(EventType event) {
        this(null, null, event);
    }

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }

    public EventType getEvent() {
        return event;
    }
    
    public int getSignifier() {
        return signifier;
    }

    @Override
    public String toString() {
        switch (event) {
            case END_TEST:
            case ERROR:
                return event.toString();
            case COMPLETE:
                return String.format("%s %s", client, event);
            default:
                return String.format("%s %s %s", client, event, server);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            Event event = (Event) o;
            return event.getSignifier() == this.signifier && this.weakEquals(event);
        } else {
            return false;
        }

    }

    public boolean weakEquals(Event event) {
        //A weaker version of equals,  checks the server, client, and event type are the same.
        //Useful for checking if an event was logged, when the exact logging details are unknown.
        return this.toString().equals(event.toString());
    }
}
