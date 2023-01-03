package bgu.spl.net.srv;

import java.util.HashMap;

public class Frame{
    enum Origin{
        Server,
        Client;
    }
    enum ConnectionType{
        CONNECT,
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        DISCONNECT,
        CONNECTED,
        MESSAGE,
        RECEIPT,
        ERROR;
    }
    Origin origin;
    ConnectionType type;
    HashMap<String,String> headers;
    String body ="";
    private void createType(String connection){
        switch(connection){
            case "CONNECT":
                origin = Origin.Client;
                type = ConnectionType.CONNECT;
                break;

            case "SEND":
                origin = Origin.Client;
                type = ConnectionType.SEND;
                break;

            case "SUBSCRIBE":
                origin = Origin.Client;
                type = ConnectionType.SUBSCRIBE;
                break;

            case "UNSUBSCRIBE":
                origin = Origin.Client;
                type = ConnectionType.UNSUBSCRIBE;
                break;
            case "DISCONNECT":
                origin = Origin.Client;
                type = ConnectionType.DISCONNECT;
                break;
            case "CONNECTED":
                origin=Origin.Server;
                type = ConnectionType.CONNECTED;
                break;
            case "MESSAGE":
                origin=Origin.Server;
                type = ConnectionType.MESSAGE;
                break;
            case "RECEIPT":
                origin=Origin.Server;
                type = ConnectionType.RECEIPT;
                break;
            case "ERROR":
                origin=Origin.Server;
                type = ConnectionType.ERROR;
                break;
            default:
                throw new IllegalArgumentException("Invalid connection request = " + connection);
            }
        }
    Frame(String frame){
        headers = new HashMap<String,String>();
        String[] lines = frame.split("\n");
        createType(lines[0]);
        boolean startBody = false;
        for(int i = 1; i<lines.length; i++){
            if(!startBody && lines[i].equals("")){
               startBody = true;
            }
            if(!lines[i].equals("^@")){
                if(startBody)
                    body = body + lines[i];
                else{
                    String[] header = lines[i].split(":");
                    headers.putIfAbsent(header[0].replaceAll(" ", ""),header[1].replaceAll(" ", ""));
                }
            }
        }
    }
    @Override
    public String toString() {
        String aString = "-------------------------------------------------\n";
        aString = aString+"--------" +origin+"\n";
        aString = aString + "--------" + type+"\n";
        aString = aString + "--------" + headers+"\n";
        aString = aString + "--------" + body+"\n";
        aString = aString + "-------------------------------------------------\n";
        return aString;
    }
    // public static void main(String[] args) {
    //     String a = "MESSAGE\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
    //     Frame frame = new Frame(a);
    //     System.out.println(frame);
    // }
}