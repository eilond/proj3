#include "../include/Event.h"
#include "../include/json.hpp"
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <sstream>
using json = nlohmann::json;
using namespace std;

Event::Event(std::string team_a_name, std::string team_b_name, std::string name, int time,
             std::map<std::string, std::string> game_updates, std::map<std::string, std::string> team_a_updates,
             std::map<std::string, std::string> team_b_updates, std::string discription)
    : team_a_name(team_a_name), team_b_name(team_b_name), name(name),
      time(time), game_updates(game_updates), team_a_updates(team_a_updates),
      team_b_updates(team_b_updates), description(discription)
{
}
//call only when summary_o_game.size()>0
// Event::Event(vector<Event> summary_o_game):team_a_name(summary_o_game[0].get_team_a_name()), team_b_name(summary_o_game[0].get_team_b_name()), name(""),
// time(summary_o_game[0].get_time()), game_updates(summary_o_game[0].get_game_updates()),
// team_a_updates(summary_o_game[0].get_team_a_updates()), team_b_updates(summary_o_game[0].get_team_b_updates()), description("Game event reports:\n")
// {
//     try{
//         for(Event& e :summary_o_game){
//             for(auto& pair: e.get_game_updates()){
//                 game_updates[pair.first]=pair.second;
//             }
//             for(auto& pair: e.get_team_a_updates()){
//                 team_a_updates[pair.first]=pair.second;
//             }
//             for(auto& pair: e.get_team_b_updates()){
//                 team_b_updates[pair.first]=pair.second;
//             }
//             description.append(std::to_string(e.get_time())+ " - "+ e.get_name()+"\n");
//             description.append(e.get_discription()+"\n\n");
//         }
//     }
//     catch(exception& e){
//         throw std::range_error("no elements in user summary");
//     }
// }
Event::Event(vector<Event> summary_o_game):team_a_name(""), team_b_name(""), name(""), time(0), game_updates(), team_a_updates(), team_b_updates(), description("")
{
    try{
        if(summary_o_game.size()==0){
            throw exception();
        }
        team_a_name =summary_o_game[0].get_team_a_name();
        team_b_name = summary_o_game[0].get_team_b_name();
        name = "";
        time = summary_o_game[0].get_time();
        description = "Game event reports:\n";
        for(Event& e :summary_o_game){
            for(auto& pair: e.get_game_updates()){
                game_updates[pair.first]=pair.second;
            }
            for(auto& pair: e.get_team_a_updates()){
                team_a_updates[pair.first]=pair.second;
            }
            for(auto& pair: e.get_team_b_updates()){
                team_b_updates[pair.first]=pair.second;
            }
            description.append(std::to_string(e.get_time())+ " - "+ e.get_name()+":\n\n");
            description.append(e.get_discription()+"\n\n\n");
        }
    }
    catch(exception& e){
        std::cout<<"no elements in user summary"<<std::endl;
    }
}
Event::~Event()
{
}

const std::string &Event::get_team_a_name() const
{
    return this->team_a_name;
}

const std::string &Event::get_team_b_name() const
{
    return this->team_b_name;
}

const std::string &Event::get_name() const
{
    return this->name;
}

int Event::get_time() const
{
    return this->time;
}

const std::map<std::string, std::string> &Event::get_game_updates() const
{
    return this->game_updates;
}

const std::map<std::string, std::string> &Event::get_team_a_updates() const
{
    return this->team_a_updates;
}

const std::map<std::string, std::string> &Event::get_team_b_updates() const
{
    return this->team_b_updates;
}

const std::string &Event::get_discription() const
{
    return this->description;
}

Event::Event(const std::string &frame_body) : team_a_name(""), team_b_name(""), name(""), time(0), game_updates(), team_a_updates(), team_b_updates(), description("")
{
    string messege = frame_body.substr(frame_body.find("user"));
    vector<string> messageVec = SplitMessege(messege,"\n");
    map<string,string> game_updates_;
    map<string,string> team_a_updates_;
    map<string,string> team_b_updates_;
    string discription;
    for(int i = 0; i<(int)messageVec.size() ;i++){
        vector<string> line = SplitMessege(messageVec[i],":");
        if(line[0].find('\t')==0){
            line[0].erase(line[0].find('\t'),1);
        }
        else if(line[0]=="team a"){
            team_a_name = line[1];
        }
        else if(line[0]=="team b"){
            team_b_name = line[1];
        }
        else if(line[0]=="event name"){
            name = line[1];
        }
        else if(line[0]=="time"){
            time = stoi(line[1]);
        }
        else if(line[0]=="general game updates"){
            i = addToUpdatesmap(game_updates, messageVec,i,"team a updates");
        }
        else if(line[0]=="team a updates"){
            i = addToUpdatesmap(team_a_updates, messageVec,i,"team b updates");
        }
        else if(line[0]=="team b updates"){
            i = addToUpdatesmap(team_b_updates, messageVec,i,"discription");
        }
        else if(line[0]=="discription"){
            description = line[1];
        }
    }
}

names_and_events parseEventsFile(std::string json_path)
{
    std::ifstream f(json_path);
    json data = json::parse(f);

    std::string team_a_name = data["team a"];
    std::string team_b_name = data["team b"];

    // run over all the events and convert them to Event objects
    std::vector<Event> events;
    for (auto &event : data["events"])
    {
        std::string name = event["event name"];
        int time = event["time"];
        std::string description = event["description"];
        std::map<std::string, std::string> game_updates;
        std::map<std::string, std::string> team_a_updates;
        std::map<std::string, std::string> team_b_updates;
        for (auto &update : event["general game updates"].items())
        {
            if (update.value().is_string())
                game_updates[update.key()] = update.value();
            else
                game_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team a updates"].items())
        {
            if (update.value().is_string())
                team_a_updates[update.key()] = update.value();
            else
                team_a_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team b updates"].items())
        {
            if (update.value().is_string())
                team_b_updates[update.key()] = update.value();
            else
                team_b_updates[update.key()] = update.value().dump();
        }
        
        events.push_back(Event(team_a_name, team_b_name, name, time, game_updates, team_a_updates, team_b_updates, description));
    }
    names_and_events events_and_names{team_a_name, team_b_name, events};

    return events_and_names;
}
string Event::to_Frame_string(string user){
    string team_a = get_team_a_name();
    string team_b = get_team_b_name();
    transform(team_a.begin(),team_a.end(),team_a.begin(),::tolower);
    transform(team_b.begin(),team_b.end(),team_b.begin(),::tolower);
    string answer =
    "SEND\ndestination:/"+team_a+'_'+team_b+"\n\n"+
    "user:"+user+"\n"+
    "team a:"+team_a+"\n"+
    "team b:"+team_b+"\n"+
    "event name:"+get_name()+"\n"+
    "time:"+std::to_string(get_time())+"\n"
    "general game updates:\n";
    map<string,string> game_updates = get_game_updates();
    for(const auto& pair :game_updates){
        answer.append("\t"+pair.first+": "+pair.second+"\n");
    }
    answer.append("team a updates:\n");
    map<string,string> team_a_updates = get_team_a_updates();
    for(const auto& pair :team_a_updates){
        answer.append("\t"+pair.first+": "+pair.second+"\n");
    }
    answer.append("team b updates:\n");
    map<string,string> team_b_updates = get_team_b_updates();
    for(const auto& pair :team_b_updates){
        answer.append("\t"+pair.first+": "+pair.second+"\n");
    }
    answer.append("discription:"+get_discription()+"\0");
    return answer;
};
string Event::to_Summary(){
    string team_a = get_team_a_name();
    string team_b = get_team_b_name();
    transform(team_a.begin(),team_a.end(),team_a.begin(),::tolower);
    transform(team_b.begin(),team_b.end(),team_b.begin(),::tolower);
    string answer =
    team_a+" vs "+team_b+"\nGame stats:\n"+
    "General stats:\n";
    map<string,string> game_updates = get_game_updates();
    for(const auto& pair :game_updates){
        answer.append(pair.first+":"+pair.second+"\n");
    }
    answer.append(team_a+" stats:\n");
    map<string,string> team_a_updates = get_team_a_updates();
    for(const auto& pair :team_a_updates){
        answer.append(pair.first+":"+pair.second+"\n");
    }
    answer.append(team_b+" stats:\n");
    map<string,string> team_b_updates = get_team_b_updates();
    for(const auto& pair :team_b_updates){
        answer.append(pair.first+":"+pair.second+"\n");
    }
    answer.append(get_discription());
    return answer;
};
vector<string> Event::SplitMessege(string s,string delimiter){
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
int Event::addToUpdatesmap(map<string,string>& updates,vector<string>& messageVec, int fromWhere,string next_updates){
    for(int i = fromWhere; i<(int)messageVec.size() ;i++){
        vector<string> line = SplitMessege(messageVec[i],":");
        if(line[0] == next_updates){
            return fromWhere--;
        }
        else if(line[0].find('\t')==0){
            line[0].erase(line[0].find('\t'),1);
            updates.insert({line[0],line[1]});
        }
    }
    return fromWhere--;
}