#pragma once

#include <map>
#include <vector>
#include "../include/Event.h"
using namespace std;
class Summary
{
private:
    map<string,vector<Event>> user_summary;
public:
    Summary(/* args */);
    map<string,vector<Event>>& getMap();
    void add_event_to_user(string user,Event);
    void add_events_to_user(string user,vector<Event> v2);
};
