import java.util.ArrayList;


/***
class used to update new ideas in reatime and perform searches
**/
class ScreenSaver extends Thread
{
  public static int WAITING = 0;
  public static int ALERT = 1;
  public static int SCREENSAVER = 2;
  public static long SCREENSAVER_TRIGGER = 10000;
  
  private ObjectProvider objects;
  private long lastMouseMove;
  private long lastCheck;
  private int mode;
  
  ScreenSaver(ObjectProvider objects)
  {
    this.mode = WAITING;
    this.objects = objects;
    this.lastMouseMove =  System.currentTimeMillis();
    this.lastCheck = 0;
  }
  
  public void run()
  {
    while (true) {
      if(mode == WAITING)
      {
        // WAITING MODE CHECKS EVERY 2 SECONDS IF THE MOUSE HAS BEEN STOPPED
        try {
          Thread.sleep(4000);
        } catch (InterruptedException x) {
          break;  
        }
        
        //System.out.println("Screensaver thread running in waiting mode: last move => " + lastMouseMove);
        
        if (lastMouseMove == lastCheck)
        {
          this.alertMode();
        }
        lastCheck = lastMouseMove;
      }
      else if(mode == ALERT)
      {
        //ALERT MODE RUNS EVERY SECOND TO SEE IF THE MOUSE IS STILL STOPPED
        try {
          Thread.sleep(1000);
        } catch (InterruptedException x) {
          break;  
        }
     
        //System.out.println("Screensaver thread running in alert mode: last move => " + lastMouseMove);
        
        if (lastMouseMove != lastCheck)
        {
          this.waitingMode();
        }
        else
        {
          if(System.currentTimeMillis() - lastMouseMove >= SCREENSAVER_TRIGGER)
          {
            this.screensaverMode();
          }
        }
        
      }
      else if(mode == SCREENSAVER)
      {
        //SCREENSAVER MODE RUNS EVERY SECOND TO ACTIVATE ROTATION AND PULSE
        try {
          Thread.sleep(1000);
        } catch (InterruptedException x) {
          break;  
        }
        
        //System.out.println("Screensaver thread running in screensaver mode: last move => " + lastMouseMove);
        
        if (lastMouseMove != lastCheck)
        {
          this.waitingMode();
        }
      }
     
    }
  }
  
  public void waitingMode()
  {
    this.mode = WAITING;
    //SET SCREENSAVER OFF
    objects.setScreensaver(false);
  }
  
  public void alertMode()
  {
    this.mode = ALERT;
  }
  
  public void screensaverMode()
  {
    this.mode = SCREENSAVER;
    //SET SCEREENSAVER ON
    objects.setScreensaver(true);
  }
  
  public void moved()
  {
     this.lastMouseMove =  System.currentTimeMillis();
  }
}
