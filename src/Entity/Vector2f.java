package Entity;

import java.io.Serial;
import java.io.Serializable;

public class Vector2f implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    public float x;
    public float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
