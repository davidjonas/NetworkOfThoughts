import processing.core.PApplet;

public interface MenuAction
{
  public boolean runAction();
  public void setCurrentObject(Thing object);
  public void unsetCurrentObject();
}
