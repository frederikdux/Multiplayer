package Entity;

import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = 6529685098267757691L;
    public String messageType;
    public Serializable message;

    public Message(String messageType, Serializable message) {
        this.messageType = messageType;
        this.message = message;
    }
}
