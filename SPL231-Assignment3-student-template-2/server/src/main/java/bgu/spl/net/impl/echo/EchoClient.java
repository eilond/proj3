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
            //TEST 1
            String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n\u0000";
            msg1 = msg1 + "SUBSCRIBE\ndestination:shug\nid:78\n\n\u0000";
            msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:79\n\n\u0000";
            msg1 = msg1 + "SEND\ndestination:shug\n\nHello topic a\u0000";


            //TEST 2
            // String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n\u0000";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:shug\nid:78\n\n\u0000";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:79\n\n\u0000";
            // msg1 = msg1 + "UNSUBSCRIBE\nid:78\n\n\u0000";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\u0000";

            // String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n\u0000";
            // msg1 = msg1 + "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:eilon\npasscode:hilba\n\n\u0000";
            // msg1 = msg1 + "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n\u0000"; //ERROR
            // msg1 = msg1 + "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:eilon\npasscode:hilba\n\n\u0000";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:shug\nid:78\n\n\u0000";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:79\n\n\u0000";
            // msg1 = msg1 + "UNSUBSCRIBE\nid:78\n\n\u0000";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:shug\nid:79\n\n\u0000";
            // msg1 = msg1 + "DISCONNECT\nreceipt:77\n\n\u0000";
            // msg1 = msg1 + "SEND\ndestination:shug\n\nHello topic a\u0000";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\u0000";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n";
            // msg1 = msg1 + "SUBSCRIBE\ndestination:hilba\nid:78\n\n";

            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // msg1 = msg1 + "DISCONNECT\nreceipt:77\n\n^@";
            // msg1 = msg1 + "SEND\ndestination:hilba\n\nHello topic a\n^@";
            // String msg1 = "A\u0000";
            



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

           


            // try (Socket sock1 = new Socket(args[0], 7777);
            //     BufferedReader in1 = new BufferedReader(new InputStreamReader(sock1.getInputStream()));
            //     BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(sock1.getOutputStream()))){
            //         msg1 = "CONNECT\naccept-version:1.2\nhost:stomp . cs . bgu . ac . il\nlogin:meni\npasscode:films\n\n\u0000";
            //         msg1 = msg1 + "SEND\ndestination:shug\n\nHello topic a\u0000";
            //         out1.write(msg1);
            //         out1.newLine();
            //         out1.flush();
        
            //         System.out.println("message from server: ");
            //          line  = "";
            //         while(line != null){
            //             line = in1.readLine();
            //             // System.out.println( "    a   ");
            //             System.out.println( line);
        
            //         }
            //     }
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
