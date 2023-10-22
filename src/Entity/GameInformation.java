package Entity;

import Entity.Vector2f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameInformation implements Serializable {
    private HashMap<Integer, Vector2f> playerPositions;
    private HashMap<Integer, Vector2f> playerRotations;

    public GameInformation(HashMap<Integer, Vector2f> playerPositions, HashMap<Integer, Vector2f> playerRotations) {
        this.playerPositions = playerPositions;
        this.playerRotations = playerRotations;
    }
    public GameInformation(){

    }

    public void addInformation(Player player){

    }

    public HashMap<Integer, Vector2f> getPlayerPositions() {
        return playerPositions;
    }

    public Vector2f getPlayerPositionByID(int id){
        return playerPositions.get(id);
    }

    public void setPlayerPositions(HashMap<Integer, Vector2f> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public HashMap<Integer, Vector2f> getPlayerRotations() {
        return playerRotations;
    }

    public void setPlayerRotations(HashMap<Integer, Vector2f> playerRotations) {
        this.playerRotations = playerRotations;
    }
}
