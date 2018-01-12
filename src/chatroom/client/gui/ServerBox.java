package chatroom.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


public class ServerBox {

    public static Pane ServerInput(Bridge bridge, String errorMessage){
            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(300, 150);
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setVgap(8);
            gridPane.setHgap(10);

            //Ip Label
            Label IpLabel = new Label("Enter Ip Adress");
            Label errorLabel = new Label(errorMessage);
            errorLabel.setStyle("-fx-text-fill: red");

            //Textfields
            TextField iPInput = new TextField();
            iPInput.setPromptText("Ip Adress");


            GridPane.setConstraints(IpLabel, 0,0);
            GridPane.setConstraints(iPInput, 1,0);

            Button ConnectButton = new Button("Connect");
            ConnectButton.setOnAction(e -> ConnectToAdress(iPInput.getText(), bridge));
            GridPane.setConstraints(ConnectButton, 1, 2);

            gridPane.getChildren().addAll(IpLabel, iPInput, ConnectButton, errorLabel);

            stackPane.getChildren().add(gridPane);
            StackPane.setAlignment(gridPane, Pos.CENTER_LEFT);


            stackPane.getStyleClass().add("mainBackground");
            ConnectButton.getStyleClass().add("Buttons");
            iPInput.getStyleClass().add("textArea");

            return stackPane;
    }
    private static void ConnectToAdress(String ip, Bridge bridge){
        bridge.ConnectToAdress(ip);
    }
}
