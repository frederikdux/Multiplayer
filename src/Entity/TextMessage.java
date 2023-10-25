package Entity;

import java.io.Serializable;

public class TextMessage implements Serializable {
    private static final long serialVersionUID = 6529685098267757694L;

    public String text;

    public TextMessage(String text) {
        this.text = text;
    }
}
