package bgu.spl.net.impl.stomp;



import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;



public class StompConnections<T> implements Connections<T>{
    ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionHendlersMap ; //connection ID mapped to user's connection handler
    ConcurrentHashMap<String,List<Integer>> topics; //server topics - maps topic to Connection ID
    ConcurrentHashMap<String,Pair<String,AtomicBoolean>> users; //server users - maps login (user-name) to passcode and boolean isConnected
    AtomicInteger messageId;
    
    public StompConnections(){
        this.messageId = new AtomicInteger(0);
        this.connectionHendlersMap = new ConcurrentHashMap<Integer,ConnectionHandler<T>>(); 
        this.topics = new ConcurrentHashMap<String,List<Integer>>();
        this.users = new ConcurrentHashMap<String,Pair<String,AtomicBoolean>>();
    }
    
    //Map<Integer,UserData> allUsersData = new HashMap<Integer,UserData>(); //maps connectionID to usrData

    @Override
    public boolean send(int connectionId, T msg) { //should use the connectionHendlersMap to get the connection handlers

        // System.out.printf("send function returned connectionId:%d\n" , (connectionId));
        // System.out.println("send function recived this message:");
        // System.out.println(msg);
        connectionHendlersMap.get(connectionId).send(msg);

        return false;
    }

    @Override
    public void send(String channel, T msg) { //should use the topics field to get all the users subbed to the topic
        
        List<Integer> subs = topics.get(channel);
        if(subs != null){
            for(Integer connectionID : subs){
                send(connectionID,msg);
            }
        }
        
    }

    @Override
    public void disconnect(int connectionId) {

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
        List<Integer> topicSubs = topics.get(topic);
        if(topicSubs != null){
            return topicSubs.contains(connectionId);
        } else{
            return false;
        }

    }
        
    public boolean topicExist(String topic) {
        return topics.get(topic) != null;
    }

    
    public int generateMessageId(){
        return messageId.incrementAndGet();
    }


    private void addNewUser(String userName, String passCode){
        AtomicBoolean isConnected = new AtomicBoolean(true);
        String passCodeInt = passCode;
        Pair<String,AtomicBoolean> usrPair = new Pair<String,AtomicBoolean>(passCodeInt, isConnected);
        users.put(userName, usrPair);

    }

    public boolean isUserConnected(String usrName){
        Pair<String,AtomicBoolean>  p = users.get(usrName);
        if(p != null){
            return p.getValue().get();
        }
        else{ 
            return false;
        }
    }

    public void disconnectUser(String usrName){
        Pair<String,AtomicBoolean>  p = users.get(usrName);
        if(p != null){
            p.getValue().compareAndSet(true, false);
        }
    }

    public void connectUser(String usrName, String passCode){
        Pair<String,AtomicBoolean>  p = users.get(usrName);
        if(p != null){
            p.getValue().compareAndSet(false, true);
        }
        else{ 
            addNewUser(usrName, passCode);
        }

    }

    public boolean userExist(String usrName){
        return users.get(usrName) != null;
        
    }

    public boolean sighInIsLegal(String usrName, String passcode){
        Pair<String,AtomicBoolean>  p = users.get(usrName);
        String passCodeInt = passcode;
        if(p != null){
            return p.getKey().equals(passCodeInt);
        }
        else{ 
            return false;
        }
    }

    public void addTopic(String topic , int connectionId) {
        List<Integer> topicToAdd = new LinkedList<Integer>();
        topicToAdd.add(connectionId);
        topics.put(topic,topicToAdd);
    }














    //testing functions

    // public void printConnections() {
    //     System.out.println(connectionHendlersMap);
    // }

    // public void printTopics() {
    //     System.out.println(topics);
    // }

    // public void createDummyConnections(){
    //     addTopic("hilba", 1);
    //     addTopic("Shug", 1);
    //     addSubToTopic(2, "hilba");
    //     addSubToTopic(3, "hilba");
    //     addSubToTopic(4, "Shug");

    //     ConnectionHandler<T> tCH1 = new BlockingConnectionHandler<T>(null, null, null);
    //     ConnectionHandler<T> tCH2 = new BlockingConnectionHandler<T>(null, null, null);
    //     ConnectionHandler<T> tCH3 = new BlockingConnectionHandler<T>(null, null, null);
    //     ConnectionHandler<T> tCH4 = new BlockingConnectionHandler<T>(null, null, null);
    //     connect(4, tCH4);
    //     connect(1, tCH1);
    //     connect(2, tCH2);
    //     connect(3, tCH3);
        
    // }

    // public static void main(String[] args) {
    //     StompConnections<String> s = new StompConnections<>();

    //     s.addTopic("hilba", 1);
    //     s.addTopic("Shug", 1);
    //     s.addSubToTopic(2, "hilba");
    //     s.addSubToTopic(3, "hilba");
    //     s.addSubToTopic(4, "Shug");
    //     System.out.println("init topics are:");
    //     s.printTopics();
        
    //     ConnectionHandler<String> tCH1 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH2 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH3 = new BlockingConnectionHandler<String>(null, null, null);

    //     s.connect(1, tCH1);
    //     s.connect(2, tCH2);
    //     s.connect(3, tCH3);
    //     // s.printConnections();
    //     ConnectionHandler<String> tCH4 = new BlockingConnectionHandler<String>(null, null, null);
    //     s.connect(4, tCH4);
    //     // s.printConnections();
    //     // s.addSubToTopic(4, "Shug");
    //     s.printTopics();

    //     System.out.println("test removing non existing ConnectionHandler");
    //     s.disconnect(8);
    //     s.printConnections();
    //     s.printTopics();
    //     System.out.println("test removing existing ConnectionHandler - 3");
    //     s.disconnect(3);
    //     s.printConnections();
    //     s.printTopics();

    // }
    
}
