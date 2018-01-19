package chatroom.server.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoomCreatingDeletingAndEditingBox {
    Bridge bridge;
    public RoomCreatingDeletingAndEditingBox(Bridge bridge, String roomToEdit){
        this.bridge = bridge;
        editRoomBox(roomToEdit);
    }


    public RoomCreatingDeletingAndEditingBox(Bridge bridge){
        this.bridge = bridge;
        createRoomBox();
    }



    private Stage window = new Stage();
    private Label heading;
    Button button;
    VBox layout = new VBox(10);
    TextField textField = new TextField();
    Label errorLabel = new Label();


    private void editRoomBox(String roomToEdit){
        setupBasicBox();
        window.setTitle("Edit Room");
        heading = new Label("Edit room: " + roomToEdit);

        HBox textfieldButtonBox = new HBox();
        textfieldButtonBox.setSpacing(10);
        textField.setPromptText("enter the new roomname");
        button = new Button("Edit Room");
        textfieldButtonBox.getChildren().addAll(textField, button);
        layout.getChildren().addAll(heading, textfieldButtonBox, errorLabel);

        button.setOnAction(e->{
            editRoom(roomToEdit);
        });
        textField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                editRoom(roomToEdit);
            }
        });
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private void createRoomBox(){
        setupBasicBox();
        window.setTitle("Create Room");
        heading = new Label("Create new Room");

        HBox textfieldButtonBox = new HBox();
        textfieldButtonBox.setSpacing(10);
        textField.setPromptText("enter the name");
        button = new Button("Create Room");
        textfieldButtonBox.getChildren().addAll(textField, button);
        layout.getChildren().addAll(heading, textfieldButtonBox, errorLabel);

        button.setOnAction(e->{
            createRoom();
        });
        textField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                createRoom();
            }
        });
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private void createRoom() {
        if(checkForIdenticalNames(textField.getText())){
            bridge.addRoom(textField.getText());
            window.close();
        }
        else
        {
            errorLabel.setText("The name: "+textField.getText() +"is already used. Please choose another one");
            errorLabel.setStyle("-fx-text-fill: red");
        }
    }

    private void setupBasicBox(){
        textField.getStyleClass().add("textArea");
        errorLabel.setWrapText(true);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getStyleClass().add("mainBackground");
        layout.getStylesheets().add(getClass().getResource("ServerHomeGuiStyle.css").toExternalForm());
        window.setMinHeight(150);
        window.initModality(Modality.APPLICATION_MODAL);
    }

    private boolean checkForIdenticalNames(String name){
        for(String k: bridge.requestRoomList()){
            if(k.equals(name)){
                return false;
            }
        }
        return true;
    }

    private void editRoom(String roomToEdit){
        if(checkForIdenticalNames(textField.getText())){
            bridge.editRoom(roomToEdit, textField.getText());
            window.close();
        }
        else
        {
            errorLabel.setText("The name: "+textField.getText() +"is already used. Please choose another one");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

}
