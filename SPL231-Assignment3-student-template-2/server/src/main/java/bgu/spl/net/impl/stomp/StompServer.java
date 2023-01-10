package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        
        if (args.length == 0) {
            args = new String[]{"7777", "reactor"};
        }
    
        if (args.length < 2) {
            System.out.println("you must supply two arguments: port, type of server implementation");
            System.exit(1);
        }

        if(args[1].equals("reactor")){
            try {

                int port =  Integer.parseInt(args[0]);
                Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, //port
                    () -> new StompProtocol(), //protocol factory
                    StompEncDec::new //message encoder decoder factory
                ).serve();
                
            } catch (NumberFormatException e) {
                System.out.println("invalid port");
            }

        } 
        else if(args[1].equals("tpc")){
            try {

                int port =  Integer.parseInt(args[0]);
                Server.threadPerClient(
                    port, //port
                    () -> new StompProtocol(), //protocol factory
                    StompEncDec::new //message encoder decoder factory
                ).serve();

            } catch (NumberFormatException e) {
                System.out.println("invalid port");
            }

        } 
        else{
            System.out.println("server only supports tpc or reactor modes");
        }
        

        
        
    }
}
