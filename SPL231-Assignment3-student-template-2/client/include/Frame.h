#pragma once

#include <string>
#include <iostream>
#include <map>
#include <vector>
#include <ctime>
#include <cstdlib>
#include "../include/Event.h"

using namespace std;

enum Origin{nullOrigin,Server,Client};
enum ConnectionType{nullType,CONNECT,SEND,SUBSCRIBE,UNSUBSCRIBE,DISCONNECT,CONNECTED,MESSAGE,RECEIPT,ERROR};
class Frame{
    private:
        Origin origin;
        ConnectionType type;
        map<string,string> headers;
        string recitpt_id = std::to_string(rand()%100);
        string body ="";
        void createType(string connection) const;
        void createServerFrame(vector<string>& vec);
        void createClientFrame(vector<string>& vec);
    public:
        vector<std::string> splitMessege(std::string s,std::string delimiter);
        string& getBody();
        Frame(string messege,Origin from);
        Frame(const Frame&);
        Frame& operator=(const Frame&);
        ~Frame();
        //String representation of the Frame;
        string toString();
        ConnectionType getType() const;
        Origin getOrigin() const;
        string getTypeName() const;
        string getOriginName() const;
        void modifyHeader(string key,string value);
        map<string,string>& getHeaders();
};