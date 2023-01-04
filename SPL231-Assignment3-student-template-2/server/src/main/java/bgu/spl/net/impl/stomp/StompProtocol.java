package bgu.spl.net.impl.stomp;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;
import javafx.util.Pair;


public class StompProtocol<T> implements StompMessagingProtocol<T> {
    
    private boolean shouldTerminate = false;
    private StompConnections<T> connections;
    private UserData usrData;

    /**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    @Override
    public void start(int connectionId, Connections<T> connections) {
        // TODO Auto-generated method stub
        this.connections = (StompConnections<T>)connections;
        this.usrData = new UserData(connectionId);
        //this.connections.addUserData(connectionId, usrData);
    }
    
    
    public void Process(T message){
        //SUBSCRIBE SHOULD USE COMPLETEUSERDATA
        //when subscription is added put the subscription ID in the usrData

        Frame<T> sentStompFrame = new Frame<T>(message);
        sentStompFrame.getCommandType(); //switch case on all this
        
    }
	
	/**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        //TODO IMPLEMENT
        return(true);
    }


    @Override //only for implemintaion
    public T process(T msg) {
        Process(msg);
        return null;
    }


}