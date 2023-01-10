#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/StompProtocol.h"
#include "../include/StompClient.h"
#include <stdlib.h>
class KeyPressThread{
    private:
        ConnectionHandler* connectionHandler;
        StompProtocol* protocol;
        StompClient* client;
    public:
        void Run();
        static void Run1();
        KeyPressThread(StompClient* client);
        KeyPressThread(ConnectionHandler* connectionHandler,StompProtocol* protocol);
        StompClient* getClient();
};