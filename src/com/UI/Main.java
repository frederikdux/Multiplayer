package com.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;


class Main {
    static GameManager manager;
    static Game game;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Server starten(true) oder Server laden(false)?");

        if(scanner.nextLine().equals("true") ){
            manager = new GameManager(true);
        }
        else{
            manager = new GameManager(false);
            game = new Game(manager);
            game.setVisible(true);
        }


    }
}