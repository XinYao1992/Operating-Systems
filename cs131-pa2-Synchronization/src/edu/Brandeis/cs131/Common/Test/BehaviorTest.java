package edu.Brandeis.cs131.Common.Test;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;
import edu.Brandeis.cs131.Common.Abstract.Server;
import edu.Brandeis.cs131.Common.Abstract.Log.Event;
import edu.Brandeis.cs131.Common.Abstract.Log.EventType;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BehaviorTest {

    @Before
    public void setUp() {
        Server.DEFAULT_LOG.clearLog();
    }

    @BeforeClass
    public static void broadcast() {
        System.out.printf("Running Behavior Tests using %s \n", TestUtilities.factory.getClass().getCanonicalName());
    }

    /**
     * Client RollCall checks the basic functions of an animal. Note if the test
     * does not pass neither will any other test *
     */
    @Test
    public void Client_RollCall() {

        for (Industry industry : Industry.values()) {
            Client sharedClient = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], industry);
            Client basicClient = TestUtilities.factory.createNewBasicClient(TestUtilities.gbNames[1], industry);

            assertTrue("sharedClient is the wrong industry", sharedClient.getIndustry().equals(industry));
            assertTrue("BasicClient is the wrong industry", basicClient.getIndustry().equals(industry));

            assertTrue("sharedClient has the wrong name", sharedClient.getName().equals(TestUtilities.gbNames[0]));
            assertTrue("BasicClient has the wrong name", basicClient.getName().equals(TestUtilities.gbNames[1]));

            assertTrue("sharedClient has the wrong initial bandwith", sharedClient.getRequestLevel() == 3);
            assertTrue("BasicClient has the wrong initial bandwith", basicClient.getRequestLevel() == 3);

            assertTrue("sharedClient toString does not function as expected", String.format("%s %s %s", industry, TestUtilities.sharedName, TestUtilities.gbNames[0]).equals(sharedClient.toString()));
            assertTrue("BasicClient toString does not function as expected", String.format("%s %s %s", industry, TestUtilities.basicName, TestUtilities.gbNames[1]).equals(basicClient.toString()));

        }
    }

    @Test
    public void Server_Basic() {
        Server server = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        assertTrue("Server has the wrong name", TestUtilities.mrNames[0].equals(server.getName()));
        assertTrue("Server toString does not function as expected", String.format("%s", TestUtilities.mrNames[0]).equals(server.toString()));
    }

    @Test
    public void shared_Consumes() {
        Client client = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.random());
        Server server = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumes(client, server);
        Event logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record shared client entering server", new Event(client, server, EventType.ENTER_ATTEMPT).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record shared client entering server", new Event(client, server, EventType.ENTER_SUCCESS).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record shared client leaving server", new Event(client, server, EventType.LEAVE_START).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record shared client leaving server", new Event(client, server, EventType.LEAVE_END).weakEquals(logEvent));
    }

    @Test
    public void Basic_Consumes() {
        Client client = TestUtilities.factory.createNewBasicClient(TestUtilities.gbNames[0], Industry.random());
        Server hill = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumes(client, hill);
        Event logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record basic client entering server", new Event(client, hill, EventType.ENTER_ATTEMPT).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record basic client entering server", new Event(client, hill, EventType.ENTER_SUCCESS).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record basic client entering server", new Event(client, hill, EventType.LEAVE_START).weakEquals(logEvent));
        logEvent = Server.DEFAULT_LOG.get();
        assertTrue("Server log did not record basic client entering server", new Event(client, hill, EventType.LEAVE_END).weakEquals(logEvent));
    }

    @Test
    public void shared_Satisfied() {
        Client client = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.random());
        Server hill = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumesTillSatisfied(client, hill);
    }

    @Test
    public void Basic_Satisfied() {
        Client client = TestUtilities.factory.createNewBasicClient(TestUtilities.gbNames[0], Industry.random());
        Server hill = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        TestUtilities.ClientConsumesTillSatisfied(client, hill);
    }

    @Test
    public void Industry_Constraint() {
        Client shared = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.CONSTRUCTION);
        Client violator = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[1], Industry.CONSTRUCTION);
        Server server = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        boolean canUse = server.connect(shared);
        assertTrue(String.format("%s cannot use", shared), canUse);
        canUse = server.connect(violator);
        assertTrue(String.format("%s is using with %s. Violates industry constraint", violator, shared), !canUse);
    }

    @Test
    public void Multiple_shared() {
        Client peter = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[0], Industry.TECHNOLOGY);
        Client ray = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[1], Industry.CONSTRUCTION);
        Client walter = TestUtilities.factory.createNewSharedClient(TestUtilities.gbNames[7], Industry.RESTAURANT);
        Server server = TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[0]);
        boolean canUse = server.connect(peter);
        assertTrue(String.format("%s cannot use", ray), canUse);
        canUse = server.connect(ray);
        assertTrue(String.format("%s is not using with %s.", peter, ray), canUse);
        canUse = server.connect(walter);
        assertTrue(String.format("%s is using with %s and %s violates number constraint.", walter, peter, ray), !canUse);
        peter.doWhileAtServer();
        server.disconnect(peter);
        ray.doWhileAtServer();
        server.disconnect(ray);
        canUse = server.connect(walter);
        assertTrue(String.format("%s cannot use, %s and %s did not leave server.", walter, peter, ray), canUse);
    }
}
