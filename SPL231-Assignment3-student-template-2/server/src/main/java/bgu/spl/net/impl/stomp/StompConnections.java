package bgu.spl.net.impl.stomp;



import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class StompConnections<T> implements Connections<T>{
    //private List<ConnectionHandler<T>> connectionHendlersMap = new LinkedList<ConnectionHandler<T>>(); //user ID is the index in list
    //Map<Integer,Integer> userIDtoConHandler = new HashMap<Integer,Integer>(); //from user ID to index of the connection handler in connectionHendlersMap
    ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionHendlersMap = new ConcurrentHashMap<Integer,ConnectionHandler<T>>(); //connection ID mapped to user's connection handler
    ConcurrentHashMap<String,List<Integer>> topics = new ConcurrentHashMap<String,List<Integer>>(); //server topics - maps topic to Connection ID
    Integer messageId = 0;
    //Map<Integer,UserData> allUsersData = new HashMap<Integer,UserData>(); //maps connectionID to usrData

    @Override
    public boolean send(int connectionId, T msg) { //should use the connectionHendlersMap to get the connection handlers
        // TODO Implement
        System.out.printf("send function returned connectionId:%d\n" , (connectionId));
        System.out.println("send function recived this message:");
        System.out.println(msg);
        return false;
    }

    @Override
    public void send(String channel, T msg) { //should use the topics field to get all the users subbed to the topic
        // TODO Test this
        List<Integer> subs = topics.get(channel);
        if(subs != null){
            for(Integer connectionID : subs){
                send(connectionID,msg);
            }
        }
        
    }

    @Override
    public void disconnect(int connectionId) {
        // TODO make thread safe
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
        connectionHendlersMap.put(connectionId,connectionHandler);
        
    }

    public boolean addSubToTopic(int connectionId , String topic) {
        List<Integer> topicSubs = topics.get(topic);
        if(topicSubs != null){
            topicSubs.add(connectionId);
        }
        else return false;
        return true;
    }

    public boolean removeSubFromTopic(int connectionId , String topic) {
        List<Integer> topicSubs = topics.get(topic);
        if(topicSubs != null){
            int idx = topicSubs.indexOf(connectionId);
            if(idx != -1){
                topicSubs.remove(idx);
            }
            else return false;
        }
        else return false;
        return true;
    }

    public boolean isSubbed(int connectionId , String topic) {
        return topics.get(topic).contains(connectionId);
    }
        
    public boolean topicExist(String topic) {
        return topics.get(topic) != null;
    }

    public int generateMessageId(){
        synchronized(messageId){
            int mId = messageId;
            messageId++;
            return mId;
        }
    }


    // public void addUserData(int connectionId , UserData usrData) {
    //    
    //     allUsersData.put(connectionId, usrData);
        
    // }
        
    // public ConnectionHandler<T> getConnecyiConnectionHandler(int connectionId) {
    //     return connectionHendlersMap.get(connectionId);
    // }


    //helper functions

    public void addTopic(String topic , int connectionId) {
        List<Integer> topicToAdd = new LinkedList<Integer>();
        topicToAdd.add(connectionId);
        topics.put(topic,topicToAdd);
    }


    public void printConnections() {
        System.out.println(connectionHendlersMap);
    }

    public void printTopics() {
        System.out.println(topics);
    }

    public void createDummyConnections(){
        addTopic("hilba", 1);
        addTopic("Shug", 1);
        addSubToTopic(2, "hilba");
        addSubToTopic(3, "hilba");
        addSubToTopic(4, "Shug");

        ConnectionHandler<T> tCH1 = new BlockingConnectionHandler<T>(null, null, null);
        ConnectionHandler<T> tCH2 = new BlockingConnectionHandler<T>(null, null, null);
        ConnectionHandler<T> tCH3 = new BlockingConnectionHandler<T>(null, null, null);
        ConnectionHandler<T> tCH4 = new BlockingConnectionHandler<T>(null, null, null);
        connect(4, tCH4);
        connect(1, tCH1);
        connect(2, tCH2);
        connect(3, tCH3);
        
    }

    public static void main(String[] args) {
        StompConnections<String> s = new StompConnections<>();
        //List<Integer> topicHilbaSubs = new LinkedList<Integer>();
        // topicHilbaSubs.add(1);
        // topicHilbaSubs.add(2);
        // topicHilbaSubs.add(3);
        // List<Integer> topicShugSubs = new LinkedList<Integer>();
        // topicShugSubs.add(1);
       


        s.addTopic("hilba", 1);
        s.addTopic("Shug", 1);
        s.addSubToTopic(2, "hilba");
        s.addSubToTopic(3, "hilba");
        s.addSubToTopic(4, "Shug");
        System.out.println("init topics are:");
        s.printTopics();
        
        //s.addSubToTopic(2, "hilba");
        // s.addSubToTopic(3, "Shug");
        // s.printTopics();
        // s.removeSubFromTopic(1,"Shug" );
        // s.printTopics();
        // s.addTopic("Shug", 1);
        ConnectionHandler<String> tCH1 = new BlockingConnectionHandler<String>(null, null, null);
        ConnectionHandler<String> tCH2 = new BlockingConnectionHandler<String>(null, null, null);
        ConnectionHandler<String> tCH3 = new BlockingConnectionHandler<String>(null, null, null);

        s.connect(1, tCH1);
        s.connect(2, tCH2);
        s.connect(3, tCH3);
        // s.printConnections();
        ConnectionHandler<String> tCH4 = new BlockingConnectionHandler<String>(null, null, null);
        s.connect(4, tCH4);
        // s.printConnections();
        // s.addSubToTopic(4, "Shug");
        s.printTopics();

        System.out.println("test removing non existing ConnectionHandler");
        s.disconnect(8);
        s.printConnections();
        s.printTopics();
        System.out.println("test removing existing ConnectionHandler - 3");
        s.disconnect(3);
        s.printConnections();
        s.printTopics();

    }
    
}
