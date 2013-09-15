import java.util.ArrayList;
import processing.core.PApplet;

/***
class used to cycle between a list of networks by logging in automatically
**/
class NetworkCycle extends Thread
{
  private ObjectProvider objects;
  private DAL server;
  private PApplet context;
  private String[] networks;
  private int networkDelay;
  private long lastCall;
  private int current;
  
  NetworkCycle(PApplet context, ObjectProvider objects, String networks, int networkDelay)
  {
    this.objects = objects;
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
      
      String [] currentNetwork = this.context.split(this.networks[this.current], ",");
      String login = currentNetwork[0];
      String password = currentNetwork[1];
      
      if(this.objects.getDAL().login(login, password))
      {
        System.out.println("logged in successfully.");
        this.objects.getUsersFirstBrainstorm();
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
