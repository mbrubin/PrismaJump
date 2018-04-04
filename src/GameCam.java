import java.awt.Dimension;
import java.awt.Color;

public class GameCam
{
  private Level currentLevel;
  
  //implement: stop scrolling camera if at eaither edge of the screen (depends on X coordinate) - if at edge, start moving player itself, rather than keeping player in middle of window
  public boolean atWestEdge;
  public boolean atEastEdge;
 
  public GameCam()
  { 
    atWestEdge = false;
    atEastEdge = false;
  }
  
  public void scroll(double amount)
  {
    //moving all platforms/enemies in a level by amount (positive or negative; comes from player based on arrow keys pressed...) 
    for(Platform p : currentLevel.getPlatforms())
      p.getHitbox().x += amount;
    
    for(Enemy e : currentLevel.getEnemies())
      e.getHitbox().x += amount;
    
    currentLevel.getPlayer().setInfoX((int)(currentLevel.getPlayer().getInfoX() + amount));
    
    //currentLevel.getLeftBound().setX(currentLevel.getLeftBound().getX() + (int)amount);
    //currentLevel.getRightBound().setX(currentLevel.getRightBound().getX() + (int)amount);
    
    currentLevel.setCurrentX(currentLevel.getCurrentX() - (int)amount);
  }
  
  public Level getCurrentLevel() { return currentLevel; }
  
  public void setCurrentLevel(Level lev) { currentLevel = lev; }
  
  public static void main(String[] args) { new MainWindow(); }
}