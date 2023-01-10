#include"../include/KeyPressThread.h"
#include <string>
#include <vector>
#include "../include/Frame.h"
#include <thread>
KeyPressThread::KeyPressThread(StompClient* client_){
    client = client_;
};
// KeyPressThread::~KeyPressThread(){
//     *client;
// };
StompClient* KeyPressThread:: getClient(){return client;};
void KeyPressThread::Run(){
    cout<<"iam termianl"<<endl;
}
void KeyPressThread::Run1(){
    cout<<"iam termianl"<<endl;
}