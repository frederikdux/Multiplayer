package Entity;

public class Enemy {
    private Vector2f pos;
    private Vector2f rot;
    private String clientName;

    public Enemy(String clientName, Vector2f pos, Vector2f rot) {
        this.pos = pos;
        this.rot = rot;
        this.clientName = clientName;
    }

    public Vector2f getPos() {
        return pos;
    }

    public Vector2f getRot() {
        return rot;
    }

    public String getClientName() {
        return clientName;
    }
    public void move(Vector2f movement){
        this.pos.x += movement.x;
        this.pos.y += movement.y;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
    }
}
