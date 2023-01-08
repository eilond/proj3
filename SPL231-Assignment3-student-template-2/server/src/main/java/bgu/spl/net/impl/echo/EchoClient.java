package bgu.spl.net.impl.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        
        if (args.length == 0) {
            args = new String[]{"localhost", "^@"};
        }
    
        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }
    
        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
    
            // System.out.println("sending message to server");
            
            // String CONNECT = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n^@";
            // // String SUBSCRIBE = "SUBSCRIBE\ndestination:hilba\nid:78\n\n^@";
            // // String SEND = "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // // //String SEND = "SEND\ndestination :/ topic / a\n\nHello topic a\n^@";
            // // String UNSUBSCRIBE = "UNSUBSCRIBE\nid:78\n\n^@";
            // String DISCONNECT = "DISCONNECT\nreceipt:77\n\n^@";
            // // String ERROR = "dfdf";
            // protocol.process(CONNECT);:CRIBE);
            // protocol.process(DISCONNECT);
            // protocol.process(CONNECT); //loging into created user
            // // String CONNECTWRONGPASSCODE = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:ilms\n\n^@";
            // // protocol.process(CONNECTWRONGPASSCODE);
            // String CONNECTCONNECTEDUSER = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n^@";
            // protocol.process(CONNECTCONNECTEDUSER);
            // // protocol.process(ERROR);
            // String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n^@";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:78\n\n^@";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // msg1 = msg1 + "UNSUBSCRIBE\nid:78\n\n^@";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:78\n\n^@";

            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // msg1 = msg1 + "DISCONNECT\nreceipt:77\n\n^@";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            String msg1 = "A\u0000";



            out.write(msg1);
            out.newLine();
            out.flush();

            // String msg2 = "SUBSCRIBE\ndestination:hilba\nid:78\n\n^@";

            // out.write(msg2);
            // out.newLine();
            // out.flush();
    
            // System.out.println("awaiting response");
            
            
            System.out.println("message from server: ");
            String line  = "";
            while(line != null){
                line = in.readLine();
                // System.out.println( "    a   ");
                System.out.println( line);

            }
        }


        
        // if (args.length == 0) {
        //     args = new String[]{"localhost", "hello"};
        // }

        // if (args.length < 2) {
        //     System.out.println("you must supply two arguments: host, message");
        //     System.exit(1);
        // }

        // //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        // try (Socket sock = new Socket(args[0], 7777);
        //         BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        //         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

        //     System.out.println("sending message to server");
        //     out.write(args[1]);
        //     out.newLine();
        //     out.flush();

        //     System.out.println("awaiting response");
        //     String line = in.readLine();
        //     System.out.println("message from server: " + line);
        // }



    }
}
