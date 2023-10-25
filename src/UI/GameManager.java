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

    long lastUpdate = 0;
    long newUpdate = 0;
    long elapsedTime = 0;

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
        if(enemysLastPositions.containsKey(id)) {
            newUpdate = System.nanoTime();
            elapsedTime = (newUpdate - lastUpdate) / 1000;
            lastUpdate = newUpdate;
            float elapsedTimeSec = ((float) elapsedTime) / 100000;
            System.out.println(elapsedTimeSec);


            Vector2f lastPosition = enemysLastPositions.get(id);
            Vector2f velocity = new Vector2f((pos.x - lastPosition.x) * elapsedTimeSec, (pos.y - lastPosition.y) * elapsedTimeSec);
            enemysVelocity.replace(id, velocity);
            System.out.println("Velocity: " + velocity.x + " " + velocity.y);
            enemysLastPositions.replace(id, pos);
        }
        else{
            enemysLastPositions.put(id, pos);
        }
    }

    public void updateEnemies(){
        newUpdate = System.nanoTime();
        elapsedTime = (newUpdate - lastUpdate) / 1000;
        lastUpdate = newUpdate;
        float elapsedTimeSec = ((float)elapsedTime) / 100000;
        System.out.println(elapsedTimeSec);

        for (Player newEnemy: client.receivedGameInformations.getPlayers()){
            if(enemies.stream().anyMatch(enemy -> enemy.id == newEnemy.getId())){
                enemies.stream().forEach(enemy -> {
                    if(enemy.id == newEnemy.getId()){
                        Vector2f lastPosition = enemysLastPositions.get(enemy.id);
                        Vector2f velocity = new Vector2f((newEnemy.getPos().x - lastPosition.x) * elapsedTimeSec, (newEnemy.getPos().y - lastPosition.y) * elapsedTimeSec);
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
