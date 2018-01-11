package chatroom.server.gui;

import javafx.stage.Stage;

public class ServerGuiMain {

    private Bridge bridge;
    private Stage window;
    public ServerHomeGui serverHomeGui;


    public void runGui(Stage primeStage, Bridge bridge){
        this.window = primeStage;
        window.setOnCloseRequest(e -> onClose());
        this.bridge = bridge;

        serverHomeGui = new ServerHomeGui(bridge, window);
    }

    private void onClose(){
        bridge.onClose();
    }
}
