#pragma once

#include "../include/Summary.h"
#include "../include/Game.h"
#include "../include/ConnectionHandler.h"
#include "../include/Frame.h"
#include <memory>
#include <map>
#include <mutex>

class StompClient{
    private:
        Summary summary_;
        Game game_;
        ConnectionHandler handler_;
        std::map<std::string,std::string> channel_to_id;
        bool isconnected_ = false;
        
    public:
        int disconect_recit_delivered = 1;
        int avilable_id=1;
        int avilable_recipt=1;
        std::string currentUser;
        mutex m_lock;
        // ConnectionHandler handler_;
        bool handshake_ = false;
        // void updateHandler(string host, short port);
        void setName(string name);
        string getName();
        void checkFrame(Frame& frame);
        void Connect();
        void Disonnect();
        bool isConnected();
        int getDisconectRecit();
        void DisconectRecitTrue();
        void DisonnectRecitFalse();
        void setDisonnectRecit(int);
        ConnectionHandler& getHandler();
        StompClient(string host, short port);
        // StompClient(Summary,Game,ConnectionHandler);
        ~StompClient();
        void printHandler();
        Summary& getSummary();
};