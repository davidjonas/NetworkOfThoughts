//Network Of Thoughts
//Programmed by David Jonas (www.davidjonas.net)
import org.json.*;
import processing.opengl.*;
import javax.media.opengl.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import picking.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.neuralnetworkofideas.nlp.MoodDetector;
import com.neuralnetworkofideas.nlp.tagger.ThoughtContext.Mood;

PFont fontA;

PImage backImage;
int backAlpha;
int backAlphaDirection;
boolean backImageFlip = true;
int backImageDistortW;
int backImageDistortH;

PImage splashImage;
PImage splashImage2;
int splashAlpha;
boolean splashOn;

public ColorConfiguration config;

Camera cam;
float fx, fy, fz;
int nObjects;
ObjectUpdater updater;
NetworkCycle networkCycle;
ScreenSaver screensaver;
ObjectProvider objects;
ConnectionProvider connections;
boolean reloadFont = false;
HUD hud;
Rectangle monitor = new Rectangle();
Picker picker;
Thing draggingObject;
Point draggingObjectInitialPosition;
String errorMessage = "";
Boolean controlKey = false;

//menus
GlobalMenu globalMenu;
boolean globalMenuMode;

ThoughtMenu menu;
Thing menuActiveOnObj;

//buffer for last mouse position and dragging flags
float lastX, lastY;
boolean dragging = false;
boolean cleared = false;

MouseWheelEventAdd wheel;

void setup()
{
  config = new ColorConfiguration(this, "config.txt");
  
  //for full screen
  GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
  GraphicsDevice[] gs = ge.getScreenDevices();
  GraphicsDevice gd = gs[0];
  GraphicsConfiguration[] gc = gd.getConfigurations();
  monitor = gc[0].getBounds();
  size(monitor.width, monitor.height, OPENGL);
  
  //For running in windowed mode while debugging 
  //frame.setLocation(0, -20);

  //for web
  //size(480, 800, OPENGL);
  //size(monitor.width, 506, OPENGL);
  //hint(ENABLE_NATIVE_FONTS);
  
  frameRate(25);
  
  try
  {
    backImage = loadImage(config.backgroundImage);
    backAlpha = 255;
    backAlphaDirection = 1;
    
    splashImage = loadImage("logo_large.png");
    splashImage2 = loadImage("nameplate_large.png");
    splashAlpha = 255;
    splashOn = true;
    
    background(config.background);
    image(backImage, 0, 0, width, height);
    image(splashImage, width/2 - 243, height/2 - 96, 485, 191);
    image(splashImage2, width/2 - 90, height -200 - 41, 179, 82);
    stroke(255);
    reFont();
    fill(config.text);
    textFont(this.fontA, 15);
    
    //initialize camera
    cam = new Camera(0, 0, 1000);
    
    //register mouse wheel event
    wheel = new MouseWheelEventAdd(cam);
    
    
    if(config.networks.equals(""))
    {
      //load initial objects
      nObjects = 85;
      objects = new ObjectProvider(this, cam);
      objects.getRandomObjects(nObjects);
      
      //load connections RANDOM by default
      connections = new ConnectionProvider(this, objects);
      objects.connect(connections);
      connections.createConnections(ConnectionProvider.RANDOM);

      //initialize updater Thread
      updater = new ObjectUpdater(objects);
      updater.setDaemon(true);
      updater.start();

    }else
    {
      objects = new ObjectProvider(this, cam);
      connections = new ConnectionProvider(this, objects);
      objects.connect(connections);
       
      String [] networks = split(config.networks, ";"); 
      String [] firstNetwork = split(networks[0], ",");
      String login = firstNetwork[0];
      String password = firstNetwork[1];
      
      if(objects.getDAL().login(login, password))
      {
        println("logged in successfully.");
        objects.getUsersFirstBrainstorm();
      }
      else
      {
        println("Error logging in automatically.");
      }
      
      //initialize updater Thread
      updater = new ObjectUpdater(objects);
      updater.setDaemon(true);
      updater.start();

      networkCycle = new NetworkCycle(this, objects, updater, config.networks, config.networkDelay);
      networkCycle.setDaemon(true);
      networkCycle.start();
    }
    
    //initialize HUD
    hud = new HUD();
    //hud.addObject(new TitleHUD(this));     //COMMENT FOR MINIMAL INTERFACE
    hud.addObject(new AddTextObjectHUD(this, objects, cam));
    
    //initialize screensaver Thread
    screensaver = new ScreenSaver(objects);
    screensaver.setDaemon(true);
    screensaver.start();
    
    //initialize picker
    picker = new Picker(this);
    
    //Initialize menu
    menu = new ThoughtMenu(this, cam, objects);
    
  //  MenuItem connThought = new MenuItem(this, menu, "Connect thought", cam);
  //  connThought.setAction(new ConnectThoughtAction(objects));
  //  menu.addItem(connThought);
    
    MenuItem delThought = new MenuItem(this, menu, "Delete thought", cam);
    delThought.setAction(new DeleteThoughtAction(objects));
    menu.addItem(delThought);
    
    MenuItem globalMenuItem = new MenuItem(this, menu, "Global menu", cam);
    globalMenuItem.setAction(new GlobalMenuAction(objects));
    menu.addItem(globalMenuItem);
    
    //Initialize Global Menu
    PImage iconNew = loadImage(config.iconNew);
    PImage iconNewActive= loadImage(config.iconNewActive);
    PImage iconOpen = loadImage(config.iconOpen);
    PImage iconOpenActive = loadImage(config.iconOpenActive);
    PImage iconConnections = loadImage(config.iconConnections);
    PImage iconConnectionsActive = loadImage(config.iconConnectionsActive);
    PImage iconBack = loadImage(config.iconBack) ;
    PImage iconBackActive = loadImage(config.iconBackActive);
    PImage iconExit = loadImage(config.iconExit) ;
    PImage iconExitActive = loadImage(config.iconExitActive);
    
    globalMenu = new GlobalMenu(this, cam, objects);
    globalMenuMode = false;
    updateGlobalMenuPosition();
    
    MenuItem createMenuItem = new GlobalMenuItem(this, globalMenu, "New", cam, iconNew, iconNewActive);
    MenuItem openMenuItem = new GlobalMenuItem(this, globalMenu, "Open", cam, iconOpen, iconOpenActive);
    MenuItem connectionModeMenuItem = new GlobalMenuItem(this, globalMenu, "Connections", cam, iconConnections, iconConnectionsActive);
    MenuItem exitMenuItem = new GlobalMenuItem(this, globalMenu, "Quit", cam, iconExit, iconExitActive);
    MenuItem backMenuItem = new GlobalMenuItem(this, globalMenu, "Back", cam, iconBack, iconBackActive);
    
    exitMenuItem.setAction(new ExitMenuAction(this));
    backMenuItem.setAction(new BackMenuAction(this, hud));
    connectionModeMenuItem.setAction(new ConnectionsMenuAction(this, hud, config));
    createMenuItem.setAction(new NewNetworkMenuAction(this, objects, hud, cam));
    openMenuItem.setAction(new OpenNetworkMenuAction(this, objects, hud, cam));
    
    globalMenu.addItem(createMenuItem);
    globalMenu.addItem(openMenuItem);
    globalMenu.addItem(connectionModeMenuItem);
    globalMenu.addItem(backMenuItem);
    globalMenu.addItem(exitMenuItem);
    
    /* //optimizations
    ////////////////GL STUFF//////////////
    PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
    GL gl = pgl.gl;
    gl.glEnable(GL.GL_BLEND);
    gl.glHint( GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST );
    */
  }
  catch (Exception e)
  {
    errorMessage = "An error has occured. Press any key to quit.\nPlease check your internet connection and try again.";
    //errorMessage = e.getMessage();
    println(e);
  }
}

void reFont()
{
     //this.fontA = loadFont("Georgia.vlw");
     //this.fontA = loadFont("Georgia-Italic-100.vlw");
     //this.fontA = createFont("Museo Sans", 200, true);
     //this.fontA = loadFont("SansSerif.plain-200.vlw");
     //this.fontA = loadFont("Futura-MediumItalic-48.vlw");
     this.fontA = loadFont("FuturaStd-BookOblique-100.vlw");
     //this.fontA = loadFont("FuturaStd-Book-100.vlw");
}

void draw()
{    
  
  if (errorMessage != "")
  {
    background(255);
    reFont();
    textFont(this.fontA, 20);
    text(errorMessage, 40, 40);
    return;
  }
  
  Thing current; 
  background(config.background);
  noStroke();
  camera();
  
  //if network is empty open add new thought
  if(objects.size() == 0 && !objects.inGlobalMenuMode())
  {
     AddTextObjectHUD addIdea =(AddTextObjectHUD) hud.get("AddTextObject");
     if(addIdea != null)
     {
       addIdea.focus();
     }
  }
  tint(255);
  image(backImage, 0, 0, width, height);
  
  ((PGraphicsOpenGL)g).gl.glDepthMask (true);
  ((PGraphicsOpenGL)g).gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
  
  if( reloadFont ){
    reFont();
    reloadFont = false;
  }
  
  cam.step(fx, fy, fz);
  cam.render(this);
  
  //textFont(this.fontA, 10);

  objects.step();
  updateMenuPosition();
  connections.render();
  menu.step();
  objects.render();
  menu.render();
  
  if(objects.inConnectionMode() && objects.getObjectInConnectionMode() != null)
  {
    boolean hitFound = false;
    
    if(menuActiveOnObj != null)
    {
      hitFound = menu.hit();
    }
    
    stroke(this.config.tempConnection);
    
    objects.reset();
    while(objects.next())
    {
       //Cache the current item for speed
       Thing currentPick = objects.current();
       
       picker.start(currentPick.getId());
       //Save the picking area
       currentPick.renderHitArea(picker.buffer);
       picker.stop();
    }
    
    //pick object
    int id;
    try
    {
      id = picker.get(mouseX, mouseY);
    }catch(Exception e)
    {
      println("picker returned an error, ignoring");
      id = -1;
    }
    
    if (id != -1)
    {
      Thing objectUnder = objects.get(objects.idToIndex(id));
      if(!connections.isConnected(objects.idToIndex(objects.getObjectInConnectionMode().getId()), objects.idToIndex(objectUnder.getId())))
      {
        stroke(155,0,0,140);
        line(objects.getObjectInConnectionMode().position.getPosition().x - (float)14.5, objects.getObjectInConnectionMode().position.getPosition().y - (float)7.5, objects.getObjectInConnectionMode().position.getPosition().z,
             objectUnder.position.getPosition().x-(float)14.5, objectUnder.position.getPosition().y-(float)7.5, objectUnder.position.getPosition().z );
        stroke(0);
      }
      
      TextObject TextObjectUnder = (TextObject) objects.get(objects.idToIndex(id));
      TextObjectUnder.setHover(true);
      objects.reset();
      while(objects.next())
      {
        if(objects.current().getId() != id)
        {
          TextObject objectCurrent = (TextObject) objects.current();
          objectCurrent.setHover(false);
        }
      }
    }
    else
    {
      objects.reset();
      while(objects.next())
      {
        TextObject objectCurrent = (TextObject) objects.current();
        objectCurrent.setHover(false);
      }
    }
  }
  else
  {
    objects.reset();
    while(objects.next())
    {
       //Cache the current item for speed
       Thing currentPick = objects.current();
       
       picker.start(currentPick.getId());
       //Save the picking area
       currentPick.renderHitArea(picker.buffer);
       picker.stop();
    }
    
        //pick object
    int id;
    try
    {
      id = picker.get(mouseX, mouseY);
    }catch(Exception e)
    {
      println("picker returned an error, ignoring");
      id = -1;
    }
    
    if (id != -1)
    {
      TextObject objectUnder = (TextObject) objects.get(objects.idToIndex(id));
      objectUnder.setHover(true);
      objects.reset();
      while(objects.next())
      {
        if(objects.current().getId() != id)
        {
          TextObject objectCurrent = (TextObject) objects.current();
          objectCurrent.setHover(false);
        }
      }
    }
    else
    {
      objects.reset();
      while(objects.next())
      {
        TextObject objectCurrent = (TextObject) objects.current();
        objectCurrent.setHover(false);
      }
    }
  }
  
  camera();
  ((PGraphicsOpenGL)g).gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
 
  globalMenu.step();
  globalMenu.render();
  hud.render();
  
  if(splashOn)
  {
   if(splashAlpha == 255)
   {
      try
      {
        Thread.currentThread().sleep(2000);
      }
      catch (InterruptedException e)
      {
         
      }
   }
   if(splashAlpha > -25)
   {
     splashAlpha -= 25;
   }
   else
   {
     splashOn = false;
   }
   tint(255,splashAlpha);
   image(splashImage, width/2 - 243, height/2 - 96, 485, 191);
   image(splashImage2, width/2 - 90, height - 200 - 41, 179, 82);
    
  }
  
  if (dragging)
  {
    fx = 0;
    fy = 0;
  }
}

void displayChanged(){
  reloadFont = true;
}

void updateMenuPosition()
{
  if(menuActiveOnObj != null)
  {
    menu.setPosition(menuActiveOnObj.position.getPosition());
  }
}

void updateGlobalMenuPosition()
{
  //globalMenu.position.setPosition(new Point(0,0,0));
}

void keyTyped()
{
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  for(int i=0; i<hud.size(); i++)
  {
    HUDObject hudObject = hud.get(i);
    if(hudObject.inFocus())
    {
      hudObject.charKeyStroke(key);
    }
  }
}

void keyPressed()
{
  if (keyCode == 157)
  {
    controlKey = true;
  }
  
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  if (objects.inGlobalMenuMode())
  {
    globalMenuKeyPressed();
    return;
  }
  
  boolean done = false;
  
  for(int i=0; i<hud.size(); i++)
  {
    HUDObject hudObject = hud.get(i);
    if(hudObject.inFocus())
    {
       if (key == CODED || key == BACKSPACE || key == RETURN || key == ENTER || key == TAB || key == ESC)
       {
          hudObject.keyStroke(keyCode);
       }
      //hudObject.charKeyStroke(key);
      done = true;
    }
  }
  
  if (key == ESC && done) key=0;
  
  if(!done)
  {
    float step = 4;
    
    switch(keyCode)
    {
        case 38: fz = -step;
                 break;
        case 40: fz = step;
                 break;
        case 37: fx = -step;
                 break;
        case 39: fx = step;
                 break;
        default: 
                 break;
    }
    switch(key)
    {
       case 'w': fy = -step;
                 break;
       case 's': fy = step;
                 break;
//       case ' ': connections.createConnections(ConnectionProvider.RANDOM);
//                 break;

//NOTE: Comment this block to disable menu interaction
//----------------------------------------------------
       case ENTER:          
       case RETURN: 
                 if (config.interaction)
                 {
                   AddTextObjectHUD addIdea =(AddTextObjectHUD) hud.get("AddTextObject");
                   if(addIdea != null)
                   {
                     addIdea.focus();
                   }
                 }
                 break;
//----------------------------------------------------

//       case 'b': objects.getBrainstorm(1);  //DEBUG CASE
//                 break;
//       case 'n': objects.addBrainstorm("testBrainstorm"); //DEBUG CASE
//                 break;
//       case 'm': connections.createConnections(ConnectionProvider.MOOD); //DEBUG CASE
//                 break;
//       case 'u':   objects.getUserTextObjects();
//                   cam.setPosition(objects.getWorldSize()/2 ,objects.getWorldSize()/2 , objects.getWorldSize()/2 + 500);
//                   break;
//       case 't':   objects.selectObject((int)random(0, objects.size()-1));
//                   //objects.selectAll();
//                   break;
//       case 'g':   //objects.selectObject((int)random(0, objects.size()-1));
//                   objects.unSelectAll();
//                   break;
    }
  }
}

void keyReleased()
{
  if (keyCode == 157)
  {
    controlKey = false;
  }
  else
  {
    println(keyCode);
  }
  
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  if (objects.inGlobalMenuMode())
  {
    globalMenuKeyReleased();
    return;
  }
  
  switch(keyCode)
  {
      case 38: fz = 0;
               break;
      case 40: fz = 0;
               break;
      case 37: fx = 0;
               break;
      case 39: fx = 0;
               break;
      default: 
               break;
  }
  switch(key)
  {
     case 'w': fy = 0;
               break;
     case 's': fy = 0;
               break;
  }
} 

void mousePressed()
{
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  if (objects.inGlobalMenuMode())
  {
    globalMenuMousePressed();
    return;
  }
  
  //noCursor();
  //cursor(HAND);
  
  if (mouseEvent.getClickCount()==2)
  {
    return;
  }
  
  objects.reset();
  cam.render(this);
  while(objects.next())
  {
     //Cache the current item for speed
     Thing current = objects.current();
     
     picker.start(current.getId());
     //Save the picking area
     current.renderHitArea(picker.buffer);
     //current.renderHitArea();
     picker.stop();
  }
  
  
  //pick object
  int id = picker.get(mouseX, mouseY);
  
  if(id != -1)
  {
    draggingObject = objects.get(objects.idToIndex(id));
    draggingObjectInitialPosition = draggingObject.position.getPosition();
    draggingObject.position.setDrag((float)4);
    draggingObject.position.setMaxSpeed((float)80);
  } 
  else
  {
    noCursor();
  }
  
  //start dragging
  lastX = mouseX;
  lastY = mouseY;
  if(cam.position.getDestination() != null)
  {
    cam.position.clearDestination();
    cleared = true;
  }
}

void mouseDragged()
{
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  if (objects.inGlobalMenuMode())
  {
    return;
  }
  
  dragging = true;
  if(draggingObject == null)
  {
    //this.cam.position.setDrag((float)0);
    fx = (lastX - mouseX);
    fy = (lastY - mouseY);
    lastX = mouseX;
    lastY = mouseY;
  }
  else
  {
    //Faking the raytrace
    float zDistance = abs(cam.position.getPosition().subtract(draggingObject.position.getPosition()).z);
    
    float distanceFactor = zDistance/1000;
    
    float destinationDeltaX = (lastX - mouseX) * distanceFactor;
    float destinationDeltaY = (lastY - mouseY) * distanceFactor;
    float destinationDeltaZ = 0;
    
    Point destination = draggingObjectInitialPosition.add(new Point(-destinationDeltaX,-destinationDeltaY,destinationDeltaZ));
   
    draggingObject.position.setDestination(destination);
  }
}

void mouseReleased()
{
  if (errorMessage != "")
  {
    super.exit();
    return;
  }
  
  TextObject textMenuActiveOnObj;
  
  if (objects.inGlobalMenuMode())
  {
    globalMenuMouseReleased();
    return;
  }
  
  cursor(ARROW);

  //stop dragging
  if (dragging)
  {
    dragging = false;
    fx = 0; 
    fy = 0;
    if(draggingObject != null)
    {
      draggingObject.position.clearDestination();
      draggingObject.resetPhysicsToDefault();
    }
    else
    {
      this.cam.resetPhysicsToDefault();
    }
    draggingObject = null;
    draggingObjectInitialPosition = null;
    return;
  }else if(draggingObject != null)
  {
    draggingObject = null;
    draggingObjectInitialPosition = null;
  }
  
  for(int i=0; i<hud.size(); i++)
  {
    if(hud.get(i).hit(mouseX, mouseY))
    {
        return;
    }
  }
  
  if (cleared)
  {
    cleared = false;
    return;
  }
    //Picking goes here
    objects.reset();
    cam.render(this);
    while(objects.next())
    {
       //Cache the current item for speed
       Thing current = objects.current();
     
       picker.start(current.getId());
       //Save the picking area
       current.renderHitArea(picker.buffer);
       //current.renderHitArea();
       picker.stop();
    }
  
  //NOTE: Comment this block to disable menu interaction
  //----------------------------------------------------
  if (mouseEvent.getClickCount()==2 && config.interaction)
  {
    int id = picker.get(mouseX, mouseY);
    
    if(id != -1 && !objects.inConnectionMode())
    { 
      AddTextObjectHUD addText = (AddTextObjectHUD) hud.get("AddTextObject");
      
      addText.unfocus();
      
      menuActiveOnObj = objects.get(objects.idToIndex(id));
      textMenuActiveOnObj = (TextObject) menuActiveOnObj;
      textMenuActiveOnObj.setActiveState(true);
      updateMenuPosition();
      menu.resetItemPositions();
      menu.activate(menuActiveOnObj);
    }
    return;
  }
  //----------------------------------------------------
  
  if(menuActiveOnObj == null || !menu.pickAndRun())
  {
    //pick object
    int id = picker.get(mouseX, mouseY);
    if(id != -1)
    {
      //println("Object " + id + " picked with index " + objects.IdToIndex(id) + " of " + objects.size());   //Debugging stuff
      Thing ob = objects.get(objects.idToIndex(id));
      
      if(objects.inConnectionMode() && ob != objects.getObjectInConnectionMode())
      {
        int indexA = objects.idToIndex(objects.getObjectInConnectionMode().getId());
        int indexB = objects.idToIndex(id);
        if (!connections.isConnected(indexA, indexB))
        {
          connections.connect(indexA, indexB);
          menu.deleteConnectionItems();
          menu.createConnectionButtons(menuActiveOnObj);
          //objects.deactivateConnectionMode();
        }
        return;
      }
      
      if (mouseEvent.getClickCount()==2)
      {
        //Doubleclick
        //cam.position.setDestination(ob.position.getPosition().add(new Point(80,0,300)));
        //return;
      }
      else if (mouseEvent.getClickCount()==1)
      {
        //Singleclick
        //println("SingleClick!!!!");
        
        if (ob.getMode() == Thing.TEMPORARY)
        {
          ob.setMode(Thing.IDLE);
          objects.getDAL().addObjectToCurrentBrainstorm(ob);
          objects.connectObject(objects.idToIndex(id));
          return;
        }else
        {
          cam.position.setDestination(ob.position.getPosition().add(new Point(80,0,300)));
        }
      }
    }
    else
    {
      //println("No object picked");
      if(menuActiveOnObj != null)
      {
        textMenuActiveOnObj = (TextObject) menuActiveOnObj;
        textMenuActiveOnObj.setActiveState(false);
        menu.deactivate();
        menuActiveOnObj = null;
        objects.deactivateConnectionMode();
      }
      else
      {
        this.cam.position.setDestination(this.cam.position.getPosition().add(new Point(0,0,500)));
      }
      return;
    }
  }
}

void mouseMoved()
{
  try
  {
    screensaver.moved();
  }
  catch(Exception e)
  {
    
  }
}  

void globalMenuKeyPressed()
{
  for(int i=0; i<hud.size(); i++)
  {
    HUDObject hudObject = hud.get(i);
    if(hudObject.inFocus())
    {
      hudObject.keyStroke(keyCode);
      hudObject.keyStroke(key);
    }
  }
}

void globalMenuKeyReleased()
{
  
}

void globalMenuMousePressed()
{
  
}

void globalMenuMouseReleased()
{
  for(int i=0; i<hud.size(); i++)
  {
    if(hud.get(i).hit(mouseX, mouseY))
    {
        return;
    }
  }
  if(globalMenu.pickAndRun())
  {
    LoginHUD login = (LoginHUD) hud.get("Login");
    ConnectionsHUD connHud = (ConnectionsHUD) hud.get("Connections");
    
    if((login == null || !login.isActive()) && (connHud == null || !connHud.isActive()))
    {
      objects.deactivateGlobalMenuMode(true);
    }
    
    
  }
}

public void exit()
{
  if(objects.getDAL().getBrainstorm() != 0)
  {
    objects.saveConnectionString();
    println("saving connections...");
    super.exit();
  }
  else
  {
    super.exit();
  }
}

public class MouseWheelEventAdd implements MouseWheelListener {
  
  private Camera cam;
  
  public MouseWheelEventAdd(Camera cam) {
     addMouseWheelListener(this);
     this.cam = cam;
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
     int notches = e.getWheelRotation();
     if (notches < 0) {
         this.cam.position.setDestination(this.cam.position.getPosition().add(new Point(0,0,-50*abs(notches))));
     }
     else {

         this.cam.position.setDestination(this.cam.position.getPosition().add(new Point(0,0,50*abs(notches))));
     }
   }
}
