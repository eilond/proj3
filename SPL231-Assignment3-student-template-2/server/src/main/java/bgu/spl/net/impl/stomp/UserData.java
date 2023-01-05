package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;


public class UserData {
    int connectionId;

    String acceptVersion;
    String host;
    String usrName;
    String usrPassCode;

    //Map<String , Integer> usrSubs; //maps topic (that the user is subbed to) to subID 
    Map<Integer , String> usrSubs; //maps subID to topic (that the user is subbed to) 

    UserData(int connectionId){
        this.connectionId = connectionId;
    }


    public void completeUser(String acceptVersion, String host, String usrName, String usrPassCode ){
        this.acceptVersion = acceptVersion;
        this.host = host;
        this.usrName = usrName;
        this.usrPassCode = usrPassCode;
        this.usrSubs = new HashMap<Integer , String>();
    }

    public void addSub(Integer subID , String topic){
        usrSubs.put(subID ,topic);
    }

    public void removeSub(Integer subID){
        usrSubs.remove(subID);
    }

    public void removeAllSubs(){ 
        for(Map.Entry<Integer , String> entry: usrSubs.entrySet()){
            usrSubs.remove(entry.getKey());
        }
        
    }
    

    // public Map<Integer , String> removeAllSubs(){ //removing user subs then returning clone object for protocol to delete in connections
    //     Map<Integer , String> retMap = usrSubs;

    //     for(Map.Entry<Integer , String> entry: usrSubs.entrySet()){
    //         usrSubs.remove(entry.getKey());
    //     }

    //     return retMap;
    // }

    // public Integer findSubscriptionId(String topic){
    //     return usrSubs.get(topic);
    // }

    public String getTopicBySubId(Integer subId){
        return usrSubs.get(subId);
    }
    
    public Integer getConnectionId(){
        return connectionId;
    }
    
}
