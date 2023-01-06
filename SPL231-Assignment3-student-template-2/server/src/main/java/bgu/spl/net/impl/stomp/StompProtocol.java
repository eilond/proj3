package bgu.spl.net.impl.stomp;



import java.util.HashMap;
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
        this.connections = (StompConnections<T>)connections;
        this.usrData = new UserData(connectionId);
        //this.connections.addUserData(connectionId, usrData);
    }
    
    
    public void Process(T message){

        Frame<T> sentStompFrame = new Frame<T>(message);
        Map<String,String> sentHeaders = sentStompFrame.getCommandHeaders();
        String retFrame = "";
        Map<String,String> retHeaders = new HashMap<>();
        switch(sentStompFrame.getCommandType()){
            case CONNECT:
                usrData.completeUser(sentHeaders.get("accept-version"),sentHeaders.get("host"), sentHeaders.get("login"),sentHeaders.get("passcode"));
                //retFrame = "CONNECTED\nversion"+sentHeaders.get("accept - version")+"\n\n^@";
                retHeaders.put("version", usrData.getAcceptVersion());
                retFrame = createStringStompFrame("CONNECTED",retHeaders,"");
                connections.send(usrData.getConnectionId(), (T)retFrame);
                break;
            
            case SEND:

                String topicToSendTo = sentHeaders.get("destination");
                if(topicToSendTo != null){
                    if (connections.isSubbed(usrData.getConnectionId(), topicToSendTo)){
                        int usrSubId = usrData.getSubIdByTopic(topicToSendTo);

                        retHeaders.put("subscription", Integer.toString(usrSubId));
                        retHeaders.put("message - id",Integer.toString(connections.generateMessageId()));
                        retHeaders.put("destination", topicToSendTo);

                        retFrame = createStringStompFrame("MESSAGE", retHeaders, sentStompFrame.getCommandBody());
                        connections.send(topicToSendTo, (T)retFrame);
                    } 
                    else{ //if this connection is not subbed to the topic
                        retHeaders.put("message","you are not subbed to topic " + topicToSendTo);
                        disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                    }
                }
                else{ //problem with the user STOMP destination header
                    retHeaders.put("message","destination recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                }
                break;
            case SUBSCRIBE:
                try {
                    
                    String topicToSubTo = sentHeaders.get("destination");
                    if(topicToSubTo != null){
                        int subId = Integer.parseInt(sentHeaders.get("id")); 
                        if(!connections.topicExist(topicToSubTo)){
                            connections.addTopic(topicToSubTo, usrData.getConnectionId());
                        } 
                        else{
                            if(!connections.isSubbed(usrData.getConnectionId(), topicToSubTo)){
                                connections.addSubToTopic(usrData.getConnectionId(), topicToSubTo);
                                usrData.addSub(subId , topicToSubTo); //saving the topic corresponding to this subID
                            }
                            else{ // ERROR - trying to sub more than once to same topic
                                retHeaders.put("message","you are already subbed to this topic");
                                disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                            }

                        }

                    }
                    else{ //problem with the user STOMP destination header 
                        retHeaders.put("message","destination recieved was invalid");
                        disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                    }
                        
                    
                } catch (Exception NumberFormatException) { //used incase parseInt method has illegal argumnts
                    retHeaders.put("message","id recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                }
                       
                    
                
                break;
            case UNSUBSCRIBE:
                try {
                    int subId = Integer.parseInt(sentHeaders.get("id")); 
                    connections.removeSubFromTopic(usrData.getConnectionId(), usrData.getTopicBySubId(subId)); //removing sub from server's topic list
                    usrData.removeSub(subId); //removing sub from local user data
                } catch (Exception NumberFormatException) {
                    retHeaders.put("message","id recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                }
                
                break;
            case DISCONNECT:
                String usrReceipt = sentHeaders.get("receipt");
                retHeaders.put("receipt - id", usrReceipt);
                disconnectUsr("RECEIPT", retHeaders, "");
                break;
            case ERROR:
                retHeaders.put("message","command recieved was invalid");
                disconnectUsr("ERROR", retHeaders, "the message:\n" + "-----\n" + message + "\n-----");
                break;
        }
        
    }

    public String createStringStompFrame(String commandType, Map<String,String> headersToSend, String body){
        String stompProtocolMessage = commandType + "\n";
        for(Map.Entry<String,String> pair : headersToSend.entrySet()){
            stompProtocolMessage = stompProtocolMessage + pair.getKey() + " : " + pair.getValue() + "\n";
        }

        stompProtocolMessage = stompProtocolMessage + "\n\n" + body;
        stompProtocolMessage = stompProtocolMessage + "^@";

        return(stompProtocolMessage);
    }

    public void disconnectUsr(String commandType,Map<String,String> retHeaders, String body){
        String retStompFrame = createStringStompFrame(commandType, retHeaders, body);
        connections.send(usrData.getConnectionId(), (T)retStompFrame);
        connections.disconnect(usrData.getConnectionId()); //removing from subbed topics and connectionHandlers Map in connections
        usrData.removeAllSubs(); //removing data locally
        
        shouldTerminate = true;
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

    //helper functions
    public StompConnections<T> gConnections(){
        return connections;
    }


    public static void main(String[] args) {
        StompProtocol<String> sp = new StompProtocol<>();
        StompConnections<String> sc = new StompConnections<>();
        sc.createDummyConnections();
        sp.start(0, sc); //this protocol isentifies with id = 0 connection handler (connectionId)
        String CONNECT = "CONNECT\naccept - version :1.2\nhost : stomp . cs . bgu . ac . il\nlogin : meni\npasscode : films\n\n^@";
        String SEND = "SEND\ndestination :/ topic / a\n\nHello topic a\n^@";
        String SUBSCRIBE = "SUBSCRIBE\ndestination :/ topic / a\nid :78\n\n^@";
        String UNSUBSCRIBE = "UNSUBSCRIBE\nid :78\n\n^@";
        String DISCONNECT = "DISCONNECT\nreceipt :77\n\n^@";
        String ERROR = "dfdf";
        sp.process(SEND);

        // StompProtocol<String> sp1 = new StompProtocol<>();
        // StompProtocol<String> sp2 = new StompProtocol<>();
        // StompConnections<String> sc = new StompConnections<>();
        // sp1.start(0, sc);
        // sp2.start(1, sc);
        // sc.createDummyConnections();
        // sc.printConnections();
        // sc.printTopics();
        // sp1.gConnections().removeSubFromTopic(1, "hilba");
        // System.out.println(sp1.gConnections());
        // System.out.println(sc);
        


    }


}