package Entity;

import java.io.Serial;
import java.io.Serializable;

public class TextMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    public String text;

    public TextMessage(String text) {
        this.text = text;
    }
}
