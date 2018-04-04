//Matt Rubin

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class MainWindow extends JFrame //implements ActionListener//, Runnable
{
  private Container pane;
  
  public static final int WIDTH = 1080, HEIGHT = 720;
  
  public static final int TARGET_FPS = 60;
  
  public static short fps = TARGET_FPS;
  
  private final int timerDelayMillis = 1000/TARGET_FPS; //1000 milliseconds divided by FPS

  private Timer timer;
  
  private long frameTime; //the time (milliseconds) that has passed since the last update
  private byte frameCount = 1; //counts how many frames have passed
  
  private static List<Level> levels = new ArrayList<Level>();
  
  private int currentLevel;
  
  private Player player;
  
  private GameCam camera;
  
  //public boolean gameWon, playing;
  
  public MainWindow()
  {
    setTitle("PrismaJump");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    setResizable(false);
    setFocusable(true);
   
    //gameWon = false;
    //playing = true;
    
    pane = getContentPane();
    
    pane.setSize(new Dimension(WIDTH, HEIGHT));
    pane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    
    pack();
    setLocationRelativeTo(null);
    
    pane.setLayout(null);
    
    Rectangle entireWindow = new Rectangle(0, 0, WIDTH, HEIGHT);
    
    /*Instantiate and add Components*/
    camera = new GameCam();
    
    player = new Player(520, 0, WIDTH/27, HEIGHT/10, camera);
    pane.add(player);
    
    setLevels();
    
    camera.setCurrentLevel(levels.get(0));
    
    /*Action Listeners*/
    addKeyListener(player);
    
    //timer = new Timer(timerDelayMillis, this);
    //timer.start();
    
    frameTime = System.currentTimeMillis();
    
    /*launch game/open window*/
    setVisible(true);
    
    currentLevel = 0;
    
    loadLevel(levels.get(currentLevel));
  }

  //@Override
  //this method is called each time timer ticks
  public void update()//actionPerformed(ActionEvent e)
  {
    //DEBUG
    if(player.forceWin)
    {
      player.forceWin = false;
      levels.get(currentLevel).didWin();
      if(currentLevel < levels.size() - 1) 
        currentLevel ++; 
      /*else 
      {
        player.setWon(true);
        pane.removeAll();
        VictoryScreen vs = new VictoryScreen((int)player.getTime(), player.getNumDeaths(), player);
        pane.add(vs);
        addKeyListener(vs);
        pane.add(new Background(new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.PINK})); 
      }*/
      setLevels();
      loadLevel(levels.get(currentLevel));
    }
    
    if(player.pushingReset())
    {
      player.pushReset(false);
      resetGame();
    }
    
    frameCount ++;
    if(Math.abs(System.currentTimeMillis() - frameTime) >= 1000)
    {
      //System.out.println("FPS: " + frameCount); 
      fps = frameCount;
      frameCount = 1; 
      frameTime = System.currentTimeMillis();
    }
    
    if(levels.get(currentLevel).shouldReload())
    {
      setLevels();///??? switch to resetLevel(int index) method??? to save memory by resetting only ONE level at a time???
      loadLevel(levels.get(currentLevel));
      levels.get(currentLevel).didReload(); 
    }
    if(levels.get(currentLevel).hasWon() && currentLevel < levels.size() - 1)
    {
      levels.get(currentLevel).didWin();
      if(currentLevel < levels.size() - 1) currentLevel ++; else currentLevel = 0; //set currentLevel to the next level
      setLevels();
      loadLevel(levels.get(currentLevel));
    }
    else if(levels.get(currentLevel).hasWon() && currentLevel == levels.size() - 1 /*&& !gameWon*/ && !player.hasWon()) //if player has won the final level
    {
      //gameWon = true;
      //playing = false;
      player.setWon(true);
      clearWindow();
      levels.get(currentLevel).invisibleInfo();
      player.enableBounds(false);
      VictoryScreen vs = new VictoryScreen((int)player.getTime(), player.getNumDeaths(), player);
      pane.add(vs);
      addKeyListener(vs);
      pane.add(new Background(new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.PINK}));
      //reset game if player preses 'R'
    }
    pane.repaint();
  }
  
  private void resetGame()
  {
    //gameWon = false; 
    currentLevel = 0;
    setLevels();
    for(int i = 0; i < levels.size(); i ++)
      levels.get(i).didWin();
    player.setWon(false);
    player.enableBounds(true);
    levels.get(0).reload();
  }
  
  private void setLevels() //initializes the levels ArrayList; resets its values (e.g. resets Platform coordinates) - used for resetting levels when Player dies
  {
    //NOTE!!!!!!!!  IDEA!! LEVELS GET MORE COLORFUL AS YOU PRGRESS: START FROM BLACK AND WHITE
    levels.clear();
    
    /*levels.add(new Level(
      new Platform[]
      {
        new Platform(-100, 580, 500, 50, player, Color.MAGENTA),
        new Platform(250, 650, 500, 50, player, Color.GRAY),
        new Platform(700, 450, 300, 50, player, Color.CYAN),
        new Platform(1000, 250, 400, 50, player, Color.GREEN), 
        new Platform(1300, 625, 300, 50, player, Color.RED),
        new Platform(1500, 500, 300, 50, player, Color.PINK),
        new DeathPlatform(1700, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 500, 50, player),
        new Goal(800, 300, 50, 50, player),
        new Boundary(-500, player),
        new Boundary(3000, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      /*},
      new Background(new Color[]{Color.RED, Color.GREEN, Color.BLUE}),
      "Starting slow.",
      500, 0)
    );*/
    
    //level 1
    levels.add(new Level(
      new Platform[]
      {
        new Platform(0, HEIGHT - 200, 1080, 200, player, Color.BLACK),
        new Goal(975, 450, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(1080, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.WHITE}),
      "Reach the goal.", Color.BLACK,
      0, 0)
    );
    
    //level 2
    levels.add(new Level(
      new Platform[]
      {
        new Platform(0, HEIGHT - 200, 1080, 200, player, Color.BLACK),
        new Platform(600, 200, 480, 200, player, Color.GRAY),
        new Goal(975, 50, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(1080, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.WHITE}),
      "Hop to it.", Color.BLACK,
      0, 0)
    );
    
    //level 3
    levels.add(new Level(
      new Platform[]
      {
        new Platform(0, HEIGHT - 200, 300, 200, player, Color.GREEN),
        new DeathPlatform(300, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 250, DeathPlatform.SPIKE_HEIGHT, player),
        new Platform(541, HEIGHT - 100, 580, 100, player, Color.GREEN),
        new Goal(900, 450, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(1080, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.WHITE, Color.MAGENTA}),
      "Don't get impaled.", Color.GREEN,
      50, 0)
    );
    
    //level 4
    levels.add(new Level(
      new Platform[]
      {
        new DeathPlatform(0, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 300, DeathPlatform.SPIKE_HEIGHT, player),
        new Platform(300, HEIGHT-450, 25, 450, player, Color.ORANGE),
        new DeathPlatform(325, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 700, DeathPlatform.SPIKE_HEIGHT, player),
        new Platform(1025, HEIGHT-75, 25, 75, player, Color.ORANGE),
        new DeathPlatform(950, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 500, DeathPlatform.SPIKE_HEIGHT, player),
        new Goal(2000, 300, 50, 50, player),
        new Goal(2100, 350, 50, 50, player),
        new Goal(2200, 400, 50, 50, player),
        new Goal(2300, 450, 50, 50, player),
        new Goal(2400, 500, 50, 50, player),
        new Goal(2500, 550, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(3000, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.ORANGE, Color.BLUE}),
      "Leap of faith.", Color.GREEN,
      293, 0)
    );
     
    //level 5
    levels.add(new Level(
      new Platform[]
      {
        new DeathPlatform(0, HEIGHT/2, 250, HEIGHT/2, player),
        new Platform(250, HEIGHT-450, 200, 450, player, Color.YELLOW),
        new Platform(700, HEIGHT - 200, 250, 150, player, new Color(0, 0, 253)),
        new DeathPlatform(950, HEIGHT - DeathPlatform.SPIKE_HEIGHT, 300, DeathPlatform.SPIKE_HEIGHT, player),
        new Platform(1250, HEIGHT - 500, 100, 500, player, new Color(0, 0, 253)),
        new Goal(2000, 300, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(3000, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.BLUE}),
      "Trust your instincts.", Color.ORANGE,
      293, 0)
    );
    
    //level 6
    levels.add(new Level(
      new Platform[]
      {
        
        new Goal(3000, 450, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(3100, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.BLACK}),
      "Void.", Color.WHITE,
      50, 0)
    );
    
    //level 7
    levels.add(new Level(
      new Platform[]
      {
        new Platform(0, HEIGHT - 200, 300, 200, player, Color.GREEN),
        new DeathPlatform(300, HEIGHT - 350, 250, 350, player),
        new Platform(541, HEIGHT - 100, 580, 100, player, Color.GREEN),
        new Goal(3000, 450, 50, 50, player),
        new Boundary(-100, player),
        new Boundary(3100, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA}),
      "Rainbow.", Color.WHITE,
      50, 0)
    );
    
    //level 8
    levels.add(new Level(
      new Platform[]
      {
        new Platform(0, HEIGHT - 200, 300, 200, player, Color.GREEN),
        new DeathPlatform(300, HEIGHT - 300, 250, 300, player),
        new Platform(541, HEIGHT - 150, 580, 150, player, Color.GREEN),
        new Goal(-4900, 450, 50, 50, player),
        new Boundary(-5000, player),
        new Boundary(1000, player),
      }, 
      new Enemy[]
      {
      /*...new Enemie()s...*/
      },
      new Background(new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA}),
      "The other way.", Color.WHITE,
      800, 0)
    );
    
    //level 9
    levels.add(new Level(
      new Platform[]
      {
        new DeathPlatform(0, HEIGHT - 50, 5000, 50, player), //DeathPlatform on bottom of window for entire level
        new Platform(0, 650, 100, 20, player, Color.RED),
        
        new Boundary(-100, player),
        new Boundary(5000, player),
        new Goal(200, 500, 75, 75, player)
      },
      new Enemy[]
      {
        /*Enemies*/
      },
      new Background(new Color[]{Color.RED, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.ORANGE, Color.MAGENTA}),
      "An actual challenge???", Color.RED,
      50, 0)
    );    
  }
  
  public void loadLevel(Level lev)
  {
    camera.setCurrentLevel(lev);
    
    clearWindow();
   
    //add level platforms
    Platform[] plats = lev.getPlatforms();
    for(Platform p : plats)
    {
      pane.add(p);
    }
    
    //add enemies
    Enemy[] enems = lev.getEnemies();
    for(Enemy e : enems)
    {
      pane.add(e);
    }
    
    //add background
    pane.add(lev.getBackground());
    
    player.resetLevelInfo();
    
    //move player to proper starting coordinates
    player.getHitbox().x = (int)lev.getStartX();
    player.getHitbox().y = (int)lev.getStartY();
    
    player.resetLevel();
  }
  
  private void clearWindow() //remove all Platforms, Enemies, Background
  {
    Component[] comps = pane.getComponents();
    for(Component c : comps)
    {
      //clear pane of all components, except, for player
      if(!(c instanceof Player))
      {
        try{
          pane.remove(c);
        }
        catch(Exception e){ System.out.println(e.getStackTrace()); }
      }
    } 
  }
  
  public static void main(String[] args)
  {
    /*
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        MainWindow game = new MainWindow();

        long t = System.nanoTime();
        while(true)//game.playing)
        {
          if(System.nanoTime() - t >= 1000000000/(TARGET_FPS-1))
          {
            game.update();
            t = System.nanoTime();
          }
        }
      }
    });
    */

    MainWindow game = new MainWindow();

        long t = System.nanoTime();
        while(true)//game.playing)
        {
          if(System.nanoTime() - t >= 1000000000/(TARGET_FPS-1))
          {
            game.update();
            t = System.nanoTime();
          }
        }
      
  }
  
}