package Networking;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public ChatClient() {
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Verbindung zum Server hergestellt.");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Leser für die Benutzereingabe
            Scanner scanner = new Scanner(System.in);

            // Benutzernamen eingeben
            System.out.print("Gib deinen Namen ein: ");
            String clientName = scanner.nextLine();
            out.println(clientName);

            // Thread zum Empfangen von Nachrichten vom Server
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // Benutzereingabe und Senden an den Server
            String userInput;
            while (true) {
                userInput = scanner.nextLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
