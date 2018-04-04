import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class VictoryScreen extends JComponent implements KeyListener
{
  int time, numDeaths;
  
  Player player;
  
  public VictoryScreen(int t, int d, Player p)
  {
    time = t;
    numDeaths = d;
    setBounds(0, 0, MainWindow.WIDTH, MainWindow.HEIGHT);
    player = p;
  }
  
  @Override
  public void paint(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g; 
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    g2.setColor(Color.BLACK);
    g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 64));
    g2.drawString("Congrats.", (int)(MainWindow.WIDTH/2.75), 75);
    
    g2.setFont(new Font(g2.getFont().getName(), Font.PLAIN, 44));
    g2.drawString("You've completed the game in " + time + " seconds.", (int)(MainWindow.WIDTH/12), 150);
    if(numDeaths == 1)
      g2.drawString("You've died " + numDeaths + " time.", (int)(MainWindow.WIDTH/3.4), 225);
    else
      g2.drawString("You've died " + numDeaths + " times.", (int)(MainWindow.WIDTH/3.4), 225);
    
    g2.setColor(Color.GRAY);
    g2.setFont(new Font(g2.getFont().getName(), Font.ITALIC, 32));
    g2.drawString("Press 'R' to play again.", (int)(MainWindow.WIDTH/3), 500);
    
    g2.setColor(Color.WHITE);
    g2.setFont(new Font(g2.getFont().getName(), Font.PLAIN, 12));
    g2.drawString("Created by Matt Rubin, 2015", 25, MainWindow.HEIGHT - 25);
  }
  
  @Override 
  public void keyPressed(KeyEvent e) 
  {
    int c = e.getKeyCode();
    if(c == KeyEvent.VK_R)
    {
      //reset the game MainWindow 
      //System.out.println("Resetting the game...");
      player.pushReset(true);
      player.resetStats();
    }
  }
  
  @Override public void keyReleased(KeyEvent e) {}
  @Override public void keyTyped(KeyEvent e) {}//Character c = e.getKeyChar(); if(c.equals('r'))System.out.println("Resetting...");}
}