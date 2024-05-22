package UI;

import Networking.DistributionServer;

import java.util.Scanner;


class Main {
    static GameManager manager;
    static Game game;

    //only to create executable
    static boolean dServer = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Server starten(true) oder Server laden(false)?");


        if(dServer){

            Thread backend = new Thread(() -> {
                DistributionServer dServer = new DistributionServer();
            });

            backend.start();
        }
        else if(scanner.nextLine().equals("true") ){
            manager = new GameManager(true);
        }
        else{
            manager = new GameManager(false);
            game = new Game(manager);
            game.setVisible(true);
        }


    }
}