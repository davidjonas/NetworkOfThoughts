import processing.core.PApplet;

/**
 **Class used to configure the design of the world
 **/
class ColorConfiguration
{
  private PApplet context;
  public int background;
  public int text;
  public int normalNode;
  public int newNode;
  public int popupNode;
  public int popupText;
  public int connection;
  public int tempConnection;
  public String backgroundImage;
  public String nodeImage;
  public String nodeImageActive;
  public String nodeImageHover;
  public String nodeMenuImage;
  public String nodeHitImage;
  public String popupNodeImage;
  public String connectionDeleteImage;
  public String connectionDeleteImageHover;
  public String textFieldImage;
  public boolean interaction;
  public String networks;
  public int networkDelay;
  
  //global menu icons
  public String iconNew;
  public String iconNewActive;
  public String iconOpen;
  public String iconOpenActive;
  public String iconConnections;
  public String iconConnectionsActive;
  public String iconBack;
  public String iconBackActive;
  public String iconExit;
  public String iconExitActive;
  public String iconApply;
  public String iconApplyActive;
  public String menuLogo;

  
  ColorConfiguration(PApplet context)
  {
      //Default configuration
      this.context = context;
      this.background = context.color(255);
      this.text = context.color(0);
      this.normalNode = context.color(211, 131, 138, 180);
      this.newNode = context.color(226, 200);
      this.popupNode = context.color(226,200);
      this.popupText = context.color(80);
      this.connection = context.color(0,0,0,40);
      //this.connection = context.color(255,194,0);
      this.tempConnection = context.color(0);
      this.backgroundImage = "background.jpg";
      this.nodeImage = "final_node.png";
      this.nodeImageActive = "final_node_active.png";
      this.nodeImageHover = "final_node_hover.png";
      this.nodeMenuImage = "final_node_menu.png";
      this.nodeHitImage = "final_node_menu_hover.png";
      this.popupNodeImage = "final_node_hover.png";
      this.connectionDeleteImage = "final_node_delete.png";
      this.connectionDeleteImageHover = "final_node_delete_hover.png";
      this.iconNew = "iconNew.png";
      this.iconNewActive = "iconNewActive.png";
      this.iconOpen = "iconOpen.png";
      this.iconOpenActive = "iconOpenActive.png";
      this.iconConnections = "iconConnections.png";
      this.iconConnectionsActive = "iconConnectionsActive.png";
      this.iconBack = "iconBack.png";
      this.iconBackActive = "iconBackActive.png";
      this.iconExit = "iconExit.png";
      this.iconExitActive = "iconExitActive.png";
      this.iconApply = "iconApply.png";
      this.iconApplyActive = "iconApplyActive.png";
      this.menuLogo = "menuLogo.png";
      this.textFieldImage = "final_field_background.png";
      this.interaction = true;
      this.networks = "";
      this.networkDelay = 60000;
  }
  
  //TODO: configuration through ini files
  ColorConfiguration(PApplet context, String filename)
  {
    String[] lines = context.loadStrings(filename);
    String mode = "";
    String[] command = null;
    String id = null;
    String value = null;
    String[] colorValue = null;
    String networks = "";
    this.context = context;
    
    
    
    for (int i=0; i < lines.length; i++) 
    {
      if (lines[i].equals("[Configuration]"))
      {
        mode = "Configuration";
      }
      else if (lines[i].equals("[Networks]"))
      {
        mode = "Networks";
      }
      else if (mode.equals("Configuration"))
      {
        command = context.split(lines[i], "=");
        id = command[0];
        value = command[1];
        
        if (id.equals("background"))
        {
          colorValue = context.split(value, ",");
          this.background = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("text"))
        {
          colorValue = context.split(value, ",");
          this.text = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("normalNode"))
        {
          colorValue = context.split(value, ",");
          this.normalNode = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("newNode"))
        {
          colorValue = context.split(value, ",");
          this.newNode = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("popupNode"))
        {
          colorValue = context.split(value, ",");
          this.popupNode = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("popupText"))
        {
          colorValue = context.split(value, ",");
          this.popupText = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("connection"))
        {
          colorValue = context.split(value, ",");
          this.connection = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("tempConnection"))
        {
          colorValue = context.split(value, ",");
          this.tempConnection = context.color(context.parseInt(colorValue[0]), context.parseInt(colorValue[1]), context.parseInt(colorValue[2]), context.parseInt(colorValue[3]));
        }
        if (id.equals("interaction"))
        {
          if(value.equals("true"))
          {
            this.interaction = true;
          }
          else
          {
            this.interaction = false;
          }
        }
        if (id.equals("networkDelay"))
        {
          this.networkDelay = context.parseInt(value); 
        }
      }
      else if (mode.equals("Networks"))
      { 
        if (networks.equals(""))
        {
          networks = lines[i];
        }
        else
        {
          networks = networks + ";" + lines[i];
        }
      }
    }
    
    this.backgroundImage = "background.jpg";
    this.nodeImage = "final_node.png";
    this.nodeImageActive = "final_node_active.png";
    this.nodeImageHover = "final_node_hover.png";
    this.nodeMenuImage = "final_node_menu.png";
    this.nodeHitImage = "final_node_menu_hover.png";
    this.popupNodeImage = "final_node_hover.png";
    this.connectionDeleteImage = "final_node_delete.png";
    this.connectionDeleteImageHover = "final_node_delete_hover.png";
    this.iconNew = "iconNew.png";
    this.iconNewActive = "iconNewActive.png";
    this.iconOpen = "iconOpen.png";
    this.iconOpenActive = "iconOpenActive.png";
    this.iconConnections = "iconConnections.png";
    this.iconConnectionsActive = "iconConnectionsActive.png";
    this.iconBack = "iconBack.png";
    this.iconBackActive = "iconBackActive.png";
    this.iconExit = "iconExit.png";
    this.iconExitActive = "iconExitActive.png";
    this.iconApply = "iconApply.png";
    this.iconApplyActive = "iconApplyActive.png";
    this.menuLogo = "menuLogo.png";
    this.textFieldImage = "final_field_background.png";
    this.networks = networks;
  }  
}

