class GlobalMenuAction implements MenuAction
{
  private ObjectProvider objects;
  private Thing currentObject;
  
  GlobalMenuAction(ObjectProvider objects)
  {
    this.objects = objects;
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
    if (currentObject != null)
    {
      objects.activateGlobalMenuMode();
    }
    
    return true;
  }
}
