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

public class DistributionServer {
    private static List<ObjectOutputStream> outputStreams = new ArrayList<>();
    private static Map<ObjectOutputStream, ServerData> serverDataMap = new HashMap<>();

    private static int counter = 0;

    public DistributionServer() {
        try {
            ServerSocket distributionServerSocket = new ServerSocket(23232);
            System.out.println("Distribution_Server gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket serverSocket = distributionServerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());

                ServerClientHandler serverClientHandler = new ServerClientHandler(serverSocket, out, this);
                Thread thread = new Thread(serverClientHandler);
                thread.start();
                System.out.println("Neue Verbindung hergestellt.");
                System.out.println("User " + serverSocket.getInetAddress() + " registriert!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServerClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private ServerData serverData = new ServerData();
        private Integer serverId = null;
        private DistributionServer distributionServer;

        public ServerClientHandler(Socket socket, ObjectOutputStream out, DistributionServer distributionServer) {
            this.socket = socket;
            this.out = out;
            this.distributionServer = distributionServer;

        }

        ////RECEIVE////
        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                Message message;

                try {
                    while ((message = (Message) in.readObject()) != null) {
                        switch (message.messageType) {
                            case "registerNewServer":
                                extractRegisterNewServerRequest(message);
                                break;
                            case "receiveServerList":
                                sendServerList();
                                break;
                            case "String":
                                System.out.println(((TextMessage) message.message).text);
                                break;
                        }
                    }
                } catch (SocketException e) {
                    //logoutUser();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                logoutServer();
            }
        }

        private void extractRegisterNewServerRequest(Message message) {
            if (serverData.getServerName() == null) {
                serverData = ((ServerData) message.message);
                serverData.setId(counter++);
                outputStreams.add(out);
                serverDataMap.put(out, serverData);
                System.out.println(serverData.getAddr() + " ist nun als Server registriert.");
            }
        }


        private void logoutServer() {
            outputStreams.remove(out);
            serverDataMap.remove(out);
            System.out.println(serverData.getServerName() + " hat sich als Server abgemeldet.");
        }


        ////SEND////

        public void sendServerList() {
            System.out.println("ServerList angefragt.");
            try {
                List<ServerData> serverDataList = new ArrayList<>();
                for (OutputStream outputStream : outputStreams) {
                    serverDataList.add(serverDataMap.get(outputStream));
                    System.out.println(serverDataMap.get(outputStream).getAddr() + "  " + serverDataMap.get(outputStream).getAddr());
                }
                out.writeObject(new Message("serverList", new ServerListDTO(serverDataList)));

                out.flush();
                System.out.println("ServerList send!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}