package chatroom.model.message;

public class LoginMessage extends Message {
    private String loginName;
    private String password;

    public LoginMessage(String loginName, String password){
        type = 3;
        this.loginName = loginName;
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }
}
