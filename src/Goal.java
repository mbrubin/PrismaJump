import java.awt.*;

public class Goal extends Platform
{ 
  public static Color[] rainbow = new Color[1000];
  
  private int colorNum = 0;
  
  public Goal(int x, int y, int w, int h, Player p)
  {
    super(x, y, w, h, p, Color.MAGENTA);  
    
    for(int n = 0; n < 1000; n++) {
        rainbow[n] = new Color((int)(Math.sin(n) * 127 + 128), (int)(Math.sin(n + Math.PI/2) * 127 + 128), (int)(Math.sin(n + Math.PI) * 127 + 128));
    }
  }
  
  @Override
  protected void playerCollisionDetect()
  {
    //super.playerCollisionDetect();
    //remove that bit ^, and implement winning/player moving onto the next level
    if(player.getHitbox().intersects(this.hitbox))
      player.getCurrentLevel().won();
  }
  
  @Override
  public void paint(Graphics g)
  {
    playerCollisionDetect();
    
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    color = new Color(color.getRed() < 255 ? (color.getRed() + 1) : 0, color.getGreen() < 255 ? (color.getGreen() + 1) : 0, color.getBlue() < 255 ? (color.getBlue() + 1) : 0);
    
    g2.setColor(rainbow[colorNum]);//color);
    if(colorNum < rainbow.length - 1)
      colorNum ++;
    else
      colorNum = 0;
    g2.fillOval(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
  }
              
}