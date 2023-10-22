package com.UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Other.Keyboard;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;


class Surface extends JPanel {

    private void doDrawing(Graphics g) {
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class Game extends JFrame implements KeyListener {
    public GameManager manager;
    int testCounter = 0;
    HashSet<Character> pressed = new HashSet<>();


    public Game(GameManager manager) {
        Thread frontend = new Thread(() -> {
            this.manager = manager;
            this.addKeyListener(this);
            initUI();
            while(true){
                updateUI();
            }
        });

        frontend.start();
    }


    private void initUI() {

        add(new Surface());
        setTitle("Simple Java 2D example");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void updateUI() {
        getGraphics().fillOval((int) manager.player.getPos().y, (int) manager.player.getPos().x, 20, 20);

        for(Enemy enemy: manager.enemies) {
            getGraphics().fillRect((int) enemy.getPos().y, (int) enemy.getPos().x, 20, 20);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        controllPlayer(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void controllPlayer(char pressed){
        switch (pressed) {
            case 'w' -> manager.player.move(new Vector2f(-2, 0));
            case 'a' -> manager.player.move(new Vector2f(0, -2));
            case 's' -> manager.player.move(new Vector2f(2, 0));
            case 'd' -> manager.player.move(new Vector2f(0, 2));
            case 'm' -> manager.getClient().sendTextMessage("" + testCounter++);
            case 't' -> System.out.println(manager.player.getClientName() + "   " + manager.player.getPos().x + " / "+ manager.player.getPos().y + "  -  " + manager.enemies.size());
        }
        manager.getClient().sendPlayerInformation(new Player(new Vector2f(manager.player.getPos().x, manager.player.getPos().y), manager.player.getRot()));

    }

}