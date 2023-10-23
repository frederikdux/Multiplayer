package UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Networking.ChatClient;
import Networking.ChatServer;
import Networking.Client;
import Networking.Server;

import java.util.ArrayList;
import java.util.Map;

public class GameManager {
    public Player player;
    public final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private Client client;
    private Game game;

    public GameManager(boolean startAsServer){
        this.player = new Player(new Vector2f(50, 50), new Vector2f(0, 0));


        Thread backend = new Thread(() -> {
            Server server;

            if (startAsServer) {
                server = new Server();
            }
            else {
                client = new Client(this);
            }

        });

        backend.start();
    }



    public void updateEnemies(){
        System.out.println(client.receivedGameInformations.getPlayers().size());
        for (Player player: client.receivedGameInformations.getPlayers()){
            if(enemies.stream().anyMatch(enemy -> enemy.id == player.getId())){
                enemies.stream().forEach(enemy -> {
                    if(enemy.id == player.getId()){
                        enemy.setPos(player.getPos());
                    }
                });
            }
            else{
                enemies.add(new Enemy(player.getId(), player.getClientName(), getPlayerPos(), new Vector2f(0, 0)));
            }
        }

        enemies.forEach(enemy -> {
            if(client.receivedGameInformations.getPlayerByID(enemy.id).isEmpty()){
                enemies.remove(enemy);
            }
        });

        /*for (Map.Entry<Integer, Vector2f> entry : client.receivedGameInformations.getPlayerPositions().entrySet()){
            if(enemies.stream().anyMatch(enemy -> enemy.id == entry.getKey())){
                enemies.stream().forEach(enemy -> {
                    if(enemy.id == entry.getKey()){
                        enemy.setPos(entry.getValue());
                    }
                });
            }
            else{
                enemies.add(new Enemy(entry.getKey(), entry.getValue(), new Vector2f(0, 0)));
            }
        }*/
    }

    public Client getClient() {
        return client;
    }

    public Vector2f getPlayerPos(){
        return player.getPos();
    }
    public void setPlayerPos(Vector2f pos){
        player.setPos(pos);
    }

    public void registerNewEnemy(Player player){
        Enemy enemy = new Enemy(player.getId(), player.getClientName(), player.getPos(), player.getRot());
        enemies.add(enemy);
    }
    public void logoutEnemy(int id){
        enemies.remove(id);
    }

}
