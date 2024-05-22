package Networking;

import Entity.*;
import Other.Constants;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    private static List<ObjectOutputStream> outputStreams = new ArrayList<>();
    private static Map<ObjectOutputStream, String> clientNames = new HashMap<>();
    private static Map<ObjectOutputStream, Integer> clientIDs = new HashMap<>();

    private static int counter=0;

    public GameInformation gameInformation = new GameInformation();

    Socket distributionServerSocket;
    ObjectOutputStream output;
    ServerData serverData;

    public Server(){
        //Clientverhalten zum DistributionServer
        try {
            distributionServerSocket = new Socket(Constants.localIpAdress, 23232);
            serverData = new ServerData("", Constants.localIpAdress, 12345);

            if (distributionServerSocket.isConnected()) {
                System.out.println("Verbindung zum Server hergestellt.");
            } else {
                System.out.println("Verbindung fehlgeschlagen!");
            }

            Scanner scanner = new Scanner(System.in);

            System.out.print("Gib deinen Namen ein: ");
            serverData.setServerName(scanner.nextLine());

            output = new ObjectOutputStream(distributionServerSocket.getOutputStream());
            output.writeObject(new Message("registerNewServer", serverData));

        } catch (Exception e) {

        }


            //Serververhalten
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

                try {
                    while ((message = (Message) in.readObject()) != null) {
                        //System.out.println("received Data of Type: " + message.messageType);

                        switch (message.messageType) {
                            case "gameInformation":
                                extractGameInformation(message);
                                break;
                            case "playerData":
                                extractPlayerData(message);
                                break;
                            case "registerNewPlayer":
                                extractRegisterNewPlayerRequest(message);
                                break;
                            case "Vector2f":
                                extractVector2f(message);
                                break;
                            case "String":
                                System.out.println(((TextMessage) message.message).text);
                                break;
                        }
                    }
                }catch(SocketException e){
                    //logoutUser();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                logoutUser();
            }
        }




        private void extractRegisterNewPlayerRequest(Message message) {
            if(clientName == null){
                Player newPlayer = ((Player) message.message);
                clientId = counter++;
                clientName = newPlayer.getClientName() + "#" + clientId;
                clientNames.put(out, clientName);
                clientIDs.put(out, clientId);
                System.out.println(clientName + " ist dem Server beigetreten.");

                server.gameInformation.updatePlayer(clientId, newPlayer);
                broadcastTextMessage(clientName + " ist dem Server beigetreten.");
                broadcastDirectGameInformation(server.gameInformation, out);

                broadcastPlayerData(new Player(clientId, newPlayer));
            }
        }

        private void extractVector2f(Message message) {
            Vector2f pos = ((Vector2f) message.message);
            //System.out.println(clientName + ": " + pos.x + " / " + pos.y);
        }

        private void extractPlayerData(Message message) {
            Player receivedPlayer = ((Player) message.message);
            //System.out.println(clientName + ": " + receivedPlayer.getPos().x + " / " + receivedPlayer.getPos().y);
            server.gameInformation.updatePlayer(clientId, receivedPlayer);
            broadcastPlayerData(new Player(clientId, receivedPlayer));
        }

        private void extractGameInformation(Message message) {
            server.gameInformation = (GameInformation) message.message;
            Vector2f pos;
            for(int i = 0; i < server.gameInformation.getPlayerPositions().size(); i++){
                pos = server.gameInformation.getPlayerPositions().get(i);
                //System.out.println(clientName + ": " + pos.x + " / " + pos.y);
            }

            System.out.println("received GameInformation");
        }


        private void logoutUser(){
            outputStreams.remove(out);
            clientNames.remove(out);
            clientIDs.remove(out);
            server.gameInformation.removePlayerByID(clientId);
            System.out.println(clientName + " hat den Server verlassen.");
            broadcastTextMessage(clientName + " hat den Chat verlassen.");
            broadcastGameInformation(server.gameInformation);
            //broadcastCustom("playerEvent", new Message("playerLeftServer_ID", clientId));
        }


        ////SEND////

        private void broadcastCustom(String messageType, Serializable message){
            System.out.println("Broadcasted data of Type: " + messageType);

            try {
                for (ObjectOutputStream output : outputStreams) {
                    if (output != out) {
                        output.writeObject(new Message(messageType, message));
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

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
                GameInformation info = new GameInformation();
                for (ObjectOutputStream output : outputStreams) {
                    int id = clientIDs.get(output);
                    info = new GameInformation(new ArrayList<>(gameInformation.getPlayers().stream().filter(player -> player.getId() != id).toList()));
                    output.writeObject(new Message("gameInformation", info));
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private void broadcastDirectGameInformation(GameInformation gameInformation, ObjectOutputStream outputStream) {
            try {
                GameInformation info = new GameInformation(new ArrayList<>(gameInformation.getPlayers().stream().filter(player -> player.getId() != clientId).toList()));
                if(info.getPlayers().size() > 0) {
                    System.out.println("Send gameInfo to " + clientName);
                    for(Player player : info.getPlayers()){
                        System.out.println(player.getId() + ": " + player.getPos().x + " " + player.getPos().y);
                    }
                    outputStream.writeObject(new Message("gameInformation", info));
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
