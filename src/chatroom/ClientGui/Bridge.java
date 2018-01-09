package chatroom.ClientGui;

import chatroom.client.ClientForTesting;

public class Bridge {
    ClientForTesting model;
    GuiMain gui;

    public Bridge(ClientForTesting client, GuiMain guiMain){
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
