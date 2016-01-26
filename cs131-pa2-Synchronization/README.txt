In the class of "BasicClient", I created the constructor and rewrote the "toString" method which indicates that
the type of its object is "BASIC".

In the class of "SharedClient", I created the constructor and rewrote the "toString" method which indicates that
the type of its object is "SHARED".

In the class of "ConcreteFactory", I set up methods of "createNewBasicServer", "createNewSharedClient", and 
"createNewBasicClient" to create and return the new classes.

In the class of "BasicServer", I first define an ArrayList called "clients" to store the clients that can access to the 
server at the same time. If "clients" has no elements, any coming client can be added into the ArrayList since
there is no violation. If "clients" already has one element, the next coming client needs to do the comparison with the
first one to see if there is violation. If "clients" has more than one elements in the list, that means that the server
is busy with processing these two elements and no other new client can be added into the ArrayList at this time. Most
importantly, both funtions of "connectInner" and "disconnectInner" must be added by key word "synchronized" to make sure
that there is only one thread can access to the server simultaneously. 