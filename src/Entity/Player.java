package Entity;

import java.io.Serializable;

public class Player implements Serializable {
    private Integer id;
    private Vector2f pos;
    private Vector2f rot;
    private String clientName;

    public Player(Vector2f pos, Vector2f rot) {
        this.pos = pos;
        this.rot = rot;
    }

    public Player(Player player){
        this.pos = player.getPos();
        this.rot = player.getRot();
    }

    public Player(String clientName, Player player){
        this.pos = player.getPos();
        this.rot = player.getRot();
        this.clientName = clientName;
    }

    public Player(Integer id, String clientName, Player player){
        this.pos = player.getPos();
        this.rot = player.getRot();
        this.clientName = clientName;
    }

    public Player(Integer id, Player player){
        this.pos = player.getPos();
        this.rot = player.getRot();
        this.id = id;
        this.clientName = player.getClientName();
    }

    public Player(String clientName, Vector2f pos, Vector2f rot){
        this.clientName = clientName;
        this.pos = pos;
        this.rot = rot;
    }

    public Vector2f getPos() {
        return pos;
    }

    public Vector2f getRot() {
        return rot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPos(Vector2f pos){
        this.pos = pos;
    }
    public void move(Vector2f movement){
        this.pos.x += movement.x;
        this.pos.y += movement.y;
    }

    public String getClientName() {
        return clientName;
    }
}
