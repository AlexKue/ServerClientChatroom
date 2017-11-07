package chatroom.model;

public class UserAccountInfo {
    private String loginName;
    private String displayName;
    private String password;

    public UserAccountInfo(String loginName, String password){
        this.loginName = loginName;
        displayName = loginName;
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isPasswordCorrect(String password){
        return this.password.equals(password);
    }
}
