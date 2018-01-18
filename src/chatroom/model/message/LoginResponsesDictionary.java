package chatroom.model.message;

import java.util.HashMap;

public class LoginResponsesDictionary {
    private HashMap<Byte, LoginResponses> loginResponseHashMap;
    private HashMap<LoginResponses,Byte> reverseHashMap;

    public LoginResponsesDictionary() {
        loginResponseHashMap = new HashMap<>();
        reverseHashMap = new HashMap<>();

        loginResponseHashMap.put((byte) 0, LoginResponses.SUCCESS);
        loginResponseHashMap.put((byte) 1, LoginResponses.CREATED_ACCOUNT);
        loginResponseHashMap.put((byte) 2, LoginResponses.WRONG_PASSWORD);
        loginResponseHashMap.put((byte) 3, LoginResponses.ALREADY_LOGGED_IN);
        loginResponseHashMap.put((byte) 4, LoginResponses.BANNED);

        for(byte key : loginResponseHashMap.keySet()){
            reverseHashMap.put(loginResponseHashMap.get(key), key);
        }
    }

    public LoginResponses getType(byte type) {
        return loginResponseHashMap.get(type);
    }

    public byte getByte(LoginResponses messageType){
        return reverseHashMap.get(messageType);
    }

}
