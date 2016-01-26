package edu.Brandeis.cs131.Common.Test;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;
import edu.Brandeis.cs131.Common.Abstract.Server;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

public class MasterServerTest {

    private final String masterServerName = "MASTER";

    @Before
    public void setUp() {
        Server.DEFAULT_LOG.clearLog();
    }

    @BeforeClass
    public static void broadcast() {
        System.out.printf("Running Master Server Tests using %s \n", TestUtilities.factory.getClass().getCanonicalName());
    }

    private Server setupSimpleMasterServer(String name) {
        Collection<Server> servers = new ArrayList<Server>();
        servers.add(TestUtilities.factory.createNewBasicServer(name));
        return TestUtilities.factory.createNewMasterServer(masterServerName, servers, new Log());
    }

    @Test
    public void Shared_Consumes() {
        Client client = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.random());
        Server server = setupSimpleMasterServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumes(client, server);
    }

    @Test
    public void Basic_Consumes() {
        Client client = TestUtilities.factory.createNewBasicClient(TestUtilities.gbNames[0], Industry.random());
        Server server = setupSimpleMasterServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumes(client, server);
    }

    @Test
    public void Shared_Satisfied() {
        Client client = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.random());
        Server server = setupSimpleMasterServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumesTillSatisfied(client, server);
    }

    @Test
    public void Basic_Satisfied() {
        Client client = TestUtilities.factory.createNewBasicClient(TestUtilities.gbNames[0], Industry.random());
        Server server = setupSimpleMasterServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumesTillSatisfied(client, server);
    }

}
