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
    "SEND\ndestination:/"+team_a+','+team_b+"\n\n"+
    "user:"+user+"\n"+
    "team a:"+team_a+"\n"+
    "team a:"+team_b+"\n"+
    "event name:"+get_name()+"\n"+
    "time:"+std::to_string(get_time())+"\n"
    "general game updates:\n";
    map<string,string> game_updates = get_game_updates();
    for(const auto& pair :game_updates){
        answer.append("\t"+pair.first+":"+pair.second+"\n");
    }
    answer.append("team a updates:\n");
    map<string,string> team_a_updates = get_team_a_updates();
    for(const auto& pair :team_a_updates){
        answer.append("\t"+pair.first+":"+pair.second+"\n");
    }
    answer.append("team b updates:\n");
    map<string,string> team_b_updates = get_team_b_updates();
    for(const auto& pair :team_b_updates){
        answer.append("\t"+pair.first+":"+pair.second+"\n");
    }
    answer.append("discription:"+get_discription()+"\n\0");
    return answer;
};
// int main(int argc, char *argv[]) {
//     names_and_events nne = parseEventsFile("data/events1.json");
//     vector<Event> events =nne.events;
//     for(auto& e :events){
//         cout<<e.to_Frame_string("yuval")<<endl;}
// }