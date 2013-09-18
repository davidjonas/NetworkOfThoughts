import java.util.ArrayList;

/***
class used to update new ideas in reatime and perform searches
**/
class ObjectUpdater extends Thread
{
  public static int NEW_THOUGHTS_ONLY = 1;
  public static int POP_UP_MODE = 2;
  public static int MAX_OBJECTS = 170;
  private ObjectProvider objects;
  private DAL server;
  private long lastCall;
  private int mode;
  private boolean frozen;
  
  ObjectUpdater(ObjectProvider objects)
  {
    this.mode = NEW_THOUGHTS_ONLY;
    this.frozen = false;
    this.objects = objects;
    this.server = objects.getDALServer();
    this.readTime();
  }
  
  public void run()
  {
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException x) {
        break;  
      }

      if (this.frozen) continue;
      
      //------------------- Check for new thoughts on the server ---------------------
      ArrayList newObjects;
      if(this.server.getBrainstorm() == 0)
      {
        newObjects = server.getNewObjectsSince(this.lastCall);
      }
      else
      {
        newObjects = server.getBrainstorm(this.server.getBrainstorm());
        //newObjects = server.getNewObjectsSince(this.lastCall);
      }
      
      if(newObjects.size() > 0)
      {
        TextObject lastObject = (TextObject)newObjects.get(newObjects.size()-1);
        this.setTime(lastObject.getTimestamp());
      }
      //System.out.println("Number of new thoughts: " + newObjects.size());
      for(int i=0; i<newObjects.size(); i++)
      {
        synchronized(this)
        {
          Thing cur = (Thing)newObjects.get(i);
          if(this.server.getBrainstorm() == 0)
          {
            if(objects.addWorldOnlyObject(cur))
            {
              objects.connectLastObject();
            }
          }
          else
          {
            if(!objects.existsInWorld(cur.getId()) && objects.addWorldOnlyObject(cur))
            {
              objects.connectLastObject();
            }
          }
        }
      }
      
      //------------------------------------------------------------------------------
      //------------------------------- Pop-up thoughts ------------------------------
      
      if(objects.currentSearchChanged() && objects.size() < MAX_OBJECTS)
      {
        if(objects.getCurrentSearch() != "")
        {
          this.mode = POP_UP_MODE;
        }
        else
        {
          this.mode = NEW_THOUGHTS_ONLY;
        }
        if (mode == POP_UP_MODE)
        {
          ArrayList popups = server.searchThoughts(objects.getCurrentSearch());
          
          //System.out.println("Number of new thoughts: " + popups.size());
          
          Thing newObj;
          for(int i=0; i<popups.size(); i++)
          {
            synchronized(this)
            {
              newObj = (Thing)popups.get(i);
              if(!objects.existsInWorld(newObj.getId()))
              {
                newObj.setMode(Thing.TEMPORARY);
                objects.addWorldOnlyObject(newObj);
                //objects.reconnectLastObject();
              }
            }
          }
        }
      }
      
      //------------------------------------------------------------------------------
    }
  }
 
  public void freeze()
  {
    this.frozen = true;
  }

  public void unfreeze()
  {
    this.frozen = false;
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
