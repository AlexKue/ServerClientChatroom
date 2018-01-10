package chatroom.ClientGui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HomeGui {

    Bridge bridge;
    Stage window;

    public HomeGui(Bridge bridge, Stage window){
        this.bridge = bridge;
        this.window = window;
        LoadHome();
    }
    //---------The View Components
    //The Base Element
    BorderPane borderPane = new BorderPane();

    //The chat box elements
    private Button sendMessage = new Button("Send");
    private VBox chatBox = new VBox(9);
    private List<String[]> messages = new ArrayList<>();
    private ScrollPane ChatBoxContainer = new ScrollPane();
    private VBox container = new VBox();
    private int index = 0;
    private TextArea message = new TextArea();

    //The right side Elements
    private VBox outerRight;
    private HBox RoomsBox;
    private VBox joinedRoomsBox;
    private VBox availableRoomsBox;

    private ListView<String> joinedRooms;
    private ListView<String> availableRooms;

    private Button joinRooms;
    private Button leaveRooms;

    //This is the start Method of this class
    private  void LoadHome(){
        //Setups the VBox witch is the chat box
        borderPane.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());

        initChatBox(bridge);
        initRightMenu(bridge);

        ChatBoxContainer.setContent(chatBox);
        ChatBoxContainer.setFitToWidth(true);
        ChatBoxContainer.setPrefViewportHeight(500);
        ChatBoxContainer.getStyleClass().add("background");
        container.getChildren().addAll(ChatBoxContainer, message, sendMessage);

        //borderPane.setPrefSize(700, 900);
        borderPane.setCenter(container);

        Scene scene = new Scene(borderPane);
        window.setScene(scene);
    }

    private void initRightMenu(Bridge bridge){
        ArrayList<String[]> rooms =  bridge.getRooms();
        UpdateRooms(rooms);

        outerRight = new VBox();
        RoomsBox = new HBox();
        joinedRoomsBox = new VBox();
        availableRoomsBox = new VBox();

        joinRooms = new Button("join");
        leaveRooms = new Button("leave");

        //Leaves or joins Selected Rooms
        joinRooms.setOnAction(e -> JoinRooms());
        leaveRooms.setOnAction(e -> LeaveRooms());

        joinRooms.setAlignment(Pos.CENTER);
        leaveRooms.setAlignment(Pos.CENTER);

        joinedRoomsBox.getChildren().addAll(new Label("Joined Rooms"),joinedRooms);
        availableRoomsBox.getChildren().addAll(new Label("Available Rooms"), availableRooms);
        RoomsBox.getChildren().addAll(joinedRoomsBox, availableRoomsBox);
        outerRight.getChildren().addAll(RoomsBox, joinRooms, leaveRooms);

        outerRight.setAlignment(Pos.TOP_CENTER);

        borderPane.setRight(outerRight);
    }

    //Updates the room View
    public void UpdateRooms(ArrayList<String[]> rooms){
        joinedRooms = new ListView<>();
        availableRooms = new ListView<>();



        for(String[] k : rooms){
            if(k[1].equals("1")){
                joinedRooms.getItems().add(k[1]);
            }
            else
            {
                availableRooms.getItems().add(k[0]);
            }
        }

        joinedRooms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableRooms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }



    private void initChatBox(Bridge bridge){
        ChatBoxContainer.setPrefSize(400, 600);
        chatBox.getStyleClass().add("chatbox");

        sendMessage.setOnAction(evt->{
            bridge.SendMessage(message.getText());
            message.clear();
        });
        ChatBoxContainer.setContent(chatBox);
    }


    public void AddMessage(String username, String message){
        messages.add(new String[]{username, message});
        Label label;
        if(username.equals(bridge.getUsername())){

            label = new Label(messages.get(index)[0]+ ": \n" + messages.get(index)[1]);
            label.setStyle("-fx-background-color:green;");
            label.setAlignment(Pos.CENTER_LEFT);

        }else{

            label = new Label(messages.get(index)[0]+ ": \n" + messages.get(index)[1]);
            label.setAlignment(Pos.CENTER_RIGHT);

        }

        label.setWrapText(true);
        chatBox.getChildren().add(label);
        index++;
    }
    public void onRoomUpdate(ArrayList<String[]> rooms){
        UpdateRooms(rooms);
    }

    private void JoinRooms(){
        ArrayList<String> selected = new ArrayList<>();
        ObservableList<String> Selection = availableRooms.getSelectionModel().getSelectedItems();
        selected.addAll(Selection);


        bridge.JoinRooms(selected);
    }
    private void LeaveRooms(){
        ArrayList<String> selected = new ArrayList<>();
        ObservableList<String> Selection = joinedRooms.getSelectionModel().getSelectedItems();
        selected.addAll(Selection);

        bridge.LeaveRooms(selected);
    }


}
