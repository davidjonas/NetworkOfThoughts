import processing.core.PApplet;

class BackMenuAction implements MenuAction
{
  private Thing currentObject;
  private PApplet context;
  private HUD hud;
  
  BackMenuAction(PApplet context, HUD hud)
  {
    this.context = context;
    this.currentObject = null;
    this.hud = hud;
  }
  
  public void setCurrentObject(Thing object)
  {
    this.currentObject = object;
  }
  
  public void unsetCurrentObject()
  {
    this.currentObject = null;
  }
  
  public boolean runAction()
  {
      hud.deactivateObject("Login");
      hud.deactivateObject("Connections");
      return true;
  }
}
