import java.awt.image.BufferedImage;
  
public class Animation
{
  private BufferedImage[] images;
  
  private int currentFrame;
  private long frameDelayMillis;
  
  public Animation(long delay)
  {
    frameDelayMillis = delay;
  }
  
}