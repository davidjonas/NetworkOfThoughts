import processing.core.PApplet;
import processing.core.PImage;

public class AddTextObjectHUD extends HUDObject
{
  private static final int NO_FOCUS = 0;
  private static final int TEXT_FOCUS = 1;

  private String txt;
  private ObjectProvider objects;
  private int mode;
  private ColorConfiguration config;
  private PImage popupNodeImage;
  private Camera cam;
  private ClipboardHelper clipboard;

  AddTextObjectHUD(PApplet context, ObjectProvider objects, Camera cam)
  {
    this.context=context;
    this.objects = objects;
    this.cam = cam;
    this.name = "AddTextObject";
    this.active = true;
    this.setPosition(context.width/2, context.height/2, 0);
    this.mode = NO_FOCUS;
    txt = "";
    this.config = ((NetworkOfThoughts)context).config;
    this.popupNodeImage = context.loadImage(config.popupNodeImage);
    this.clipboard = new ClipboardHelper();
  }
 
  public void focus()
  {
    mode = TEXT_FOCUS;
    this.cam.position.setDestination(new Point(0,-50,3000));
  }
  
  public void unfocus()
  {
    mode = NO_FOCUS;
    txt="";
    objects.setCurrentSearch("");
    objects.deleteAllTemporaryObjects();
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
      NetworkOfThoughts castContext = (NetworkOfThoughts)this.context;
      if ((key == 'v' || key == 'V') && castContext.controlKey)
      {
          txt += this.clipboard.getClipboardContents();
          return;
      }
      if((key >= ' ' && (int)key != 65535))
      {
        txt += key;
        if (key == ' ' || key == '.' || key == ',' || key == '!' || key == '?' || key == ':')
        {
          objects.setCurrentSearch(txt);
        }
      } 
    }
    else if(key == 'n' || key == 'N')
    {
      mode = TEXT_FOCUS;
    }
  }
  
  public void keyStroke(int keyCode)
  {
     if(inFocus() && txt != "" && keyCode == 10)
     {
       objects.addTextObject(txt);
       txt = "";
       objects.setCurrentSearch(txt);
       mode = NO_FOCUS;
       //this.objects.unSelectAll();  //WARNING: adds all temp thoughts into the world
       
       //Deletes the temp objects for now
       objects.deleteAllTemporaryObjects();
     }
     if(inFocus() && keyCode == 8)
     {
       if(txt.length() > 0)
               txt = txt.substring(0, txt.length() - 1);
     }
     if(inFocus() && keyCode == 27)
     {
       mode = NO_FOCUS;
       //this.objects.unSelectAll(); //WARNING: adds all temp thoughts into the world
       txt = "";
       objects.setCurrentSearch("");
       objects.deleteAllTemporaryObjects();
       if (objects.size() == 0)
       {
         objects.activateGlobalMenuMode();
       }
     }
  }
  
  public boolean hit(int x, int y)
  {
    //Deactivated because of picking.
    //We need to be able to pick objects while in insert mode so I deactivate this and add escape key to exit the mode
    /*if(inFocus())
    {
      mode = NO_FOCUS;
      objects.setCurrentSearch("");
      objects.deleteAllTemporaryObjects();
      return true;
    }
    else
    {
      return false;
    }*/
    return false;
  }
  
  public void render()
  {   
    if(inFocus())
    {
      ObjectCleaner cleaner = new ObjectCleaner();
      String msg = cleaner.cleanTextObject(txt);
      
      context.textSize(40);
      context.textAlign(context.LEFT, context.TOP);
      
      context.noStroke();
      
      //HERE GOES THE IMAGE RENDERING
      context.image(this.popupNodeImage, this.position.getPosition().x - 45 - context.textWidth(msg)/2, this.position.getPosition().y - 5, 40, 40);
      
//      context.fill(config.popupNode);
//      context.ellipse(this.position.getPosition().x - 20 - context.textWidth(msg)/2, this.position.getPosition().y + 10, 40, 40);
      
      context.fill(config.popupText);
     
      context.text(msg + "|", this.position.getPosition().x - context.textWidth(msg)/2, this.position.getPosition().y);
      context.textAlign(context.LEFT);
            
      //context.fill(255, 210);
      //context.stroke(0);
      //context.rect(context.width/2 - 150, context.height - 30, 300, 30);
      //context.stroke(200);
      //context.fill(0);
      //context.textSize(15);
      //context.text("Write your idea. press ENTER to send it.", context.width/2 - 140, context.height - 10);    //COMMENT FOR MINIMAL INTERCFACE
    }
  }
  
}

