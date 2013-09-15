import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PFont;
import picking.*;

public class TextObject extends Thing
{
  private String textstr;
  private boolean setFont;
  private boolean hover;
  private boolean activeState;
  private int fontSize;
  private PFont font;
  private ColorConfiguration config;
  private PImage nodeImage;
  private PImage popupNodeImage;
  private PImage nodeImageHover;
  private PImage nodeImageActive;
    
  TextObject(PApplet context, String txt, String user, long timestamp, int id, int fontSize, PFont font)
  {
    this.activate();
    this.textstr = txt;
    this.fontSize = fontSize;
    this.font = font;
    this.setFont = true;
    this.position = new PhysicalParameter(0, 0, 0, 0, 0, 0);
    this.resetPhysicsToDefault();
    this.setPos = false;
    this.setContext(context);
    this.setAlpha(255);
    this.setTimestamp(timestamp);
    this.setMode(IDLE);
    this.setId(id);
    this.config = ((NetworkOfThoughts)context).config;
    this.nodeImage = context.loadImage(config.nodeImage);
    this.popupNodeImage = context.loadImage(config.popupNodeImage);
    this.nodeImageHover = context.loadImage(config.nodeImageHover);
    this.nodeImageActive = context.loadImage(config.nodeImageActive);
    this.hover = false;
    this.activeState = false;
  }
  
  TextObject(PApplet context, String txt, String user, long timestamp, int id)
  {
    this.activate();
    this.textstr = txt;
    this.setUser(user);
    this.fontSize = 15;
    this.setFont = false;
    this.position = new PhysicalParameter(0, 0, 0, 0, 0, 0);
    this.resetPhysicsToDefault();
    this.setPos = false;
    this.setContext(context);
    this.setAlpha(255);
    this.setTimestamp(timestamp);
    this.setMode(IDLE);
    this.setId(id);
    this.config = ((NetworkOfThoughts)context).config;
    this.nodeImage = context.loadImage(config.nodeImage);
    this.popupNodeImage = context.loadImage(config.popupNodeImage);
    this.nodeImageHover = context.loadImage(config.nodeImageHover);
    this.nodeImageActive = context.loadImage(config.nodeImageActive);
    this.hover = false;
    this.activeState = false;
  }
  
  public void setHover(boolean value)
  {
    this.hover = value;
  }
  
  public void setActiveState(boolean value)
  {
    this.activeState = value;
  }
  
  public void resetPhysicsToDefault()
  {
    this.position.setMaxSpeed((float)40);
    this.position.setSpring((float)0);
    this.position.setDrag((float)1);
  }
  
  public String getText()
  {
    return textstr;
  }
  
  //TODO: WROOONG... set position and call render()... Maybe not, its goot to render out of position for interface design reasons
   public void render(float x, float y, float z)
   {
     if(this.active)
     {
        if(this.setFont)
        {
          this.context.textFont(font, fontSize);
        }
        this.context.text(textstr, x, y, z);
     }
   }
   
   public void render(float x, float y)
   {
     if(this.active)
     {
        if(this.setFont)
        {
          this.context.textFont(font, fontSize);
        }       
        this.context.text(textstr, x, y);
     }
   }
   
   public void render()
   {
     if(this.active)
     {
       context.pushMatrix();
       context.translate(0,0,this.position.getPosition().z);
       if(this.getMode() != TEMPORARY)
       {
        if(this.activeState)
        {
          //HERE GOES THE IMAGE RENDERING
          context.tint(255, this.getAlpha());
          context.image(this.nodeImageActive, this.position.getPosition().x-26, this.position.getPosition().y-19, 23, 23);
          context.tint(255);
        }else if (!this.hover)
         {
          //HERE GOES THE IMAGE RENDERING
          context.tint(255, this.getAlpha());
          context.image(this.nodeImage, this.position.getPosition().x-26, this.position.getPosition().y-19, 23, 23);
          context.tint(255);
         }
         else
         {
          //HERE GOES THE IMAGE RENDERING
          context.tint(255, this.getAlpha());
          context.image(this.nodeImageHover, this.position.getPosition().x-26, this.position.getPosition().y-19, 23, 23);
          context.tint(255);
         }
       }else
       {
         //HERE GOES THE IMAGE RENDERING
         context.tint(255, this.getAlpha());
         context.image(this.popupNodeImage, this.position.getPosition().x-26, this.position.getPosition().y-19, 23, 23);
         context.tint(255);
       }
       
   

       
       /*int lineHeight = context.round(context.textAscent()+context.textDescent())+5;
       int nLines = textstr.split("\n").length;
       context.fill(255,255, 255, 50);
       context.noStroke();
       context.rect(this.position.getPosition().x-3, this.position.getPosition().y-context.textAscent()-5, context.textWidth(this.textstr)+5, lineHeight*nLines+1);*/
       context.fill(config.text);
       context.popMatrix();
       
       if(this.getMode() == TEMPORARY)
       {
          context.fill(config.popupText);
       }
       else
       {
          context.fill(context.red(config.text),context.green(config.text),context.blue(config.text),this.getAlpha());
          //context.fill(0,0,0);
       }
       
       if(this.setFont)
       {
          this.context.textFont(font, fontSize);
       } 
       
       /*if(this.getMode() == SELECTED)
       {
         this.context.textSize(fontSize + 5);
       }
       else
       {
         this.context.textSize(fontSize); //TODO: DO NOT SET ALLWAYS, ONLY WHEN NECESSARY
       }*/
       
       this.context.textSize(fontSize); //TODO: DO NOT SET ALLWAYS, ONLY WHEN NECESSARY
       
       this.context.text(textstr, this.position.getPosition().x, this.position.getPosition().y, this.position.getPosition().z);
     }
   }
   
   
   public void render(PGraphics context)
   { 
     context.background(255);
     if(this.active)
     {
       if(this.getMode() != TEMPORARY)
       {
         context.fill(config.normalNode);
       }else
       {
         context.fill(config.popupNode);
       }
       //context.ellipse(this.position.getPosition().x-5, this.position.getPosition().y-12, 10, 10);
       //HERE GOES THE IMAGE RENDERING
       context.tint(255, this.getAlpha());
       context.image(this.nodeImage, 0, 0, 20, 20);
       context.tint(255);
       
       if(this.getMode() == TEMPORARY)
       {
          context.fill(config.popupText);
       }
       else
       {
          context.fill(context.red(config.text),context.green(config.text),context.blue(config.text),this.getAlpha());
          //context.fill(0,0,0);
       }
       
       if(this.setFont)
       {
          context.textFont(font, fontSize);
       } 
       
       context.textSize(fontSize); //TODO: DO NOT SET ALLWAYS, ONLY WHEN NECESSARY
       
       context.text(textstr, 15, 18, 0);
     }
   }
   
   
   public void renderHitArea()
   {
     this.context.textSize(fontSize); //TODO: DO NOT SET ALLWAYS, ONLY WHEN NECESSARY
     context.pushMatrix();
     context.translate(0,0,this.position.getPosition().z);
     context.fill(100,100,100,50);
     context.ellipse(this.position.getPosition().x-10, this.position.getPosition().y-10, 40, 40);
     int lineHeight = context.round(context.textAscent()+context.textDescent())+5;
     int nLines = textstr.split("\n").length;
     context.rect(this.position.getPosition().x, this.position.getPosition().y-context.textAscent(), context.textWidth(this.textstr), lineHeight*nLines+1);
     context.fill(0);
     context.popMatrix();
   }
   
   public void renderHitArea(Buffer buffer)
   {    
     this.context.textSize(fontSize); //TODO: DO NOT SET ALLWAYS, ONLY WHEN NECESSARY
     buffer.pushMatrix();
     buffer.translate(0,0,this.position.getPosition().z);
     buffer.ellipse(this.position.getPosition().x-5, this.position.getPosition().y-5, 40, 40);
     int lineHeight = context.round(context.textAscent()+context.textDescent())+5;
     int nLines = textstr.split("\n").length;
     buffer.rect(this.position.getPosition().x, this.position.getPosition().y-context.textAscent(), context.textWidth(this.textstr), lineHeight*nLines+1);
     buffer.fill(0);
     buffer.popMatrix();
   }
}


