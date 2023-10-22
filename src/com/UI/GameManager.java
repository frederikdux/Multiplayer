package com.UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Networking.ChatClient;
import Networking.ChatServer;
import Networking.Client;
import Networking.Server;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameManager {
    public Player player;
    public ArrayList<Enemy> enemies = new ArrayList<>();
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
            while (!startAsServer) {
                //client.sendPlayerInformation(player);
            }
        });

        backend.start();
    }



    public void updateEnemies(){
        for(Enemy enemy: enemies){

        }
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
        Enemy enemy = new Enemy(player.getClientName(), player.getPos(), player.getRot());
        enemies.add(enemy);
    }
    public void logoutEnemy(int id){
        enemies.remove(id);
    }

}
