import java.awt.*;

public class DeathPlatform extends Platform
{
  public static final int SPIKE_WIDTH = 30; //the width of one entire spike
  public static final int SPIKE_HEIGHT = 40; //the height of one entire spike
  
  public DeathPlatform(int x, int y, int w, int h, Player p)
  {
    super(x, y, w, h, p, Color.RED);
   
    //assure that the width isn't smaller than the width of one spike
    if(hitbox.width < SPIKE_WIDTH)
      hitbox.width = SPIKE_WIDTH;
    
    //assure that spikes will fit evenly in the platform
    while(hitbox.width % SPIKE_WIDTH != 0)
    {
      hitbox.width --;
    }
  }
  
  @Override
  protected void playerCollisionDetect()
  {
    if(player.getHitbox().intersects(hitbox))
    {
      player.died();
      player.getCurrentLevel().reload();
    }
  }
  
  @Override
  public void paint(Graphics g)
  {
    playerCollisionDetect();
   
    Graphics2D g2 = (Graphics2D)g;
    
    //PAINT RED DEATH SPIKES
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    g2.setColor(Color.RED);
    
    //draw the series of triangles/spikes that make up the platform
    int numSpikes = hitbox.width/SPIKE_WIDTH;
    for(int i = 0; i <  numSpikes; i ++)
    {
      g2.fillPolygon(new int[]{(i * SPIKE_WIDTH) + (hitbox.x), (i * SPIKE_WIDTH) + (hitbox.x + SPIKE_WIDTH/2), (i * SPIKE_WIDTH) + (hitbox.x + SPIKE_WIDTH)}, new int[]{hitbox.y + hitbox.height, hitbox.y, hitbox.y + hitbox.height}, 3); 
    }
  }
}