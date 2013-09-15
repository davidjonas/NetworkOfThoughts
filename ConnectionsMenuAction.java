import processing.core.PApplet;

class ConnectionsMenuAction implements MenuAction
{
  private Thing currentObject;
  private HUD hud;
  private PApplet context;
  private ColorConfiguration config;
  
  ConnectionsMenuAction(PApplet context, HUD hud, ColorConfiguration config)
  {
    this.currentObject = null;
    this.hud = hud;
    this.context = context;
    this.config = config;
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
      HUDObject connectionsHud = this.hud.get("Connections");
      HUDObject loginHud = this.hud.get("Login");
      
      if (loginHud != null)
      {
        this.hud.deactivateObject("Login");
      }
      
      if (connectionsHud == null)
      {
        this.hud.addObject(new ConnectionsHUD(context, config));
        this.hud.activateObject("Connections");
      }
      else
      {
        connectionsHud.activate();
      }
      
      return false;
  }
}