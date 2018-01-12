package chatroom.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HomeGui {

    Bridge bridge;
    Stage window;
    String currentRoom = "";
    ArrayList<String> rooms;
    //---------The View Components
    //The Base Element
    BorderPane borderPane = new BorderPane();
    ComboBox<String> roomSelection;
    Label selectedRoom;
    //The chat box elements
    private Button sendMessage = new Button("Send");
    private VBox chatBox = new VBox(9);
    private List<String[]> messages = new ArrayList<>();
    private ScrollPane chatBoxContainer = new ScrollPane();
    private VBox container = new VBox();
    private int index = 0;
    private TextArea message = new TextArea();
    //The right side Elements
    private VBox outerRight;
    private Label userRequest;
    private Label roomConnectionStatus;
    private TabPane userLists;
    private ObservableList<String> allUsers = FXCollections.observableArrayList();
    private ObservableList<String> currentRoomUsers = FXCollections.observableArrayList();

    public HomeGui(Bridge bridge, Stage window) {
        this.bridge = bridge;
        this.window = window;
        LoadHome();
    }

    //This is the start Method of this class
    private void LoadHome() {
        //Setups the VBox witch is the chat box
        borderPane.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());


        initChatBox(bridge);
        initRightMenu(bridge);


        message.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER && !key.isShiftDown()) {
                bridge.SendMessage(message.getText());
                message.clear();
                key.consume();
            }
            else
            {
             if(key.getCode() == KeyCode.ENTER && key.isShiftDown()) {
                 message.appendText("\n");

             }
            }
        });

        HBox SendMessageButtonAndTextArea = new HBox();
        SendMessageButtonAndTextArea.getChildren().addAll(message, sendMessage);
        chatBoxContainer.setContent(chatBox);
        chatBoxContainer.setFitToWidth(true);
        chatBoxContainer.setPrefViewportHeight(500);

        container.getChildren().addAll(chatBoxContainer, SendMessageButtonAndTextArea);

        //borderPane.setPrefSize(700, 900);
        borderPane.setCenter(container);

        chatBoxContainer.getStyleClass().add("background");
        borderPane.getStyleClass().add("mainBackground");
        borderPane.setPadding(new Insets(80, 80, 80, 80));
        message.getStyleClass().add("textArea");
        roomSelection.getStyleClass().add("roomSelection");
        userLists.getStyleClass().add("tabPane");
        sendMessage.getStyleClass().add("Buttons");


        Scene scene = new Scene(borderPane);
        window.setScene(scene);
    }

    private void initRightMenu(Bridge bridge) {
        ArrayList<String> rooms = bridge.getRooms();
        this.rooms = rooms;
        Separator roomUserSeparator = new Separator();
        roomUserSeparator.setPadding(new Insets(10, 0, 10, 0));

        selectedRoom = new Label("Current room: NONE");
        roomSelection = new ComboBox<>();
        roomSelection.valueProperty().addListener((property, oldValue, newValue) -> selectRoom(newValue));
        roomConnectionStatus = new Label();
        updateRooms(rooms);

        outerRight = new VBox();


        userLists = new TabPane();
        userLists.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab allUsersTab = new Tab("All Users");
        allUsersTab.setContent(new ListView<>(allUsers));
        Tab currentRoomUsersTab = new Tab("Users in Current Room");
        currentRoomUsersTab.setContent(new ListView<>(currentRoomUsers));
        userLists.getTabs().addAll(allUsersTab, currentRoomUsersTab);

        userRequest = new Label();
        userRequest.getStyleClass().add("UserSelection");

        outerRight.getChildren().addAll(new Label("Rooms"), selectedRoom, roomSelection, roomConnectionStatus, roomUserSeparator, new Label("Users"), userLists);
        outerRight.setAlignment(Pos.TOP_CENTER);

        borderPane.setRight(outerRight);

        allUsersUpdate(bridge.getAllUsers());
        userRoomUpdate(bridge.getUsersFromSelection(currentRoom));
    }

    //Updates the room View
    public void updateRooms(ArrayList<String> rooms) {
        this.rooms = rooms;

        for (String k : rooms) {
            roomSelection.getItems().add(k);
        }

/*
        availableRooms.getItems().clear();
        joinedRooms.getItems().clear();

        for (String[] k : rooms) {

            if (k[1].equals("1")) {

                joinedRooms.getItems().add(k[0]);
            } else {
                availableRooms.getItems().add(k[0]);
            }
        }*/
    }


    private void initChatBox(Bridge bridge) {
        chatBoxContainer.setPrefSize(400, 600);
        chatBox.getStyleClass().add("chatbox");

        sendMessage.setOnAction(evt -> {
            bridge.SendMessage(message.getText());
            message.clear();
        });
        chatBoxContainer.setContent(chatBox);
    }


    public void addMessage(String username, String message) {
        messages.add(new String[]{username, message});
        Label label;
        if (username.equals(bridge.getUsername())) {

            label = new Label(messages.get(index)[0] + ": \n" + messages.get(index)[1]);
            label.setStyle("-fx-background-color:#CC6666; -fx-text-fill: #C5C8C6");
            label.setAlignment(Pos.CENTER_LEFT);

        } else {

            label = new Label(messages.get(index)[0] + ": \n" + messages.get(index)[1]);
            label.setStyle("-fx-background-color:#8ABEB7; -fx-text-fill: #C5C8C6");
            label.setAlignment(Pos.CENTER_RIGHT);

        }

        label.setWrapText(true);
        chatBox.getChildren().add(label);
        index++;
    }

    public void onRoomUpdate(ArrayList<String> rooms) {
        updateRooms(rooms);
    }

    private void selectRoom(String room) {
        roomConnectionStatus.setStyle("-fx-text-fill: red");
        roomConnectionStatus.setText("Connecting to room: " + room);
        bridge.requestRoomChange(room);

    }

    public void onRoomChange(String room) {
        roomConnectionStatus.setStyle("-fx-text-fill: #248000");
        roomConnectionStatus.setText("Successful connected to: " + room);
        selectedRoom.setText("Current room: " + room);
        currentRoom = room;
    }

    public void userRoomUpdate(ArrayList<String> newCurrentUsers) {
        currentRoomUsers.clear();
        currentRoomUsers.addAll(newCurrentUsers);
    }

    public void allUsersUpdate(ArrayList<String> newAllUsers) {
        allUsers.clear();
        allUsers.addAll(newAllUsers);
    }

    public void showIssueAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("!!! [WARNING69] !!!");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
