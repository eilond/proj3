#include "../include/Summary.h"

Summary::Summary():user_summary(){};
void Summary::add_event_to_user(string user,Event event){
    user_summary[user].push_back(event);
};
map<string,vector<Event>>& Summary::getMap(){return user_summary;};
void Summary::add_events_to_user(string user,vector<Event> v1){
    for(auto e: v1){
         user_summary[user].push_back(e);
    }
};