#!/bin/bash

# compile the StompWCIClient.cpp file
g++ -o StompWCIClient StompWCIClient.cpp

# run the StompWCIClient program with the specified command-line arguments
./bin/StompWCIClient 127.0.0.1 7777
login 127.0.0.1:7777 yuval yuval 
join germany_japan 
exit germany_japan
join germany_japan
report events1_partial.json
summary germany_japan yuval yuvalByYuval.txt

summary germany_japan yuval yuvalByEilon.txt

summary germany_japan yuval yuvalByYuval

summary germany_japan yuval yuvalByEilon

summary germany_japan eilon eilonByYuval.txt
#should fail to connect

./bin/StompWCIClient 127.0.0.1 7777
login 127.0.0.1:7787 yuval yuval
#already connected

./bin/StompWCIClient 127.0.0.1 7777
login 127.0.0.1:7777 eilon duchan
join germany_japan

./bin/StompWCIClient 127.0.0.1 7777
login 127.0.0.1:7777 guy duchan

summary germany_japan eilon client.txt
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="7777 tpc"
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="7777 reactor"
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="8888 reactor"