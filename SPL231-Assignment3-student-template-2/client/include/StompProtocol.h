#pragma once

#include "../include/ConnectionHandler.h"
#include <vector>
#include "../include/Frame.h"
#include "../include/StompClient.h"
#include "../include/Event.h"
#include <memory>
#include "../include/Summary.h"

using namespace std;
// TODO: implement the STOMP protocol
class StompProtocol
{
private:
        StompClient* client;
    public:
        StompProtocol(StompClient* client);
        StompClient* getClient();
        void Connect(std::string name, std::string password);
        void proccesFromServer();
        void proccesFromClient();
        void handleReport(string messege);
        void proccesMESSEGE(Frame& recived,string& messege);

};
