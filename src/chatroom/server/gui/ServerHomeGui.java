package chatroom.server.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerHomeGui {
    private Bridge bridge;
    private Stage window;

    public ServerHomeGui(Bridge bridge, Stage window){
        this.bridge = bridge;
        this.window = window;
        runHomeGui();
    }


    BorderPane mainScene;

    //-------RIGHT SIDE-------
    private VBox outerRight;
    private TabPane roomAndUserLists;
    private ObservableList<String> connectedUsers = FXCollections.observableArrayList();
    private ObservableList<String> rooms = FXCollections.observableArrayList();












    private void runHomeGui(){
        mainScene = new BorderPane();

        initRight();

        Scene scene = new Scene(mainScene);
        window.setScene(scene);
        window.show();

    }

    private void initRight(){

        //Rooms Buttons
        HBox roomButtonContainer = new HBox();
        roomButtonContainer.setAlignment(Pos.CENTER);
        Button createRoom = new Button("Create Room");
        Button editRoom = new Button("Edit Room");
        Button deleteRoom = new Button("deleteRoom");
        roomButtonContainer.getChildren().addAll(createRoom, editRoom, deleteRoom);

        //User Buttons
        HBox userButtonContainer = new HBox();
        userButtonContainer.setAlignment(Pos.CENTER);
        Button warnUser = new Button("Warn user");
        Button kickUser = new Button("Kick user");
        Button banUser = new Button("Ban user");
        userButtonContainer.getChildren().addAll(warnUser, kickUser, banUser);

        outerRight = new VBox();

        roomAndUserLists = new TabPane();
        roomAndUserLists.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox userBox = new VBox();
        userBox.getChildren().addAll(new ListView<>(connectedUsers), roomButtonContainer);
        Tab users = new Tab("Users");
        users.setContent(userBox);


        VBox roomsBox = new VBox();
        roomsBox.getChildren().addAll(new ListView<>(rooms), userButtonContainer);
        Tab roomsPane = new Tab("Rooms");
        roomsPane.setContent(roomsBox);

        roomAndUserLists.getTabs().addAll(users, roomsPane);

        outerRight.getChildren().add(roomAndUserLists);
        mainScene.setRight(outerRight);

    }


}
