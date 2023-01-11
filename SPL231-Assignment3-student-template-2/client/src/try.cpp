#include "../include/Event.h"
#include <fstream>
int main(int argc, char *argv[]) {
	names_and_events nne = parseEventsFile("data/events1_partial.json");
	Event t(nne.events);
    for(Event e: nne.events){
        std::cout<<e.to_Frame_string("uuu")<<std::endl;
    }
	string a = t.to_Summary();
	string path1 = "./data/try.text";
    std::ofstream file(path1);
    cout<<a<<endl;
    file << a;
    file.close();
}