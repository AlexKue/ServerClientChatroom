package chatroom.client;

import chatroom.ClientGui.Bridge;

import java.util.ArrayList;

//Diese Klasse Kann dann entfernt Werden und alle methoden vom richtigen Client in die Bridge Kopieren :)
public class ClientForTesting {
    Bridge bridge;
    String username;
    public void setBridge(Bridge bridge){
        this.bridge = bridge;
    }

    public void login(String username, String password){
        System.out.println("The Password is: " + password + "\n The username is: "+ username);
        this.username = username;
        bridge.onServerLoginAnswer(true);
    }

    public void sendMessage(String message){
        bridge.AddMessageToView(username, message);

    }
    public String getUsername(){
        return this.username;
    }
    public ArrayList<String[]> getRooms(){
        return new ArrayList<String[]>(){{
            add(new String[]{"lobby", "1"});
            add(new String[]{"slim shady", "0"});
        }};
    }

}
