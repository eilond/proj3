package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;


public class UserData {
    int connectionId;

    String acceptVersion;
    String host;
    String usrName;
    String usrPassCode;

    Map<String , Integer> usrTopicToSubId; //maps topic (that the user is subbed to) to subID 
    Map<Integer , String> usrSubIdToTopic; //maps subID to topic (that the user is subbed to) 

    UserData(int connectionId){
        this.connectionId = connectionId;
    }


    public void completeUser(String acceptVersion, String host, String usrName, String usrPassCode ){
        this.acceptVersion = acceptVersion;
        this.host = host;
        this.usrName = usrName;
        this.usrPassCode = usrPassCode;
        this.usrSubIdToTopic = new HashMap<Integer , String>();
        this.usrTopicToSubId = new HashMap<String , Integer>();
    }

    public void addSub(Integer subID , String topic){
        usrSubIdToTopic.put(subID ,topic);
        usrTopicToSubId.put(topic,subID);
    }

    public void removeSub(Integer subID){
        String topicRemoved = usrSubIdToTopic.remove(subID);
        if(topicRemoved != null) usrTopicToSubId.remove(topicRemoved);
    }

    public void removeAllSubs(){ 
        if(usrSubIdToTopic != null){
            usrSubIdToTopic.clear();
        }
        if(usrTopicToSubId != null) usrTopicToSubId.clear();
        
    }

    public String getTopicBySubId(Integer subId){
        return usrSubIdToTopic.get(subId);
    }

    public int getSubIdByTopic(String topic){
        return usrTopicToSubId.get(topic);
    }
    
    public Integer getConnectionId(){
        return connectionId;
    }
    public String getAcceptVersion(){
        return acceptVersion;
    }

    public String getUserName(){
        return usrName;
    }
    public String getUserPasscode(){
        return usrPassCode;
    }
    
}
