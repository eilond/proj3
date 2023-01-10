#include "../include/StompProtocol.h"

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
        if(!client->isConnected()&&(recived.getType()==RECEIPT)){
            client->handshake_ = (stoi(recived.getHeaders()["id"]) == client->getDisconectRecit());
            client->getHandler().close();
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
            cout<<"here"<<endl;
				for(auto e: client->getSummary().getMap()[client->getName()]){
					cout<<"\u001b[31m"+e.to_Frame_string("yuval")+"\u001b[0m"<<endl;
				}
			}
         cout<<line.substr(0,6)<<endl;
        cout<<(line.substr(0,6)!="report")<<endl;
        cout<<(line.find(' ')==0)<<endl;
        if((line.substr(0,6)!="report")){
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
        else{
            handleReport(line);
        }
    }

};
void StompProtocol::handleReport(string messege){
    string file = "data/"+messege.substr(messege.find(' ')+1);
    string userName = client->getName();
    names_and_events nne = parseEventsFile("data/events1.json");
    client->getSummary().add_events_to_user(userName,nne.events);
    for(Event e : nne.events){
        if(!client->getHandler().sendFrameAscii(e.to_Frame_string(userName),'\0')){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                client->Disonnect();
                break;
            }
    }
    for(auto e: client->getSummary().getMap()[userName]){
        cout<<"\u001b[31m"+e.to_Frame_string("yuval")+"\u001b[0m"<<endl;
        }
    }