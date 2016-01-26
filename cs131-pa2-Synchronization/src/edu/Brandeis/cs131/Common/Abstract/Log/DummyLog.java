package edu.Brandeis.cs131.Common.Abstract.Log;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Server;

public class DummyLog extends Log {

    public final void addToLog(Client client, Server server, EventType type) {
        //do nothing
    }

    public void addToLog(Client client, EventType type) {
        //do nothing
    }

    public void addToLog(EventType type) {
        //do nothing
    }
}
