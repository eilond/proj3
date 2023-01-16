#include "../include/Frame.h"
#include <string>
#include <vector>

vector<string> Frame::splitMessege(string s,string delimiter){
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
void Frame::createType(string connection) const{}
void Frame::createServerFrame(vector<string>& lines){
    bool bodyLines = false;
    for(int i =1; i < (int)lines.size(); i++){
        if(lines[i] == " "){
            bodyLines = true;
        }
        if(lines[i]!="\0" && bodyLines){
            body + lines[i]+"\n";
        }
        else if(lines[i]!= "\0"){
            vector<string> header = splitMessege(lines[i],":");
            headers.insert({header[0],header[1]});
        }

    }
}
// enum ConnectionType{nullType,CONNECT,SEND,SUBSCRIBE,
// UNSUBSCRIBE,DISCONNECT,CONNECTED,MESSAGE,RECEIPT,ERROR};
void Frame::createClientFrame(vector<string>& lines){
    try{
        switch (type)
        {
        case CONNECT:
            headers.insert({"accept-version","1.2"});
            headers.insert({"host",lines[1]});
            headers.insert({"login",lines[2]});
            headers.insert({"passcode",lines[3]});
            break;
        case SEND:
            break;
        case SUBSCRIBE:

            headers.insert({"destination","/"+lines[1]});
            headers.insert({"id",""}); 
            // headers.insert({"recipt",""});
            break;
        case UNSUBSCRIBE:
            headers.insert({"destination","/"+lines[1]});
            headers.insert({"id",""}); 
            break;
        case DISCONNECT: break;
            headers.insert({"receipt",""});
        default:
            break;
        }
    }catch(const std::exception& e){
        switch (type)
        {
        case CONNECT:throw std::invalid_argument("Wrong Login Structure: login {host:port} {username} {password}");break;
        case SEND:throw std::invalid_argument("Wrong Report Stractur: reprot {file} "); break;
        case SUBSCRIBE:throw std::invalid_argument("Wrong Join Structure: join {game_name}"); break;
        case UNSUBSCRIBE:throw std::invalid_argument("Wrong Exit  Structure: exit {game_name} "); break;
        case DISCONNECT:throw std::invalid_argument("Wrong "); break;
        default:
            break;
        }
    }
}
// Frame::Frame(Event event,string username,Origin origin):origin(Origin),type(SEND),headers(){
//     string team_a = event.get_team_a_name();
//     team_a[0]=to_lower(team_a[0]);
//     string team_b = event.get_team_b_name();
//     team_b[0]=to_lower(team_b[0]);

// };
string& Frame::getBody(){return body;};
Frame::Frame(string message,Origin from):origin(nullOrigin),type(nullType),headers(){
    vector<string> lines = vector<string>();
    switch(from){
        case Client:lines = splitMessege(message," ");break;
        case Server:lines = splitMessege(message,"\n");break;
        case nullType: lines[0]= nullptr;break;
    }
    if(lines[0]=="summary"&& lines.size()!=4){
        throw std::invalid_argument("Wrong summary Structure: summary {game_name} {username} {file}");
    }
    if(lines[0] == "login"){
        type = CONNECT;
        origin = Client;
        createClientFrame(lines);
    }
    else if(lines[0] == "report"){
        type = SEND;
        origin = Client;
        createClientFrame(lines);
    }
    else if(lines[0] == "join"){
        type = SUBSCRIBE;
        origin = Client;
        createClientFrame(lines);
    }
    else if(lines[0] == "exit"){
        type = UNSUBSCRIBE;
        origin = Client;
        createClientFrame(lines);
    }
    else if(lines[0] == "logout"){
        type = DISCONNECT;
        origin = Client;
        createClientFrame(lines);
    }
    else if(lines[0] == "CONNECTED"){
        type = CONNECTED;
        origin = Server;
        createServerFrame(lines);
    }
    else if(lines[0] == "MESSAGE"){
        type = MESSAGE;
        origin = Server;
        createServerFrame(lines);
    }
    else if(lines[0] == "RECEIPT"){
        type = RECEIPT;
        origin = Server;
        createServerFrame(lines);
    }
    else if(lines[0] == "ERROR"){
        type = ERROR;
        origin = Server;
        std::vector<std::string> reason = splitMessege(lines[1],":");
        headers.insert({"message",reason[1]});
    }
    else if(((lines[0]!="DISCONNECT")|(lines[0]!="RECEIPT"))&(lines[0]!="summary")) {
        cout<<lines.size()<<endl;
        throw std::invalid_argument("Ivalid command");
    }
}
const string TypeToString(ConnectionType e) throw()
{
    switch (e)
    {
    case 0: return "nullType";
    case 1: return "CONNECT";
    case 2: return "SEND";
    case 3: return "SUBSCRIBE";
    case 4: return "UNSUBSCRIBE";
    case 5: return "DISCONNECT";
    case 6: return "CONNECTED";
    case 7: return "MESSAGE";
    case 8: return "RECEIPT";
    case 9: return "ERROR";
    default: throw std::invalid_argument("Unimplemented item");
    }
}
const string OriginToString(Origin e) throw()
{
    switch (e)
    {
    case 0: return "nullOrigin";
    case 1: return "Server";
    case 2: return "Client";
    default: throw std::invalid_argument("Unimplemented item");
    }
}
string Frame::toString(){
    string obj = TypeToString(type);
    obj.append("\n");
    for(auto const& x : headers){
    obj.append(x.first+":"+x.second+"\n");  // string (key) 
    }
    obj.append(body+'\0');
    return obj;
}
Frame::Frame(const Frame& other):origin(other.origin),type(other.type),headers(other.headers),body(other.body){}
Frame::~Frame(){}
ConnectionType Frame::getType() const{
    return type;
}
Origin Frame::getOrigin() const{
    return origin;
}
string Frame::getTypeName() const{return TypeToString(type);};
string Frame::getOriginName() const{return OriginToString(origin);};
map<string,string>& Frame::getHeaders(){return headers;}
void Frame::modifyHeader(string key,string value){
    // headers.insert({key,value});
    headers[key]=value;
};