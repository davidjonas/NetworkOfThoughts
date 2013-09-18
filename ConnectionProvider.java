import processing.core.PApplet;
import java.util.ArrayList;
import com.neuralnetworkofideas.nlp.MoodDetector;
import com.neuralnetworkofideas.nlp.tagger.ThoughtContext.Mood;

class ConnectionProvider
{
  public static final int CONN_STR = 0;
  public static final int RANDOM = 1;
  public static final int MOOD = 2;
  public static final int NEW_CONNECTION_AGE = 100;
  
  private ObjectProvider objects;
  private boolean matrix[][];
  private ArrayList newConnections;
  private PApplet context;
  private int currentMode;
  private ColorConfiguration config;
  
  ConnectionProvider(PApplet context, ObjectProvider objects)
  {
    this.context = context;
    this.objects = objects;
    this.matrix = new boolean[objects.size()][objects.size()];
    this.config = ((NetworkOfThoughts)context).config;
    this.newConnections = new ArrayList();
    this.currentMode = RANDOM;
  }
  
  public boolean [][] getMatrix()
  {
    return matrix;
  }
  
  public void refresh(ObjectProvider obj)
  {
    this.objects = obj;
    this.rebuildMatrix();
    this.createConnections(currentMode);
  }
  
  private void rebuildMatrix()
  {
    this.matrix = new boolean[objects.size()][objects.size()];
  }
  
  public int[] getConnectionsOf(int index)
  {
    ArrayList alResults = new ArrayList();
    
    for(int i=0; i<objects.size(); i++)
    {
      if(isConnected(index, i))
      {
        alResults.add(new Integer(i));
      }
    }
    
    if(alResults.size() > 0)
    {
      int [] results = new int[alResults.size()];
      
      for (int r=0; r<alResults.size(); r++)
      {
        results[r] = (int) ((Integer)alResults.get(r));
      }
      return results;
    }
    else
    {
      return null;
    }
  }
  
  public void deleteConnection(int indexA, int indexB)
  {
    this.matrix[indexA][indexB] = false;
    this.matrix[indexB][indexA] = false;
    for(int i=0; i<this.newConnections.size(); i++)
    {
      int [] newConn = (int [])newConnections.get(i);
      if((newConn[0] == indexA && newConn[1] == indexB) || (newConn[0] == indexB && newConn[1] == indexA))
      {
        newConnections.remove(i);
      }
    }
  }
  
  public int getConnectionAge(int indexA, int indexB)
  {
    for(int i=0; i<this.newConnections.size(); i++)
    {
      int [] newConn = (int [])newConnections.get(i);
      if((newConn[0] == indexA && newConn[1] == indexB) || (newConn[0] == indexB && newConn[1] == indexA))
      {
        newConn[2] = newConn[2]-1;
        if(newConn[2] == 0)
        {
          newConnections.remove(i);
        }
        else
        {
          newConnections.set(i, newConn);
        }
        
        return newConn[2];
      }
    }
    return 0;
  }
  private boolean matrixNeedsRebuild()
  {
    boolean result = (matrix.length != objects.size());
    if(!result)
    {
      for (int i=0; i<objects.size(); i++)
      {
        if (matrix[i].length != objects.size())
        {
          result = true;
          break;
        }
      }
    }
    return result;
  }
  
  public void connect(int a, int b)
  {
     matrix[a][b] = true;
     int newConn[] = new int[3];
     newConn[0] = a;
     newConn[1] = b;
     newConn[2] = NEW_CONNECTION_AGE;
     this.newConnections.add(newConn);
  }
  
  public void rebuildIfNecessary()
  {
    if(this.matrixNeedsRebuild())
    {
      this.rebuildMatrix();
    }
  }
  
  public boolean isConnected(int a, int b)
  {
    if(a < matrix.length && b < matrix.length)
    {
      return matrix[a][b] || matrix[b][a];
    }
    else
    {
      return false;
    }
  }
  
  public void setMode(int mode)
  {
    currentMode = mode;
  }
  
  public void createConnections(int mode)
  {
    switch(mode)
    {
        case RANDOM:    connectRandom();
                        currentMode = RANDOM;
                        break;
        case MOOD:      connectMood();
                        currentMode = MOOD;
                        break;
        case CONN_STR:  connectConnStr();
                        currentMode = CONN_STR;
                        break;
    }
  }
  
  
  public int numberOfTemps()
  {
    int counter = 0;
    for(int a=0; a<objects.size(); a++)
    {
      if(objects.get(a).getMode() == Thing.TEMPORARY)
      {
        counter++;
      }
    }
    return counter;
  } 
  
  public void connectRandom()
  {
    this.rebuildIfNecessary();
    
    for(int a=0; a<objects.size(); a++)
    {
      for(int j=0; j<objects.size(); j++)
      {
        matrix[a][j] = false;
      }
    }
    
    for(int i=0; i<objects.size(); i++)
    { 
      connectRandom(i);
    }
  }
  
  
  public void connectConnStr()
  {
    this.rebuildIfNecessary();
    
    for(int a=0; a<objects.size(); a++)
    {
      for(int j=0; j<objects.size(); j++)
      {
        matrix[a][j] = false;
      }
    }
  
    String connStr = objects.getDAL().getConnectionString();
    
    System.out.println("-- string: " + connStr);
    
    String [] pairs = connStr.split(",");
    for(int p=0; p<pairs.length; p++)
    {
      String [] pair = pairs[p].split("-");
      int [] intPair = new int[2];
      try
      {
        intPair[0] = objects.idToIndex(Integer.parseInt(pair[0].trim()));
        intPair[1] = objects.idToIndex(Integer.parseInt(pair[1].trim()));
      }catch (NumberFormatException e)
      {
        System.out.println("Error in ConnectionProvider.java at connectConnStr() -> NumberFormatException");
        return;
      }
      
      if(intPair[0] >= 0 && intPair[1] >= 0)
      {
        //System.out.println("connecting pair: " + intPair[0] + " - " + intPair[1]);
        matrix[intPair[0]][intPair[1]] = true;
      }
      else
      {
        System.out.println("Found a negative connection. Parsing error or bad data: (" + intPair[0] + "," +intPair[1] + ")");
      }
    }
  }
  
  //XXX: This function was a test and needs to be recoded with stored moods. Needs some architecture changes. the Mood detection takes too long
  public void connectMood()
  {
    //XXX: these thoughts are made to be connected to the ideas of the respective mood type.
    //they should be the last ideas on the world but that does not happen when the world is poping up ideas assyncronously
    objects.addWorldOnlyObject(new TextObject(context, "IMPERATIVE", "Semantics", 0, -1));
    objects.addWorldOnlyObject(new TextObject(context, "SUBJUNCTIVE", "Semantics", 0, -1));
    objects.addWorldOnlyObject(new TextObject(context, "INDICATIVE", "Semantics", 0, -1));
    
    this.rebuildMatrix();
    
    Mood moods[];
    moods = new Mood[objects.size()];
    MoodDetector detector = new MoodDetector();
    
    for(int a=0; a<objects.size(); a++)
    {
      try
      {
        String utf8_thought = new String(((TextObject)objects.get(a)).getText().getBytes(), "UTF-8");
        moods[a] = detector.detectMood(utf8_thought);
        System.out.println(moods[a]);
      }
      catch (Exception ex)
      {
        System.out.println("failed because: " + ex.getMessage() + " Connecting in Random mode instead.");
        connectRandom();
        this.currentMode = RANDOM;
        return;
      }
    }
    
    for(int b=0; b<objects.size()-3; b++)
    {
       if(moods[b] == Mood.IMPERATIVE)
       {
         matrix[b][objects.size()-3] = true;
       }
       else if(moods[b] == Mood.SUBJUNCTIVE)
       {
         matrix[b][objects.size()-2] = true;
       }
       else
       {
         matrix[b][objects.size()-1] = true;
       }
    }
  }
  
  public void connectRandom(int i)
  {
      int rNumber = (int)context.random(0, objects.size()-1);
      while(objects.get(rNumber).getMode() == Thing.TEMPORARY)
      {
        rNumber = (int)context.random(0, objects.size()-1);
      }
      matrix[i][rNumber] = true;
  }
  
  public void connectConnStr(int i)
  {
    //TODO: get the connection string and connect this thought.
  }
  
  public void connectMood(int i)
  {
    //XXX: analyse mood and connect.testing porpuses only, not implemented
  }
  
  public synchronized void connectObject(int i)
  {
    if(i < matrix.length)
    {
      switch(currentMode)
      {
          case RANDOM:    connectRandom(i);
                          break;
          case MOOD:      connectMood(i);
                          break;
          case CONN_STR:  connectConnStr(i);
                          break;
      }
    }
  }
  
  public synchronized void connectLastObject()
  {
    this.connectObject(matrix.length - 1);
  }
  
  public void reindexMaintainingConnections()
  {
    boolean oldMatrix[][] = matrix;
    rebuildMatrix();
    for(int i=0; i<oldMatrix.length; i++)
    {
      for(int j=0; j<oldMatrix.length; j++)
      {
        matrix[i][j] = oldMatrix[i][j];
      }
    }
  }
  
  public void reindexMaintainingConnections(int deleted)
  {
    boolean oldMatrix[][] = matrix;
    rebuildMatrix();
    boolean reachedDeletedItemI = false;
    boolean reachedDeletedItemJ = false;
    int indexI;
    int indexJ;
    
    for(int i=0; i<oldMatrix.length; i++)
    {
      if(i == deleted)
      {
        reachedDeletedItemI = true;
        continue;
      }
      for(int j=0; j<oldMatrix.length; j++)
      {
        if(j == deleted)
        {
          reachedDeletedItemJ = true;
          continue;
        }
        if (reachedDeletedItemJ)
        {
          indexJ = j-1;
        }
        else
        {
          indexJ = j;
        }
        if (reachedDeletedItemI)
        {
          indexI = i-1;
        }
        else
        {
          indexI = i;
        }
        matrix[indexI][indexJ] = oldMatrix[i][j];
      }
      reachedDeletedItemJ = false;
    }
  }
  
  public void render()
  {
    context.stroke(config.connection);
    context.strokeWeight(1);
    for(int i=0; i<objects.size(); i++)
    {
      for(int j=0; j<objects.size(); j++)
      {
        try
        {
          if(matrix[i][j])
          {
            int age = this.getConnectionAge(i, j);
            if(age > 0)
            {
              context.stroke((255-this.NEW_CONNECTION_AGE)+age, 0,0, context.alpha(config.connection)+age);
            }
            context.line(objects.get(i).position.getPosition().x-(float)10.50, objects.get(i).position.getPosition().y-(float)7.5, objects.get(i).position.getPosition().z,
                         objects.get(j).position.getPosition().x-(float)10.50, objects.get(j).position.getPosition().y-(float)7.5, objects.get(j).position.getPosition().z);
            context.stroke(config.connection);
          }
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
          System.out.println("Error in ConnectionProvider.java at render() -> ArrayIndexOutOfBoundsException, refreshing objects to fix it.");
          this.refresh(objects);
        }
      }
    }
    context.noStroke();
  }
}
