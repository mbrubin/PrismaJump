import java.awt.*;

public class Boundary extends Platform
{
  private static final int WIDTH = 100;
 
  public Boundary(int x, Player p) //creates a boundary at x (left side is at x-coord, right side is at x coord + WIDTH)
  {
    super(x, 0, WIDTH, MainWindow.HEIGHT, p, null); 
    hitbox = new Rectangle(x, 0, WIDTH, MainWindow.HEIGHT);
  }
  
  @Override //just make this platform act like a big (invisible) wall that acts as the boundaries of the level
  public void paint(Graphics g)
  {
    playerCollisionDetect();
    assureNoPlayerClipping();
    
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    /*DEBUG*/
    g2.setColor(Color.BLACK);
    g2.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
  }
}