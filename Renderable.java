import processing.core.PApplet;

public interface Renderable
{ 
  public void render(float x, float y, float z);
  public void render(float x, float y); 
  public boolean isActive();
}
