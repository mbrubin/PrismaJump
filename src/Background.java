import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Background extends JComponent
{
  Color[] colorRange;
  Color currentColor;
  
  int currentIndex;
  int nextIndex; 
  
  final int colorChangeAmount = 1;
  
  public Background(Color[] cr)
  {
    setBounds(new Rectangle(0, 0, MainWindow.WIDTH, MainWindow.HEIGHT)); //set bounds of component to the entire window
    colorRange = cr; 
    currentIndex = 0;
    currentColor = colorRange[0];
    //colorChangeAmount = 1;
  }
  
  //fills entire background with rectangle of a color that keeps changeing to different rainbowy colors and stuff
  @Override
  public void paint(Graphics g)
  {
    loopColors();
    g.setColor(currentColor);
    g.fillRect(0, 0, MainWindow.WIDTH, MainWindow.HEIGHT);
  }
  
  private void loopColors()
  {
    if(colorRange.length > 1)
    {
      if(currentIndex == colorRange.length - 1 )
        nextIndex = 0;
      else //if currentIndex isn't the last index of colorRange
        nextIndex = currentIndex + 1;
      
      int r1 = currentColor.getRed(), g1 = currentColor.getGreen(), b1 = currentColor.getBlue();
      int r2 = colorRange[nextIndex].getRed(), g2 = colorRange[nextIndex].getGreen(), b2 = colorRange[nextIndex].getBlue();
      
      int newR, newG, newB;
                                                                                                      
      if(r1 < r2)
        newR = r1 + colorChangeAmount;
      else if(r1 > r2)
        newR = r1 - colorChangeAmount;
      else
        newR = r1;
                                                                                                      
      if(g1 < g2)
        newG = g1 + colorChangeAmount;
      else if(g1 > g2)
        newG = g1 - colorChangeAmount;
      else
        newG = g1;
                                                                                                      
      if(b1 < b2)
        newB = b1 + colorChangeAmount;
      else if(b1 > b2)
        newB = b1 - colorChangeAmount;
      else
        newB = b1;

      currentColor = new Color(newR, newG, newB);
      if(currentColor.equals(colorRange[nextIndex]))
      {
        currentIndex = nextIndex;
      }
    }
  }
  
  public static void main(String[] args)
  {
    new MainWindow(); 
  }
}