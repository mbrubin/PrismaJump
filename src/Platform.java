import java.awt.*;
import javax.swing.*;
import java.util.Arrays;

import javax.sound.sampled.*;
import java.io.*;

public class Platform extends JComponent
{
  protected Color color;
  
  protected Rectangle hitbox;
  protected Player player;
  
  protected int id; //each platform in a Level has a unique id number (its index in the platforms array)
  
  protected boolean verbose = false;
  
  public Platform(int x, int y, int width, int height, Player p, Color c)
  {
    setBounds(new Rectangle(0, 0, MainWindow.WIDTH, MainWindow.HEIGHT)); //set bounds of component to the entire window
    hitbox = new Rectangle(x, y, width, height);
    player = p;
    color = c;
  }
  
  protected void assureNoPlayerClipping()
  {
    int px = player.getHitbox().x, py = player.getHitbox().y, pw = player.getHitbox().width, ph = player.getHitbox().height;
    //int pvx = (int)player.getVelX(), pvy = (int)player.getVelY();
    //int pdestx = px + pvx, pdesty = py + pvy; //(dest = destination) the coordinates of the player after one movement tick (increase by velocities)
    int x = hitbox.x, y = hitbox.y, w = hitbox.width, h = hitbox.height; 
    
    String direction = ""; //stores which direction player is moving when it first collides
    
    //if player clips inside this Platform's hitbox, then get player the heck out!! 
    if(player.getHitbox().intersects(this.hitbox))
    {
      if(player.getVelY() < 0 && px + pw >= x && px <= x + w) //prevent player clipping through bottom of Platform
      {
        player.topBlocked = true;
        player.getHitbox().y = y + h;
      }
      else if(player.getVelY() > 0 && px + pw >= x && px <= x + w) //prevent player clipping through top of Platform
      {
        player.bottomBlocked = true;
        player.getHitbox().y = y - ph;
      }
      else if(player.getVelX() > 0 && py + ph >= y && py <= y + h) //prevent player clipping through left of Platform
      {
        player.leftBlocked = true;
        player.getHitbox().x = x - pw;
      }
      else if(player.getVelX() < 0 && py + ph >= y && py <= y + h) //prevent player clipping through right of Platform
      {
        player.rightBlocked = true;
        player.getHitbox().x = x + w;
      }
    }
  }
 
  protected void playerCollisionDetect()
  {
    int px = player.getHitbox().x, py = player.getHitbox().y, pw = player.getHitbox().width, ph = player.getHitbox().height;
    int pvx = (int)player.getVelX(), pvy = (int)player.getVelY();
    int pdestx = px + pvx, pdesty = py + pvy; //(dest = destination) the coordinates of the player after one movement tick (increase by velocities)
    int x = hitbox.x, y = hitbox.y, w = hitbox.width, h = hitbox.height; 
    
    Level currentLevel = player.getCurrentLevel();
    currentLevel.setCollisionIDs();
    
    if(currentLevel.topCollisionID == -1 && currentLevel.bottomCollisionID == -1 && currentLevel.leftCollisionID == -1 && currentLevel.rightCollisionID == -1)//!hasCollision)//if player is colliding with no Platforms
    {
      //playSound("Piano_brokencrash-Brandondorf-1164520478.wav");
      player.topBlocked = false;
      player.bottomBlocked = false;
      player.leftBlocked = false;
      player.rightBlocked = false;
      return; 
    }
    
    if((pvy > 0 && px + pw >= x && px <= x + w && py + ph <= y && pdesty + ph >= y) || (px + pw >= x && px <= x + w && Math.abs(y - (py + ph)) <= 1))//if bottom of player is colliding with platform
    {
      player.bottomBlocked = true;
      player.getHitbox().y = y - ph;
      return;
    }
    else if(currentLevel.bottomCollisionID == id)
    {
      player.bottomBlocked = false;
    }
    if((pvy < 0 && px + pw >= x && px <= x + w && py >= y + h && pdesty <= y + h) || (px + pw >= x && px <= x + w && Math.abs(py - (y + h)) <= 1)) //if top of player is colliding with platform
    {
      player.topBlocked = true;
      player.getHitbox().y = y + h;
      return;
    }
    else if(currentLevel.topCollisionID == id)
      player.topBlocked = false;
    if(pvx > 0 && py + ph >= y && py <= y + h && px + pw <= x && pdestx + pw >= x) //if right side of player is colliding with platform
    {
      player.rightBlocked = true;
      player.getHitbox().x = x - pw;
      return;
    }
    else if(currentLevel.rightCollisionID == id)
      player.rightBlocked = false;
    if(pvx < 0 && py + ph >= y && py <= y + h && px >= x + w && pdestx <= x + w) //if left side of player is colliding with platform
    {
      player.leftBlocked = true; 
      player.getHitbox().x = x + w;
      return;
    }
    else if(currentLevel.topCollisionID == id)
      player.leftBlocked = false;
    
    if(currentLevel.topCollisionID == -1)
      player.bottomBlocked = false;
    if(currentLevel.bottomCollisionID == -1)
      player.topBlocked = false;
    if(currentLevel.leftCollisionID == -1)
      player.rightBlocked = false;
    if(currentLevel.rightCollisionID == -1)
      player.leftBlocked = false;
  }
  
  @Override
  public void paint(Graphics g)
  {
    playerCollisionDetect();
    assureNoPlayerClipping(); //this method eliminates (?) the double jumping clip-through-Platform glitch !!!ADD OTHER THREE SIDES TO assureNoPlayerClipping() !!!!!!!!!!!!!!!!!!
    
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    if(verbose)
    {
      g2.setColor(Color.BLACK);
      //g.drawString("[INSERT SOMETHING DEBUGGY HERE]", 50, 300);
    }
    
    g2.setColor(color);
    g2.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);    
  }
  
  public Rectangle getHitbox(){ return hitbox; }
  public Player getPlayer() { return player; }
  
  //for debugging - set to true if debug statements should be printed - turning on reduces framerate significantly
  public void setVerbose(boolean b) { verbose = b; }
  
  public void setID(int id) { this.id = id; }
  
  public String toString()
  {
    return "X = " + hitbox.x + ", Y = " + hitbox.y + ", Width = " + hitbox.width + ", Height = " + hitbox.height;
  }
  
  public static void main(String[] args)
  {
    new MainWindow(); 
  }
  
  
  /*private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
    
  public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }*/

}