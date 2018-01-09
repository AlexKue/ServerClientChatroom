package chatroom.client;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class Login {
    public static Pane LoginBox(Bridge bridge){
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(300, 150);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        //Password and name label
        Label nameLabel = new Label("Username");
        Label passwordLabel = new Label("Passoword");

        //Textfields
        TextField nameInput = new TextField();
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("password");


        GridPane.setConstraints(nameLabel, 0,0);
        GridPane.setConstraints(nameInput, 1,0);
        GridPane.setConstraints(passwordLabel, 0,1);
        GridPane.setConstraints(passwordInput, 1,1);

        Button loginButton = new Button("Log in");
        loginButton.setOnAction(e -> login(passwordInput, nameInput, bridge));
        GridPane.setConstraints(loginButton, 1, 2);

        gridPane.getChildren().addAll(nameInput, nameLabel, passwordInput, passwordLabel, loginButton);

        stackPane.getChildren().add(gridPane);
        StackPane.setAlignment(gridPane, Pos.CENTER_LEFT);

        return stackPane;
    }

    public static boolean login(TextField password, TextField username, Bridge bridge){
        bridge.sendLoginData(username.getText(), password.getText());

        return true;

    }
}
