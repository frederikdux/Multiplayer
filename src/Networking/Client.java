package Networking;

import Entity.*;
import UI.GameManager;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public GameInformation receivedGameInformations;

    PrintWriter writer;

    ObjectOutputStream output;

    Socket socket;

    GameManager manager;

    public Client(GameManager manager) {
        this.manager = manager;
        try {
            socket = new Socket("localhost", 12345);
            System.out.println("Verbindung zum Server hergestellt.");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Gib deinen Namen ein: ");
            String clientName = scanner.nextLine();

            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(new Message("registerNewPlayer", new Player(clientName, manager.player)));


            // Thread zum Empfangen von Nachrichten vom Server
            Thread receiveThread = new Thread(() -> {
                try {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Message message;
                    while ((message = (Message) in.readObject()) != null) {

                        if(message.messageType.equals("gameInformation")){
                            receivedGameInformations = (GameInformation) message.message;
                            System.out.println(receivedGameInformations.getPlayers().size());
                            manager.updateEnemies();
                        }



                        if(message.messageType.equals("playerData")){
                            Player player = (Player) message.message;
                            if(manager.enemies.stream().noneMatch(enemy -> player.getId() == enemy.id)) {
                                System.out.println("Neuer Gegner registriert");
                                manager.registerNewEnemy(player);
                            }
                            else{
                                manager.enemies.stream().filter(enemy -> enemy.id == player.getId()).forEach(enemy -> enemy.setPos(player.getPos()));
                            }
                        }


                        if(message.messageType.equals("String")){
                            System.out.println(((TextMessage) message.message).text);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // Benutzereingabe und Senden an den Server
            String userInput;

        } catch (IOException e) {
            e.printStackTrace();
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
