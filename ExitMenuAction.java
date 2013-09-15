import processing.core.PApplet;

class ExitMenuAction implements MenuAction
{
  private Thing currentObject;
  private PApplet context;
  
  ExitMenuAction(PApplet context)
  {
    this.context = context;
    this.currentObject = null;
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
      context.exit();
      return false;
  }
}
