#include "../include/StompProtocol.h"
#include <fstream>
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
        // cout<<"\u001b[31m_____________________________________"<<endl;
        // cout<<line<<endl;
        // cout<<"_____________________________________\u001b[0m"<<endl;
        if (!client->getHandler().sendFrameAscii(line,'\0')) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            }
        std::string recive;
        if(!client->getHandler().getFrameAscii(recive,'\0')){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }
        cout<<"\u001b[32m_____________________________________"<<endl;
        cout<<recive<<endl;
        cout<<"_____________________________________\u001b[0m"<<endl;
        Frame fromServer(recive,Server);
        if(fromServer.getType()!=CONNECTED){
          throw std::invalid_argument("no connection");
        }
        else{
        //     // client->isConnected=true;
            client->Connect();
            client->setName(name);
        }
    }
};
void StompProtocol::proccesFromServer(){
    while(client->isConnected()&&!client->handshake_){
        std::string recive;
        if(!client->getHandler().getFrameAscii(recive,'\0')){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            client->Disonnect();
            break;
        }
        Frame recived(recive,Server);
        cout<<"\u001b[32m_____________________________________"<<endl;
        cout<<recived.getType()<<endl;
        cout<<"_____________________________________\u001b[0m"<<endl;
        if(recived.getType()==MESSAGE){
            proccesMESSEGE(recived,recive);
        }
        // the addition of the || in the end might not work;
        //join /germany_japan
        //report events1.json
        map<string, map<string,vector<Event>>>& Mappp = client->getSummary().getMap();
        if(!client->isConnected()&&((recived.getType()==RECEIPT)||(recived.getType()==ERROR))){
            client->handshake_ = (stoi(recived.getHeaders()["id"]) == client->getDisconectRecit());
            client->getHandler().close();
            client->Disonnect();
        }
        cout<<"\u001b[32m_____________________________________"<<endl;
        cout<<recive<<endl;
        cout<<"_____________________________________\u001b[0m"<<endl;
    }
};
void StompProtocol::proccesFromClient(){
     while(client->isConnected()){
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
		std::string line(buf);
        if((line.substr(0,5)=="print")){
                try{
                    cout<<line.substr(6)<<endl;
                    vector<Event> a =client->getSummary().getMap()[line.substr(6)]["germany_japan"];
                    for(auto e: client->getSummary().getMap()[line.substr(6)]["germany_japan"]){
					    cout<<"\u001b[31m"+e.to_Frame_string("yuval")+"\u001b[0m"<<endl;
                    }
                }
                catch(std::exception e){
                    for(auto e: client->getSummary().getMap()[client->getName()]["germany_japan"]){
                        cout<<"\u001b[31m"+e.to_Frame_string("yuval")+"\u001b[0m"<<endl;
                    }
                }
			}
        else if((line.substr(0,6)=="report")){
                   handleReport(line);
        }
        else if((line.substr(0,7)=="summary")){
            vector<string> lines = SplitMessege(line, " ");
            // client->getSummary().get_user_game_summary(lines[2],lines[1],lines[3]);
            vector<Event> nne = client->getSummary().getMap()[lines[2]][lines[1]];
            Event t(nne);
            for(Event e: nne){
                cout<<e.to_Frame_string("yuval")<<endl;
            }
            string a = t.to_Summary();
            string path1 = "./data/client.text";
            std::ofstream file(path1);
            cout<<a<<endl;
            file << a;
            file.close();;
        }
        else if((line.substr(0,5)=="login")){
                   cout<<"\u001b[31mClient already logged in\u001b[0m"<<endl;
        }
        else{
            Frame frameToSend(line,Client);
            client->checkFrame(frameToSend);
            line = frameToSend.toString();
            cout<<"\u001b[31m_____________________________________"<<endl;
            cout<<line<<endl;
            cout<<"_____________________________________\u001b[0m"<<endl;
            if(frameToSend.getType() == DISCONNECT){
                client->Disonnect();
            }
            if(!client->getHandler().sendFrameAscii(line,'\0')){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                client->Disonnect();
                break;
            }
        }
    }

};
void StompProtocol::handleReport(string messege){
    string userName = client->getName();
    string path = "data/"+messege.substr(7);
    names_and_events nne = parseEventsFile(path);
    // Event e("MESSEGE\ndestination:/germany_japan\nmessage-id:1\nsubscription:1\nuser:yuval\nteam a:germany\nteam b:japan\nevent name:final whistle\ntime:5400\ngeneral game updates:\n\tactive:false\nteam a updates:\nteam b updates:\ndiscription:Well, what a way to kick off Group E! Germany sit at the bottom of\0");
    for(Event e : nne.events){
        std::cout << e.to_Frame_string(userName)<< std::endl;
        if(!client->getHandler().sendFrameAscii(e.to_Frame_string(userName),'\0')){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                client->Disonnect();
                // break;
            }
    }
}
void StompProtocol::proccesMESSEGE(Frame& recived,string& messege){
    map<string,string>& headers = recived.getHeaders();
    string userName = headers["user"];
    string clientName = client->getName();
    string game_name = headers["team_a"]+"_"+headers["team_b"];
    // Event t(messege);
    // cout<<"\u001b[36m_____________________________________"<<endl;
    // cout<<messege<<endl;
    // cout<<"_____________________________________________________"<<endl;
            // cout<<t.to_Frame_string("yuval")<<endl;
            // cout<<"_____________________________________\u001b[0m"<<endl;
    if(client->getName()==userName){
        client->getSummary().add_event_to_user(clientName,messege);
        return;
    }
    client->getSummary().add_event_to_user(userName,messege);

}