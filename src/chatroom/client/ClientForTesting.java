package chatroom.client;

import chatroom.client.gui.Bridge;

import java.util.ArrayList;

//Diese Klasse Kann dann entfernt Werden und alle methoden vom richtigen Client in die Bridge Kopieren :)
public class ClientForTesting {
    Bridge bridge;
    String username;
    ArrayList<String[]> rooms = new ArrayList<String[]>(){{
        add(new String[]{"lobby", "1"});
        add(new String[]{"slim shady", "0"});
    }};
    ArrayList<String> users = new ArrayList<String>(){{
        add("slim");
        add("shady");
        add("bla");
    }};

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
        return rooms;
    }
    public void JoinRooms( ArrayList<String> selected){
        for(int i = 0; i < rooms.size(); i++){
            for(String l: selected){
                if(rooms.get(i)[0].equals(l)){
                    String k [] = new String[]{rooms.get(i)[0], "1"};
                    rooms.set(i, k);
                    bridge.onRoomUpdate(rooms);
                }
            }
            for (String []k : rooms){
                System.out.println(k[0]);
                System.out.println(k[1]);
            }

        }
    }

    public void LeaveRooms( ArrayList<String> selected){
        for(int i = 0; i < rooms.size(); i++){
            for(String l: selected){
                if(rooms.get(i)[0].equals(l)){
                    String k [] = new String[]{rooms.get(i)[0], "0"};
                    rooms.set(i, k);
                    bridge.onRoomUpdate(rooms);
                }
            }
            for (String []k : rooms){
                System.out.println(k[0]);
                System.out.println(k[1]);
            }
        }
    }

    public ArrayList<String> getAllUsers(){
        return users;
    }

    public void ConnectToAdress(String adress) {
        System.out.println(adress);
        bridge.onConnect(true);
    }

}
