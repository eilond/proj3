package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;


public class StompProtocol<T> implements StompMessagingProtocol<T> {
    
    private boolean shouldTerminate = false;
    private Connections<T> connections;
    int connectionId;

    /**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    @Override
    public void start(int connectionId, Connections<T> connections) {
        // TODO Auto-generated method stub
        this.connections = connections;
        this.connectionId = connectionId;
        
    }
    
    
    public void StompProcess(T message){

    }
	
	/**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return(shouldTerminate);
    }


    @Override //only for implemintaion
    public T process(T msg) {
        
        return null;
    }


}