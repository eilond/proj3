#include "../include/Summary.h"
#include <fstream>

Summary::Summary():user_summary(){};
void Summary::add_event_to_user(string user,Event event){
    string destination = event.get_team_a_name()+"_"+event.get_team_b_name();
    user_summary[user][destination].push_back(event);
};
map<string,map<string,vector<Event>>>& Summary::getMap(){return user_summary;};
// vector<Event> get_user_game_summary(string user,string game){return user_summary[user][game];};
void Summary::add_events_to_user(string user,vector<Event> v1){
    for(auto e: v1){
        string destination = e.get_team_a_name()+"_"+e.get_team_b_name();
        user_summary[user][destination].push_back(e);
    }
};
vector<Event> Summary::get_user_game_summary(string user,string game,string path){
    map<string, map<string,vector<Event>>> user_summary_ = user_summary;
    vector<Event> user_game_summary = user_summary[user][game];
    try{
        if(user_game_summary.size()==0){
            throw invalid_argument("no summary for user");
        }
            Event sum(user_game_summary);
            string path1 = "./data/"+path;
            std::ofstream file(path1);
            string a = sum.to_Summary();
            cout<<a<<endl;
            file << a;
            file.close();
    }
    catch(exception& e){
        std::cout<<e.what()<<std::endl;
    }
    return user_game_summary;
    
};
// bin/StompWCIClient 127.0.0.1 7777
// login y yuval yuval
// join /germany_japan
// report events1.json
// summary germany_japan yuval yuva_summ.json
// summary germany_japan noa yuva_summ.json