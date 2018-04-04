import javax.swing.*;
import java.awt.*;

public abstract class Entity extends JComponent
{
  protected Rectangle hitbox;
  
  public Entity(int x, int y, int width, int height)
  {
    hitbox = new Rectangle(x, y, width, height); //System.out.println("X = " + hitbox.x+", Y = "+hitbox.y+", W = "+hitbox.width+", H = " +hitbox.height);
  }
  
  public Rectangle getHitbox(){ return hitbox; }
  
  public boolean standingOn(Platform platform)
  {
    int yCoord = platform.getHitbox().y;
    return this.hitbox.y + this.hitbox.height == yCoord;
  }
}