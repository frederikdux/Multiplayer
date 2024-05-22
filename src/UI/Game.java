package UI;

import Entity.Enemy;
import Entity.Player;
import Entity.Vector2f;
import Other.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;


class Surface extends JPanel {

    Surface(){
    }

    private void doDrawing(Graphics g) {
    }

    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        //doDrawing(g);
    }
}


public class Game extends JFrame implements KeyListener {
    HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();

    private Image dbImage;
    private Graphics dbGraphics;

    public volatile GameManager manager;
    int testCounter = 0;
    HashSet<Character> pressed = new HashSet<>();


    Long start = 0L;
    Long finish = 0L;


    public Game(GameManager manager) {
        keys.put(KeyEvent.VK_W, false);
        keys.put(KeyEvent.VK_S, false);
        keys.put(KeyEvent.VK_A, false);
        keys.put(KeyEvent.VK_D, false);

        Thread frontend = new Thread(() -> {
            initUI();
            this.manager = manager;
            this.addKeyListener(this);
        });

        Thread playerController = new Thread(this::controllPlayer);

        frontend.start();

        playerController.start();

    }


    private void initUI() {
        setTitle("Simple Java 2D example");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void paint(Graphics g){
        dbImage = createImage(getWidth(), getHeight());
        dbGraphics = dbImage.getGraphics();

        paintComponent(dbGraphics);
        g.drawImage(dbImage, 0, 0, this);
    }

    public void paintComponent(Graphics g){
        if(manager.getClient() != null) {
            setTitle(manager.getClient().currentServerInformation);
        }
        g.setColor(Color.green);
        g.fillOval((int) manager.player.getPos().x, (int) manager.player.getPos().y, 20, 20);

        g.setColor(Color.RED);
        try {
            for (Enemy enemy : manager.enemies) {
                g.fillRect((int) enemy.getPos().x, (int) enemy.getPos().y, 20, 20);
                g.drawString(enemy.getClientName(), (int)enemy.getPos().x, (int)enemy.getPos().y);
            }
        } catch (ConcurrentModificationException e){
            e.printStackTrace();
        }

        repaint();
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.replace(e.getKeyCode(), true);
        controllPlayer();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.replace(e.getKeyCode(), false);
        controllPlayer();
    }

    public void controllPlayer() {

        start = System.nanoTime();
        double elapsedTime = ((double) (start - finish) / 1000);
        elapsedTime = 10;
        //System.out.println(elapsedTime);
        for (Map.Entry<Integer, Boolean> entry : keys.entrySet()) {
            if (entry.getValue()) {
                switch (entry.getKey()) {
                    case KeyEvent.VK_W -> manager.player.move(new Vector2f(0, (float) (-0.2 * elapsedTime)));
                    case KeyEvent.VK_A -> manager.player.move(new Vector2f((float) (-0.2 * elapsedTime), 0));
                    case KeyEvent.VK_S -> manager.player.move(new Vector2f(0, (float) (0.2 * elapsedTime)));
                    case KeyEvent.VK_D -> manager.player.move(new Vector2f((float) (0.2 * elapsedTime), 0));
                }
                manager.getClient().sendPlayerInformation(new Player(new Vector2f(manager.player.getPos().x, manager.player.getPos().y), manager.player.getRot()));
            }
        }
        finish = System.nanoTime();

    }

}