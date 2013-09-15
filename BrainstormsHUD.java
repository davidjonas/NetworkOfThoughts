import processing.core.PApplet;

public class BrainstormsHUD extends HUDObject
{  
  private static final int NO_FOCUS = 0;
  private static final int TEXT_FOCUS = 1;
  
  private String brainstormName;
  private ObjectProvider objects;
  private int mode;
  
  BrainstormsHUD(PApplet context, ObjectProvider objects)
  {
    this.context=context;
    this.objects = objects;
    this.name = "Brainstorms";
    this.active = true;
    this.setPosition(context.width/2, context.height/2, 0);
    this.mode = NO_FOCUS;
    this.brainstormName = "";
  } 
  
  public void render()
  {
    
  }
  
   public void focus()
  {
    mode = TEXT_FOCUS;
  }
  
  public boolean inFocus()
  {
    if(mode == NO_FOCUS)
    {
      return false;
    }
    else
    {
      return true;
    }
  }
  
  public void charKeyStroke(char key)
  {
    if(inFocus())
    {
      if((key >= ' ' && key <= '~'))
      {
        brainstormName += key;
      } 
    }
  }
  
  public void keyStroke(int keyCode)
  {
     if(inFocus() && brainstormName != "" && keyCode == 10)
     {
       objects.addTextObject(brainstormName);
       brainstormName = "";
       mode = NO_FOCUS;
     }
     if(inFocus() && keyCode == 8)
     {
       if(brainstormName.length() > 0)
               brainstormName = brainstormName.substring(0, brainstormName.length() - 1);
     }
  }
  
  public boolean hit(int x, int y)
  {
    if(inFocus())
    {
      mode = NO_FOCUS;
      return true;
    }
    else
    {
      return false;
    }
  }
}
