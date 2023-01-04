package bgu.spl.net.impl.stomp;


import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class StompConnections<T> implements Connections<T>{
    private List<ConnectionHandler<T>> connectionsList = new LinkedList<ConnectionHandler<T>>(); //user ID is the index in list
    Map<String,List<Integer>> topics = new HashMap<String,List<Integer>>(); //server topics

    @Override
    public boolean send(int connectionId, T msg) { //should use the connectionsList to get the connection handlers
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void send(String channel, T msg) { //should use the topics field to get all the users subbed to the topic
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnect(int connectionId) {
        // TODO Auto-generated method stub
        connectionsList.remove(connectionsList.get(connectionId));
        for(Map.Entry<String,List<Integer>> pair : topics.entrySet()){
            List<Integer> currList = pair.getValue();
            boolean found = false;
            for(int i=0 ; i < currList.size() & !found ; i++){
                if(currList.lastIndexOf(connectionId) != -1){
                    found = true;
                    currList.remove(connectionId);
                }
            }
        }
        
    }

    
    public void connect(ConnectionHandler<T> connectionHandler) {
        // TODO Auto-generated method stub
        connectionsList.add(connectionHandler);
        
    }


    public static void main(String[] args) {
        StompConnections<Integer> s = new StompConnections<>();
        
    }
    
}
