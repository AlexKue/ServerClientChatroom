package chatroom.model;

public abstract class Message {
    protected byte type;
    private transient int id;

    public byte getType(){
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
