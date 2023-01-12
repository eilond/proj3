#!/bin/bash

# compile the StompWCIClient.cpp file
g++ -o StompWCIClient StompWCIClient.cpp

# run the StompWCIClient program with the specified command-line arguments
./bin/StompWCIClient 127.0.0.1 7777
login 127.0.0.1:7777 yuval yuval 
login 127.0.0.1:7777 eilon duchan
login 127.0.0.1:7777 guy duchan
login 127.0.0.1:7777 roee duchan
login 127.0.0.1:7777 tomer duchan
join germany_japan 
exit germany_japan
report events1.json 
report events1_partial.json 
summary germany_japan yuval clien555t.txt
summary germany_japan yuval clien555tasdad.txt
summary germany_japan yuval clie.txt
summary germany_japan eilon client.txt
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="7777 tpc"