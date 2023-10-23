package Networking;

import Entity.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {
    private static List<ObjectOutputStream> outputStreams = new ArrayList<>();
    private static Map<ObjectOutputStream, String> clientNames = new HashMap<>();

    private static int counter=0;

    public GameInformation gameInformation = new GameInformation();

    public Server(){
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStreams.add(out);


                ClientHandler clientHandler = new ClientHandler(clientSocket, out, this);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println("Neue Verbindung hergestellt.");
                System.out.println("User " + clientSocket.getInetAddress() + " registriert!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String clientName = null;
        private Integer clientId = null;
        private Server server;

        public ClientHandler(Socket socket, ObjectOutputStream out, Server server) {
            this.socket = socket;
            this.out = out;
            this.server = server;

        }

        ////RECEIVE////
        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                Message message;
                while ((message = (Message) in.readObject()) != null) {
                    //System.out.println("received Data of Type: " + message.messageType);
                    if(message.messageType.equals("gameInformation")){
                        server.gameInformation = (GameInformation) message.message;
                        Vector2f pos;
                        for(int i = 0; i < server.gameInformation.getPlayerPositions().size(); i++){
                            pos = server.gameInformation.getPlayerPositions().get(i);
                            System.out.println(clientName + ": " + pos.x + " / " + pos.y);
                        }
                    }

                    if(message.messageType.equals("playerData")){
                        Player receivedPlayer = ((Player)message.message);
                        System.out.println(clientName + ": " + receivedPlayer.getPos().x + " / " + receivedPlayer.getPos().y);
                        server.gameInformation.updatePlayer(clientId, receivedPlayer);
                        broadcastPlayerData(new Player(clientId, receivedPlayer));
                    }

                    if(message.messageType.equals("Vector2f")){
                        Vector2f pos = ((Vector2f) message.message);
                        System.out.println(clientName + ": " + pos.x + " / " + pos.y);
                    }

                    if(message.messageType.equals("String")){
                        System.out.println(((TextMessage) message.message).text);
                    }

                    if(message.messageType.equals("registerNewPlayer")){
                        if(clientName == null){
                            Player newPlayer = ((Player) message.message);
                            clientId = counter;
                            clientName = newPlayer.getClientName() + "#" + counter++;
                            clientNames.put(out, clientName);
                            System.out.println(clientName + " ist dem Server beigetreten.");

                            server.gameInformation.updatePlayer(clientId, newPlayer);
                            System.out.println(clientName + ": name Set!");
                            broadcastTextMessage(clientName + " ist dem Server beigetreten.");
                            broadcastDirectGameInformation(server.gameInformation, out);
                            System.out.println("Send " + server.gameInformation.getPlayers().size() + " player positions!");
                            broadcastPlayerData(new Player(clientId, newPlayer));
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                outputStreams.remove(out);
                clientNames.remove(out);
                server.gameInformation.removePlayer(clientId);
                broadcastTextMessage(clientName + " hat den Chat verlassen.");
                broadcastGameInformation(server.gameInformation);
            }
        }




        ////SEND////
        private void broadcastTextMessage(String message) {
            try {
                for (ObjectOutputStream output : outputStreams) {
                    if (output != out) {
                        output.writeObject(new Message("String", new TextMessage(message)));
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private void broadcastPlayerData(Player player) {
            try {
                for (ObjectOutputStream output : outputStreams) {
                    if (output != out) {
                        output.writeObject(new Message("playerData", player));
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private void broadcastGameInformation(GameInformation gameInformation) {
            try {
                for (ObjectOutputStream output : outputStreams) {
                    if (output != out) {
                        output.writeObject(new Message("gameInformation", gameInformation));
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private void broadcastDirectGameInformation(GameInformation gameInformation, ObjectOutputStream outputStream) {
            try {
                GameInformation info = new GameInformation(new ArrayList<Player>(gameInformation.getPlayers().stream().filter(player -> player.getId() != clientId).toList()));
                if(info.getPlayers().size() > 0) {
                    outputStream.writeObject(new Message("gameInformation", info));
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
