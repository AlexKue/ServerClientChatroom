package chatroom.ClientGui;


import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GuiMain{

   Bridge bridge;
   Stage window;



   public void runGui(Stage primeStage, Bridge bridge){
       this.window = primeStage;
       this.bridge = bridge;


       window.setTitle("The Chat Client");


       Pane loginBox = Login.LoginBox(bridge);
       Scene scene = new Scene(loginBox);

       window.setScene(scene);
       window.show();

    }
    public void onLoginAnswer(boolean answer){
        if(answer)
            window.close();
    }



}
