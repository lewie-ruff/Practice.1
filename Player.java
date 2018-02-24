import java.awt.*;
/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends Collidable
{
    private double xSpeed = 0;
    private double ySpeed = 0;
    public Player(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }
    
    public void draw(Graphics g)
    {
        g.setColor(Color.RED);
        g.fillRect(boundaries.x, boundaries.y, boundaries.width, boundaries.height);
    }
    
    public void accelerateHorizontal(double accelerationRate, double runningSpeed)
    {
        boundaries.x += xSpeed;
        xSpeed += accelerationRate;
        if(Math.abs(xSpeed) > runningSpeed) xSpeed = runningSpeed;
    }
    
    public void accelerateVertical(double gravity, double terminalVelocity)
    {
        boundaries.y += ySpeed;
        ySpeed += gravity;
        if(ySpeed > terminalVelocity) ySpeed = terminalVelocity;
    }
    
    public double getXSpeed()
    {
        return xSpeed;
    }
    
    public double getYSpeed()
    {
        return ySpeed;
    }
    
    public void setXSpeed(double xSpeed)
    {
        this.xSpeed = xSpeed;
    }
    
    public void setYSpeed(double ySpeed)
    {
        this.ySpeed = ySpeed;
    }
}