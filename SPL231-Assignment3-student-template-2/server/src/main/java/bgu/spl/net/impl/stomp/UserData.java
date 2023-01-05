package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;


public class UserData {
    int connectionId;

    String acceptVersion;
    String host;
    String usrName;
    String usrPassCode;

    Map<Integer,String> usrSubs; //maps subID to topic

    UserData(int connectionId){
        this.connectionId = connectionId;
    }


    public void completeUser(String acceptVersion, String host, String usrName, String usrPassCode ){
        this.acceptVersion = acceptVersion;
        this.host = host;
        this.usrName = usrName;
        this.usrPassCode = usrPassCode;
        this.usrSubs = new HashMap<Integer,String>();
    }

    public void addSub(String topic, Integer subID){
        usrSubs.put(subID, topic);
    }

    public String findSubscription(Integer subID){
        return usrSubs.get(subID);
    }
    
}
