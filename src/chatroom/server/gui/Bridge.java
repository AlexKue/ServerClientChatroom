package chatroom.server.gui;

import chatroom.server.ServerForTesting;

public class Bridge {
    ServerForTesting model;
    ServerGuiMain gui;

    public Bridge(ServerForTesting server, ServerGuiMain serverGuiMain){
        this.model = server;
        this.gui = serverGuiMain;

    }

    //This Method is for Code to run when a Close is requested
    public void onClose() {

    }
}
