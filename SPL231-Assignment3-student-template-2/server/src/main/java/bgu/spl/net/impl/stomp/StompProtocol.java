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
        Map<String,String> sentHeaders = sentStompFrame.getCommandHeaders();
        String retFrame = "";
        switch(sentStompFrame.getCommandType()){
            case CONNECT:
                usrData.completeUser(sentHeaders.get("accept - version"),sentHeaders.get("host"), sentHeaders.get("login"),sentHeaders.get("passcode"));
                //retFrame = "CONNECTED\nversion"+sentHeaders.get("accept - version")+"\n\n^@";
                retFrame = "" //SEND CONNECTED FRAME
                connections.send(usrData.getConnectionId(), (T)retFrame);
                break;
            
            case SEND:

                String topicToSendTo = sentHeaders.get("destination");
                if(topicToSendTo != null){
                    if (connections.isSubbed(usrData.getConnectionId(), topicToSendTo)){
                        retFrame = "" //SEND MESSAGE FRAME
                        connections.send(topicToSendTo, (T)retFrame);
                    }
                    else{
                          // TODO: ERROR FRAME
                    }
                }
                else{ //problem with the user STOMP 
                     // TODO: ERROR FRAME
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
                                // TODO: ERROR FRAME
                            }

                        }

                    }
                    else{ //ERROR - problem with the user STOMP 
                        // TODO: ERROR FRAME
                    }
                        
                    
                } catch (Exception NumberFormatException) {
                    // TODO: ERROR FRAME
                }
                       
                    
                
                break;
            case UNSUBSCRIBE:
                try {
                    int subId = Integer.parseInt(sentHeaders.get("id")); 
                    connections.removeSubFromTopic(usrData.getConnectionId(), usrData.getTopicBySubId(subId)); //removing sub from server's topic list
                    usrData.removeSub(subId); //removing sub from local user data
                } catch (Exception NumberFormatException) {
                    // TODO: handle exception
                }
                
                break;
            case DISCONNECT:
                int usrReceipt = Integer.parseInt(sentHeaders.get("receipt")); 
                retFrame = "" //SEND RECIEPT FRAME
                connections.send(usrData.getConnectionId(), (T)retFrame);
                connections.disconnect(usrData.getConnectionId()); //removing from subbed topics and connectionHandlers Map in connections
                usrData.removeAllSubs(); //removing data locally
                
                shouldTerminate = true;
                break;
            case ERROR:
                retFrame = "" //SEND ERROR FRAME
                connections.send(usrData.getConnectionId(), (T)retFrame);
                connections.disconnect(usrData.getConnectionId()); //removing from subbed topics and connectionHandlers Map in connections
                usrData.removeAllSubs(); //removing data locally

                shouldTerminate = true;
                break;
        }
        
    }

    public String createStringFrame(String CommandType, Map<String,String> headersToSend, String body){
        String stompProtocolMessage = ""
        return(stompProtocolMessage);
    }

	
	/**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        //TODO IMPLEMENT
        return(shouldTerminate);
    }


    @Override //only for implemintaion
    public T process(T msg) {
        Process(msg);
        return null;
    }


    


}