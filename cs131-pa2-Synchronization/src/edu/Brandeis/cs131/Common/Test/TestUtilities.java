package edu.Brandeis.cs131.Common.Test;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Factory;
import edu.Brandeis.cs131.Common.Abstract.Log.Event;
import edu.Brandeis.cs131.Common.Abstract.Log.EventType;
import edu.Brandeis.cs131.Common.Abstract.Server;
import edu.Brandeis.cs131.Common.XinYao.ConcreteFactory;
//the above line is the only change you need to make.
import static org.junit.Assert.assertTrue;

public class TestUtilities {
    //Change the import to use your concreteFactory and nothing else
    
    public static final String sharedName = "SHARED";
    public static final String basicName = "BASIC";
    //Names used in testing
    public static final String[] gbNames = {"VENKMAN", "STANTZ", "SPENGLER", "ZEDDEMORE", "BARRETT", "TULLY", "MELNITZ", "PECK", "LENNY", "GOZER", "SLIMER", "STAY PUFT", "GATEKEEPER", "KEYMASTER"};
    public static final String[] mrNames = {"CATSKILL", "ROCKY", "APPALACHIAN", "OLYMPIC", "HIMALAYA", "GREAT DIVIDING", "TRANSANTRIC", "URAL", "ATLAS", "ALTAI", "CARPATHIAN", "KJOLEN", "BARISAN", "COAST", "QIN", "WESTERN GHATS"};
    
    public static final Factory factory = new ConcreteFactory();
    
    public static void ClientConsumes(Client client, Server server) {
        int initialRequestLevel = client.getRequestLevel();
        boolean canUse = server.connect(client);
        assertTrue(String.format("%s cannot use", client), canUse);
        client.doWhileAtServer();
        server.disconnect(client);
        assertTrue(String.format("%s request level did not decrease", client), client.getRequestLevel() == (initialRequestLevel - 1));
    }

    //Run the client simulation in the current thread
    public static void ClientConsumesTillSatisfied(Client client, Server server) {
        client.addServer(server);
        client.run();
        assertTrue("Client satisfied, but still has request needs", client.getRequestLevel() == 0);
        Event lastEvent = new Event(EventType.ERROR);
        while (!Server.DEFAULT_LOG.isEmpty()) {
            lastEvent = Server.DEFAULT_LOG.get();
        }
        assertTrue("Server log did not record client becoming satisfied.", new Event(client, EventType.COMPLETE).weakEquals(lastEvent));
    }
}
