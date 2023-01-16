#include "../include/StompProtocol.h"
#include <fstream>
using namespace std;
vector<string> SplitMessege(string s,string delimiter){
    size_t pos = 0;
    string token;
    vector<string> a;
    while ((pos = s.find(delimiter)) != std::string::npos) {
        token = s.substr(0, pos);
        a.push_back(token);
        s.erase(0, pos + delimiter.length());
    } 
    a.push_back(s);
    return a;   
}
StompProtocol::StompProtocol(StompClient* client_):client(nullptr){
    client = client_;
};
StompClient* StompProtocol:: getClient(){return client;};
void StompProtocol::Connect(string name,string password){
    if(client->getHandler().connect()){
        std::string line = "CONNECT\naccept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:"+name+"\npasscode:"+password+"\n\0";
        if (!client->getHandler().sendFrameAscii(line,'\0')) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            }
        std::string recive;
        if(!client->getHandler().getFrameAscii(recive,'\0')){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }
        Frame fromServer(recive,Server);
        if(fromServer.getType()!=CONNECTED){
            cout<<fromServer.getHeaders()["message"]<<endl;
        }
        else{
            if(fromServer.getType()==CONNECTED){
                client->m_lock.lock();
                std::cout<<"Login successful"<<std::endl;
                client->m_lock.unlock();
            }
            client->Connect();
            client->setName(name);
        }
    }
};
void StompProtocol::proccesFromServer(){
    while(client->isConnected()&&!client->handshake_){
        std::string recive;
        if(!client->getHandler().getFrameAscii(recive,'\0')){
            client->m_lock.lock();
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            client->Disonnect();
            client->m_lock.unlock();
            break;
        }
        Frame recived(recive,Server);
        if(recived.getType()==MESSAGE){
            client->m_lock.lock();
            proccesMESSEGE(recived,recive);
            client->m_lock.unlock();
        } 
        // the addition of the || in the end might not work;
        //join /germany_japan
        // //report events1.json
        // map<string, map<string,vector<Event>>>& Mappp = client->getSummary().getMap();
        if(!client->isConnected()&&(recived.getType()==RECEIPT)){
            client->m_lock.lock();
            client->handshake_ = (stoi(recived.getHeaders()["receipt-id"]) == client->getDisconectRecit());
            client->m_lock.unlock();
            client->getHandler().close();
            client->Disonnect();
        }
        if(recived.getType()==ERROR){
            client->handshake_ = true;
            std::cout<<recived.getHeaders()["message"]<<std::endl;
            client->getHandler().close();
            client->Disonnect();
        }
        if(recived.getType()!=MESSAGE){
            client->m_lock.lock();
            client->m_lock.unlock();
        }
    }
};
void StompProtocol::proccesFromClient(){
     while(client->isConnected()){
            try{
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            Frame frameToSend(line,Client);
            if((line.substr(0,6)=="report")){
                    handleReport(line);
            }
            else if((line.substr(0,7)=="summary")){
                client->m_lock.lock();
                vector<string> lines = SplitMessege(line, " ");
                // client->getSummary().get_user_game_summary(lines[2],lines[1],lines[3]);
                vector<Event> nne = client->getSummary().getMap()[lines[2]][lines[1]];
                Event t(nne);
                string a = t.to_Summary();
                string path1 = "./data/"+lines[3];
                std::ofstream file(path1);
                file << a;
                file.close();
                client->m_lock.unlock();
            }
            else if((line.substr(0,5)=="login")){
                client->m_lock.lock();
                cout<<"The Client is already logged in, log out before trying again"<<endl;
                client->m_lock.unlock();
            }
            else{
                client->checkFrame(frameToSend);
                line = frameToSend.toString();
                client->m_lock.lock();
                client->m_lock.unlock();
                if(frameToSend.getType() == DISCONNECT){
                    client->Disonnect();
                }
                if(!client->getHandler().sendFrameAscii(line,'\0')){
                    client->m_lock.lock();
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    client->Disonnect();
                    client->m_lock.unlock();
                    break;
                }
                if(frameToSend.getType()==SUBSCRIBE){
                    client->m_lock.lock();
                    cout<<"Joined channel "<<frameToSend.getHeaders()["destination"].substr(1)<<endl;
                    client->m_lock.unlock();
                }
                if(frameToSend.getType()==UNSUBSCRIBE){
                    client->m_lock.lock();
                    cout<<"Exited channel "<<frameToSend.getHeaders()["destination"].substr(1)<<endl;
                    client->m_lock.unlock();
                }
            }
        }
        catch(exception& e){
                client->m_lock.lock();
                cout<<e.what()<<endl;
                client->m_lock.unlock();
            }
    }

};
void StompProtocol::handleReport(string messege){
    string userName = client->getName();
    string path = "data/"+messege.substr(7);
    names_and_events nne = parseEventsFile(path);
    for(Event e : nne.events){
        if(!client->getHandler().sendFrameAscii(e.to_Frame_string(userName),'\0')){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                client->Disonnect();
                break;
            }
    }
}
void StompProtocol::proccesMESSEGE(Frame& recived,string& messege){
    map<string,string>& headers = recived.getHeaders();
    string userName = headers["user"];
    string clientName = client->getName();
    string game_name = headers["team_a"]+"_"+headers["team_b"];
    if(client->getName()==userName){
        client->getSummary().add_event_to_user(clientName,messege);
        return;
    }
    client->getSummary().add_event_to_user(userName,messege);

}