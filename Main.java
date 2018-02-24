import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;
/**
 * Write a description of class Collision here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Main extends JPanel implements ActionListener, KeyListener
{
    private static final double WALKING = 0.8;//1.4m/s(0.224pix/5msec)
    private static final double RUNNING = 5.6;//4.1m/s(0.656pix/5msec)
    private static final double GRAVITY = 1.6;//9.8m/s^2(1.568pix/5msec^2)
    private static final double TERMINAL_VELOCITY = 8.6;//54m/s(8.64pix/5msec)
    
    private final int LEFT = 0;
    private final int RIGHT = 1;
    private final int UP = 2;
    private boolean[] keys = new boolean[3];
    
    private int sunX;
    private double sunY;
    private int drawX;
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    
    private Player player = new Player(70, 80, 64, 64);
    //private Point player = new Point(54, 120);
    //private double xSpeed = 0;
    //private double ySpeed = GRAVITY;
    private boolean landed = false;
    private Rectangle collision;
    private Tile[][] map;
    private Tile[] localTiles = new Tile[0];
    public Main()
    {
        loadMap("map0");
        //printMap();
        setBackground(new Color(0, 190, 255));
        setPreferredSize(new Dimension(1024, 512));

        JFrame frame = new JFrame("Collision");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        new Timer(5, this).start();
        frame.addKeyListener(this);
        
        sunX = -(getWidth() / 2) - 32;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(new Color(red, green, blue));
        g.fillRect(0, 0, getWidth(), getHeight());
        drawX = sunX + getWidth() / 2;
        sunY = Math.pow(sunX / 32.0, 2);
        g.setColor(Color.YELLOW);
        g.fillOval(drawX, (int)sunY, 64, 64);
        
        g.setColor(Color.RED);
        g.fillRect(getWidth() / 2, getHeight() / 2, 64, 64);
        for(int r = 0; r < map.length; r++)
        {
            for(int c = 0; c < map[r].length; c++)
            {
                if(map[r][c] != null) map[r][c].draw(g, getWidth() / 2 - player.getX(), getHeight() / 2 - player.getY());
                //if(map[r][c] != null) map[r][c].draw(g, getWidth() / 2 - player.x, getHeight() / 2 - player.y);
            }
        }
        g.setColor(Color.BLACK);
        //g.drawString("x-speed: " + player.getXSpeed(), 20, 20);
        //g.drawString("Pressing Left? " + keys[LEFT], 20, 40);
        //g.drawString("y-speed: " + player.getYSpeed(), 20, 60);
        g.setColor(Color.YELLOW);
        for(Tile tile : localTiles) 
        {
            if(tile != null)
            {
                //g.drawRect(tile.getColumn() * 64 + getWidth() / 2 - player.getX(), tile.getRow() * 64 + getHeight() / 2 - player.getY(), 64, 64);
            }
        }
    }

    public void actionPerformed(ActionEvent ae)
    {
        /*
        if(!landed)
        {
            player.y += ySpeed;
            ySpeed += GRAVITY;
            if(ySpeed > TERMINAL_VELOCITY) ySpeed = TERMINAL_VELOCITY;
        }
        else ySpeed = 0;
        if(player.y > getHeight() * 2)
        {
            ySpeed = GRAVITY;
            player.y = 0;
        }
        if(keys[LEFT])
        {
            player.x += xSpeed;
            xSpeed -= WALKING;
            if(Math.abs(xSpeed) > RUNNING) xSpeed = -RUNNING;
        }
        if(keys[RIGHT]) 
        {
            player.x += xSpeed;
            xSpeed += WALKING;
            if(xSpeed > RUNNING) xSpeed = RUNNING;
        }
        if(!keys[LEFT] && !keys[RIGHT]) xSpeed = 0;
        */
        if(keys[RIGHT]) player.accelerateHorizontal(WALKING, RUNNING);
        if(keys[LEFT]) player.accelerateHorizontal(-WALKING, -RUNNING);
        if(!keys[LEFT] && !keys[RIGHT]) player.setXSpeed(0);
        if(player.getY() > getHeight() * 2) player.setY(0);
        localTiles = getLocalTiles();
        if(!landed) player.accelerateVertical(GRAVITY, TERMINAL_VELOCITY);
        else player.setYSpeed(0);
        for(Tile tile : localTiles) if(isCollision(tile)) collide(tile);
        if(localTiles[0] == null && localTiles[1] == null && localTiles[2] == null && localTiles[3] == null) landed = false;
        //for(Tile tile : localTiles) System.out.println(tile);
        //System.out.println("____________");
        sunX++;
        if(drawX + 32 > getWidth()) sunX = -(getWidth() / 2) - 32;
        if(green < 200 && drawX > getWidth() / 16 && drawX < 5 * getWidth() / 8) green++;
        else if(green > 0 && drawX > 5 * getWidth() / 8) green--;
        if(blue < 255 && drawX < getWidth() / 4) blue++;
        else if(blue > 0 && drawX > 3 * getWidth() / 4) blue--;
        repaint();
    }

    public void keyPressed(KeyEvent key)
    {
        processCommand(key.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent key)
    {
        processCommand(key.getKeyCode(), false);
    }

    public void keyTyped(KeyEvent key)
    {
    }

    private void processCommand(int code, boolean active)
    {
        switch(code)
        {
            case 65: case KeyEvent.VK_LEFT: keys[LEFT] = active; break;
            case 68: case KeyEvent.VK_RIGHT: keys[RIGHT] = active; break;
            case 87: case KeyEvent.VK_UP:
            if(active && landed)
            {
                landed = false;
                player.setYSpeed(GRAVITY * -15);
            }
            /*
            if(active && landed) 
            {
                landed = false;
                ySpeed = GRAVITY * -15; 
            }*/
            break;
            default: if(active) System.out.println((char)code + ": " + code); break;
        }
    }

    private boolean inMap(int point, int range)
    {
        return point >= 0 && point < range;
    }

    private boolean isCollision(Tile tile)
    {
        if(tile != null) collision = player.getBoundaries().intersection(tile.getBoundaries());
        //if(tile != null) collision = new Rectangle(player.x, player.y, 64, 64).intersection(tile.getBoundaries());
        else return false;
        return collision.width > 0 && collision.height > 0;
    }

    private void collide(Tile tile)
    {
        if(collision.width > collision.height)
        {
            if(player.getY() < tile.getRow() * 64)
            {
                player.setY(player.getY() - collision.height);
                landed = true;
            }
            else player.setY(player.getY() + collision.height);
        }
        else if(collision.width <= collision.height)
        {
            if(player.getX() < tile.getColumn() * 64)
            {
                player.setX(player.getX() - collision.width);
            }
            else if(player.getX() > tile.getColumn() * 64)
            {
                player.setX(player.getX() + collision.width);
            }
        }
        /*
        if(collision.width > collision.height) 
        {
            if(player.y < tile.getRow() * 64) 
            {
                player.y -= collision.height;
                landed = true;
            }
            else player.y += collision.height;
        }
        else if(collision.width <= collision.height)
        {
            if(player.x < tile.getColumn() * 64) 
            {
                player.x -= collision.width;
            }
            else if(player.x > tile.getColumn() * 64) 
            {
                player.x += collision.width;
            }
        }
        */
    }

    private Tile[] getLocalTiles()
    {
        Tile[] localTiles = new Tile[4];
        if(inMap(player.getX(), map[map.length - 1].length * 64) && inMap(player.getY(), map.length * 64))
            localTiles[0] = map[player.getY() / 64][player.getX() / 64];
        if(inMap(player.getX() + 64, map[map.length - 1].length * 64) && inMap(player.getY(), map.length * 64))
            localTiles[1] = map[player.getY() / 64][player.getX() / 64 + 1];
        if(inMap(player.getX(), map[map.length - 1].length * 64) && inMap(player.getY() + 64, map.length * 64))
            localTiles[2] = map[player.getY() / 64 + 1][player.getX() / 64];
        if(inMap(player.getX() + 64, map[map.length - 1].length * 64) && inMap(player.getY() + 64, map.length * 64))
            localTiles[3] = map[player.getY() / 64 + 1][player.getX() / 64 + 1];
        return localTiles;
        /*
        if(inMap(player.x, map[map.length - 1].length * 64) && inMap(player.y, map.length * 64)) 
            localTiles[0] = map[player.y / 64][player.x / 64];
        if(inMap(player.x + 64, map[map.length - 1].length * 64) && inMap(player.y, map.length * 64)) 
            localTiles[1] = map[player.y / 64][player.x / 64 + 1];
        if(inMap(player.x, map[map.length - 1].length * 64) && inMap(player.y + 64, map.length * 64))
            localTiles[2] = map[player.y / 64 + 1][player.x / 64];
        if(inMap(player.x + 64, map[map.length - 1].length * 64) && inMap(player.y + 64, map.length * 64))
            localTiles[3] = map[player.y / 64 + 1][player.x / 64 + 1];
        return localTiles;
        */
    }

    public void loadMap(String filename)
    {
        Scanner file = null;
        try{
            file = new Scanner(new File(filename + ".txt"));
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        int rows = file.nextInt();
        int columns = file.nextInt();
        map = new Tile[rows][columns];
        file.nextLine();
        String currentLine;
        for(int r = 0; file.hasNextLine(); r++)
        {
            currentLine = file.nextLine();
            for(int c = 0; c < currentLine.length(); c++)
            {
                switch(currentLine.charAt(c))
                {
                    case '_': map[r][c] = new Tile(r, c, 64, 64, Color.GREEN);
                }
            }
        }
    }

    private void printMap()
    {
        for(int r = 0; r < map.length; r++)
        {
            for(int c = 0; c < map[r].length; c++)
            {
                System.out.print(map[r][c]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
        new Main();
    }
}