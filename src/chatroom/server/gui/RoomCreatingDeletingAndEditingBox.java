package chatroom.server.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoomCreatingDeletingAndEditingBox {
    Bridge bridge;
    public RoomCreatingDeletingAndEditingBox(Bridge bridge, String roomToEdit){
        this.bridge = bridge;
        editRoom(roomToEdit);
    }


    public RoomCreatingDeletingAndEditingBox(Bridge bridge){
        this.bridge = bridge;
        createRoom();
    }



    private Stage window = new Stage();
    private Label heading;
    Button button;
    VBox layout = new VBox(10);
    TextField textField = new TextField();
    Label errorLabel = new Label();


    private void editRoom(String roomToEdit){
        setupBasicBox();
        window.setTitle("Edit Room");
        heading = new Label("Edit room: " + roomToEdit);

        HBox textfieldButtonBox = new HBox();
        textField.setPromptText("enter the new roomname");
        button = new Button("Edit Room");
        textfieldButtonBox.getChildren().addAll(textField, button);
        layout.getChildren().addAll(heading, textfieldButtonBox, errorLabel);

        button.setOnAction(e->{
            if(checkForIdenticalNames(textField.getText())){
                bridge.editRoom(roomToEdit, textField.getText());
                window.close();
            }
            else
            {
                errorLabel.setText("The name: "+textField.getText() +"is already used. Please choose another one");
                errorLabel.setStyle("-fx-text-fill: red; -fx-content-display: block");
            }
        });
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private void createRoom(){
        setupBasicBox();
        window.setTitle("Create Room");
        heading = new Label("Create new Room");

        HBox textfieldButtonBox = new HBox();
        textField.setPromptText("enter the name");
        button = new Button("Create Room");
        textfieldButtonBox.getChildren().addAll(textField, button);
        layout.getChildren().addAll(heading, textfieldButtonBox, errorLabel);

        button.setOnAction(e->{
            if(checkForIdenticalNames(textField.getText())){
                bridge.addRoom(textField.getText());
                window.close();
            }
            else
            {
                errorLabel.setText("The name: "+textField.getText() +"is already used. Please choose another one");
                errorLabel.setStyle("-fx-text-fill: red; -fx-content-display: block");
            }
        });
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private void setupBasicBox(){
        errorLabel.setStyle("-fx-content-display: none;");
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

}
