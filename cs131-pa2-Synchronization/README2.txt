In the class of "MasterServer", I completed both methods of "connectInner" and "disconnectInner". 

The idea in "connectInner" is: this class is used to connect the client to certain server, if fails, keep the client
in the wait queue and make it waiting, if succeeds, remove the client from the wait queue and return true. First, use 
"getKey()" method to find which server the client belong to, then make this server's wait queue "synchronized" since
only one client can use this wait queue at the same time. Next, to check if this client is the head of the wait queue, 
if it is, make it to connect to that server, if they can connect, remove this client from the wait queue and notify all 
the waiting clients and return true.Else, make this client waiting until other client wakes it up, once it wake up again, 
back to try to connect to the server and so on. If this client is not the head of the wait queue, make it waiting. When it 
becomes the head of the wait queue and be notified by others, it will try to connect to the server, if succeeds, remove 
this client from the wait queue and wake all the waiting clients upand return true, else, make it waiting again for the 
server and so on.


The idea in "disconnectInner" is: this class is to disconnect the client from certain server, and notify all the 
clients that are waiting for this server. First, find the server that the client belong to, and then make the wait queue 
of this server synchronized. In the synchronized block, we disconnect the client from the server, and use "notifyAll" to 
wake all the waiting clients up. (But only the head of the queue will be processed, other threads awaked will go back to 
the waiting status)