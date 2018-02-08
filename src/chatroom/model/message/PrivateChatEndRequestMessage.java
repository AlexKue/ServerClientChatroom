package chatroom.model.message;

public class PrivateChatEndRequestMessage extends Message{

    private String requester;
    private String partner;

    public PrivateChatEndRequestMessage(String requester, String partner){
        this.requester = requester;
        this.partner = partner;
        type = 15;
    }

    public String getRequester() {
        return requester;
    }

    public String getPartner() {
        return partner;
    }
}
