package chatroom.client;

public class Bridge {
    Client model;
    GuiMain gui;

    public Bridge(Client client, GuiMain guiMain){
        this.model = client;
        this.gui = guiMain;
    }


    //called by the gui to send the login data
    public void sendLoginData(String username, String password){
        model.login(username, password);
    }

    //called by the model to let the gui know whether the login succeeded or not
    public void onServerLoginAnswer(boolean answer){
        gui.onLoginAnswer(answer);
    }


}
