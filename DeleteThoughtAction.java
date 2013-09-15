

class DeleteThoughtAction implements MenuAction
{
  private ObjectProvider objects;
  private Thing currentObject;
  
  DeleteThoughtAction(ObjectProvider objects)
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
      int index = objects.idToIndex(currentObject.getId());
      objects.permanentDeleteObject(index);
    }
    
    return true;
  }
  
}
