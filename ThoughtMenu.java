import java.util.ArrayList;
import processing.core.PApplet;
import picking.*;

class ThoughtMenu extends Thing
{
  protected ArrayList menuItems;
  private ColorConfiguration config;
  private Picker picker;
  private Camera cam;
  private ObjectProvider objects;
  
  ThoughtMenu(PApplet context, Camera cam, ObjectProvider objects)
  {
    this.context = context;
    this.objects = objects;
    this.setMode(NON_CONNECTABLE);
    this.position = new PhysicalParameter(0, 0, 0, 0, 0, 0);
    this.setUser("");
    this.setId(1);
    this.menuItems = new ArrayList();
    this.config = ((NetworkOfThoughts)context).config;
    this.picker = new Picker(this.context);
    this.cam = cam;
    this.deactivate();
  }
  
  //Add a menu item
  public void addItem(MenuItem item)
  {
    menuItems.add(item);
  }
  
  public void render()
  {
    if(this.isActive())
    {
      context.pushMatrix();
      context.noFill();
      context.stroke(255,0,0,30);
      context.translate(0,0,this.position.getPosition().z);
      context.fill(0);
      //context.ellipse(this.position.getPosition().x - (float)14.5, this.position.getPosition().y-(float)7.5, 4, 4);
      context.popMatrix();
      
      for(int i=0; i<menuItems.size(); i++)
      { 
        MenuItem currentItem = (MenuItem) menuItems.get(i);
               
        //render node
        currentItem.render();
        //currentItem.renderHitArea();
      }
    }
  }
  
  public void resetItemPositions()
  {
     for(int i=0; i<menuItems.size(); i++)
     {
       MenuItem currentItem = (MenuItem) menuItems.get(i);
       currentItem.position.setPosition(this.position.getPosition());
     }
  }
  
  public void render(float x, float y, float z)
  {
    this.position.setPosition(x,y,z);
    render();
  }

  public void step()
  {
      for(int i=0; i<menuItems.size(); i++)
      {
        MenuItem currentItem = (MenuItem) menuItems.get(i);
        
        float angle = Utils.RAD((float) 0 + i*(float)180.0);
        
        float radius = 17;
        float posX = this.position.getPosition().x - (float)14.5 - (float) 3.8 + (radius * context.sin(angle));
        float posY = this.position.getPosition().y - (float)7.5 - (float) 3.5 + (radius * context.cos(angle));
        
        Point pos = new Point(posX, posY, this.position.getPosition().z);
        currentItem.position.setPosition(pos);
        //currentItem.step(0,0,0);
      }
  }

  public void step(Point a)
  {
    return;
  }

  public void render(float x, float y)
  {
    render(x,y,0);
  }
  
  public void render(Point p)
  {
    render(p.x, p.y, p.z);
  }
  
  public void renderHitArea()
  {

  }
  
  public void resetPhysicsToDefault()
  {
    position.setDrag((float)0);
    position.setSpring((float)0);
    position.setMaxSpeed((float)0);
  }
  
  public void deleteConnectionItems()
  {
    //Deleting all connection specific buttons
    for(int i=0; i<menuItems.size(); i++)
    {
      MenuItem currentItem = (MenuItem) menuItems.get(i);
      if(currentItem instanceof DeleteConnectionMenuItem)
      {
        menuItems.remove(i);
      }
    }
  }
  
  public void createConnectionButtons(Thing ob)
  {
    //Adding all the connection buttons
    int[] connections = objects.getConnectionsOf(ob);
  
    if(connections != null)
    {
      for(int i=0; i<connections.length; i++)
      {
         int indexB = connections[i];
         DeleteConnectionMenuItem connItem = new DeleteConnectionMenuItem(this.context, this.objects, this, "", this.cam);
         connItem.setAction(new DeleteConnectionAction(objects));
         connItem.setCurrentObject(ob);
         connItem.setIndexB(indexB);
         this.addItem(connItem);
      }
    }
  }
  
  public void activate(Thing ob)
  {   
    this.deleteConnectionItems();
    
    //Adapting all the menu items to the current object
    for(int i=0; i<menuItems.size(); i++)
    {
      MenuItem currentItem = (MenuItem) menuItems.get(i);
      currentItem.setCurrentObject(ob);
    }
    
    this.createConnectionButtons(ob);
    
    this.objects.activateConnectionMode(ob);
    super.activate();
  }
  
  public void deactivate()
  { 
    this.objects.deactivateConnectionMode();
    
    this.deleteConnectionItems();
    
    for(int i=0; i<menuItems.size(); i++)
    {
      MenuItem currentItem = (MenuItem) menuItems.get(i);
      currentItem.unsetCurrentObject();
    }
    super.deactivate();
  }
  
  public boolean pickAndRun()
  {
    if(!this.isActive())
    {
      return false;
    }
    
    MenuItem currentItem;
    
    for(int i=0; i<menuItems.size(); i++)
    {
       currentItem = (MenuItem) menuItems.get(i);
       
       picker.start(i);
       currentItem.renderHitArea(picker.buffer);
       picker.stop();
    }
    
    int id = picker.get(context.mouseX, context.mouseY);
    if (id != -1)
    {
      try
      {
        currentItem = (MenuItem) menuItems.get(id);
      }catch (Exception e)
      {
        System.out.println("Picker returned an error id in ThoughtMenu (line 203), ignoring");
        currentItem = null;
      }
      if(currentItem != null)
      {
        currentItem.run();
      }
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public boolean hit()
  {
    if(!this.isActive())
    {
      return false;
    }
    
    context.recorder = null;
    picker.buffer.background(0);
    
    MenuItem currentItem;
    
    cam.render(context);
    for(int i=0; i<menuItems.size(); i++)
    {
       currentItem = (MenuItem) menuItems.get(i);  
       picker.start(i);
       currentItem.renderHitArea(picker.buffer);
       picker.stop();
    }
    
    int id;
    
    try
    {
      id = picker.get(context.mouseX, context.mouseY);
    }
    catch(Exception e)
    {
      System.out.println("picker returned an error in ThoughtMenu (line 246), ignoring");
      id = -1;
    }
    
    if (id != -1)
    {
      try
      {
        currentItem = (MenuItem) menuItems.get(id);
      }catch (Exception e)
      {
        System.out.println("picker returned an error in ThoughtMenu (line 258), ignoring");
        currentItem = null;
      }
      if(currentItem != null)
      {
        currentItem.hitOn();
        for(int j=0; j<menuItems.size(); j++)
        {
          if (j != id)
          {
            MenuItem it = (MenuItem) menuItems.get(j);
            it.hitOff();
          }
        }
        return true;
      }
      return true;
    }
    else
    {
      for(int j=0; j<menuItems.size(); j++)
      {
        MenuItem it = (MenuItem) menuItems.get(j);
        it.hitOff();
      }
      return false;
    }
  }
  
   public void renderHitArea(Buffer buffer)
   {
    System.out.println("This should never run");
    //if(this.isActive())
    //{
    //  cam.render(buffer);
    //  buffer.pushMatrix();
    //  buffer.fill(255);
    //  buffer.stroke(255);
    //  buffer.translate(0,0,this.position.getPosition().z);
    //  buffer.ellipse(this.position.getPosition().x-8, this.position.getPosition().y-11, 20, 20);
    //  buffer.popMatrix();
    //  
    //  for(int i=0; i<menuItems.size(); i++)
    //  {
    //    MenuItem currentItem = (MenuItem) menuItems.get(i);
    //    
    //    //render node
    //    currentItem.renderHitArea();
    //  }
    //}
   }
}
