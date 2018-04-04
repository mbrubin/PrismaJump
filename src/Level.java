import java.awt.*;

public class Level
{
  private Platform[] platforms; //if n = platforms.length, then platforms[n-3] = the Goal, platforms[n-2] = leftBound and platforms[n-1] = rightBound <<<<<<but the indexes probably don't matter (???)
  private Enemy[] enemies;
  private Background bg;
  private int playerStartX, playerStartY;
  private int minX, maxX, currentX;
  
  private String info;
  private Color infoColor;
  
  private boolean reload = false, won = false;
  
  public int topCollisionID = -1, bottomCollisionID = -1, leftCollisionID = -1, rightCollisionID = -1;
  
  public Level(Platform[] plats, Enemy[] enems, /*Boundary leftBound, Boundary rightBound,*/ Background bg, String str, Color col, int playerX, int playerY)
  {
    platforms = plats;
    for(int i = 0; i < platforms.length; i ++)
      platforms[i].setID(i);
    enemies = enems;
    this.bg = bg;
    info = str;
    infoColor = col;
    playerStartX = playerX;
    playerStartY = playerY;
    //leftBoundary = leftBound;
    //rightBoundary = rightBound;
    currentX = 0;
  }
  
  public /*static*/ Level generateRandomLevel(int numPlatforms, int numEnemies, int startX, int startY, int minX, int maxX, String str, Color col, Player player)
  {
    Platform[] plats = new Platform[numPlatforms];
    for(int i = 0; i < plats.length; i ++)
    {
      //plats[i] = new Platform((int) Math.random() //...IMPLEMENT THE REST OF THIS
    }
    Enemy[] enems = new Enemy[numEnemies];
    for(int i = 0; i < plats.length; i ++)
    {
      //enems[i] = new Enemy((int) Math.random() //...IMPLEMENT THE REST OF THIS
    }
    
    //generate random Backgound
    Background bg = new Background(new Color[]{});
    
    return new Level(plats, enemies, /*new Boundary(minX, player), new Boundary(maxX, player),*/ bg, str, col, startX, startY);
  }
  
  public void setCollisionIDs() //sets the four [side]CollisionID variables to the IDs of the proper platforms; sets the ID to -1 if the Player is not colliding on that side
  {
    Player player = platforms[0].getPlayer();
    
    boolean foundTopCollision = false, foundBottomCollision = false, foundLeftCollision = false, foundRightCollision = false; 
    
    for(int i = 0; i < platforms.length; i ++)
    {
      int px = player.getHitbox().x, py = player.getHitbox().y, pw = player.getHitbox().width, ph = player.getHitbox().height;
      int pvx = (int)player.getVelX(), pvy = (int)player.getVelY();
      int pdestx = px + pvx, pdesty = py + pvy;
      int x = platforms[i].getHitbox().x, y = platforms[i].getHitbox().y, w = platforms[i].getHitbox().width, h = platforms[i].getHitbox().height; 
      
        
      if((pvx < 0 && py + ph >= y && py <= y + h && px >= x + w && pdestx <= x + w) || (py + ph >= y && py <= y + h && Math.abs((x + w) - px) <= 1)) /*player colliding with right side of platform*/ 
      {
        rightCollisionID = i;
        foundRightCollision = true;
      }
      else if((pvx > 0 && py + ph >= y && py <= y + h && px + pw <= x && pdestx + pw >= x) || (py + ph >= y && py <= y + h && Math.abs(x - (px + pw)) <= 1)) /*player colliding with left side of platform*/
      { 
        leftCollisionID = i;
        foundLeftCollision = true;
      }
      else if((pvy < 0 && px + pw >= x && px <= x + w && py >= y + h && pdesty <= y + h) || (px + pw >= x && px <= x + w && Math.abs((y + h) - py) <= 1)) /*player colliding with bottom side of platform*/
      {
        bottomCollisionID = i;
        foundBottomCollision = true;
      }
      else if((pvy > 0 && px + pw >= x && px <= x + w && py + ph <= y && pdesty + ph >= y) || (px + pw >= x && px <= x + w && Math.abs(y - (py + ph)) <= 1)) /*player colliding with top side of platform*/
      {
        topCollisionID = i;
        foundTopCollision = true;
      }
    }
    
    if(!foundTopCollision) topCollisionID = -1;
    if(!foundBottomCollision) bottomCollisionID = -1;
    if(!foundLeftCollision) leftCollisionID = -1;
    if(!foundRightCollision) rightCollisionID = -1;
  }
  
  public boolean shouldReload() { return reload; }
  public void reload() { reload = true; }
  public void didReload() { reload = false; }
  
  public boolean hasWon() { return won; }
  public void won() { won = true; }
  public void didWin() { won = false; }
  
  public Platform[] getPlatforms() { return platforms; }
  public Enemy[] getEnemies() { return enemies; }
  public Player getPlayer() { return platforms[0].getPlayer(); }
  public Background getBackground() { return bg; }
  public String getInfo() { return info; }
  public Color getInfoColor() { return infoColor; }
  public int getStartX() { return playerStartX; }
  public int getStartY() { return playerStartY; }
  public int getCurrentX() { return currentX; }
  public int getMinX() { return minX; }
  public int getMaxX() { return maxX; }
  public Boundary getLeftBound() { return (Boundary)platforms[platforms.length - 2]; }//leftBoundary; }
  public Boundary getRightBound() { return (Boundary)platforms[platforms.length - 1]; }//rightBoundary; }
  
  public void setCurrentX(int x) { currentX = x; }
  public void invisibleInfo() { info = ""; }
}