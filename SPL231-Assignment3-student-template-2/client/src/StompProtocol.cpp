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
        // cout<<"\u001b[31m________________________________________"<<endl;
        // cout<<line<<endl;
        // cout<<"_____________________________________\u001b[0m"<<endl;
        if (!client->getHandler().sendFrameAscii(line,'\0')) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            }
        std::string recive;
        if(!client->getHandler().getFrameAscii(recive,'\0')){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }
        Frame fromServer(recive,Server);
        if(fromServer.getType()!=CONNECTED){
            cout<<"\033[0;31m"+string(47,'_')<<endl;
            cout<<recive<<endl;
            cout<<"\033[4m"+string(14,' ')+"recived from server"+string(14,' ')+"\u001b[0m"<<endl;
          throw std::invalid_argument("\033[4m\033[0;31m"+string(14,' ')+"no connection"+string(14,' ')+"\u001b[0m");
        }
        else{
            // cout<<"\u001b[33m"+string(47,'_')<<endl;
            // cout<<recive<<endl;
            // cout<<"\033[4m"+string(14,' ')+"recived from server"+string(14,' ')+"\u001b[0m"<<endl;
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
        if(!client->isConnected()&&(recived.getType()==ERROR)){
            client->handshake_ = true;
            client->getHandler().close();
            client->Disonnect();
        }
        if(recived.getType()!=MESSAGE){
            client->m_lock.lock();
            // if(recived.getType()==ERROR){cout<<"\033[0;31m"+string(47,'_')<<endl;}
            // else{cout<<"\u001b[33m"+string(47,'_')<<endl;}
            // cout<<recive<<endl;
            // cout<<"\033[4m"+string(14,' ')+"recived from server"+string(14,' ')+"\u001b[0m"<<endl;
            client->m_lock.unlock();
        }
    }
};
void StompProtocol::proccesFromClient(){
     while(client->isConnected()){
            try{
            const short bufsize = 1024;
            char buf[bufsize];
            cout<<"\033[0;32m";
            std::cin.getline(buf, bufsize);
            cout<<"\u001b[0m"<<endl;
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
                cout<<"\u001b[31mClient already logged in\u001b[0m"<<endl;
                client->m_lock.unlock();
            }
            else{
                client->checkFrame(frameToSend);
                line = frameToSend.toString();
                //about to send
                client->m_lock.lock();
                // cout<<"\u001b[32m"+string(47,'_')<<endl;
                // cout<<line<<endl;
                // cout<<string(47,'_')+"\u001b[0m"<<endl;
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
                if(frameToSend.getType()==UNSUBSCRIBE){
                    client->m_lock.lock();
                    cout<<"\u001b[33mExited channel "<<frameToSend.getHeaders()["destination"].substr(1)+"\u001b[0m"<<endl;
                    client->m_lock.unlock();
                }
            }
        }
        catch(exception& e){
                client->m_lock.lock();
                cout<<"\033[0;31m"<<e.what()<<"\u001b[0m"<<endl;
                client->m_lock.unlock();
            }
    }

};
void StompProtocol::handleReport(string messege){
    string userName = client->getName();
    string path = "data/"+messege.substr(7);
    names_and_events nne = parseEventsFile(path);
    // Event e("MESSEGE\ndestination:/germany_japan\nmessage-id:1\nsubscription:1\nuser:yuval\nteam a:germany\nteam b:japan\nevent name:final whistle\ntime:5400\ngeneral game updates:\n\tactive:false\nteam a updates:\n\tpossession:51\nteam b updates:\ndiscription:Well, what a way to kick off Group E! Germany sit at the bottom of\0");
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