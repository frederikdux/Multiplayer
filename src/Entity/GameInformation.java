package Entity;

import Entity.Vector2f;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class GameInformation implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    private ArrayList<Player> players = new ArrayList<Player>();
    private HashMap<Integer, Vector2f> playerPositions;
    private HashMap<Integer, Vector2f> playerRotations;

    public GameInformation(HashMap<Integer, Vector2f> playerPositions, HashMap<Integer, Vector2f> playerRotations) {
        this.playerPositions = playerPositions;
        this.playerRotations = playerRotations;
    }
    public GameInformation(){
        playerPositions = new HashMap<Integer, Vector2f>();
        playerRotations = new HashMap<Integer, Vector2f>();
    }

    public GameInformation(ArrayList<Player> players){
        this.players = players;
    }




    public void updatePlayer(Integer id, Player player){
        if(players.stream().noneMatch(player1 -> player1.getId() == id)){
            players.add(new Player(id, player));
        }
        else{
            players.stream().filter(player1 -> player1.getId() == id).forEach(player1 -> player1.setPos(player.getPos()));
        }
    }


    public void removePlayer(Integer id){
        playerPositions.remove(id);
        playerRotations.remove(id);
    }

    public void removePlayerByID(int id){
        players.removeIf(player -> player.getId() == id);
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

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public Optional<Player> getPlayerByID(Integer id) {
        return players.stream().filter(player -> player.getId() == id).findFirst();
    }
}
