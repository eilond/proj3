package bgu.spl.net.impl.stomp;

import java.util.HashMap;

public class Frame<T>{

    enum CommandType{
        CONNECT,
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        DISCONNECT,
        ERROR;
    }

    public CommandType commandType;
    public HashMap<String,String> commandHeaders;
    public String commandBody ="";
    public T frameSent;
    
    public Frame(T frame){
        frameSent = frame;
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
    
                    if(!lines[i].equals("\u0000")){
                        if(startBody){
                            commandBody = commandBody + lines[i] + "\n";
                        }
                        else{
                            String[] header = lines[i].split(":");
                            commandHeaders.putIfAbsent(header[0].trim(), header[1].trim());
                            
                        }
    
                        
                    }
                }
            }
           
            
        }

    }


    private void createType(String connection){
        switch(connection){
            case "CONNECT":
                
                commandType = CommandType.CONNECT;
                break;

            case "SEND":
            
                commandType = CommandType.SEND;
                break;

            case "SUBSCRIBE":
            
                commandType = CommandType.SUBSCRIBE;
                break;

            case "UNSUBSCRIBE":
            
                commandType = CommandType.UNSUBSCRIBE;
                break;
            case "DISCONNECT":
                
                commandType = CommandType.DISCONNECT;
                break;
            default:

                commandType = CommandType.ERROR;
            
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
    public T getFrameSent(){
        return frameSent;
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
    // public static void main(String[] args) {
    //     String a = "CONNECT\naccept - version :1.2\n host : stomp . cs . bgu . ac . il\nlogin : meni\npasscode : films\n\nHello Topic a\n^@";
    //     Frame<String> frame = new Frame<>(a);
    //     System.out.println(frame);
    //     a = "SEND\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);
    //     a = "SUBSCRIBE\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);
    //     a = "DISCONNECT\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);

    //     a = "EDCKJEIFJE\nsubscription :78\nmessage - id :20\n\nHello Topic a\n^@";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);
    //     a = "";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);
    //     a = " ";
    //     frame = new Frame<>(a);
    //     System.out.println(frame);

        
    // }
}