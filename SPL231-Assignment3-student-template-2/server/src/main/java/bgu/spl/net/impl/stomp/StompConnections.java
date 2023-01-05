package bgu.spl.net.impl.stomp;



import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.HashMap;
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
                int idx = currList.lastIndexOf(connectionId);
                if(idx != -1){
                    found = true;
                    currList.remove(idx);
                }
            }
        }
        
    }

    
    public void connect(int connectionId , ConnectionHandler<T> connectionHandler) {
        // TODO Auto-generated method stub
        connectionHendlersMap.put(connectionId,connectionHandler);
        
    }

    public boolean addSubToTopic(int subId , String topic) {
        // TODO Auto-generated method stub
        List<Integer> topicSubs = topics.get(topic);
        if(topicSubs != null){
            topicSubs.add(subId);
        }
        else return false;
        return true;
    }
        
    // public void addUserData(int connectionId , UserData usrData) {
    //     // TODO Auto-generated method stub
    //     allUsersData.put(connectionId, usrData);
        
    // }
        


    //helper functions

    public void putTopic(String topic , List<Integer> subs) {
        topics.put(topic,subs);
    }


    public void printConnections() {
        System.out.println(connectionHendlersMap);
    }

    public void printTopics() {
        System.out.println(topics);
    }

    // public static void main(String[] args) {
    //     StompConnections<String> s = new StompConnections<>();
    //     List<Integer> topicHilbaSubs = new LinkedList<Integer>();
    //     topicHilbaSubs.add(1);
    //     topicHilbaSubs.add(2);
    //     topicHilbaSubs.add(3);
       
    //     List<Integer> topicShugSubs = new LinkedList<Integer>();
    //     topicShugSubs.add(1);
    //     s.putTopic("hilba", topicHilbaSubs);
    //     s.putTopic("Shug", topicShugSubs);
    //     s.printTopics();

    //     //s.addSubToTopic(2, "hilba");
    //     s.addSubToTopic(3, "shug");
    //     s.printTopics();
    //     ConnectionHandler<String> tCH1 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH2 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH3 = new BlockingConnectionHandler<String>(null, null, null);

    //     s.connect(1, tCH1);
    //     s.connect(2, tCH2);
    //     s.connect(3, tCH3);
    //     //s.printConnections();
    //     ConnectionHandler<String> tCH4 = new BlockingConnectionHandler<String>(null, null, null);
    //     s.connect(4, tCH4);
    //     topicHilbaSubs.add(4);
    //     s.printConnections();
    //     s.disconnect(1);
    //     s.printConnections();
    //     s.printTopics();

    // }
    
}
