import processing.core.PApplet;
import processing.core.PImage;

//TODO: this class should be a Singleton
class GlobalMenu extends ThoughtMenu
{
  protected ColorConfiguration config;
  protected PImage logoImage;
  
  GlobalMenu(PApplet context, Camera cam, ObjectProvider objects)
  {
    super(context, cam, objects);
    this.setId(0);
    this.config = ((NetworkOfThoughts)context).config;
    this.logoImage = context.loadImage(config.menuLogo);
    this.position.setPosition(0,0,0);
  }

  public void deactivate()
  {
    this.active = false;
  }
  
  public void step()
  {
    if(this.isActive())
    {
      //Check for mouse hover
      this.hit();
      
      //Setting positions
      for (int i=0; i<menuItems.size(); i++)
      {
        MenuItem currentItem = (MenuItem) menuItems.get(i);
        currentItem.position.setPosition(context.width/2 - 152 + (i*75), 130 ,0);
      }
    }
  }
  
  public void render()
  {
    if(this.isActive())
    {
      context.camera();
      context.image(this.logoImage, context.width/2 - 50, 40, 100, 50);
      
      for(int i=0; i<menuItems.size(); i++)
      { 
        MenuItem currentItem = (MenuItem) menuItems.get(i);
        
        //render node
        currentItem.render();
        //currentItem.renderHitArea();
      }
    }
  }  
}
