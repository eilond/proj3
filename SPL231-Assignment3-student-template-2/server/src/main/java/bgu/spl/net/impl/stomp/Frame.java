package bgu.spl.net.impl.stomp;

import java.util.HashMap;

public class Frame<T>{
    // enum Origin{
    //     Server,
    //     Client;
    // }

    enum CommandType{
        CONNECT,
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        DISCONNECT,
        ERROR;
    }

    // public Origin CommandOrigin;
    public CommandType commandType;
    public HashMap<String,String> commandHeaders;
    public String commandBody ="";


    private void createType(String connection){
        switch(connection){
            case "CONNECT":
                // CommandOrigin = Origin.Client;
                commandType = CommandType.CONNECT;
                break;

            case "SEND":
                // CommandOrigin = Origin.Client;
                commandType = CommandType.SEND;
                break;

            case "SUBSCRIBE":
                // CommandOrigin = Origin.Client;
                commandType = CommandType.SUBSCRIBE;
                break;

            case "UNSUBSCRIBE":
                // CommandOrigin = Origin.Client;
                commandType = CommandType.UNSUBSCRIBE;
                break;
            case "DISCONNECT":
                // CommandOrigin = Origin.Client;
                commandType = CommandType.DISCONNECT;
                break;
            // case "CONNECTED":
            //     CommandOrigin=Origin.Server;
            //     commandType = CommandType.CONNECTED;
            //     break;
            // case "MESSAGE":
            //     CommandOrigin=Origin.Server;
            //     commandType = CommandType.MESSAGE;
            //     break;
            // case "RECEIPT":
            //     CommandOrigin=Origin.Server;
            //     commandType = CommandType.RECEIPT;
            //     break;
            // case "ERROR":
            //     CommandOrigin=Origin.Server;
            //     commandType = CommandType.ERROR;
            //     break;
            default:
                commandType = CommandType.ERROR;
            
        }
    }



    Frame(T frame){
        commandHeaders = new HashMap<String,String>();
        if(frame instanceof String){
            
            String[] lines = ((String)frame).split("\n");
            createType(lines[0]);
            
            if(commandType != CommandType.ERROR){
                boolean startBody = false;
                for(int i = 1; i<lines.length; i++){
    
                    if(!startBody && lines[i].equals("")){
                       startBody = true;
                    }
    
                    if(!lines[i].equals("^@")){
                        if(startBody){
                            commandBody = commandBody + lines[i];
                        }
                        else{
                            String[] header = lines[i].split(":");
                            commandHeaders.putIfAbsent(header[0].replaceAll(" ", ""),header[1].replaceAll(" ", ""));
                        }
    
                        
                    }
                }
            }
        }

    }
                
        



    public CommandType getCommandType(){
        return commandType;
    }
    public HashMap<String,String> getCommandHeaders(){
        return commandHeaders;
    }
    public String getCommandBody(){
        return commandBody;
    }
    
    @Override
    public String toString() {
        String aString = "-------------------------------------------------\n";
        // aString = aString+"--------" +CommandOrigin+"\n";
        aString = aString + "--------" + commandType+"\n";
        aString = aString + "--------" + commandHeaders+"\n";
        aString = aString + "--------" + commandBody+"\n";
        aString = aString + "-------------------------------------------------\n";
        return aString;
    }
    public static void main(String[] args) {
        String a = "CONNECT\naccept - version :1.2\n host : stomp . cs . bgu . ac . il\nlogin : meni\npasscode : films\n\nHello Topic a\n^@";
        Frame<String> frame = new Frame<>(a);
        System.out.println(frame);
        a = "SEND\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
        frame = new Frame<>(a);
        System.out.println(frame);
        a = "SUBSCRIBE\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
        frame = new Frame<>(a);
        System.out.println(frame);
        a = "DISCONNECT\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
        frame = new Frame<>(a);
        System.out.println(frame);

        a = "EDCKJEIFJE\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
        frame = new Frame<>(a);
        System.out.println(frame);
        a = "";
        frame = new Frame<>(a);
        System.out.println(frame);
        a = " ";
        frame = new Frame<>(a);
        System.out.println(frame);

        
    }
}