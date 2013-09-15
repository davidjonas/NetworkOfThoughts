import processing.core.PApplet;
import processing.core.PImage;
import picking.*;

class MenuItem extends Thing
{
  protected String title;
  protected PImage nodeImage;
  protected PImage nodeHitImage;
  protected ColorConfiguration config;
  protected int tintColor;
  protected MenuAction action;
  protected Camera cam;
  protected ThoughtMenu parent;
  protected boolean hitState;
  
  MenuItem(PApplet context, ThoughtMenu parent, String title, Camera cam)
  {
    this.context = context;
    this.activate();
    this.title = title;
    this.config = ((NetworkOfThoughts)context).config;
    this.nodeImage = context.loadImage(config.nodeMenuImage);
    this.nodeHitImage = context.loadImage(config.nodeHitImage);
    this.position = new PhysicalParameter(0,0,0,0,0,0);
    this.resetPhysicsToDefault();
    this.setAlpha(255);
    //this.tintColor = context.color(context.random(0,255),context.random(0,255),context.random(0,255));
    this.tintColor = context.color(255);
    this.action = null;
    this.cam = cam;
    this.parent = parent;
    this.hitState = false;
  }
  
  public void render(float x, float y, float z)
  {
  }
  
  public void render(float x, float y)
  {
  }
  
  public void setAction(MenuAction action)
  {
      this.action = action;
  }
  
  public void hitOn()
  {
    context.cursor(context.HAND);
    this.hitState = true;
  }
  
  public void hitOff()
  {
    context.cursor(context.ARROW);
    this.hitState = false;
  }
  
  public void setCurrentObject(Thing ob)
  {
    if (this.action != null)
    {
      this.action.setCurrentObject(ob);
    }
  }
  
  public void unsetCurrentObject()
  {
    if (this.action != null)
    {
      this.action.unsetCurrentObject();
    }
  }
  
  public void run()
  {
    if (this.action != null)
    {
      if(this.action.runAction())
      {
        parent.deactivate();
      }
    }
    else
    {
        parent.deactivate();
    }
  }
  
  public boolean isActive()
  {
    return true;
  }
  
  public void render()
  {
    if(this.active)
     {
       context.pushMatrix();
       context.translate(0,0,this.position.getPosition().z);
       context.tint(tintColor);
       if(!this.hitState)
       {
        context.image(this.nodeImage, this.position.getPosition().x, this.position.getPosition().y, 7, 7);
       }
       else
       {
        context.image(this.nodeHitImage, this.position.getPosition().x, this.position.getPosition().y, 7, 7);
       }
       context.tint(255);
       context.popMatrix();
       
       this.context.textSize(6);
       
       context.fill(context.red(config.text),context.green(config.text),context.blue(config.text),this.getAlpha());
       this.context.text(title, this.position.getPosition().x - (context.textWidth(title) + 3), this.position.getPosition().y + 5, this.position.getPosition().z);
     }
  }
  
  public void renderHitArea()
  {
    int height = 7;
    int width = (int)context.textWidth(title) + 10;
    
    context.fill(0,0,0,80);
    context.pushMatrix();
    context.translate(0,0,this.position.getPosition().z);
    context.rect(this.position.getPosition().x - width + 7, this.position.getPosition().y, width, height);
    context.popMatrix();
  }
  
  public void resetPhysicsToDefault()
  {
    this.position.setMaxSpeed((float)20);
    this.position.setSpring((float)0);
    this.position.setDrag((float)3);
  }
  
  public void renderHitArea(Buffer buffer)
  {    
    cam.render(buffer);
    int height = 7;
    int width = (int)context.textWidth(title) + 10;
    
    buffer.pushMatrix();
    buffer.translate(0,0,this.position.getPosition().z);
    buffer.rect(this.position.getPosition().x - width + 7, this.position.getPosition().y, width, height);
    buffer.popMatrix();
  }
}
