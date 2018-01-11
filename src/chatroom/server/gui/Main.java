package chatroom.server.gui;

import chatroom.server.ServerForTesting;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ServerGuiMain serverGuiMain = new ServerGuiMain();
        ServerForTesting server = new ServerForTesting();
        Bridge bridge = new Bridge(server, serverGuiMain);

        server.setBridge(bridge);
        serverGuiMain.runGui(primaryStage, bridge);
    }
}
