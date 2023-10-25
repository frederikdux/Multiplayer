package UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Networking.Client;
import Networking.Server;

import java.util.ArrayList;

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
        System.out.println("Received " + client.receivedGameInformations.getPlayers().size() + " player positions!");
        for(Player newPlayer : client.receivedGameInformations.getPlayers()){
            System.out.println(newPlayer.getId() + ": " + newPlayer.getPos().x + " " + newPlayer.getPos().y);
        }
        for (Player newEnemy: client.receivedGameInformations.getPlayers()){
            if(enemies.stream().anyMatch(enemy -> enemy.id == newEnemy.getId())){
                enemies.stream().forEach(enemy -> {
                    System.out.println("enemy: " + enemy.id + "  newEnemy: " + newEnemy.getId());
                    if(enemy.id == newEnemy.getId()){
                        enemy.setPos(newEnemy.getPos());
                    }
                });
            }
            else{
                enemies.add(new Enemy(newEnemy.getId(), newEnemy.getClientName(), newEnemy.getPos(), new Vector2f(0, 0)));
            }
            enemies.forEach(enemy-> System.out.println(enemy.getClientName()));
        }

        enemies.removeIf(enemy -> client.receivedGameInformations.getPlayerByID(enemy.id).isEmpty());
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
        //client.receivedGameInformations.removePlayerByID(id);
        //enemies.remove(id);
    }

}
