package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class UserData {
    int connectionId;
    String usrName;
    String usrPassCode;
    Map<Integer,String> usrSubs; //maps subID to topic

    UserData(int connectionId){
        this.connectionId = connectionId;
    }


    public void completeUser(String usrName, String usrPassCode,List<Pair<String,Integer>> usrSubs ){
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
