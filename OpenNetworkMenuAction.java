import processing.core.PApplet;

class OpenNetworkMenuAction implements MenuAction
{
  private ObjectProvider objects;
  private Thing currentObject;
  private HUD hud;
  private PApplet context;
  private Camera cam;
  
  OpenNetworkMenuAction(PApplet context, ObjectProvider objects, HUD hud, Camera cam)
  {
    this.objects = objects;
    this.currentObject = null;
    this.hud = hud;
    this.context = context;
    this.cam = cam;
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
      HUDObject loginHud = this.hud.get("Login");
      HUDObject connectionsHud = this.hud.get("Connections");
      
      if (connectionsHud != null)
      {
          this.hud.deactivateObject("Connections");
      }
      
      if (loginHud == null)
      {
        this.hud.addObject(new LoginHUD(context, objects, cam));
        loginHud = this.hud.get("Login");
        this.hud.activateObject("Login");
      }
      else
      {
        this.hud.activateObject("Login");
      }
      
      LoginHUD loginHudCasted = (LoginHUD)loginHud;
      loginHudCasted.setAction(LoginHUD.AUTHENTICATE);
      
      return false;
  }
}
