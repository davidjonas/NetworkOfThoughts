import java.util.ArrayList;
import processing.core.PApplet;

/***
class used to cycle between a list of networks by logging in automatically
**/
class NetworkCycle extends Thread
{
  private ObjectProvider objects;
  private ObjectUpdater updater;
  private DAL server;
  private PApplet context;
  private String[] networks;
  private int networkDelay;
  private long lastCall;
  private int current;
  
  NetworkCycle(PApplet context, ObjectProvider objects, ObjectUpdater updater, String networks, int networkDelay)
  {
    this.objects = objects;
    this.updater = updater;
    this.server = objects.getDALServer();
    this.context = context;
    this.networks = this.parseNetworks(networks);
    this.networkDelay = networkDelay;
    this.current = 0;
    this.readTime();
  }
  
  public void run()
  {
    while (true) {
      try {
        Thread.sleep(this.networkDelay);
      } catch (InterruptedException x) {
        break;  
      }
      
      this.updater.freeze();

      String [] currentNetwork = this.context.split(this.networks[this.current], ",");
      String login = currentNetwork[0];
      String password = currentNetwork[1];
      
      if(this.objects.getDAL().login(login, password))
      {
        System.out.println("logged in successfully.");
        this.objects.getUsersFirstBrainstorm();
        this.objects.resetGlobalFader();
      }
      else
      {
        System.out.println("Error logging in automatically.");
      }
      
      if (this.current < this.networks.length-1)
      {
        this.current++;
      }
      else
      {
        this.current = 0;
      }

      this.updater.unfreeze();
    }
  }
 
  public String[] parseNetworks(String networks)
  {
    return this.context.split(networks,";");
  } 
  
  public void readTime()
  {
    this.lastCall = server.getCurrentTime();
  }
  
  public void setTime(long time)
  {
    lastCall = time;
  }
}
