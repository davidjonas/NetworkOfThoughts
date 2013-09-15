import picking.Buffer;
import processing.core.PApplet;
import processing.core.PImage;

class GlobalMenuItem extends MenuItem
{
  protected PImage icon;
  protected PImage iconActive;
  
  GlobalMenuItem(PApplet context, ThoughtMenu parent, String title, Camera cam, PImage icon, PImage iconActive)
  {
    super(context, parent, title, cam);
    this.icon = icon;
    this.iconActive = iconActive;
  }
  
  public void render()
  {
     if(this.active)
     {
      if(this.hitState)
      {
          context.image(this.iconActive, this.position.getPosition().x-15, this.position.getPosition().y-15, 30, 30);
      }
      else
      {
          context.image(this.icon, this.position.getPosition().x-15, this.position.getPosition().y-15, 30, 30);
      }
      this.context.textSize(13);
       
      context.fill(context.red(config.text),context.green(config.text),context.blue(config.text),this.getAlpha());
      this.context.text(title, this.position.getPosition().x - (context.textWidth(title)/2), this.position.getPosition().y + 30);
     }
  }
  
  public void renderHitArea()
  { 
    context.ellipse(this.position.getPosition().x, this.position.getPosition().y, 30, 30);
  }
  
  public void renderHitArea(Buffer buffer)
  {
    buffer.camera();    
    buffer.ellipse(this.position.getPosition().x, this.position.getPosition().y, 30, 30);
  }
}
