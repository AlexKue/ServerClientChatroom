package chatroom.model.message;

public class LoginResponseMessage extends Message {
    private LoginResponses response;
    public LoginResponseMessage(LoginResponses response){
        type = 6;
        this.response = response;
    }
    public LoginResponses getResponse(){
        return response;
    }
}
