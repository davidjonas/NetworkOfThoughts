import picking.*;

abstract class HUDObject extends Thing
{
  String name;
  
  public String getName()
  {
    return this.name;
  }
  
  public void render(float x, float y, float z)
  {
    render();
  }
  
  public void render(float x, float y)
  {
    render();    
  }
  
  public void renderHitArea()
  {
  }
  
  public void renderHitArea(Buffer buffer)
  {
  }
  
  public void resetPhysicsToDefault()
  {
  }
  
  public abstract void render();
  
  public abstract boolean inFocus();
  
  public abstract void charKeyStroke(char key);
  
  public abstract void keyStroke(int keyCode);
  
  public abstract boolean hit(int x, int y);
}
