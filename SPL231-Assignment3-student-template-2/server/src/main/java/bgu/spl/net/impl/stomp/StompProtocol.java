package bgu.spl.net.impl.stomp;



import java.util.HashMap;
import java.util.Map;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;



public class StompProtocol implements StompMessagingProtocol<String> {
    
    private boolean shouldTerminate = false;
    //private boolean isConnected = false;
    private StompConnections<String> connections;
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
    public void start(int connectionId, Connections<String> connections) {
        this.connections = (StompConnections<String>)connections;
        this.usrData = new UserData(connectionId);
        //this.connections.addUserData(connectionId, usrData);
    }

    
    public void Process(String message){
        Frame<String> sentStompFrame = new Frame<String>(message);
        Map<String,String> sentHeaders = sentStompFrame.getCommandHeaders();
        String retFrame = "";
        Map<String,String> retHeaders = new HashMap<>();

        switch(sentStompFrame.getCommandType()){
            case CONNECT:
                String usrName = sentHeaders.get("login");
                String passcode = sentHeaders.get("passcode");
                if(!connections.userExist(usrName)){ //userName doesnt exist in the system
                    connectCase(sentStompFrame);
                }
                else{

                    if(connections.sighInIsLegal(usrName, passcode)){ //correct password from client
                        if(connections.isUserConnected(usrName)){ //someone is logged in 
                            
                            retHeaders.put("message","User already logged in");
                            disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                        }
                        else{ //no one is logged in 
                            connectCase(sentStompFrame);
                        }
                    }
                    else{//incorrect password from client
                        retHeaders.put("message","Wrong password");
                        disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                    }
                }
                break;
            
            case SEND:

                String topicToSendTo = sentHeaders.get("destination");
                if(topicToSendTo != null){
                    if (connections.isSubbed(usrData.getConnectionId(), topicToSendTo)){
                        int usrSubId = usrData.getSubIdByTopic(topicToSendTo);
                        retHeaders.put("subscription", Integer.toString(usrSubId));
                        retHeaders.put("message-id",Integer.toString(connections.generateMessageId()));
                        retHeaders.put("destination", topicToSendTo);
                        // System.out.println(sentStompFrame.getCommandBody());
                        retFrame = createStringStompFrame("MESSAGE", retHeaders, sentStompFrame.getCommandBody());
                        connections.send(topicToSendTo, retFrame);
                    } 
                    else{ //if this connection is not subbed to the topic
                        retHeaders.put("message","you are not subbed to topic " + topicToSendTo);
                        disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                    }
                }
                else{ //problem with the user STOMP destination header
                    retHeaders.put("message","destination recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                }
                break;
            case SUBSCRIBE:
                try {
                    
                    String topicToSubTo = sentHeaders.get("destination");
                    if(topicToSubTo != null){
                        int subId = Integer.parseInt(sentHeaders.get("id")); 
                        if(!connections.topicExist(topicToSubTo)){
                            connections.addTopic(topicToSubTo, usrData.getConnectionId());
                            usrData.addSub(subId , topicToSubTo); //saving the topic corresponding to this subID
                        }
                        else{
                            if(!connections.isSubbed(usrData.getConnectionId(), topicToSubTo)){
                                connections.addSubToTopic(usrData.getConnectionId(), topicToSubTo);
                                usrData.addSub(subId , topicToSubTo); //saving the topic corresponding to this subID
                            }
                            else{ // ERROR - trying to sub more than once to same topic
                                retHeaders.put("message","you are already subbed to this topic");
                                disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                            }

                        }

                    }
                    else{ //problem with the user STOMP destination header 
                        retHeaders.put("message","destination recieved was invalid");
                        disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");                    }
                        
                    
                } catch (Exception NumberFormatException) { //used incase parseInt method has illegal argumnts
                    retHeaders.put("message","id recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                }
                       
                    
                
                break;
            case UNSUBSCRIBE:
                try {
                    int subId = Integer.parseInt(sentHeaders.get("id")); 
                    connections.removeSubFromTopic(usrData.getConnectionId(), usrData.getTopicBySubId(subId)); //removing sub from server's topic list
                    usrData.removeSub(subId); //removing sub from local user data
                } catch (Exception NumberFormatException) {
                    retHeaders.put("message","id recieved was invalid");
                    disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                }
                
                break;
            case DISCONNECT:
                String usrReceipt = sentHeaders.get("receipt");
                retHeaders.put("receipt-id", usrReceipt);
                disconnectUsr("RECEIPT", retHeaders, "");
                break;
            case ERROR:
                retHeaders.put("message","command recieved was invalid");
                disconnectUsr("ERROR", retHeaders, "the message:\n" + "------------\n" + sentStompFrame.getFrameSent() + "\n------------");
                break;
        }
        
    }


    public void connectCase(Frame<String> sentStompFrame){
        Map<String,String> sentHeaders = sentStompFrame.getCommandHeaders();
        String retFrame = "";
        Map<String,String> retHeaders = new HashMap<>();

        usrData.completeUser(sentHeaders.get("accept-version"),sentHeaders.get("host"),sentHeaders.get("login"), sentHeaders.get("passcode"));        
        connections.connectUser(usrData.getUserName(), usrData.getUserPasscode());
        retHeaders.put("version", usrData.getAcceptVersion());
        retFrame = createStringStompFrame("CONNECTED",retHeaders,"");
        connections.send(usrData.getConnectionId(), retFrame);
    }

    public String createStringStompFrame(String commandType, Map<String,String> headersToSend, String body){
        String stompProtocolMessage = commandType + "\n";
        for(Map.Entry<String,String> pair : headersToSend.entrySet()){
            stompProtocolMessage = stompProtocolMessage + pair.getKey() + ":" + pair.getValue() + "\n";
        }

        // if(!body.equals("")) stompProtocolMessage = stompProtocolMessage + "\n" + body + "\n";
        // else stompProtocolMessage = stompProtocolMessage + "\n";
        stompProtocolMessage = stompProtocolMessage + "\n" + body;
        

        return(stompProtocolMessage);
    }

    public void disconnectUsr(String commandType,Map<String,String> retHeaders, String body){
        String retStompFrame = createStringStompFrame(commandType, retHeaders, body);
        connections.send(usrData.getConnectionId(), retStompFrame);
        connections.disconnect(usrData.getConnectionId()); //removing from subbed topics and connectionHandlers Map in connections
        if(usrData.getUserName() != null) connections.disconnectUser(usrData.getUserName()); //setting user as disconnected 
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
    public String process(String msg) {
        Process(msg);
        return null;
    }

    //testing functions
    public StompConnections<String> gConnections(){
        return connections;
    }


    // public static void main(String[] args) {
    //     StompProtocol protocol = new StompProtocol();
    //     StompConnections<String> mconnectionss = new StompConnections<>();
    //     // mconnectionss.createDummyConnections();
    //     protocol.start(1, mconnectionss); //this protocol isentifies with id = 0 connection handler (connectionId)


    //     // mconnectionss.addTopic("hilba", 1);
    //     // mconnectionss.addTopic("Shug", 1);
    //     // mconnectionss.addSubToTopic(2, "hilba");
    //     // mconnectionss.addSubToTopic(3, "hilba");
    //     // mconnectionss.addSubToTopic(4, "Shug");

    //     ConnectionHandler<String> tCH1 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH2 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH3 = new BlockingConnectionHandler<String>(null, null, null);
    //     ConnectionHandler<String> tCH4 = new BlockingConnectionHandler<String>(null, null, null);

    //     mconnectionss.connect(1, tCH1);
    //     mconnectionss.connect(2, tCH2);
    //     mconnectionss.connect(3, tCH3);
    //     mconnectionss.connect(4, tCH4);
        
    //     protocol.process("^@");

    //     String CONNECT = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n^@";
    //     // String SUBSCRIBE = "SUBSCRIBE\ndestination:hilba\nid:78\n\n^@";
    //     // String SEND = "SEND\ndestination:hilba\n\nHello topic a\n^@";
    //     // //String SEND = "SEND\ndestination :/ topic / a\n\nHello topic a\n^@";
    //     // String UNSUBSCRIBE = "UNSUBSCRIBE\nid:78\n\n^@";
    //     String DISCONNECT = "DISCONNECT\nreceipt:77\n\n^@";
    //     // String ERROR = "dfdf";
    //     protocol.process(CONNECT);
    //     // protocol.process(SUBSCRIBE);
    //     // protocol.process(SEND);
    //     // protocol.process(UNSUBSCRIBE);
    //     protocol.process(DISCONNECT);
    //     protocol.process(CONNECT); //loging into created user
    //     // String CONNECTWRONGPASSCODE = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:ilms\n\n^@";
    //     // protocol.process(CONNECTWRONGPASSCODE);
    //     String CONNECTCONNECTEDUSER = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n^@";
    //     protocol.process(CONNECTCONNECTEDUSER);
    //     // protocol.process(ERROR);

    //     // StompProtocol<String> sp1 = new StompProtocol<>();
    //     // StompProtocol<String> sp2 = new StompProtocol<>();
    //     // StompConnections<String> mconnectionss = new StompConnections<>();
    //     // sp1.start(0, mconnectionss);
    //     // sp2.start(1, mconnectionss);
    //     // mconnectionss.createDummyConnections();
    //     // mconnectionss.printConnections();
    //     // mconnectionss.printTopics();
    //     // sp1.gConnections().removeSubFromTopic(1, "hilba");
    //     // System.out.println(sp1.gConnections());
    //     // System.out.println(mconnectionss);
        


    // }


}