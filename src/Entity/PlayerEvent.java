package Entity;

import java.io.Serializable;

public class PlayerEvent implements Serializable {
    public Player player;
    public String event;

    public PlayerEvent(Player player, String event) {
        this.player = player;
        this.event = event;
    }
}
