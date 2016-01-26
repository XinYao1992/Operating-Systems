package edu.Brandeis.cs131.Common.Abstract;

import edu.Brandeis.cs131.Common.Abstract.Log.Log;
import java.util.Collection;


public interface Factory {

    public abstract Server createNewBasicServer(String label);

    public abstract Server createNewMasterServer(String label, Collection<Server> servers, Log log);

    public abstract Client createNewSharedClient(String label, Industry industry);

    public abstract Client createNewBasicClient(String label, Industry industry);
}
