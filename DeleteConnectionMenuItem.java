import processing.core.PApplet;
import processing.core.PImage;
import picking.*;

class DeleteConnectionMenuItem extends MenuItem
{
  private int connectionA;
  private int connectionB;
  private ObjectProvider objects;
  
  DeleteConnectionMenuItem(PApplet context, ObjectProvider objects, ThoughtMenu parent, String title, Camera cam)
  {
    super(context, parent, title, cam);
    this.context = context;
    this.activate();
    this.title = title;
    this.config = ((NetworkOfThoughts)context).config;
    this.nodeImage = context.loadImage(config.connectionDeleteImage);
    this.nodeHitImage = context.loadImage(config.connectionDeleteImageHover);
    this.position = new PhysicalParameter(0,0,0,0,0,0);
    this.resetPhysicsToDefault();
    this.setAlpha(255);
    this.tintColor = context.color(context.random(0,255),context.random(0,255),context.random(0,255));
    this.action = null;
    this.cam = cam;
    this.parent = parent;
    this.objects = objects;
    this.connectionA = -1;
    this.connectionB = -1;
  }
  
  public void setConnection(int indexA, int indexB)
  {
    this.connectionA = indexA;
    this.connectionB = indexB;
    //set action
    if(this.action != null)
    {
      DeleteConnectionAction ac = (DeleteConnectionAction) this.action;
      ac.setConnection(indexA, indexB);
    }
  }
  
  public void setIndexA(int indexA)
  {
    this.connectionA = indexA;
    if(this.action != null)
    {
      DeleteConnectionAction ac = (DeleteConnectionAction) this.action;
      ac.setIndexA(indexA);
    }
  }
  
  public void setIndexB(int indexB)
  {
    this.connectionB = indexB;
    if(this.action != null)
    {
      DeleteConnectionAction ac = (DeleteConnectionAction) this.action;
      ac.setIndexB(indexB);
    }
  }
  
  public void setCurrentObject(Thing ob)
  {
    if (this.action != null)
    {
      this.action.setCurrentObject(ob);
      this.setIndexA(objects.idToIndex(ob.getId()));
    }
    
  }
  
  public void render()
  {
    //System.out.println("Rendering DeleteThoughtMenuItem");
    
    Point posA = objects.get(this.connectionA).position.getPosition();
    Point posB = objects.get(this.connectionB).position.getPosition();
    Point vector = posB.subtract(posA).getNormalizedVector();
    Point pos = this.parent.position.getPosition().add(vector.multiply(posA.distance(posB)/(float)1.5));
    this.position.setPosition(pos);
    
    context.pushMatrix();
    context.translate(0,0,this.position.getPosition().z);
    if (!this.hitState)
    {
      context.image(this.nodeImage, this.position.getPosition().x-(float)20.2, this.position.getPosition().y-(float)12.8, 11, 11);
    }
    else
    {
      context.image(this.nodeHitImage, this.position.getPosition().x-(float)20.2, this.position.getPosition().y-(float)12.8, 11, 11);
    }
    //context.fill(255,0,0);
    //context.ellipse(this.position.getPosition().x-5, this.position.getPosition().y-12, 15, 15);
    //context.fill(0);
    context.popMatrix();
    
    //debug
    //renderHitArea();
  }
  
  public void renderHitArea()
  {
    Point posA = objects.get(this.connectionA).position.getPosition();
    Point posB = objects.get(this.connectionB).position.getPosition();
    Point vector = posB.subtract(posA).getNormalizedVector();
    Point pos = this.parent.position.getPosition().add(vector.multiply(posA.distance(posB)/(float)1.5));
    this.position.setPosition(pos);
    
    context.pushMatrix();
    context.translate(0,0,this.position.getPosition().z);
    //context.image(this.nodeImage, this.position.getPosition().x, this.position.getPosition().y, 15, 15);
    context.fill(0);
    context.ellipse(this.position.getPosition().x-15, this.position.getPosition().y-8, 11, 11);
    context.popMatrix();
  }
  
  public void renderHitArea(Buffer buffer)
  {
    this.cam.render(buffer);
    
    Point posA = objects.get(this.connectionA).position.getPosition();
    Point posB = objects.get(this.connectionB).position.getPosition();
    Point vector = posB.subtract(posA).getNormalizedVector();
    Point pos = this.parent.position.getPosition().add(vector.multiply(posA.distance(posB)/(float)1.5));
    this.position.setPosition(pos);
    
    buffer.pushMatrix();
    buffer.translate(0,0,this.position.getPosition().z);
    //context.image(this.nodeImage, this.position.getPosition().x, this.position.getPosition().y, 15, 15);
    buffer.fill(255,0,0);
    buffer.ellipse(this.position.getPosition().x-15, this.position.getPosition().y-8, 11, 11);
    buffer.fill(0);
    buffer.popMatrix();
  }
  
  public void run()
  {
    if (this.action != null)
    {
      this.action.runAction();
      DeleteConnectionAction a = (DeleteConnectionAction)this.action;
      Thing ob = a.getCurrentObject();
      this.parent.deactivate();
      this.parent.activate(ob);
      
    }
  }
}
