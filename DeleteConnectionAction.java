class DeleteConnectionAction implements MenuAction
{
  private ObjectProvider objects;
  private Thing currentObject;
  private int connectionA;
  private int connectionB;
  
  DeleteConnectionAction(ObjectProvider objects)
  {
    this.objects = objects;
    this.currentObject = null;
  }
  
  public void setConnection(int indexA, int indexB)
  {
    this.connectionA = indexA;
    this.connectionB = indexB;
  }
  
  public void setIndexA(int indexA)
  {
    this.connectionA = indexA;
  }
  
  public void setIndexB(int indexB)
  {
    this.connectionB = indexB;
  }
  
  public void setCurrentObject(Thing object)
  {
    this.currentObject = object;
  }
  
  public Thing getCurrentObject()
  {
    return this.currentObject;
  }
  
  public void unsetCurrentObject()
  {
    this.currentObject = null;
  }
  
  public boolean runAction()
  {
    if (currentObject != null)
    {
      objects.deleteConnection(connectionA, connectionB);
    }
    
    return false;
  }
}
