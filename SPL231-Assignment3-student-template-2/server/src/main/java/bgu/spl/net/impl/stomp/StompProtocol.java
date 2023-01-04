package bgu.spl.net.impl.stomp;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;
import javafx.util.Pair;


public class StompProtocol<T> implements StompMessagingProtocol<T> {
    
    private boolean shouldTerminate = false;
    private Connections<T> connections;
    int connectionId;
    List<Pair<String,Integer>> usrSubs = new LinkedList<Pair<String,Integer>>(); //holds all the different subID of this user and the topic they sighed into

    /**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    @Override
    public void start(int connectionId, Connections<T> connections) {
        // TODO Auto-generated method stub
        this.connections = connections;
        this.connectionId = connectionId;
    }
    
    
    public void Process(T message){

    }
	
	/**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return(shouldTerminate);
    }


    @Override //only for implemintaion
    public T process(T msg) {
        Process(msg);
        return null;
    }


}