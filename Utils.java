
final class Utils
{
  public static final float pi = (float)3.14159;
  
  public static float RAD(float deg)
  {
    return deg * (pi / (float)180.0);
  }
  
  public static float DEG(float rad)
  {
    return rad * ((float)180.0 / pi);
  }
}
