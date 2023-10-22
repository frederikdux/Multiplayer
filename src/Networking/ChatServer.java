package Networking;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static Map<PrintWriter, String> clientNames = new HashMap<>();

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Chat Server gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                ClientHandler clientHandler = new ClientHandler(clientSocket, out);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println("Neue Verbindung hergestellt.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
            try {
                //out.println("Gib deinen Namen ein:");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientName = in.readLine();
                clientNames.put(out, clientName);
                broadcastMessage(clientName + " ist dem Chat beigetreten.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(out);
                clientNames.remove(out);
                broadcastMessage(clientName + " hat den Chat verlassen.");
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(message);
                }
            }
        }
    }
}
