package chatroom.server.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AllUserDataBox {
    Stage window = new Stage();
    Bridge bridge;

    public AllUserDataBox(Bridge bridge){
        this.bridge = bridge;
        showBox();
    }

    private void showBox() {
        window.setTitle("All Users");
        VBox layout = new VBox();
        Label AllUsers = new Label("All Users");
        bridge.getAllUsers();
    }
}
