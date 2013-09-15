import processing.core.PApplet;

public class TitleHUD extends HUDObject
{
  TitleHUD(PApplet context)
  {
    this.context=context;
    this.name = "Title";
    this.active = true;
    this.setPosition(15, 15, 0);
  }
  
  public boolean inFocus()
  {
    return false;
  }
  
  public void charKeyStroke(char key)
  {

  }
  
  public void keyStroke(int keyCode)
  {

  }
  
  public boolean hit(int x, int y)
  {
    return false;
  }
  
  public void render()
  { 
    context.textSize(15);
    context.text("The Network of Thoughts 0.5 Demo", this.position.getPosition().x, this.position.getPosition().y, this.position.getPosition().z);
   }
}
