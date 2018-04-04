import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player extends Entity implements KeyListener
{
  private GameCam cam;
  
  private double velX = 0.0, velY = 0.0;
  private final int MAX_VEL_X = 15, MAX_VEL_Y = 10;
  
  private int moveX, moveY; //the velocities calculated with the FPS at which the game is running, in order to assure that the Player moves a constant amount, despite the speed at which the computer is running
  
  public final int GRAVITY_VEL_Y = 40; //change to MAX_VEL_Y, make gravity ACCELERATE to this maximum velocity
  
  private final int BOOST_VEL = 30;
  
  private final int MAX_JUMP_HEIGHT = 200;
  private final int MAX_NUM_JUMPS = 2;
  
  private boolean falling = false, releasedSpace = true;;
  private long jumpTime = 0; //in milliseconds
  private final int MAX_JUMP_TIME = 400; //in milliseconds 
  private final int JUMP_VEL = -10;
  private int jumpNum = 0;
  
  public static final int LEFT_BOUND = MainWindow.WIDTH/2 - MainWindow.WIDTH/8; //the bounds in the middle of the screen that restrict the player's coordinate movement - makes level scroll instead
  public static final int RIGHT_BOUND = MainWindow.WIDTH/2 + MainWindow.WIDTH/8;
  private boolean inCenterBounds;
  
  public final int infoX_constant = 30, infoY_constant = 200;
  private int infoX, infoY;
  
  private long gameTime;
  
  private int numDeaths;
  
  public boolean topBlocked, bottomBlocked, leftBlocked, rightBlocked, colliding;
  private boolean onGround, touchingCeiling, gravity;
 
  private boolean boosting;
  
  private boolean boundsEnabled;
  
  private boolean firstInitialized = true; //used for the fpsFraction in the move() method
  
  private boolean gameWon; //used for resetting the game after the game is won
  private boolean reset;
  
  public boolean forceWin = false; //for debugging purposes
  
  private long timer = System.currentTimeMillis();
  
  public Player(int x, int y, int w, int h, GameCam c)
  {
    super(x, y, w, h);
    setBounds(new Rectangle(0, 0, MainWindow.WIDTH, MainWindow.HEIGHT)); //set bounds of component to the entire window
    boosting = false;
    cam = c;
    topBlocked = false;
    bottomBlocked = false;
    leftBlocked = false;
    rightBlocked = false;
    onGround = false;
    touchingCeiling = false;
    gravity = true;
    inCenterBounds = false;
    boundsEnabled = true;
    gameTime = 0;
    numDeaths = 0;
    gameWon = false;
    reset = false;
    infoX = infoX_constant;
    infoY = infoY_constant;
    velY = GRAVITY_VEL_Y;
  }
  
  private void move()
  {
    /*Prevent hitbox from going out of the Y bounds of the window*/
    if(hitbox.y >= getHeight() - hitbox.height) 
    {
      hitbox.y = getHeight() - hitbox.height; 
      onGround = true;
    }
    else
      onGround = false;
    if(hitbox.y <= 0 && velY < 0) 
    {  
      touchingCeiling = true;
    }
    else
      touchingCeiling = false;
    
    /*Set the X or Y velocity to zero if Player is blocked on certain side(s)*/
    if((onGround || bottomBlocked) && velY > 0)
    {
      velY = 0;
    }
    else if((touchingCeiling || topBlocked) && velY < 0)
    {
      velY = 0;
      jumpTime = System.currentTimeMillis() + 10000; //end the jump
    }
    else if(leftBlocked && velX < 0)
    {
      velX = 0;
    }
    else if(rightBlocked && velX > 0)
    {
      velX = 0;
    }
      
    if(onGround || bottomBlocked)
      gravity = false;
    else
      gravity = true;
    

    /*jumping*/
    if(jumpNum < MAX_NUM_JUMPS && Math.abs(System.currentTimeMillis() - jumpTime) <= MAX_JUMP_TIME && !topBlocked && !touchingCeiling)
    {
      velY = JUMP_VEL; 
    }

    if((onGround || bottomBlocked) && velY >= 0)
    {
      jumpNum = 0;
      falling = false;
    }
    
    if(jumpNum == MAX_NUM_JUMPS)
    {
      jumpNum = 0;
      falling = true;
    }

    /*apply gravity if necessary*/
    if(gravity && !bottomBlocked && !onGround && Math.abs(System.currentTimeMillis() - jumpTime) > MAX_JUMP_TIME)// && jumpNum < MAX_NUM_JUMPS) //if Player is standing on nothing and Player does not currently have jump velocity
    {
      if(velY < GRAVITY_VEL_Y)
        velY ++;
    }
    
    /*Assure the Player is not moving over its (X) speed limit*/
    if(velX > BOOST_VEL)
      velX = BOOST_VEL;
    else if(velX < -1 * BOOST_VEL)
      velX = -1 * BOOST_VEL;
    
    /*Prevent hitbox from going out of the X bounds of the window*/
    if(hitbox.x > getWidth() - hitbox.width) 
      hitbox.x = getWidth() - hitbox.width; 
    else if(hitbox.x < 0) 
      hitbox.x = 0;
    
    if(velY < 0 && Math.abs(System.currentTimeMillis() - jumpTime) > MAX_JUMP_TIME)
      velY = 0;

    if(hitbox.y + hitbox.height == getHeight() && velY > 0)
      velY = 0;
    
    double fpsFraction;
    if(MainWindow.fps == MainWindow.TARGET_FPS)
      fpsFraction = 1;
    else //if the true fps is either less than or greater than the target fps
      fpsFraction = (double)MainWindow.TARGET_FPS/(double)MainWindow.fps;
      
    if(firstInitialized || fpsFraction > 5) //prevents Player from glitching out (having huge velY) when game first starts
    {
      MainWindow.fps = MainWindow.TARGET_FPS;
      fpsFraction = 1;
      firstInitialized = false;
    }
    
    if(System.currentTimeMillis() - timer >= 1000)
    {
      /*DEBUG*///System.out.println("FPS: " + MainWindow.fps + "; fpsFraction: " + fpsFraction); 
      timer = System.currentTimeMillis();
      gameTime ++;
    }
    
    moveX = (int)(velX * fpsFraction);
    moveY = (int)(velY * fpsFraction);
    
    if(!inCenterBounds && hitbox.x >= LEFT_BOUND && hitbox.x <= RIGHT_BOUND)
      inCenterBounds = true;
    
    /*adjust Player's hitbox, therefore adjusting where on the screen the Player will be painted*/
    if(hitbox.x <= LEFT_BOUND && velX < 0 && inCenterBounds && boundsEnabled)
    {
      hitbox.x = LEFT_BOUND;
      cam.scroll(-1 * moveX);
    }
    else if(hitbox.x + hitbox.width >= RIGHT_BOUND && velX > 0 && inCenterBounds && boundsEnabled)
    { 
      hitbox.x = RIGHT_BOUND - hitbox.width;
      cam.scroll(-1 * moveX);
    }
    else
      hitbox.x += moveX;
    
    hitbox.y += moveY;
  }
  
  @Override
  public void paint(Graphics g)
  {
    move();
    
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    /*DEBUG*/
    g2.setColor(Color.gray);
    //g2.drawLine(MainWindow.WIDTH/2 - MainWindow.WIDTH/8, 0, MainWindow.WIDTH/2 - MainWindow.WIDTH/8, MainWindow.HEIGHT);
    //g2.drawLine(MainWindow.WIDTH/2 + MainWindow.WIDTH/8, 0, MainWindow.WIDTH/2 + MainWindow.WIDTH/8, MainWindow.HEIGHT);
    g2.drawLine(cam.getCurrentLevel().getMinX(), 0, cam.getCurrentLevel().getMinX(), MainWindow.HEIGHT);
    g2.drawLine(cam.getCurrentLevel().getMaxX(), 0, cam.getCurrentLevel().getMaxX(), MainWindow.HEIGHT);
    
    g2.setColor(cam.getCurrentLevel().getInfoColor());
    
    //g2.drawString(this.toString(), 30, 50);
    
    //draw currentLevel's info String
    g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 80));
    g2.drawString(cam.getCurrentLevel().getInfo(), infoX, infoY);
   
    //draw info Strings at top of window
    g2.setColor(Color.LIGHT_GRAY);
    g2.setFont(new Font(g2.getFont().getName(), Font.PLAIN, 12));
    g2.drawString("Seconds Elapsed: " + gameTime, MainWindow.WIDTH - MainWindow.WIDTH/8, MainWindow.HEIGHT/32);
    g2.drawString("Deaths: " + numDeaths, MainWindow.WIDTH/64, MainWindow.HEIGHT/32);
    
    g2.setColor(Color.BLUE);
 
    g2.fillRoundRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height, 20, 20);
    
    //paint eyes
    g2.setColor(Color.WHITE);
    if(velX == 0)
    {
      g2.fillOval(hitbox.x + hitbox.width/8, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/8 - hitbox.width/3, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5);
      g2.setColor(Color.BLACK);
      g2.fillOval(hitbox.x + hitbox.width/4, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/6 - hitbox.width/4, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10);
    }
    else if (velX > 0)
    {
      g2.fillOval(hitbox.x + hitbox.width/4, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/16 - hitbox.width/3, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5);
      g2.setColor(Color.BLACK);
      g2.fillOval(hitbox.x + hitbox.width/2 - hitbox.width/16, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/6 - hitbox.width/16, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10);
    }
    else if (velX < 0)
    {
      g2.fillOval(hitbox.x + hitbox.width/16, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/4 - hitbox.width/3, hitbox.y + hitbox.height/8, hitbox.width/3, hitbox.height/5);
      g2.setColor(Color.BLACK);
      g2.fillOval(hitbox.x + hitbox.width/16, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10); 
      g2.fillOval(hitbox.x + hitbox.width - hitbox.width/6 - hitbox.width/2 + hitbox.width/16, hitbox.y + hitbox.height/6, hitbox.width/6, hitbox.height/10);
    }
    
    //paint mouth
    g2.setColor(Color.RED);
    if(velX == 0)
    {
      g2.drawLine(hitbox.x + hitbox.width/6, hitbox.y + (int)(hitbox.height/2.5), hitbox.x + hitbox.width - hitbox.width/6, hitbox.y + (int)(hitbox.height/2.5));
    }
    else if (velX > 0)
    {
      g2.drawLine(hitbox.x + hitbox.width/4, hitbox.y + (int)(hitbox.height/2.5), hitbox.x + hitbox.width - hitbox.width/8, hitbox.y + (int)(hitbox.height/2.5));
    }
    else if (velX < 0)
    {
      g2.drawLine(hitbox.x + hitbox.width/8, hitbox.y + (int)(hitbox.height/2.5), hitbox.x + hitbox.width - hitbox.width/4, hitbox.y + (int)(hitbox.height/2.5));
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    int c = e.getKeyCode();
 
    if(c == KeyEvent.VK_RIGHT && !rightBlocked)
    {
      if(velX < 0) velX = -1 * velX;
      if(velX == 0) velX = MAX_VEL_X/2;
      if(!boosting)
      {
        if(velX > MAX_VEL_X) velX -= 5;
        if(velX < MAX_VEL_X)
        {
          velX ++;
        }
      }
      else
      {
        if(velX < BOOST_VEL)
        { 
          velX ++;
        }
      }
    }
    else if(c == KeyEvent.VK_LEFT && !leftBlocked)
    {
      if(velX > 0) velX = -1 * velX;
      if(velX == 0) velX = -1 * MAX_VEL_X/2;
      if(!boosting)
      {
        if(velX < -1 * MAX_VEL_X) velX += 5;
        if(velX > -1 * MAX_VEL_X)
        {
          velX --;
        }
      }
      else
      {
        if(velX > -1 * BOOST_VEL)
        { 
          velX --;
        }
      }
    }
    else if(c == KeyEvent.VK_SHIFT)
    {
      //holding shift boosts speed
      boosting = true;
      if(velX > 0 && velX < BOOST_VEL)
          velX ++;
      else if(velX < 0 && velX > -1 * MAX_VEL_X + -1 * BOOST_VEL)
          velX --;
    }
    
    if(c == ' ' && (!topBlocked && !touchingCeiling) && releasedSpace) //if space is typed, there is nothing blocking above
    {releasedSpace = false;
      if(jumpNum < MAX_NUM_JUMPS && !falling) //if the Player still has jumps remaining and the player *didn't* just do a max jump (double, triple, etc.)
      {
        //play jump sound effect
        try{
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(MainWindow.class.getResource("../resources/Mario_Jump_-_Sound_Effect.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
        }
        catch(Exception ex){}

        jumpTime = System.currentTimeMillis();
        jumpNum ++;
      }
    }
    //DEBUG
    if(c == KeyEvent.VK_G)
    {
      forceWin = true; 
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    int c = e.getKeyCode();
    if(c == KeyEvent.VK_RIGHT)
    {
      while(velX > 0)
      {
        velX -= 1;
      }
    }
    else if(c == KeyEvent.VK_LEFT)
    {
      while(velX < 0)
      {  
        velX += 1;
      }
    }
    else if(c == KeyEvent.VK_SHIFT)
    {
      boosting = false;
      if(velX > MAX_VEL_X)
        velX --;
      else if(velX < -1 * MAX_VEL_X)
      velX ++;
    }
    else if (c == KeyEvent.VK_SPACE)
    {
      releasedSpace = true;
    }  
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {
    //char c = e.getKeyChar();
  }
  
  public void resetLevelInfo()
  {
    infoX = infoX_constant; 
    infoY = infoY_constant;
  }
  
  public double getVelX() { return velX; }
  public double getVelY() { return velY; }
  
  public Level getCurrentLevel() { return cam.getCurrentLevel(); }
  
  public int getInfoX() { return infoX; }
  
  public long getTime() { return gameTime; }
  public int getNumDeaths() { return numDeaths; }
  
  public boolean hasWon() { return gameWon; }
  
  public boolean pushingReset() { return reset; }
  
  public void pushReset(boolean b) { reset = b; }
  
  public void setWon(boolean b) { gameWon = b; }
  
  public void enableBounds(boolean b) { boundsEnabled = b; }
  
  public void setInfoX(int x) { infoX = x; }
  //public void setInfoY(int y) { infoY = y; }
  
  public void resetStats()
  {
    gameTime = 0;
    numDeaths = 0;
  }
  
  public void resetLevel() { inCenterBounds = false; }
  
  public void died() { numDeaths ++; }
  
  public String toString()
  {
    return "";//X-Velocity: " + velX + "   Y-Velocity: " + velY + "   Sides Blocked: " + ((topBlocked || touchingCeiling)? "TOP," : "") +((bottomBlocked || onGround) ? "BOTTOM," : "") + (leftBlocked ? "LEFT," : "") + (rightBlocked ? "RIGHT," : "") + "   Level Coordinate: " + cam.getCurrentLevel().getCurrentX() + "   Coords: (" + hitbox.x + ", " + hitbox.y + ")";//    8isJumping: " + isJumping*/;
  }
  
  public static void main(String[] args)
  {
    MainWindow game = new MainWindow();
    
    long t = System.nanoTime();
      
    while(true)
    {
      if(System.nanoTime() - t >= 1000000000/(MainWindow.TARGET_FPS-1))
      {
        game.update();
        t = System.nanoTime();
      }
    }
  }
  
}