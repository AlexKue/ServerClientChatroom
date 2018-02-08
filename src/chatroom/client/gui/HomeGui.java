package chatroom.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    private ListView<String> allUserView;
    private ListView<String> currentRoomUsersView;

    //Programm objects
    HashMap<String, PrivateChatWindow> privateChatWindows = new HashMap<>();

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

        window.setTitle("The Chat Client || Logged in as: " + bridge.getUsername());

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

        container.getStyleClass().add("CenterContainer");

        HBox sendMessageButtonAndTextArea = new HBox();
        sendMessageButtonAndTextArea.getChildren().addAll(message, sendMessage);
        chatBoxContainer.setContent(chatBox);
        chatBoxContainer.setFitToWidth(true);
        chatBoxContainer.setPrefViewportHeight(500);
        sendMessageButtonAndTextArea.setPadding(new Insets(10 ,10 ,10 ,0));
        sendMessageButtonAndTextArea.setSpacing(10);

        container.getChildren().addAll(chatBoxContainer, sendMessageButtonAndTextArea);

        //borderPane.setPrefSize(700, 900);
        borderPane.setCenter(container);

        chatBoxContainer.getStyleClass().add("scroll-pane");
        borderPane.getStyleClass().add("mainBackground");
        borderPane.setPadding(new Insets(20, 10, 10, 20));
        message.getStyleClass().add("textArea");
        roomSelection.getStyleClass().add("roomSelection");
        userLists.getStyleClass().add("tabPane");
        sendMessage.getStyleClass().add("Buttons");

        borderPane.getStyleClass().add("spacing");
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
        allUserView= new ListView<>(allUsers);
        allUsersTab.setContent(allUserView);
        Tab currentRoomUsersTab = new Tab("Users in Current Room");
        currentRoomUsersView= new ListView<>(currentRoomUsers);
        currentRoomUsersTab.setContent(currentRoomUsersView);
        userLists.getTabs().addAll(allUsersTab, currentRoomUsersTab);

        //Functionality for private Chat
        allUserView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String username = allUserView.getSelectionModel().getSelectedItem();
                    if(!username.equals(bridge.getUsername())) {
                        startPrivateChat(username);
                    }
                }
            }
        });
        currentRoomUsersView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String username = allUserView.getSelectionModel().getSelectedItem();
                    if(!username.equals(bridge.getUsername())) {
                        startPrivateChat(username);
                    }
                }
            }
        });

        userRequest = new Label();
        userRequest.getStyleClass().add("UserSelection");

        outerRight.getChildren().addAll(selectedRoom, roomSelection, roomConnectionStatus, roomUserSeparator, userLists);
        outerRight.setAlignment(Pos.TOP_CENTER);
        outerRight.getStyleClass().add("spacing");
        outerRight.setPadding(new Insets(10, 10, 10, 20));
        borderPane.setRight(outerRight);

        allUsersUpdate(bridge.getAllUsers());
        userRoomUpdate(bridge.getUsersFromSelection(currentRoom));
    }

    //Updates the room View
    public void updateRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
        roomSelection.getItems().setAll(rooms);

//        this.rooms = rooms;
//
//        for (String k : rooms) {
//            roomSelection.getItems().add(k);
//        }

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
        chatBoxContainer.getStyleClass().add("spacing");
        chatBox.getStyleClass().add("chatbox");

        sendMessage.setOnAction(evt -> {
            bridge.SendMessage(message.getText());
            message.clear();
        });
        chatBox.getStyleClass().add("spacing");
        chatBoxContainer.setContent(chatBox);
    }


    public void addMessage(String username, String message) {
        messages.add(new String[]{username, message});
        Label label;
        HBox hbox;
        if (username.equals(bridge.getUsername())) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_RIGHT);
            label = new Label(messages.get(index)[0] + ": \n" + messages.get(index)[1]);
            label.setPrefWidth(400);
            label.getStyleClass().add("ownMessage");
            hbox.getChildren().add(label);

        } else if(username.equals("SERVER")) {
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            label = new Label(messages.get(index)[0] + ": \n" + messages.get(index)[1]);
            label.setPrefWidth(400);
            label.getStyleClass().add("serverMessage");
            hbox.getChildren().add(label);
        }else{
            hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            label = new Label(messages.get(index)[0] + ": \n" + messages.get(index)[1]);
            label.setPrefWidth(400);
            label.getStyleClass().add("roomMessage");
            hbox.getChildren().add(label);
        }
        label.setWrapText(true);
        label.setPadding(new Insets(2,2,2,2));
        chatBox.getChildren().add(hbox);
        chatBox.setPadding(new Insets(3,3,3,3));
        index++;
    }

    public void onRoomUpdate(ArrayList<String> rooms) {
        updateRooms(rooms);
    }

    private void selectRoom(String room) {
        roomConnectionStatus.setStyle("-fx-text-fill: red");
        roomConnectionStatus.setText("Connecting to room: " + room);
        if(room.equals(currentRoom)){
            roomConnectionStatus.setStyle("-fx-text-fill: #248000");
            roomConnectionStatus.setText("Successful connected to: " + room);
            selectedRoom.setText("Current room: " + room);
        }
        bridge.requestRoomChange(room);

    }

    public void onRoomChange(String room) {
        roomConnectionStatus.setStyle("-fx-text-fill: #248000");
        roomConnectionStatus.setText("Successful connected to: " + room);
        selectedRoom.setText("Current room: " + room);
        currentRoom = room;
        roomSelection.setValue(room);
    }

    public void userRoomUpdate(ArrayList<String> newCurrentUsers) {
        currentRoomUsers.clear();
        currentRoomUsers.addAll(newCurrentUsers);
    }

    public void allUsersUpdate(ArrayList<String> newAllUsers) {
        allUsers.clear();
        allUsers.addAll(newAllUsers);
    }

    public void showIssueAlert(String message, boolean closeWindow) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
        if (closeWindow){
            window.close();
        }
    }

    public void startPrivateChat(String username){
        processStartRequest(username);
        bridge.privateChatStartet(username);
    }
    public void processStartRequest(String username){
        if(!privateChatWindows.containsKey(username)){
            PrivateChatWindow privateChatWindow = new PrivateChatWindow(bridge, username);
            privateChatWindow.loadPrivateChatBox();
            privateChatWindows.put(username,  privateChatWindow);
        }else{
            privateChatWindows.get(username).changeChatFielStatus(true);
        }

    }
    public void addPrivateMessage(String originUser, String message, boolean isServer){
        PrivateChatWindow privateChatWindow =  privateChatWindows.get(originUser);
        privateChatWindow.addMessage(originUser, message, isServer);
    }
    public void chatClosed(String username){
        privateChatWindows.remove(username);
    }
    public void chatDisconnected(String username){
        privateChatWindows.get(username).changeChatFielStatus(false);
    }
    public void closeAllChatWindows(){
        for (PrivateChatWindow privateChatWindow: privateChatWindows.values()) {
            privateChatWindow.closeWindow();
        }
    }
}
