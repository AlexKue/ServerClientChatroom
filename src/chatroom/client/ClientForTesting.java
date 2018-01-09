package chatroom.client;
//Diese Klasse Kann dann entfernt Werden und alle methoden vom richtigen Client in die Bridge Kopieren :)
public class ClientForTesting {
    Bridge bridge;
    public void setBridge(Bridge bridge){
        this.bridge = bridge;
    }

    public void login(String username, String password){
        System.out.println("The Password is: " + password + "\n The username is: "+ username);
        bridge.onServerLoginAnswer(true);
    }

}
