package chatroom.ClientGui;

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
        ClientForTesting client = new ClientForTesting();
        GuiMain guiMain = new GuiMain();
        Bridge bridge = new Bridge(client, guiMain);

        client.setBridge(bridge);

        guiMain.runGui(primeStage, bridge);
    }
}
