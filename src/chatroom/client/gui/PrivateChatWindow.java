package chatroom.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PrivateChatWindow {
    Bridge bridge;
    String username;
    public PrivateChatWindow(Bridge bridge, String username){
        this.bridge = bridge;
        this.username= username;
    }

    private Stage window = new Stage();
    private Label heading;
    private VBox layout = new VBox(10);

    //Chat Box Elements
    private Button sendMessage = new Button("Send");
    private VBox chatBox = new VBox(9);
    private List<String[]> messages = new ArrayList<>();
    private ScrollPane chatBoxContainer = new ScrollPane();
    private VBox container = new VBox();
    private int index = 0;
    private TextArea message = new TextArea();




    public void loadPrivateChatBox(){
        window.setTitle("Private Chat with "+ this.username);
        layout.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());
        layout.getStyleClass().add("mainBackground");
        layout.setPadding(new Insets(10, 10, 10, 10));

        message.setPrefSize(200, 100);

        message.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER && !key.isShiftDown()) {
                bridge.sendPrivateMessage(message.getText(), username);
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
        initChatBox();

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
        layout.getChildren().addAll(container);
        window.setScene(new Scene(layout));
        window.setOnCloseRequest(e->closePrivateChat());
        window.show();

    }



    private void initChatBox() {
        chatBoxContainer.setPrefSize(300, 200);
        sendMessage.setMinSize(100, 10);
        chatBoxContainer.getStyleClass().add("spacing");
        chatBox.getStyleClass().add("chatbox");

        sendMessage.setOnAction(evt -> {
            bridge.sendPrivateMessage(message.getText(), username);
            message.clear();
        });
        chatBox.getStyleClass().add("spacing");
        chatBoxContainer.setContent(chatBox);
    }


    public void addMessage(String username, String message, boolean isServer) {
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

        } else if(isServer) {
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
    private void closePrivateChat() {
        bridge.privateChatDisconnected(bridge.getUsername(), username);
    }


    public void changeChatFielStatus(boolean newStatus){
        message.setEditable(newStatus);
    }
    public void closeWindow(){
        window.close();
    }
}
