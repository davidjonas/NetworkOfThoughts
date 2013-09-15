import processing.core.PApplet;
import processing.core.PImage;

public class ConnectionsHUD extends HUDObject
{
  private PImage closeImage;
  
  ConnectionsHUD(PApplet context, ColorConfiguration config)
  {
    this.context=context;
    this.name = "Connections";
    this.active = true;
    this.setPosition(0, 0, 0);
    this.closeImage = context.loadImage(config.iconConnectionsActive);
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
    if(this.isActive())
    {
      if( (x < context.width/2 - 152 -15 + 150 + 30)
          && (x > context.width/2 - 152 -15 + 150)
          && (y < 115 + 30)
          && (y > 115) )
      {
        this.deactivate();
        return true;
      }
    }
    return false;
  }
  
  public void render()
  { 
    context.textSize(13);
    String line1 = "With Connections mode, you can choose how you'd like the system";
    String line2 = "to connect and suggest thoughts to you";
    String line3 = "This feature is currently not available";
    context.text(line1, context.width/2 - context.textWidth(line1)/2, 200);
    context.text(line2, context.width/2 - context.textWidth(line2)/2, 220);
    context.text(line3, context.width/2 - context.textWidth(line3)/2, 260);
    context.image(this.closeImage, context.width/2 - 152 + 150 -15, 115, 30, 30);
   }
}
