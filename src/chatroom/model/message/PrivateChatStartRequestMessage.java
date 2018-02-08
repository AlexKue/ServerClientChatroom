package chatroom.model.message;

public class PrivateChatStartRequestMessage extends Message {

    private String requester;
    private String partner;

    public PrivateChatStartRequestMessage(String requester, String partner){
        this.requester = requester;
        this.partner = partner;
        type = 14;
    }

    public String getRequester() {
        return requester;
    }

    public String getPartner() {
        return partner;
    }
}
