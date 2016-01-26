package edu.Brandeis.cs131.Common.Test;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;
import edu.Brandeis.cs131.Common.Abstract.Server;
import edu.Brandeis.cs131.Common.Abstract.Log.DummyLog;
import edu.Brandeis.cs131.Common.Abstract.Log.Event;
import edu.Brandeis.cs131.Common.Abstract.Log.EventType;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimulationTest {

    public static boolean DEBUG_MODE = false;
    private static final int wave1Clients = 50;
    private static final int wave2Clients = 50;
    private static final int wave3Clients = 50;
    //wave 1 and 3 are sharedClients, wave2 is basicClients
    
    
    
    @Before
    public void setUp() {
        Server.DEFAULT_LOG.clearLog();
    }
    
    @BeforeClass
    public static void broadcast() {
        System.out.printf("Running Simulation Tests using %s \n", TestUtilities.factory.getClass().getCanonicalName());
    }

    @Test
    public void Basic_Server_Test() {
        LogVerifier verifier = new LogVerifier(Server.DEFAULT_LOG);
        Thread verifierThread = new Thread(verifier);
        verifierThread.start();
        Collection<Server> servers = new ArrayList<Server>();
        Collection<Thread> clientThread = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            servers.add(TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[i]));
        }
        for (int i = 0; i < wave1Clients; i++) {
            Client shared = TestUtilities.factory.createNewSharedClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            shared.addServer(servers);
            Thread sharedThread = new Thread(shared);
            sharedThread.start();
            clientThread.add(sharedThread);
        }
        for (int i = wave1Clients; i < wave1Clients+wave2Clients; i++) {
            Client basic = TestUtilities.factory.createNewBasicClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            basic.addServer(servers);
            Thread basicThread = new Thread(basic);
            basicThread.start();
            clientThread.add(basicThread);
        }
      
        for (int i = wave1Clients+wave2Clients; i < wave1Clients+wave2Clients+wave3Clients; i++) {
            Client shared = TestUtilities.factory.createNewSharedClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            shared.addServer(servers);
            Thread sharedThread = new Thread(shared);
            sharedThread.start();
            clientThread.add(sharedThread);
        }
        try {
            for (Thread t : clientThread) {
                t.join();
            }
            Server.DEFAULT_LOG.addToLog(EventType.END_TEST);
            verifierThread.join();
        } catch (InterruptedException ex) {
            assertTrue("Interruption exception occurred.", false);
        }
        assertTrue(verifier.printErrors(), !verifier.hasErrors());
    }

    @Test
    public void Master_Server_Test() {
        LogVerifier verifier = new LogVerifier(Server.DEFAULT_LOG);
        DummyLog scheduler_log = new DummyLog();
        Thread verifierThread = new Thread(verifier);
        verifierThread.start();
        Collection<Server> servers = new ArrayList<Server> ();
       
        Collection<Thread> clientThread = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            servers.add(TestUtilities.factory.createNewBasicServer(TestUtilities.mrNames[i]));
        }
        Server masterServer = TestUtilities.factory.createNewMasterServer("Scheduled", servers, scheduler_log);
        for (int i = 0; i < wave1Clients; i++) {
            Client shared = TestUtilities.factory.createNewSharedClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            shared.addServer(masterServer);
            Thread sharedThread = new Thread(shared);
            sharedThread.start();
            clientThread.add(sharedThread);
        }
        for (int i = wave1Clients; i < wave1Clients+wave2Clients; i++) {
            Client basic = TestUtilities.factory.createNewBasicClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            basic.addServer(masterServer);
            Thread basicThread = new Thread(basic);
            basicThread.start();
            clientThread.add(basicThread);
        }
        
        for (int i = wave1Clients+wave2Clients; i < wave1Clients+wave2Clients+wave3Clients; i++) {
            Client shared = TestUtilities.factory.createNewSharedClient(Integer.toString(i), Industry.values()[i % Industry.values().length]);
            shared.addServer(masterServer);
            Thread sharedThread = new Thread(shared);
            sharedThread.start();
            clientThread.add(sharedThread);
        }
        try {
            for (Thread t : clientThread) {
                t.join();
            }
            Server.DEFAULT_LOG.addToLog(EventType.END_TEST);
            verifierThread.join();
        } catch (InterruptedException ex) {
            assertTrue("Interruption exception occurred.", false);
        }
        assertTrue(verifier.printErrors(), !verifier.hasErrors());
    }

    private class LogVerifier implements Runnable {

        private final Log log;
        private final Collection<Client> satisfiedClients;
        private Map<Server, Collection<Client>> servers;
        private Map<Integer, Event> potential_entry_events;
        private Map<Server, Collection<Client>> exitSet;
        private Set<String> errors;

        public LogVerifier(Log log) {
            this.log = log;
            this.servers = new HashMap<Server, Collection<Client>>();
            this.potential_entry_events = new HashMap<Integer, Event>();
            this.exitSet = new HashMap<Server, Collection<Client>>();
            this.satisfiedClients = new ArrayList<Client>();
            this.errors = new HashSet<String>();
        }

        @Override
        public void run() {
            Event currentEvent;
            do {
                currentEvent = log.get();
                Client curClient = currentEvent.getClient();
                Server curServer = currentEvent.getServer();
                if (curServer != null) {
                    if (exitSet.get(curServer) == null) {
                        exitSet.put(curServer, new ArrayList<Client>());
                    }
                    if (servers.get(curServer) == null) {
                        servers.put(curServer, new ArrayList<Client>());
                    }
                }
                if(SimulationTest.DEBUG_MODE && (!currentEvent.getEvent().equals(EventType.ENTER_ATTEMPT) && !currentEvent.getEvent().equals(EventType.ENTER_FAILED) && !currentEvent.getEvent().equals(EventType.LEAVE_START))){
                    System.out.println(currentEvent.toString());
                }
                switch (currentEvent.getEvent()) {
                    case ENTER_ATTEMPT:
                        potential_entry_events.put(currentEvent.getSignifier(), currentEvent);
                        break;
                    case ENTER_SUCCESS:
                        potential_entry_events.remove(currentEvent.getSignifier());
                        checkEnterConditions(curClient, curServer);
                        servers.get(curServer).add(curClient);
                        break;
                    case ENTER_FAILED:
                        potential_entry_events.remove(currentEvent.getSignifier());
                        break;
                    case LEAVE_START:
                        checkLeaveConditions(curClient, curServer);
                        servers.get(curServer).remove(curClient);
                        exitSet.get(curServer).add(curClient);
                    case LEAVE_END:
                        exitSet.get(curServer).remove(curClient);
                        break;
                    case COMPLETE:
                        satisfiedClients.add(curClient);
                        break;
                    case ERROR:
                        errors.add("An error occurred during the simulation");
                        break;
                    case INTERRUPTED:
                        break;

                }
            } while (!currentEvent.getEvent().equals(EventType.END_TEST));
        }

        private void checkEnterConditions(Client newClient, Server toServer) {
            if (satisfiedClients.contains(newClient)) {
                errors.add(String.format("%s entered %s when the client is already satisfied.", newClient, toServer));
            }


            if (isBasic(newClient)) {
                this.errors.addAll(verifyBasicEntry(newClient, toServer.toString(), servers.get(toServer)));
            }
            if (isShared(newClient)) {
                this.errors.addAll(verifySharedEntry(newClient, toServer.toString(), servers.get(toServer)));
            }
           
        }

        private void checkLeaveConditions(Client newAnimal, Server server) {
            if (satisfiedClients.contains(newAnimal)) {
                errors.add(String.format("%s was satisfied before leaving %s.", newAnimal, server));
            }
            Collection<Client> currentOccupants = servers.get(server);
            if (currentOccupants == null || currentOccupants.isEmpty() || !currentOccupants.contains(newAnimal)) {
                errors.add(String.format("%s left %s before entering.", newAnimal, server));
            }
        }

        private Collection<String> verifySharedEntry(Client shared, String server, Collection<Client> occupants) {
            Collection<String> errors = new ArrayList<String>();
            if (occupants != null && !occupants.isEmpty()) {
                int sharedCount = 0;
                for (Client din : occupants) {
                    if (isBasic(din)) {
                        errors.add(String.format("%s entered %s with %s.", shared, server, din));
                    }
                    if (isShared(din)) {
                        sharedCount++;
                        if (sharedCount > 1) {
                            errors.add(String.format("%s entered %s with multiple shared clients present already.", shared, server, din));
                        }
                    }
                    if (shared.getIndustry().equals(din.getIndustry())) {
                        errors.add(String.format("%s entered %s with clients of the same industry.", shared, server));
                    }
                }
            }
            return errors;
        }

        private Collection<String> verifyBasicEntry(Client basic, String server, Collection<Client> occupants) {
            Collection<String> errors = new ArrayList<String>();
            if (occupants != null && !occupants.isEmpty()) {
                for (Client din : occupants) {
                    if (!isGuest(din)) {
                        errors.add(String.format("%s entered %s with %s.", basic, server, din));
                    }
                    if (basic.getIndustry().equals(din.getIndustry())) {
                        errors.add(String.format("%s entered %s with clients of the same industry.", basic, server));
                    }
                }
            }
            return errors;
        }

        private boolean isShared(Client client) {
            return client.toString().contains(TestUtilities.sharedName);

        }

        private boolean isBasic(Client client) {
            return client.toString().contains(TestUtilities.basicName);

        }

        private boolean isGuest(Client client) { //legacy
            return false;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public String printErrors() {
            StringBuilder builder = new StringBuilder();
            for (String er : errors) {
                builder.append(er);
                builder.append("\n");
            }
            return builder.toString();
        }

    

     
    }
}
