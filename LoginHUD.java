import processing.core.PApplet;
import processing.core.PImage;

public class LoginHUD extends HUDObject
{  
  private static final int NO_FOCUS = 0;
  private static final int LOGIN_FOCUS = 1;
  private static final int PASSWORD_FOCUS = 2;
  private static final int LOGGED_IN = 3;
  private static final int CLOSED = 4;
  private static final int BAD_LOGIN = 5;
  
  public static final int AUTHENTICATE = 0;
  public static final int CREATE = 1;
 
  private String login;
  private String password;
  private DAL server;
  private ObjectProvider objects;
  private int mode;
  private int action;
  private int width;
  private int height;
  private String titleMsg;
  private Camera cam;
  private ColorConfiguration config;
  private PImage nodeImageNew;
  private PImage nodeImageOpen;
  private PImage textFieldImage;
  private PImage applyButton;
  private PImage applyButtonActive;
  
  
  LoginHUD(PApplet context, ObjectProvider obj, Camera cam)
  {
    this.context=context;
    this.server = obj.getDAL();
    this.objects = obj;
    this.name = "Login";
    this.active = true;
    this.setPosition(context.width/2 - 206, 150, 0);
    this.width = 440;
    this.height = 200;
    this.mode = LOGIN_FOCUS;
    this.login = "";
    this.password = "";
    this.action = AUTHENTICATE;
    titleMsg = "Choose a name and password for the new network.";
    this.cam = cam;
    this.config = ((NetworkOfThoughts)context).config;
    this.nodeImageNew = context.loadImage(config.iconNewActive);
    this.nodeImageOpen = context.loadImage(config.iconOpenActive);
    this.textFieldImage = context.loadImage(config.textFieldImage);
    this.applyButton = context.loadImage(config.iconApply);
    this.applyButtonActive = context.loadImage(config.iconApplyActive);
  }
  
  //These two setters are for automatic login feature.
  public void setLogin(String loginStr)
  {
    this.login = loginStr;
  }
  
  public void setPassword(String passwordStr)
  {
    this.password = passwordStr;
  }
  
  public void setAction(int ac)
  {
    this.action = ac;
    if(this.action == AUTHENTICATE)
    {
      this.titleMsg = "";
    }
    else if(this.action == CREATE)
    {
      this.titleMsg = "";
    }
  }
  
  public boolean inFocus()
  {
      if(mode == NO_FOCUS || mode == LOGGED_IN || mode == CLOSED)
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
    if(mode != LOGGED_IN && mode != CLOSED)
    {
      if((key >= '0' && key <= 'z') || (key == ' '))
      {
        if(mode == LOGIN_FOCUS && login.length() < 20)
        {
           login += key;
        }
        else if (mode == PASSWORD_FOCUS && password.length() < 20)
        {
           password += key;
        }
      }
      if((int)key == 9)
      {
        if(mode == LOGIN_FOCUS)
        {
           mode = PASSWORD_FOCUS;
        }
        else if (mode == PASSWORD_FOCUS)
        {
           mode = LOGIN_FOCUS;
        } 
      }
    }
  }
  
  public void keyStroke(int keyCode)
  {
    if(mode != LOGGED_IN && mode != CLOSED)
    {
      switch(keyCode)
      {
        case 8: 
          if(mode == LOGIN_FOCUS)
          {
             if(login.length() > 0)
               login = login.substring(0, login.length() - 1);
          }
          else if (mode == PASSWORD_FOCUS)
          {
             if(password.length() > 0)
               password = password.substring(0, password.length() - 1);
          }
          break;
        case 9:
          if(mode == LOGIN_FOCUS)
          {
             mode = PASSWORD_FOCUS;
          }
          else if (mode == PASSWORD_FOCUS)
          {
             mode = LOGIN_FOCUS;
          }
          break;
        case 10:
          if(this.runAction())
          {
            break;
          }
          break;
      }
    }
  }
  
  
  public boolean runAction()
  {
    if(this.action == AUTHENTICATE)
    {
      if(login.length() != 0 && password.length() != 0)
      {
        if(objects.getDAL().getBrainstorm() != 0)
        {
          objects.saveConnectionString();
          System.out.println("saving connections...");
        }
        if(server.login(login, password))
        {
          mode = LOGGED_IN;
          this.cam.position.setPosition(new Point(0,0,1000));
          objects.deactivateGlobalMenuMode(false);
          objects.getUsersFirstBrainstorm();
          return true;
        }
        else
        {
          login = "";
          password = "";
          titleMsg = "Login or password incorrect. Try again.";
          mode = LOGIN_FOCUS;
          return false;
        }
      }
    }
    else if(this.action == CREATE)
    {
      if(login.length() != 0 && password.length() != 0)
      {
        if(objects.getDAL().getBrainstorm() != 0)
        {
          objects.saveConnectionString();
          System.out.println("saving connections...");
        }
        if(server.createUser(login, password))
        {
          server.login(login, password);
          mode = LOGGED_IN;
          this.cam.position.setPosition(new Point(0,0,1000));
          objects.deactivateGlobalMenuMode(false);
          objects.getUsersFirstBrainstorm();
          return true;
        }
        else
        {
          login = "";
          password = "";
          titleMsg = "Network already exists. Try again.";
          mode = LOGIN_FOCUS;
          return false;
        }
      }
    }
    return false;
  }
  
  public void activate()
  {
    super.activate();
    mode = LOGIN_FOCUS;
    this.login = "";
    this.password = "";
  }
  
  public boolean hit(int x, int y)
  {
    if(mode != LOGGED_IN && mode != CLOSED)
    {      
        if((x < position.getPosition().x + 200)
           && (x > position.getPosition().x)
           && (y < position.getPosition().y + 40 + 40)
           && (y > position.getPosition().y + 40))
        {
          mode = LOGIN_FOCUS;
          return true;
        }
        else if ((x < position.getPosition().x + 210 + 200)
                 && (x > position.getPosition().x + 210)
                 && (y < position.getPosition().y + 40 + 40)
                 && (y > position.getPosition().y + 40))
        {
          mode = PASSWORD_FOCUS;
          return true;
        }
        else if(this.action == this.AUTHENTICATE
                && (x < context.width/2 - 152 -15 + 75 + 30)
                && (x > context.width/2 - 152 -15 + 75)
                && (y < 115 + 30)
                && (y > 115))
        {
          mode = CLOSED;
          this.deactivate();
          return true;
        }
        else if(this.action == this.CREATE
                && (x < context.width/2 - 152-15 + 30)
                && (x > context.width/2 - 152-15)
                && (y < 115 + 30)
                && (y > 115))
        {
          mode = CLOSED;
          this.deactivate();
          return true;
        }
        else if((x < context.width/2 - 10 + 20)
                && (x > context.width/2 - 10)
                && (y < 230 + 20)
                && (y > 230))
        {
          if(this.runAction())
          {
            
          }
          return true;
        }
        return false;
    }
    else
    {
      if(mode == CLOSED)
      {
          if((x < context.width - 200 + 210) && (x > context.width - 200) && (y < context.height - 25 + 30) && (y > context.height - 25))
          {
            mode = LOGIN_FOCUS;
          }
      }
      return false;
    }
  }
  
  public void render()
  {
    //System.out.println(mode);
    if(mode != LOGGED_IN && mode != CLOSED)
    {
      char lCursor = ' ';
      char pCursor = ' ';
      
      if(mode == LOGIN_FOCUS)
        lCursor = '|';
      else if(mode == PASSWORD_FOCUS)
        pCursor = '|';
      
      context.fill(240, 210);
      context.stroke(240);
      //context.rect(this.position.getPosition().x, this.position.getPosition().y, this.width, this.height);  //Background rect
      //context.rect(this.position.getPosition().x + 140, this.position.getPosition().y + 40, 250, 20);
      //context.rect(this.position.getPosition().x + 140, this.position.getPosition().y + 70, 250, 20);
      context.image(this.textFieldImage, this.position.getPosition().x, this.position.getPosition().y + 40, 200, 30);
      context.image(this.textFieldImage, this.position.getPosition().x + 210, this.position.getPosition().y + 40, 200, 30);
      
      if(this.action == this.AUTHENTICATE)
      {
        context.image(this.nodeImageOpen, context.width/2 - 152 + 75 -15, 115, 30, 30);
      }
      else
      {
        context.image(this.nodeImageNew, context.width/2 - 152 - 15, 115, 30, 30);
      }
      
      context.stroke(200);
      context.textSize(13);
      context.fill(120);
      context.text(titleMsg, context.width/2 - context.textWidth(titleMsg)/2, this.position.getPosition().y + 32);
      context.fill(0);
      //context.text("name:", this.position.getPosition().x + 83, this.position.getPosition().y + 55);
      //context.text("password:", this.position.getPosition().x + 50, this.position.getPosition().y + 85);
      if(login == "" && mode != LOGIN_FOCUS)
      {
        context.fill(100);
        context.text("username" + lCursor, this.position.getPosition().x + 15, this.position.getPosition().y + 60);
        context.fill(0);
      }
      else
      {
        context.text(login + lCursor, this.position.getPosition().x + 15, this.position.getPosition().y + 60);
      }
      String hiddenPass = "";
      for(int i=0; i<password.length(); i++)
      {
        hiddenPass += "*";
      }
      if(hiddenPass == "" && mode != PASSWORD_FOCUS)
      {
        context.fill(100);
        context.text("password" + pCursor, this.position.getPosition().x + 225, this.position.getPosition().y + 60);
        context.fill(0);
      }
      else
      {
        context.text(password + pCursor, this.position.getPosition().x + 225, this.position.getPosition().y + 60);
      }
      
      int x = context.mouseX;
      int y = context.mouseY;
      
      if((x < context.width/2 - 10 + 20)
           && (x > context.width/2 - 10)
           && (y < 230 + 20)
           && (y > 230))
      {
        context.image(this.applyButtonActive, context.width/2 - 10, 230, 20, 20);

      }
      else
      {
        context.image(this.applyButton, context.width/2 - 10, 230, 20, 20);
      }
    }
  }
}
