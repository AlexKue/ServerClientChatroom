package chatroom.server.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class ServerHomeGui {
    BorderPane mainScene;
    private Bridge bridge;
    private Stage window;
    //-------RIGHT SIDE-------
    private VBox outerRight;
    private TabPane roomAndUserLists;
    private ObservableList<String> connectedUsers = FXCollections.observableArrayList();
    private ObservableList<String> rooms = FXCollections.observableArrayList();

    //--------CENTER--------
    TextArea log = new TextArea();

    public ServerHomeGui(Bridge bridge, Stage window) {
        this.bridge = bridge;
        this.window = window;
        runHomeGui();
    }

    private void runHomeGui() {
        mainScene = new BorderPane();
        mainScene.setPadding(new Insets(20, 20, 20, 20));
        mainScene.getStylesheets().add(getClass().getResource("ServerHomeGuiStyle.css").toExternalForm());
        initRight();
        initCenter();

        Scene scene = new Scene(mainScene);
        window.setScene(scene);

        mainScene.getStyleClass().add("mainBackground");

        window.show();

    }

    private void initRight() {
        ListView<String> connectedUsersView = new ListView<>(connectedUsers);
        ListView<String> roomsView = new ListView<>(rooms);
        connectedUsers.add(0, "");
        connectedUsers.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                if(connectedUsers.isEmpty()){
                    connectedUsersView.getItems().add("");
                }
            }
        });

        //Rooms Buttons
        HBox roomButtonContainer = new HBox();
        roomButtonContainer.setAlignment(Pos.CENTER);
        Button createRoom = new Button("Create Room");
        Button editRoom = new Button("Edit Room");
        Button deleteRoom = new Button("Delete Room");

        createRoom.setOnAction(e -> createRoom());
        editRoom.setOnAction(e -> editRoom(roomsView.getSelectionModel().getSelectedItem()));
        deleteRoom.setOnAction(e->deleteRoom(roomsView.getSelectionModel().getSelectedItem()));
        roomButtonContainer.getChildren().addAll(createRoom, editRoom, deleteRoom);


        //User Buttons
        HBox userButtonContainer = new HBox();
        userButtonContainer.setAlignment(Pos.CENTER);
        Button warnUser = new Button("Warn user");
        Button kickUser = new Button("Kick user");
        Button banUser = new Button("Ban user");

        warnUser.setOnAction(e -> warnUser(connectedUsersView.getSelectionModel().getSelectedItem()));
        kickUser.setOnAction(e -> kickUser(connectedUsersView.getSelectionModel().getSelectedItem()));
        banUser.setOnAction(e -> banUser(connectedUsersView.getSelectionModel().getSelectedItem()));
        userButtonContainer.getChildren().addAll(warnUser, kickUser, banUser);

        outerRight = new VBox();

        roomAndUserLists = new TabPane();
        roomAndUserLists.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox userBox = new VBox();
        userBox.getChildren().addAll(connectedUsersView, userButtonContainer);
        Tab users = new Tab("Users");
        users.getStyleClass().add("connectedUsersBox");
        userButtonContainer.setSpacing(10);
        userBox.setSpacing(10);
        users.setContent(userBox);


        VBox roomsBox = new VBox();
        roomsBox.getChildren().addAll(roomsView, roomButtonContainer);
        Tab roomsPane = new Tab("Rooms");
        roomsPane.setContent(roomsBox);
        roomsBox.setSpacing(10);
        roomButtonContainer.setSpacing(10);

        roomAndUserLists.getTabs().addAll(users, roomsPane);

        outerRight.getChildren().add(roomAndUserLists);
        outerRight.setSpacing(10);
        mainScene.setRight(outerRight);
        requestRoomList();
        requestUserList();
    }

    private void initCenter(){
        log.getStyleClass().add("log");
        VBox centerBox = new VBox();
        Button showUserData = new Button("Show user data");
        showUserData.setOnAction(e->showAllUserData());
        log.setWrapText(true);
        log.setEditable(false);
        centerBox.getChildren().addAll(log, showUserData);
        centerBox.setPadding(new Insets(20, 20, 20, 20));
        centerBox.setSpacing(10);
        mainScene.setCenter(centerBox);
    }

    private void showAllUserData() {
        AllUserDataBox allUserDataBox = new AllUserDataBox(bridge);
    }


    public void requestRoomList() {
        updateRoomListView(bridge.requestRoomList());
    }

    public void requestUserList() {
        updateUserListView(bridge.requestUserList());
    }

    public void updateRoomListView(ArrayList<String> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
    }

    public void updateUserListView(ArrayList<String> users) {
       this.connectedUsers.setAll(users);
       if(connectedUsers.isEmpty()){
            connectedUsers.add(0, "");
       }

       //this.connectedUsers.clear();
       //this.connectedUsers.addAll(users);
    }

    private void kickUser(String user) {
        try{
            user = user.substring(0, user.indexOf('|'));
        }catch (Exception e){

        }
        bridge.kickUser(user);
    }

    private void warnUser(String user) {
        try{
            user = user.substring(0, user.indexOf('|'));
        }catch (Exception e){

        }
        bridge.warnUser(user);
    }

    private void banUser(String user) {
        try{
            user = user.substring(0, user.indexOf('|'));
        }catch (Exception e){

        }
        bridge.banUser(user);
    }

    private void createRoom() {
        RoomCreatingDeletingAndEditingBox createRoomBox = new RoomCreatingDeletingAndEditingBox(bridge);
    }

    private void editRoom(String roomToBeEdited) {
        RoomCreatingDeletingAndEditingBox editRoomBox = new RoomCreatingDeletingAndEditingBox(bridge, roomToBeEdited);
    }

    private void deleteRoom(String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("You want to delete the Room: " + name);
        alert.setContentText("Are you sure about this");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            bridge.deleteRoom(name);
        }
    }
    public void addEventToLog(String event){
        String oldLog = log.getText();
        log.clear();
        log.setText(oldLog + "\n >>>>" + event);
    }


}
