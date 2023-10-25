package UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Networking.Client;
import Networking.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GameManager {
    public Player player;
    public final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public final HashMap<Integer, Vector2f> enemysLastPositions = new HashMap<>();
    public final HashMap<Integer, Vector2f> enemysVelocity = new HashMap<>();

    Long lastUpdate = 0L;
    Long newUpdate = 0L;
    long elapsedTimeMilli = 0;

    private Client client;

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

        Thread gameUpdater = new Thread(() -> {

        });

        backend.start();
        gameUpdater.start();
    }


    public void gameUpdate(){
        for (Enemy enemy : enemies){
        }
    }

    public void calculateVelocity(int id, Vector2f pos){
        newUpdate = System.nanoTime();
        elapsedTimeMilli = (newUpdate - lastUpdate) / 1000000;
        lastUpdate = newUpdate;
        float elapsedTime = ((float)elapsedTimeMilli) / 100;
        System.out.println(elapsedTime);


        Vector2f lastPosition = enemysLastPositions.get(id);
        Vector2f velocity = new Vector2f((pos.x - lastPosition.x) * elapsedTime, (pos.y - lastPosition.y) * elapsedTime);
        enemysVelocity.replace(id, velocity);
        System.out.println("Velocity: " + velocity.x + " " + velocity.y);
        enemysLastPositions.replace(id, pos);
    }

    public void updateEnemies(){
        newUpdate = System.nanoTime();
        elapsedTimeMilli = (newUpdate - lastUpdate) / 1000000;
        lastUpdate = newUpdate;
        float elapsedTime = ((float)elapsedTimeMilli) / 100;
        System.out.println(elapsedTime);

        for (Player newEnemy: client.receivedGameInformations.getPlayers()){
            if(enemies.stream().anyMatch(enemy -> enemy.id == newEnemy.getId())){
                enemies.stream().forEach(enemy -> {
                    if(enemy.id == newEnemy.getId()){
                        Vector2f lastPosition = enemysLastPositions.get(enemy.id);
                        Vector2f velocity = new Vector2f((newEnemy.getPos().x - lastPosition.x) * elapsedTime, (newEnemy.getPos().y - lastPosition.y) * elapsedTime);
                        enemysVelocity.replace(enemy.id, velocity);
                        System.out.println("Velocity: " + velocity.x + " " + velocity.y);
                        enemy.setPos(newEnemy.getPos());
                        enemysLastPositions.replace(enemy.id, enemy.getPos());
                    }
                });
            }
            else{
                enemies.add(new Enemy(newEnemy.getId(), newEnemy.getClientName(), newEnemy.getPos(), new Vector2f(0, 0)));
            }
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
