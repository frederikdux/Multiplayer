package Networking;

import Entity.*;
import UI.GameManager;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public GameInformation receivedGameInformations;

    PrintWriter writer;

    ObjectOutputStream output;

    Socket socket;

    GameManager manager;

    String clientName;

    public Client(GameManager manager) {
        this.manager = manager;
        try {
            socket = new Socket("25.37.137.22", 12345);

            if(socket.isConnected()) {
                System.out.println("Verbindung zum Server hergestellt.");
            }
            else{
                System.out.println("Verbindung fehlgeschlagen!");
            }

            Scanner scanner = new Scanner(System.in);

            System.out.print("Gib deinen Namen ein: ");
            clientName = scanner.nextLine();
            manager.player.setClientName(clientName);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(new Message("registerNewPlayer", new Player(clientName, manager.player)));


            // Thread zum Empfangen von Nachrichten vom Server
            Thread receiveThread = new Thread(() -> {
                try {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Message message;
                    while ((message = (Message) in.readObject()) != null) {
                        System.out.println("Received data of Type: " + message.messageType);

                        switch(message.messageType){
                            case "gameInformation":
                                extractGameInformation(manager, message);
                                break;
                            case "playerData":
                                extractPlayerData(manager, message);
                                break;
                            case "String":
                                System.out.println(((TextMessage)message.message).text);
                                break;
                            default:
                                extractCustomMessage(message);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractPlayerData(GameManager manager, Message message) {
        Player player = (Player) message.message;
        if(manager.enemies.stream().noneMatch(enemy -> player.getId() == enemy.id)) {
            System.out.println("Neuer Gegner registriert");
            manager.registerNewEnemy(player);
        }
        else{
            manager.enemies.stream().filter(enemy -> enemy.id == player.getId()).forEach(enemy -> enemy.setPos(player.getPos()));
        }
    }

    private void extractGameInformation(GameManager manager, Message message) {
        receivedGameInformations = (GameInformation)message.message;
        manager.updateEnemies();
    }


    private void extractCustomMessage(Message message){
        switch (message.messageType){
            case "playerEvent":
                PlayerEvent playerEvent = (PlayerEvent) message.message;
                switch (playerEvent.event){
                    case "playerLeftServer":
                        manager.logoutEnemy(playerEvent.player.getId());
                        break;
                }
                break;
        }
    }


    public void sendPlayerInformation(Player player){
        try {
            output.writeObject(new Message("playerData", player));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendVector2f(Vector2f vector2f){
        try {
            output.writeObject(new Message("Vector2f", vector2f));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text){
        try {
            output.writeObject(new Message("String", new TextMessage(text)));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
