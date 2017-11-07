package chatroom.model;

public class LoginMessage extends Message {
    //TODO: find parent
    private String loginName;
    private String password;

    public LoginMessage(String loginName, String password){
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
