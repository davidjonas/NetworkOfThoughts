public class Point
{
   public float x;
   public float y;
   public float z;
   
   Point()
   {
     this.x = 0;
     this.y = 0;
     this.z = 0;
   }
   
   Point(float x, float y, float z)
   {
     this.x = x;
     this.y = y;
     this.z = z;
   }
   
   public void set(float x, float y, float z)
   {
     this.x = x;
     this.y = y;
     this.z = z;
   }
   
   public void set(Point a)
   {
     this.x = a.x;
     this.y = a.y;
     this.z = a.z;
   }
   
   public Point add(Point a)
   {
     return new Point(this.x + a.x, this.y + a.y, this.z + a.z);
   } 
   
   public Point subtract(Point a)
   {
     return new Point(this.x - a.x, this.y - a.y, this.z - a.z);
   }
   
   public Point multiply(Point a)
   {
     return new Point(this.x * a.x, this.y * a.y, this.z * a.z);
   }
   
   public Point multiply(float c)
   {
     return new Point(this.x * c, this.y * c, this.z * c);
   }
   
   public Point divide(Point a)
   {
     return new Point(this.x / a.x, this.y / a.y, this.z / a.z);
   }
   
   public Point x(Point b)
   {
     float x = this.y * b.z - this.z * b.y;
     float y = this.z * b.x - this.x * b.z;
     float z = this.x * b.y - this.y * b.x;
     
     return new Point(x, y, z);
   }
   
   public float distance(Point a)
   {
     return (float) Math.sqrt((a.x-this.x)*(a.x-this.x) + (a.y-this.y)*(a.y-this.y) + (a.z-this.z)*(a.z-this.z));
   }
   
   public double getVectorLength()
   {
    return Math.sqrt( this.x*this.x + this.y*this.y + this.z*this.z );
   }
  
   public Point getNormalizedVector()
   {
    double len = this.getVectorLength();
    return new Point((float)(this.x/len), (float)(this.y/len), (float)(this.z/len));
  }
}
