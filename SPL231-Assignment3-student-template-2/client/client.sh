#!/bin/bash

# compile the StompWCIClient.cpp file
g++ -o StompWCIClient StompWCIClient.cpp

# run the StompWCIClient program with the specified command-line arguments
./bin/StompWCIClient 127.0.0.1 7777
login y yuval yuval 
join /germany_japan 
report events1.json 
report events1_partial.json 
summary germany_japan yuval client.txt
