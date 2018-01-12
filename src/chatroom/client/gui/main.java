package chatroom.client.gui;

import chatroom.client.Client;
import chatroom.client.ClientForTesting;
import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application{

    public static void main( String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primeStage) throws Exception {
        Client client = new Client();
        ClientGuiMain clientGuiMain = new ClientGuiMain();
        Bridge bridge = new Bridge(client, clientGuiMain);

        client.setBridge(bridge);

        clientGuiMain.runGui(primeStage, bridge);
    }
}
