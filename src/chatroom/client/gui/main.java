package chatroom.client.gui;

import chatroom.client.ClientForTesting;
import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application{

    public static void main( String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primeStage) throws Exception {
        ClientForTesting client = new ClientForTesting();
        ClientGuiMain clientGuiMain = new ClientGuiMain();
        Bridge bridge = new Bridge(client, clientGuiMain);

        client.setBridge(bridge);

        clientGuiMain.runGui(primeStage, bridge);
    }
}
