import processing.core.PApplet;
import java.util.ArrayList;

//TODO: this class should be a Singleton
public class HUD
{
  private ArrayList objects;
  
  public HUD()
  {
    this.objects = new ArrayList();
  }
  
  public void addObject(HUDObject obj)
  {
    this.objects.add(obj);
  }
  
  public boolean isActive(String name)
  {
     return this.get(name).isActive();
  }
  
  public void activateObject(String name)
  {
    HUDObject hudObj = this.get(name);
    
    if (hudObj != null)
    {
        hudObj.activate();
    }
  }
  
  public void deactivateObject(String name)
  {
    HUDObject hudObj = this.get(name);
    
    if (hudObj != null)
    {
        hudObj.deactivate();
    }
  }
  
  public HUDObject get(int i)
  {
    return (HUDObject) objects.get(i);
  }
  
  public HUDObject get(String name)
  {
    HUDObject cur;
    for (int i=0; i<objects.size(); i++)
    {
       cur = (HUDObject) objects.get(i);
       if(cur.getName() == name)
       {
          return cur;
       }
    }
    return null;
  }
  
  public int size()
  {
    return objects.size();
  }
  
  public void render()
  {
    HUDObject cur;
    for (int i=0; i<objects.size(); i++)
    {
       cur = (HUDObject) objects.get(i);
       if(cur.isActive())
       {
         cur.render();
       }
    }
  }
}
