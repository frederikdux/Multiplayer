package Networking;

import Entity.*;
import Other.Constants;
import UI.GameManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    public GameInformation receivedGameInformations;

    PrintWriter writer;

    ObjectOutputStream output;
    ObjectInputStream in;

    Socket dServerSocket;
    Socket socket;

    GameManager manager;

    String clientName;

    Scanner scanner;


    List<ServerData> serverDataList = new ArrayList<>();

    public String currentServerInformation;
    public Client(GameManager manager) {
        scanner = new Scanner(System.in);
        this.manager = manager;

        receiveServerList();
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

    private void extractServerList(Message message){
        System.out.println("Received ServerDataList...");
        serverDataList = ((ServerListDTO) message.message).getServerDataList();
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


    public void receiveServerList(){
        try {
            dServerSocket = new Socket(Constants.localIpAdress, 23232);

            if(dServerSocket.isConnected()) {
                System.out.println("Verbindung zum Distribution-Server hergestellt.");
            }
            else{
                System.out.println("Verbindung zum Distribution-Server fehlgeschlagen!");
            }


            System.out.print("Gib deinen Namen ein: ");
            clientName = scanner.nextLine();
            manager.player.setClientName(clientName);
            output = new ObjectOutputStream(dServerSocket.getOutputStream());
            output.writeObject(new Message("receiveServerList", "null"));

            System.out.println("Läd Server-List...");

            // Thread zum Empfangen von Nachrichten vom Server
            Thread receiveThread = new Thread(() -> {
                try {
                    in = new ObjectInputStream(dServerSocket.getInputStream());
                    Message message;
                    while ((message = (Message) in.readObject()) != null && this.dServerSocket.isConnected()) {
                        System.out.println("Received data of Type: " + message.messageType);

                        switch(message.messageType){
                            case "serverList":
                                extractServerList(message);
                                break;
                            case "String":
                                System.out.println(((TextMessage)message.message).text);
                                break;
                            default:
                                extractCustomMessage(message);
                        }
                        joinServer();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            System.out.println("Es ist ein Fehler aufgetreten!");
            e.printStackTrace();;
        }
    }

    public void joinServer(){
        int counter = 0;
        System.out.println("\n\nServer-List:");
        for (ServerData serverData : serverDataList) {
            System.out.println(counter + ": " + serverData.getServerName() + "  Adress: " + serverData.getAddr());
            counter++;
        }
        System.out.println("Welchem Server joinen?");


        int joinServerNumber = Integer.parseInt(scanner.nextLine());
        try {
            System.out.println(serverDataList.get(joinServerNumber).getAddr());
            socket = new Socket(serverDataList.get(joinServerNumber).getAddr(), serverDataList.get(joinServerNumber).getPort());

            if(socket.isConnected()) {
                System.out.println("Verbindung zum Server hergestellt.");
                currentServerInformation = serverDataList.get(joinServerNumber).getServerName() + " - " + serverDataList.get(joinServerNumber).getAddr();
            }
            else{
                System.out.println("Verbindung fehlgeschlagen!");
            }

            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(new Message("registerNewPlayer", new Player(clientName, manager.player)));


            // Thread zum Empfangen von Nachrichten vom Server
            Thread receiveThread = new Thread(() -> {
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    Message message;
                    while ((message = (Message) in.readObject()) != null && this.socket.isConnected()) {
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
}
