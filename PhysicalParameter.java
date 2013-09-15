//3D Physical Parmeter class
public class PhysicalParameter
{
  //position
  public float pX, pY, pZ;
  //acelleration
  private float aX, aY, aZ;
  //speed
  private float sX, sY, sZ;
  
  private Point destination;

  //properties
  private float maxSpeed, spring, drag;

  PhysicalParameter(float pX, float pY, float pZ, float maxSpeed, float spring, float drag)
  {
    this.pX = pX;
    this.pY = pY;
    this.pZ = pZ;

    this.aX = aX;
    this.aY = aY;
    this.aZ = aZ;

    this.maxSpeed = maxSpeed;
    this.spring = spring;
    this.drag = drag;
    
    this.destination = null;
  }

  public void setPosition(float x, float y, float z)
  {
    this.pX = x;
    this.pY = y;
    this.pZ = z;
  }
  
  public void setMaxSpeed(float value)
  {
    this.maxSpeed = value;
  }
  
  public void setDrag(float value)
  {
    this.drag = value;
  }
  
  public void setSpring(float value)
  {
    this.spring = value;
  }
  
  public void setDestination(Point d)
  {
    this.destination = d;
  }
  
  public Point getDestination()
  {
    return this.destination;
  }
  
  public void clearDestination()
  {
    this.destination = null;
  }
  
  public float distanceFromDestination()
  {
    if(this.getDestination() != null)
    {
      return this.getPosition().distance(this.getDestination());
    }
    else
    {
      return (float) -1;
    }
  }
  
  public void setPosition(Point a)
  {
    this.pX = a.x;
    this.pY = a.y;
    this.pZ = a.z;
  }
  
  private float calculateDragComponent(float a)
  {
    float d = 0;

    if (a == 0) return 0;

    if(a >= drag)
    {  
      d = -drag;
    }
    else if(a <= -drag)
    {
      d = drag;
    }
    else
    {
      d = -a;
    }

    return d;
  }
  
  public Point getPosition()
  {
    return new Point(this.pX, this.pY, this.pZ);
  }
  
  
  public void step(float fX, float fY, float fZ)
  {
    
    if(this.destination != null)
    {
      Point force = new Point(fX, fY, fZ);
      Point position = new Point(pX, pY, pZ);
      
      float destDistance = position.distance(this.destination);
      
      if (destDistance < 6)
      {
        this.clearDestination();
      }
      else
      {
         float destStrength = (destDistance/5);
         if(destStrength > 13)
         {
           destStrength = 13;
         }
         Point destAtraction = destination.subtract(position).getNormalizedVector().multiply(destStrength);
         Point finalStrength = force.add(destAtraction);
         fX = finalStrength.x;
         fY = finalStrength.y;
         fZ = finalStrength.z;
      }
    } 
    
    float dX, dY, dZ;
    dX = this.calculateDragComponent(sX);
    dY = this.calculateDragComponent(sY);
    dZ = this.calculateDragComponent(sZ);

    aX = fX;
    aY = fY;
    aZ = fZ;

    sX += aX + dX;
    sY += aY + dY;
    sZ += aZ + dZ;

    if(sX > maxSpeed)
      sX = maxSpeed;
    if(sY > maxSpeed)
      sY = maxSpeed;
    if(sZ > maxSpeed)
      sZ = maxSpeed;
      
    if(sX < -maxSpeed)
      sX = -maxSpeed;
    if(sY < -maxSpeed)
      sY = -maxSpeed;
    if(sZ < -maxSpeed)
      sZ = -maxSpeed;

    pX += sX;
    pY += sY;
    pZ += sZ;
  }
}

