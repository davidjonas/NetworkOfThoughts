import processing.core.PApplet;
import processing.core.PGraphics3D;

public class Camera
{
  private static final int CHASE_MODE = 0;
  private static final int NORMAL_MODE = 1;
 
  //Camera position
  public PhysicalParameter position;
  //Camera target
  public PhysicalParameter target;
  //Camera up
  public PhysicalParameter up;
  //Camera mode
  private int mode;
  private int stepCounter;
  private Thing chaseObj;
  private int chaseSteps;

  Camera()
  {
    position = new PhysicalParameter(0,0,0,0,0,0);
    target = new PhysicalParameter(0,0,0,0,0,0);
    up = new PhysicalParameter(0, 1, 1, 50, 0, (float)3);
    resetPhysicsToDefault();
    this.mode = NORMAL_MODE;
    this.stepCounter = 0;
    this.chaseSteps = 0;
  }
  
  Camera(float px, float py, float pz)
  {
    position = new PhysicalParameter(px,py,pz,0,0,0);
    target = new PhysicalParameter(px,py,pz,0,0,0);
    up = new PhysicalParameter(0, 1, 1, 50, 0, (float)3);
    resetPhysicsToDefault();
    this.mode = NORMAL_MODE;
    this.stepCounter = 0;
    this.chaseSteps = 0;
  }
  
  public void resetPhysicsToDefault()
  {
    position = new PhysicalParameter(this.position.getPosition().x,this.position.getPosition().y,
                                     this.position.getPosition().z,
                                     40,
                                     0,
                                     (float)7.2);
    target = new PhysicalParameter(this.position.getPosition().x,
                                   this.position.getPosition().y,
                                   this.position.getPosition().z-10000,
                                   40,
                                   0,
                                   (float)7.2);
    up = new PhysicalParameter(0, 1, 1, 50, 0, (float)3);
  }
  
  public int getMode()
  {
    return this.mode;
  }
  
  public void chaseFor(Thing obj, int steps)
  {
    this.chaseObj = obj;
    this.stepCounter = 0;
    this.mode = CHASE_MODE;
    this.chaseSteps = steps;
  }

  public void chase(Thing obj)
  {
    this.chaseFor(obj, -1);
  }
  
  public void stopChase()
  {
    this.chaseObj = null;
    this.mode = NORMAL_MODE;
    this.chaseSteps = 0;
  }
  
  public void setPosition(float x, float y, float z)
  {
    this.position.setPosition(x, y, z);
    this.target.setPosition(x,y,z-10000);
  }
  
  public void setTarget(float x, float y, float z)
  {
    this.target.setPosition(x, y, z);
  }
  
  public void setUp(float x, float y, float z)
  {
     this.up.setPosition(x, y, z);
  }
  
  public void step(float x, float y, float z)
  {
    if(this.mode == CHASE_MODE)
    {
      if(this.stepCounter < this.chaseSteps || this.chaseSteps == -1)
      {
        if(this.chaseSteps != -1)
        {
          this.stepCounter++;
        }
        
        this.position.setDestination(new Point(this.chaseObj.position.getPosition().x, this.chaseObj.position.getPosition().y, this.chaseObj.position.getPosition().z + 200));
      }
      else
      {
        this.stopChase();
        this.position.clearDestination();
      }
    }
    
    this.position.step(x, y, z);
    this.target.setPosition(position.getPosition().x, position.getPosition().y, position.getPosition().z-10000);
    
  }
  
  public void render(PGraphics3D context)
  {
    context.camera(position.pX, position.pY, position.pZ, target.pX, target.pY, target.pZ, up.pX, up.pY, up.pZ);
  }
  
  public void render(PApplet context)
  {
    context.camera(position.pX, position.pY, position.pZ, target.pX, target.pY, target.pZ, up.pX, up.pY, up.pZ);
  }
}
