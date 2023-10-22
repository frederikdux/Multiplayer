package Entity;

import java.io.Serializable;

public class TextMessage implements Serializable {
    public String text;

    public TextMessage(String text) {
        this.text = text;
    }
}
