package edu.Brandeis.cs131.Common.XinYao;

import java.util.ArrayList;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Server;

public class BasicServer extends Server {
	private ArrayList<Client> clientsToAccess = new ArrayList<Client>();

	public BasicServer(String name) {
		super(name);
	}

	// we define an ArrayList called 'clients' to store the clients that can be
	// processes by the server at the same time.
	// if 'clients' has no elements, we can add any new client into 'clients'
	// since there is no violation.
	// if 'clients' has one element already, we have to compare the new coming
	// client type with the old one to see if there is violation.
	// if 'clients' has more than one elements, that means that the server is
	// busy with processing these two elements, and no other
	// client can be added into the list at this time.
	@Override
	public synchronized boolean connectInner(Client client) {
		if (clientsToAccess.isEmpty()) {
			clientsToAccess.add(client);
			return true;
		} else if (clientsToAccess.size() == 1) {
			if (clientsToAccess.get(0) instanceof SharedClient
					&& clientsToAccess.get(0).getIndustry() != client.getIndustry()
					&& client instanceof SharedClient) {
				clientsToAccess.add(client);
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void disconnectInner(Client client) {
		clientsToAccess.remove(client);
	}
	
}
