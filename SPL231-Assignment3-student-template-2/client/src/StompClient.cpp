#include "../include/StompClient.h"
#include "../include/StompProtocol.h"
#include "../include/Event.h"
#include <vector>
#include <thread>
#include <mutex>
#include <memory>

#include "../include/KeyPressThread.h"
using namespace std;
vector<string> splitMessege(string s,string delimiter){
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
 void StompClient::setName(string name){
	currentUser = name;
 }
void StompClient::checkFrame(Frame& frame){
	if(frame.getType()==SUBSCRIBE){
		string a = std::to_string(avilable_id);
		string b = std::to_string(avilable_recipt);
		frame.modifyHeader("id",a); 
        // frame.modifyHeader("recipt",b);
		channel_to_id.insert({frame.getHeaders()["destination"],a});
		avilable_id++;
		avilable_recipt++;
	}
	if(frame.getType()==DISCONNECT){
		string b = std::to_string(avilable_recipt);
        frame.modifyHeader("receipt",b);
		disconect_recit_delivered = avilable_id;
		avilable_id++;
		avilable_recipt++;
	}
};
void StompClient::Connect(){isconnected_ = true;};

void StompClient::Disonnect(){isconnected_ = false;};
bool StompClient::isConnected(){return isconnected_;};
int StompClient::getDisconectRecit(){return disconect_recit_delivered;};
void StompClient::DisconectRecitTrue(){disconect_recit_delivered = 1;};
void StompClient::DisonnectRecitFalse(){disconect_recit_delivered = 0;};
void StompClient::setDisonnectRecit(int a){
	// disconect_recit_delivered = stoi(a);
	// cout<<stoi(a)<<endl;
	// if(stoi(a)==0){
	// 	cout<<"here"<<endl;
	// 	throw std::invalid_argument("somthing wrong with recipt id resived from server");}
	disconect_recit_delivered = a;
};
StompClient::StompClient(string host, short port):summary_(),game_(),handler_(host,port),channel_to_id(),currentUser(){
	// static ConnectionHandler tmp(host,port);
	// handler_ = &tmp;
};
StompClient::~StompClient(){
};
ConnectionHandler& StompClient::getHandler(){return handler_;};
// void StompClient::updateHandler(string host, short port){handler_.updateHandler(host,port);};
void StompClient::printHandler(){std::cout<<&handler_<<std::endl;};
Summary& StompClient::getSummary(){return summary_;};
string StompClient::getName(){return currentUser;};

StompClient client("127.0.0.1",7777);
int main(int argc, char *argv[]) {
	if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
	// names_and_events nne = parseEventsFile("data/events1_partial.json");
	// for(auto e: nne.events){
    //     cout<<e.to_Frame_string("yuval")<<endl;
    //     }
	while(1){
		try{
			const short bufsize = 1024;
			char buf[bufsize];
			std::cin.getline(buf, bufsize);
			string loginString(buf);
			vector<string> loginline = splitMessege(loginString," ");
			if((loginline.size()!=4) || (loginline[0]!="login")){
				throw std::invalid_argument("First Log To Server");
			}
			StompClient client(host,port);
			StompProtocol protocol(&client);
			protocol.Connect(loginline[2],loginline[3]);
			thread terminal(&StompProtocol::proccesFromClient,protocol);
			thread sever(&StompProtocol::proccesFromServer,protocol);
			terminal.join();
			sever.join();
		}
		catch(std::exception& e){
			cout<< e.what() << endl;
		}
	};
	// StompClient client(host,port);
	// Frame a("join 111",Client);
	// client.checkFrame(a);
	// std::cout<<a.toString()<<std::endl;
	// names_and_events nne = parseEventsFile("data/events1_partial.json");
	// std::string t = nne.events[0].get_team_a_name();
	// std::string x = nne.events[1].get_discription();
	// t[0]=std::tolower(t[0]);
	// std::cout<<t<<std::endl;
	// std::cout<<x<<std::endl;
	return 0;
}