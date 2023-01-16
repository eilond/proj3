#include "../include/StompClient.h"
#include "../include/StompProtocol.h"
#include "../include/Event.h"
#include <vector>
#include <thread>
#include <mutex>
#include <memory>
#include <fstream>
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
	if(frame.getType()==UNSUBSCRIBE){
		string a = channel_to_id[frame.getHeaders()["destination"]];
		channel_to_id.erase(frame.getHeaders()["destination"]);
        frame.modifyHeader("id",a);
		disconect_recit_delivered = avilable_id;
	}
};
void StompClient::Connect(){isconnected_ = true;};

void StompClient::Disonnect(){isconnected_ = false;};
bool StompClient::isConnected(){return isconnected_;};
int StompClient::getDisconectRecit(){return disconect_recit_delivered;};
void StompClient::DisconectRecitTrue(){disconect_recit_delivered = 1;};
void StompClient::DisonnectRecitFalse(){disconect_recit_delivered = 0;};
void StompClient::setDisonnectRecit(int a){
	disconect_recit_delivered = a;
};
StompClient::StompClient(string host, short port):summary_(),game_(),handler_(host,port),channel_to_id(),currentUser(),m_lock(){
};
StompClient::~StompClient(){
};
ConnectionHandler& StompClient::getHandler(){return handler_;};
void StompClient::printHandler(){std::cout<<&handler_<<std::endl;};
Summary& StompClient::getSummary(){return summary_;};
string StompClient::getName(){return currentUser;};
bool is_int(const std::string& s)
{
    std::stringstream ss(s);
    int temp;
    return (ss >> temp) && (ss.eof());
}
StompClient client("127.0.0.1",7777);
int main(int argc, char *argv[]) {
	if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
	while(1){
		try{
 			const short bufsize = 1024;
			char buf[bufsize];
			std::cin.getline(buf, bufsize);
			string loginString(buf);
			vector<string> loginline = splitMessege(loginString," ");
			try{
				vector<string> host_port = splitMessege(loginline[1],":");
				if(!is_int(host_port[1])){
					throw invalid_argument("port isnt instance of string");
				}
				host = host_port[0];
				port = stoi(host_port[1]);
			}
			catch(invalid_argument& e){
				cout<<e.what()<<endl;
			}catch(exception& e){}
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
			cout<<e.what()<< endl;
		}
	};
	return 0;
}