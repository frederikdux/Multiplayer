package Entity;

import java.io.Serial;
import java.io.Serializable;

public class PlayerEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;
    public Player player;
    public String event;

    public PlayerEvent(Player player, String event) {
        this.player = player;
        this.event = event;
    }
}
