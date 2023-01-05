package bgu.spl.net.impl.stomp;


import java.util.Map;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;



public class StompProtocol<T> implements StompMessagingProtocol<T> {
    
    private boolean shouldTerminate = false;
    private StompConnections<T> connections;
    private UserData usrData;

    enum CommandType{
        CONNECT,
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        DISCONNECT,
        ERROR;
    }

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
        Frame<T> sentStompFrame = new Frame<T>(message);
        
        switch(sentStompFrame.getCommandType()){
            case CONNECT:
            
            Map<String,String> sentMap = sentStompFrame.getCommandHeaders();
            usrData.completeUser(sentMap.get("accept - version"),sentMap.get("host"), sentMap.get("login"),sentMap.get("passcode"));
                break;
            case SEND:
                break;
            case SUBSCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            case DISCONNECT:
                break;
            case ERROR:
                break;
        }
        
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