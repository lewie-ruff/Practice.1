import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Generates a GUI that loops a sunset animation.
 *
 * @author Dylan Jump
 * @version August 30, 2017
 */
public class Sunset extends JPanel implements ActionListener
{
    private JFrame frame;
    private int sunX;
    private double sunY;
    private int red = 0;
    private int blue = 0;
    /**
     * Don't worry about this; it's just a bunch of set-up for the screen.
     */
    public Sunset()
    {
        setPreferredSize(new Dimension(1024, 512));

        frame = new JFrame("Sunset");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        new Timer(20, this).start();

        sunX = -(getWidth() / 2);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(new Color(red, 0, blue));
        g.fillRect(0, 0, getWidth(), getHeight());

        sunY = Math.pow(sunX / 32.0, 2);//This makes the sun follow an arc.
        g.setColor(Color.YELLOW);
        //because the x is negative, I have to offset it in the animation.
        //Keep this in mind if you're looking at the position calculations.
        g.fillOval(sunX + getWidth() / 2 - 32, (int)sunY, 64, 64);
    }

    /**
     * This is where I need help. I'd like a smoother, more natural-looking transition.
     */
    public void actionPerformed(ActionEvent ae)
    {
        sunX++; //Controls direction of the sun arc
        if(sunX > getWidth() / 2) sunX = -(getWidth() / 2); //Working on it
        if(blue < 255 && sunX < 0) blue++;
        else if(blue > 1 && sunX > getWidth() / 3) blue--;
        if(red < 80 && sunX > getWidth() / 5 && sunX < getWidth() / 3) red++;
        else if(red > 0 && sunX > getWidth() / 3) red--;
        repaint();
    }

    public static void main(String[] args)
    {
        new Sunset();
    }
}