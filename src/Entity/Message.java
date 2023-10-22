package Entity;

import java.io.Serializable;

public class Message implements Serializable{
    public String messageType;
    public Serializable message;

    public Message(String messageType, Serializable message) {
        this.messageType = messageType;
        this.message = message;
    }
}
