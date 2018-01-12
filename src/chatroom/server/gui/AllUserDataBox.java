package chatroom.server.gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

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
        Label allUsersLabel = new Label("All Users");
        ArrayList<String> allUsersList =  bridge.getAllUsers();
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(allUsersList);
        layout.getChildren().addAll(allUsersLabel, listView);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }
}
