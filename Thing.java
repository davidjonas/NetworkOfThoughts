import processing.core.PApplet;
import picking.*;

public abstract class Thing implements Renderable
{
  public static final int IDLE = 0;
  public static final int SELECTED = 1;
  public static final int TEMPORARY = 2;
  public static final int NON_CONNECTABLE = 3;
  
  protected int mode;
  protected boolean active;
  protected boolean setPos;
  protected PApplet context;
  public PhysicalParameter position;
  private String user;
  private int alpha;
  private long timestamp;
  private int id;

  public abstract void render(float x, float y, float z);

  public abstract void render(float x, float y);

  public abstract void render();
  
  public abstract void renderHitArea(Buffer buffer);
  
  public abstract void renderHitArea();
  
  public abstract void resetPhysicsToDefault();
  
  public void step(float fx, float fy, float fz)
  {
    this.position.step(fx, fy, fz);
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public void setMode(int m)
  {
    this.mode = m;
  }
  
  public int getMode()
  {
    return this.mode;
  }
  
  public void step(Point a)
  {
    this.position.step(a.x, a.y, a.z);
  }
  
  public String getUser()
  {
    return this.user;
  }
  
  public void setUser(String user)
  {
    this.user = user;
  }

  public void setAlpha(int a)
  {
    this.alpha = a;
  }
  
  public int getAlpha()
  {
    return this.alpha;
  }
  
  public void setTimestamp(long time)
  {
    this.timestamp = time;
  }
  
  public long getTimestamp()
  {
    return this.timestamp;
  }

  public void deactivate()
  {
    this.active = false;
  }

  public void activate()
  {
    this.active = true;
  }

  public boolean isActive()
  {  
    return active;
  }

  public void setContext(PApplet context)
  {
    this.context = context;
  }

  public void setPosition(float x, float y, float z)
  { 
    if(this.position == null)
    {
      this.position = new PhysicalParameter(x, y, z, 50, 0, (float)3);
    } 
    this.position.setPosition(x, y, z);
    this.setPos = true;
  }

  public void setPosition(Point a)
  {  
    if(this.position == null)
    {
      this.position = new PhysicalParameter(a.x, a.y, a.z, 50, 0, (float)3);
    } 
    this.position.setPosition(a);
    this.setPos = true;
  }
}

