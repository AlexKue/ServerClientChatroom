package chatroom.ClientGui;


import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GuiMain{

   Bridge bridge;
   Stage window;
   HomeGui homeGui;




   public void runGui(Stage primeStage, Bridge bridge){
       this.window = primeStage;
       this.bridge = bridge;


       window.setTitle("The Chat Client");
       window.setOnCloseRequest(e -> onClose());


       Pane ipBox = ServerBox.ServerInput(bridge);
       Scene scene = new Scene(ipBox);

       window.setScene(scene);
       window.show();

    }
    public void onLoginAnswer(boolean answer){
        if(answer)
            LoadHomeWindow();
    }

    public void LoadHomeWindow(){
        this.homeGui = new HomeGui(bridge, window);
    }

    public void AddMessage(String username, String message){
        homeGui.addMessage(username, message);
    }

    public void onRoomUpdate(ArrayList<String[]> rooms){
       homeGui.onRoomUpdate(rooms);
    }


    public void onConnect(boolean b) {
       if(b) {
           Pane loginBox = Login.LoginBox(bridge);
           Scene scene = new Scene(loginBox);
           window.setScene(scene);
       }

    }

    public void onRoomChange(String room) {
       homeGui.onRoomChange(room);
       }

       public void onClose(){
       bridge.runClosinOperations();
    }
}
