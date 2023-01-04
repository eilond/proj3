package bgu.spl.net.impl.stomp;


import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class StompConnections<T> implements Connections<T>{
    //private List<ConnectionHandler<T>> connectionHendlersMap = new LinkedList<ConnectionHandler<T>>(); //user ID is the index in list
    //Map<Integer,Integer> userIDtoConHandler = new HashMap<Integer,Integer>(); //from user ID to index of the connection handler in connectionHendlersMap
    Map<Integer,ConnectionHandler<T>> connectionHendlersMap = new HashMap<Integer,ConnectionHandler<T>>(); //connection ID mapped to user's connection handler
    Map<String,List<Integer>> topics = new HashMap<String,List<Integer>>(); //server topics - maps topic to Connection ID
    //Map<Integer,UserData> allUsersData = new HashMap<Integer,UserData>(); //maps connectionID to usrData

    @Override
    public boolean send(int connectionId, T msg) { //should use the connectionHendlersMap to get the connection handlers
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void send(String channel, T msg) { //should use the topics field to get all the users subbed to the topic
        // TODO Auto-generated method stub
        List<Integer> subs = topics.get(channel);
        if(subs != null){
            for(Integer connectionID : subs){
                send(connectionID,msg);
            }
        }
        
    }

    @Override
    public void disconnect(int connectionId) {
        // TODO Auto-generated method stub
        connectionHendlersMap.remove(connectionId);
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

    
    public void connect(int connectionId , ConnectionHandler<T> connectionHandler) {
        // TODO Auto-generated method stub
        connectionHendlersMap.put(connectionId,connectionHandler);
        
    }
        
    // public void addUserData(int connectionId , UserData usrData) {
    //     // TODO Auto-generated method stub
    //     allUsersData.put(connectionId, usrData);
        
    // }
        


    //helper functions

    public void putTopic(String topic , List<Integer> subs) {
        topics.put(topic,subs);
    }

    public void putConnectionHendlers(Integer id , ConnectionHandler<T> Ch) {
        connectionHendlersMap.put(id,Ch);
    }

    public static void main(String[] args) {
        StompConnections<Integer> s = new StompConnections<>();
        

    }
    
}
