import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PGraphics;

class ObjectProvider
{
  
  
  private ArrayList sectors;
  private ArrayList objects;
  private ArrayList renderQueue;
  private int iterator;
  private int objectCount;
  private DAL server;
  private PApplet context;
  private Camera cam;
  private ConnectionProvider conn;
  private int expectedId;
  private String currentSearch;
  private boolean currentSearchChanged;
  private ColorConfiguration config;
  
  
  private boolean screensaver;
  private boolean pulse;
  private int pulseCount;
  private float rotationStrength;
  
  private boolean connectionMode;
  private Thing objectInConnectionMode;
  private boolean globalMenuMode;
  private Point camLastPosition;
  
  //faders for the forces
  private float globalFader;
  private float screensaverFader;
  
  //global menu timer
  private boolean globalClose;
  private int globalStepper;
  
  ObjectProvider(PApplet context, Camera cam)
  {
     this.objects = new ArrayList();
     this.sectors = new ArrayList();
     this.renderQueue = new ArrayList();
     this.iterator = 0;
     this.objectCount = 0;
     //TODO:Add a static global var to be able to change the server easily
     this.server = new DAL(context, "http://www.neuralnetworkofideas.com/providers/appletAccess.php");
     this.context = context;
     this.conn = null;
     this.cam = cam;
     this.expectedId = 0;
     this.currentSearch = "";
     this.currentSearchChanged = true;
     this.globalMenuMode = false;
     this.connectionMode = false;
     this.objectInConnectionMode = null;
     this.config = ((NetworkOfThoughts)context).config;
     this.camLastPosition = new Point(0,0,0);
     this.globalFader = (float)0.0;
     this.screensaverFader = (float)0.0;
     this.globalClose = false;
     this.globalStepper = 100;
  }
  
  ObjectProvider(PApplet context, ConnectionProvider conn, Camera cam)
  {
     this.objects = new ArrayList();
     this.sectors = new ArrayList();
     this.iterator = 0;
     this.objectCount = 0;
     //TODO:Add a static global var to be able to change the server easily
     this.server = new DAL(context, "http://91.184.23.45/networkofthoughts/appletAccess.php");
     this.context = context;
     this.conn = conn;
     this.cam = cam;
     this.expectedId = 0;
     this.currentSearch = "";
     this.currentSearchChanged = true;
     this.connectionMode= false;
     this.objectInConnectionMode = null;
     this.config = ((NetworkOfThoughts)context).config;
     this.camLastPosition = new Point(0,0,0);
     this.globalFader = (float)0.0;
     this.screensaverFader = (float)0.0;
     this.globalClose = false;
     this.globalStepper = 100;
  }
  
  public void activateConnectionMode(Thing ob)
  {
    this.connectionMode = true;
    this.objectInConnectionMode = ob;
  }
  
  public void deactivateConnectionMode()
  {
    this.connectionMode = false;
  }
  
  public void activateGlobalMenuMode()
  {
    this.globalMenuMode = true;
    this.camLastPosition = cam.position.getPosition();
    this.cam.position.setDestination(new Point(0,0,3250));
    this.cam.position.setMaxSpeed((float)150.0);
    
    //XXX: I'm a very very bad boy for this!!
    NetworkOfThoughts tmp = (NetworkOfThoughts) this.context;
    tmp.globalMenu.activate();
  }
  
  public void deactivateGlobalMenuMode(boolean reposition)
  {
    this.globalMenuMode = false;
    if(reposition)
    {
      this.cam.position.setDestination(this.camLastPosition);
    }
    this.cam.position.setMaxSpeed((float)30.0);
    
    //XXX: I'm a very very bad boy for this!!
    NetworkOfThoughts tmp = (NetworkOfThoughts) this.context;
    
    TextObject textMenuActiveOnObj = (TextObject) tmp.menuActiveOnObj;
    textMenuActiveOnObj.setActiveState(false);

    this.globalClose = true; 
  }
  
  public boolean inGlobalMenuMode()
  {
    return this.globalMenuMode;
  }
  
  public boolean inConnectionMode()
  {
    return this.connectionMode;
  }
  
  public Thing getObjectInConnectionMode()
  {
    return this.objectInConnectionMode;
  }
  
  public String generateConnectionString()
  {
    boolean matrix[][] = conn.getMatrix();
    String result = "";
    
    for(int i=0; i<matrix.length; i++)
    {
      for(int j=0; j<matrix.length; j++)
      {
        if( i != j && matrix[i][j])
        {
          if(result != "")
          {
            result += ",";
          }
          result += this.get(i).getId()+"-"+this.get(j).getId();
        }
      }
    }
    
    return result;
  }
  
  public void saveConnectionString()
  {
    server.saveConnectionString(this.generateConnectionString());
  }
  
  public void selectObject(int i)
  {
    Thing obj = (Thing) objects.get(i);
    obj.setMode(Thing.SELECTED);
  }
  
  public void unSelectObject(int i)
  {
    Thing obj = (Thing) objects.get(i);
    obj.setMode(Thing.IDLE);
  }
  
  public void selectAll()
  {
    for(int i=0; i < objects.size(); i++)
    {
      Thing obj = (Thing) objects.get(i);
      obj.setMode(Thing.SELECTED);
    }
  }
  
  public void unSelectAll()
  {
    for(int i=0; i < objects.size(); i++)
    {
      Thing obj = (Thing) objects.get(i);
      obj.setMode(Thing.IDLE);
    }
  }
  
  public void setScreensaver(boolean setting)
  {
    if(setting)
    {
      context.noCursor();
    }
    else
    {
      context.cursor(context.ARROW);
    }
    this.screensaver = setting;
    this.pulseCount = 0;
    this.pulse = false;
    this.screensaverFader = (float)0.0;
  }
  
  public int findFirstTemporary()
  {
    for(int i=0; i < objects.size(); i++)
    {
      Thing obj = (Thing) objects.get(i);
      if (obj.getMode() == Thing.TEMPORARY)
      {
        return i;
      }
    }
    return -1;
  }
  
  public void deleteAllTemporaryObjects()
  {
    int i;
    while((i = this.findFirstTemporary()) != -1)
    { 
        this.deleteObject(i);
    }
  }
  
  public void deleteObject(int i)
  {
    objects.remove(i);
    conn.reindexMaintainingConnections(i);
  }
  
  public void permanentDeleteObject(int i)
  {
    Thing object = (Thing)objects.get(i);
    if(this.server.getBrainstorm() == 0)
    {
      this.server.permanentRemoveObject(object);
    }
    else
    {
      this.server.removeObjectFromBrainstorm(object, this.server.getBrainstorm());
    }
    objects.remove(i);
    conn.reindexMaintainingConnections(i);
  }
  
  public boolean addWorldOnlyObject(Thing obj)
  {
    if(obj.getId() == expectedId)
    {
      obj.setPosition(cam.position.getPosition().subtract(new Point(100,0,250)));
      cam.chaseFor(obj, 200);
      this.expectedId = 0;
    }else if (obj.getMode() == Thing.TEMPORARY)
    {
      obj.setPosition(cam.position.getPosition().subtract(new Point(100+(context.random(-100, 100)),context.random(-100,100),850+(context.random(0, 100)))));
    }
    else
    {
      if (this.size() > ObjectUpdater.MAX_OBJECTS)
      {
        return false;
      }
      obj.setPosition(randomPosition());
    }
    
    objects.add(obj);
    conn.reindexMaintainingConnections();
    return true;
  }
  
  public boolean existsInWorld(int ID)
  {
    for(int i=0; i < objects.size(); i++)
    {
      Thing obj = (Thing) objects.get(i);
      if(obj.getId() == ID)
      {
        return true;
      }
    }
    return false;
  }
  
  public int idToIndex(int ID)
  {
    for(int i=0; i < objects.size(); i++)
    {
      Thing obj = (Thing) objects.get(i);
      if(obj.getId() == ID)
      {
        return i;
      }
    }
    return -1;
  }
  
  public synchronized void setCurrentSearch(String s)
  {
    this.currentSearch = s;
    this.currentSearchChanged = true;
  }
  
  public String getCurrentSearch()
  {
    this.currentSearchChanged = false;
    return this.currentSearch;
  }
  
  public boolean currentSearchChanged()
  {
    return currentSearchChanged;
  }
  
  public DAL getDALServer()
  {
    return this.server;
  }
  
  public void connect(ConnectionProvider conn)
  {
    this.conn = conn;
    conn.refresh(this);
  }
  
  public int[] getConnectionsOf(Thing ob)
  {
    int index = this.idToIndex(ob.getId());
    return this.conn.getConnectionsOf(index);
  }
  
  public void deleteConnection(int indexA, int indexB)
  {
    this.conn.deleteConnection(indexA, indexB);
  }
  
  public int getWorldSize()
  {
    return (int)(3 * context.log(2*(this.objects.size()+(float)0.4)) + 7)*100;
  }
  
  public Point randomPosition()
  {
    int worldSize = getWorldSize();
    return new Point(context.random(this.cam.position.getPosition().x-50, this.cam.position.getPosition().x+50), 
                     context.random(this.cam.position.getPosition().y-50, this.cam.position.getPosition().y+50),
                     context.random(this.cam.position.getPosition().z-worldSize-50, this.cam.position.getPosition().z-worldSize));
  }
  
  public void resetGlobalFader()
  {
    this.globalFader = (float)0.0;
  }
  
  public int getRandomObjects(int n)
  {
    ArrayList results = this.server.getNTextObjects(n);
    
    Thing obj;
    for(int i=0; i<results.size(); i++)
    {
      obj = (Thing)results.get(i);
      obj.setPosition(randomPosition());
      objects.add(obj);
    }
    if(conn != null)
    {
      conn.refresh(this);
    }
    return results.size();
  }
  
  public int getUserTextObjects()
  {
    if(server.loggedIn())
    {
      int id;
      
      ArrayList results = this.server.getUserTextObjects();
      
      objects = new ArrayList();
      
      Thing obj;
      for(int i=0; i<results.size(); i++)
      {
        obj = (Thing)results.get(i);
        obj.setPosition(randomPosition());
        objects.add(obj);
      }
      
      if(conn != null)
      {
        conn.refresh(this);
      }
      return results.size();
    }else
    {
       return 0;
    }
  }
  
  
  public int getBrainstorm(int n)
  {
    ArrayList results = this.server.getBrainstorm(n);
    
    objects = new ArrayList();
    
    Thing obj;
    for(int i=0; i<results.size(); i++)
    {
      obj = (Thing)results.get(i);
      obj.setPosition(randomPosition());
      objects.add(obj);
    }
    
    if(conn != null)
    {
      conn.createConnections(ConnectionProvider.CONN_STR);
      //conn.refresh(this);
    }
    
    this.server.setBrainstorm(n);
    
    return results.size();
  }
  
  public int getUsersFirstBrainstorm()
  {
    if(this.server.loggedIn())
    {
      ArrayList results = this.server.getUsersFirstBrainstorm();
    
      objects = new ArrayList();
      
      Thing obj;
      for(int i=0; i<results.size(); i++)
      {
        obj = (Thing)results.get(i);
        obj.setPosition(randomPosition());
        objects.add(obj);
      }
      
      if(conn != null)
      {
        conn.createConnections(ConnectionProvider.CONN_STR);
        //conn.refresh(this);
      }
      
      return results.size();
    }
    else
    {
      return -1;
    }
  }
  
  public void addBrainstorm(String name)
  {
    int idBrainstorm = this.server.addBrainstorm(name);
    getBrainstorm(idBrainstorm);
  }
  
  public void refreshConnections()
  {
    conn.refresh(this);
  }
  
  public void connectObject(int i)
  {
    conn.connectObject(i);
  }
  
  public void connectLastObject()
  {
    conn.connectLastObject();
  }
  
  //This is an expected object, meaning that it was created by this app and sent to the server and is expected to come back as a thought
  public void setExpectedId(int id)
  {
    this.expectedId = id;
  }
  
  public void addTextObject(String newMsg)
  {
     ObjectCleaner cleaner = new ObjectCleaner();
     String msg = cleaner.cleanTextObject(newMsg);
     TextObject newObj;
     String user = server.getUser();
     
     if(user == null)
     {
       newObj = new TextObject(context, msg, "null", 0, -1);
     }
     else
     {
       newObj = new TextObject(context, msg, user, 0, -1);
     }
     
     //This would add the new object to the world but now it just sends it to the server and recieves it back an "expectedObject"
     //newObj.setPosition(randomPosition());
     //objects.add(newObj);
     //conn.refresh(this);
     
     if(this.server.getBrainstorm() == 0)
     {
       this.setExpectedId(server.addTextObject(msg));
     }
     else
     {
       this.setExpectedId(server.addTextObject(msg, this.server.getBrainstorm()));
     }
  }
  
  public DAL getDAL()
  {
    return server;
  }
  
  public void reset()
  {
    this.iterator = -1;
  }
  
  public int size()
  {
    return objects.size();
  }
  
  public boolean next()
  {
    if(objects.size() > 0)
    {
      if (iterator+1 < objects.size())
      {
        iterator++;
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      return false;
    }
  }
  
  public Thing get(int index)
  {
    if(index < objects.size())
    {
      return (Thing)objects.get(index);
    }
    else
    {
      System.out.println("ERROR: ObjectProvider was asked index " + index + " which does not exist in the world");
      return null;
    }
  }
  
  public Thing current()
  {
    if(objects.size() > 0)
    {
      return (Thing)objects.get(iterator);
    }
    else
    {
      return null;
    }
    //TODO: throw exception to when objects is empty
  }
  
  public int currentIndex()
  {
    return iterator;
  }
  
  public float compareDistance(Thing a, Thing b)
  {
    float camDistanceA = a.position.getPosition().distance(cam.position.getPosition());
    float camDistanceB = b.position.getPosition().distance(cam.position.getPosition());
    
    return Math.abs(camDistanceA) - Math.abs(camDistanceB);
  }
  
  public void calculateRenderQueue()
  {
    renderQueue.clear();
    int pos = -1;
    
    for(int i=0; i<objects.size(); i++)
    {
      pos=-1;
      if(renderQueue.size() > 0)
      {
        for(int j=0; j<renderQueue.size(); j++)
        {
          if(compareDistance((Thing)objects.get(i), (Thing)renderQueue.get(j)) < 0)
          {
            pos=j;
            break;
          }
        }
      }
      if(pos == -1)
      {
        renderQueue.add(objects.get(i));
      }
      else
      {
        renderQueue.add(pos, objects.get(i));
      }
      
      //System.out.println("Added to position: " + pos);
    }
  }
  
  public void step()
  { 
     this.reset();
     while(this.next())
     {
        this.current().step(getCurrentResultingForce());
     }
     
     //INFO: this closes the global menu imideately
     if (this.globalClose)
     {
        //XXX: I'm a very very bad boy for this!!
         NetworkOfThoughts tmp = (NetworkOfThoughts) this.context;
         tmp.globalMenu.deactivate(); 
         this.globalClose = false;
     }
     
     //INFO: this is the stepper to delay the closing of the menu. I can also be adapted to fade it out.
     /*if (this.globalClose)
     {
       if(this.globalStepper == 0)
       {
         //XXX: I'm a very very bad boy for this!!
         NetworkOfThoughts tmp = (NetworkOfThoughts) this.context;
         tmp.globalMenu.deactivate(); 
         this.globalClose = false;
         this.globalStepper = 10;
       }
       else
       {
         this.globalStepper--;
       }
     }*/
  }
  
  public void render()
  {
    calculateRenderQueue();
    for(int i=renderQueue.size()-1; i>=0; i--)
    {
       //Cache the current item for speed
       Thing current = (Thing)renderQueue.get(i);
      
       //calculate aplha for fog effect
       int a;
       float distance = cam.position.getPosition().distance(current.position.getPosition());
       if(distance > 100)
       {
         a = 255 - ((int)Math.floor(distance) - 500)/8;
         if (a < 100)
         {
           a = 120;
         }
       }
       else
       {
         a = 255;
       }
       current.setAlpha(a);
       current.render();
    }
  }
  
  public Point getCurrentResultingForce()
  { 
     //Force field effect aka. shy effect
     Point finalForce = new Point(0,0,0);
     int triggerDistance = 400;
     Thing cur = this.current();
     
     //Hole lotta shakin' goin' on! at least for selected items!!
     Point randomForce;
     if(cur.getMode() == Thing.SELECTED || cur.getMode() == Thing.TEMPORARY)
     {
       randomForce = new Point(context.random((float)-0.3 , (float)0.3), context.random((float)-0.3 , (float)0.3), context.random((float)-0.3 , (float)0.3));
     }
     else
     {
       randomForce = new Point(0,0,0);
     }
    
    
    //Ram the cam!!
    Point camAtraction;
    if(cur.getMode() == Thing.TEMPORARY)
    {
       float camDistance = cur.position.getPosition().distance(cam.position.getPosition());
       float camStrength = (float)5.0/triggerDistance * (triggerDistance - camDistance);
       camAtraction = cam.position.getPosition().subtract(cur.position.getPosition().add(new Point(0,0,800))).getNormalizedVector().multiply(-camStrength/5);
     }
     else
     {
       camAtraction = new Point(0,0,0);
     }
     
     for (int i=0; i<objects.size(); i++)
     {
        Thing target = (Thing) objects.get(i);
        if(target != cur)
        {
          float distance = cur.position.getPosition().distance(target.position.getPosition());
          if (distance < triggerDistance)
          {
            Point direction = cur.position.getPosition().subtract(target.position.getPosition()).getNormalizedVector();
            float strength = (float)5.0/triggerDistance * (triggerDistance - distance);
            Point component = new Point(direction.x*strength, direction.y*strength, direction.z*strength);
            finalForce = finalForce.add(component);
          }
          if (conn.isConnected(i, iterator))
          {
            Point springDirection = target.position.getPosition().subtract(cur.position.getPosition()).getNormalizedVector();
            Point springComponent = new Point(springDirection.x*3, springDirection.y*3, springDirection.z*3);
            finalForce = finalForce.add(springComponent);
          }
        } 
     
     }
     
     //centriptal force
     Point center = new Point(0,0,0);
     Point centriptalDirection = center.subtract(cur.position.getPosition()).getNormalizedVector();
     Point centriptal = centriptalDirection.multiply((float)0.5);
     
     Point pulsation = new Point(0,0,0);
     Point rotation = new Point(0,0,0);
     
     if(this.screensaver)
     {     
       if (screensaverFader < 1.0)
       {
          screensaverFader += (float)0.0001; 
       }
       
       float distanceToCenter = cur.position.getPosition().distance(center);
       
       //Pulsation on
       if(this.pulseCount == 6308)
       {
         this.pulseCount = 0;
         this.pulse = !this.pulse;
       }
       this.pulseCount++;
       
//       if(this.pulse)
//       {
//         pulsation = centriptalDirection.multiply((float)-0.3).multiply(screensaverFader);
//       }
//       else
//       {
//         pulsation = centriptalDirection.multiply((float)0.3).multiply(screensaverFader);
//       }

       pulsation = centriptalDirection.multiply(context.sin(pulseCount*(float)0.00006)).multiply((float)0.7);  
       
       //Rotation on
       rotation = centriptalDirection.x(new Point(0,1,0)).multiply((float)0.5).multiply(screensaverFader);
     }

     if (globalFader < 1.0)
     {
          globalFader += (float)0.0001; 
     } 
     return finalForce.add(randomForce).add(camAtraction).add(centriptal).add(rotation).add(pulsation).multiply(globalFader);
  }
}
