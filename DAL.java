import org.json.*;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Date;
import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;

//TODO: this class should be a Singleton
public class DAL
{ 
  private String url;
  private PApplet context;
  private boolean isAnon;
  private String user;
  private int brainstorm;
  
  
  DAL(PApplet context, String url)
  { 
    this.context = context;
    this.url = url;
    this.user = null;
    this.brainstorm = 0;
    this.isAnon = true;
  }
  
  public int getBrainstorm()
  {
    return this.brainstorm;
  }
  
  public void setBrainstorm(int bs)
  {
    this.brainstorm = bs;
  }
  
  private String replace( String s, String f, String r )
  {
    if (s == null)  return s;
    if (f == null)  return s;
    if (r == null)  r = "";
 
    int index01 = s.indexOf( f );
    while (index01 != -1)
    {
       s = s.substring(0,index01) + r + s.substring(index01+f.length());
       index01 += r.length();
       index01 = s.indexOf( f, index01 );
    }
    return s;
  }
  
  private String clean(String str)
  {
     return replace(str,"\'","\\\'");
  }
  
  public String getRandomTweet()
  {
    return "Nothing";
  }
  
  public void saveConnectionString(String connectionString)
  {
    if (brainstorm != 0 && connectionString != null)
    {
      try {
        // Construct data
        String data = "func=saveConnectionStringToBrainstorm";
        data += "&" + "attr[]" + "=" + Integer.toString(this.brainstorm);
        data += "&" + "attr[]" + "=" + connectionString;
    
        System.out.println("data = "+ data); 
    
        // Send data
        URL url = new URL(this.url);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
    
        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
          /*if(!line.substring(1, 5).equals("true"))
          {
            System.out.println("Error Saving connectionString: "+line);
          }
          else
          {
            System.out.println("saved connectionString");
          }*/
          System.out.println("Saving connectionString response: "+line);
        }
        wr.close();
        rd.close();
      } catch (Exception e) {
        System.out.println("Error Saving connectionString: " + e.toString());
      }
    }else
    {
      System.out.println("Failed initial condition to save connecitonString");
    }
  }
  
  public String getConnectionString()
  {
    if (brainstorm != 0)
    {
      String connectionString = makeCall("getBrainstormConnectionString", Integer.toString(brainstorm));
      return connectionString;
    }
    else
    {
      return null;
    }
  }
  
  public String makeCall(String funcStr, String attrStr)
  {
     URL call;
     
    try
    {
      call = new URL(this.url + "?func="+ funcStr +"&attr=" + attrStr);
    }catch(MalformedURLException e)
    {
      System.out.println("Malformed URL:" + e.getMessage());
      return null;
    }
    
     try
    {
      InputStream is = call.openStream();
      char c;
      StringBuffer putBackTogether = new StringBuffer (  ) ; 
      Reader r = new InputStreamReader ( is, "UTF-8" ) ; 
      char [  ]  cb = new char [ 2048 ] ; 
      
      int amtRead = r.read ( cb ) ; 
      while  ( amtRead  >  0 )   {  
          putBackTogether.append ( cb, 0, amtRead ) ; 
          amtRead = r.read ( cb ) ; 
      }  
      
      String resultString = putBackTogether.toString (  );
      return resultString;
    }catch (IOException e)
    {
      System.out.println("Error getting contents from server:" + e.getMessage());
      return null;
    }
  }
  
  public String makeCall(String funcStr, ArrayList attr)
  {
     URL call;
     
    try
    {
      String attrStr = "";
      for(int i=0; i<attr.size(); i++)
      {
         attrStr += "&attr[]=" + attr.get(i);
      }
      call = new URL(this.url + "?func="+ funcStr + attrStr);
    }catch(MalformedURLException e)
    {
      System.out.println("Malformed URL:" + e.getMessage());
      return null;
    }
    
     try
    {
      InputStream is = call.openStream();
      char c;
      StringBuffer putBackTogether = new StringBuffer (  ) ; 
      Reader r = new InputStreamReader ( is, "UTF-8" ) ; 
      char [  ]  cb = new char [ 2048 ] ; 
      
      int amtRead = r.read ( cb ) ; 
      while  ( amtRead  >  0 )   {  
          putBackTogether.append ( cb, 0, amtRead ) ; 
          amtRead = r.read ( cb ) ; 
      }  
      
      String resultString = putBackTogether.toString (  );
      return resultString;
    }catch (IOException e)
    {
      System.out.println("Error getting contents from server:" + e.getMessage());
      return null;
    }
  }
  
  public int addBrainstorm(String name)
  {
    if (name == "")
    {
      return 0;
    }
    
    ArrayList attr = new ArrayList();
    attr.add(name);
    if(this.user == null)
    {
      attr.add("null");
    }
    else
    {
      attr.add(this.user);
    }
    
    String resultString = makeCall("addBrainstorm", attr);
    try
    {
      int resultInt = Integer.parseInt(resultString.substring(1, resultString.length()));
      return resultInt;
    }catch(NumberFormatException ex)
    {
      System.out.println("ERROR adding brainstorm, reply: " + resultString);
      return 0;
    }
  }
  
  public int addTextObject(String msg)
  {
    if(msg == "")
    {
      return -1;
    }
    
    msg = replace(msg,"\n", "");
    ArrayList attr = new ArrayList();
    try
    {
      attr.add(URLEncoder.encode(msg, "UTF-8"));
    }catch(UnsupportedEncodingException ex)
    {
      System.out.println("Encoding not supported: " + ex.getMessage());
    }
    if(this.user == null)
    {
      attr.add("null");
    }
    else
    {
      attr.add(this.user);
    }
    
    String resultString = makeCall("addTextObject", attr);
    try
    {
      int id = Integer.parseInt(resultString.trim());
      return id;
    }
    catch (NumberFormatException nfe)
    {
      System.out.println("NumberFormatException: " + nfe.getMessage());
      return -1;
    }

  }
  
  public int addTextObject(String msg, int brainstorm)
  {
    if(msg == "")
    {
      return -1;
    }
    
    msg = replace(msg,"\n", "");
    ArrayList attr = new ArrayList();
    try
    {
      attr.add(URLEncoder.encode(msg, "UTF-8"));
    }catch(UnsupportedEncodingException ex)
    {
      System.out.println("Encoding not supported: " + ex.getMessage());
    }
    if(this.user == null)
    {
      attr.add("null");
    }
    else
    {
      attr.add(this.user);
    }
    
    attr.add(brainstorm);
    
    String resultString = makeCall("addTextObject", attr);
    try
    {
      int id = Integer.parseInt(resultString.trim());
      return id;
    }
    catch (NumberFormatException nfe)
    {
      System.out.println("NumberFormatException: " + nfe.getMessage());
      return -1;
    }
  }
  
  public boolean permanentRemoveObject(Thing ob)
  {
    int id = ob.getId();
    String resultString = makeCall("permanentRemoveObject", Integer.toString(id));
    if(resultString.substring(1, 5).equals("true"))
    { 
      return true;
    }
    else
    {
      return false;
    }
  }
  public boolean removeObjectFromBrainstorm(Thing ob, int brainstorm)
  {
    int object = ob.getId();
    if(object == 0 || brainstorm == 0)
    {
       return false; 
    }
  
    ArrayList attr = new ArrayList();
    attr.add(object);
    attr.add(brainstorm);
  
    String resultString = makeCall("removeObjectFromBrainstorm", attr);
    if(resultString.substring(1, 5).equals("true"))
    { 
      return true;
    }
    else
    {
      return false;
    }
  }
  public ArrayList getNTextObjects(int n)
  {
    try
   {  
      ObjectCleaner cleaner = new ObjectCleaner();
       
      JSONArray responseArray = new JSONArray(makeCall("getNThoughts", Integer.toString(n)));
      ArrayList finalArray = new ArrayList();
      
      String cleanResult;
      JSONObject it;
      for(int i=0; i<responseArray.length(); i++)
      {
          it = responseArray.getJSONObject(i);
          cleanResult = cleaner.cleanTextObject(it.getString("message"));
          long timestamp = Long.parseLong(it.getString("timestamp").trim());
          int id = Integer.parseInt(it.getString("ID").trim());
          finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
      }
      return finalArray;
   }catch (Exception ex)
   {
       System.out.println("Server Comunication Exception: " + ex.getCause().toString());
       //this.context.errorMessage = "Server Comunication Exception";
   }
    return new ArrayList(); 
  }
  
    
  public ArrayList getUserTextObjects()
  {
    if(this.user != null)
    {
       try
       {  
          ObjectCleaner cleaner = new ObjectCleaner();
           
          JSONArray responseArray = new JSONArray(makeCall("getUserTextObjects", this.user));
          ArrayList finalArray = new ArrayList();
          
          String cleanResult;
          JSONObject it;
          for(int i=0; i<responseArray.length(); i++)
          {
              it = responseArray.getJSONObject(i);
              cleanResult = cleaner.cleanTextObject(it.getString("message"));
              long timestamp = Long.parseLong(it.getString("timestamp").trim());
              int id = Integer.parseInt(it.getString("ID").trim());
              finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
          }
          return finalArray;
       }catch (Exception ex)
       {
           System.out.println("Server Comunication Exception: " + ex.getMessage());
       }
    }
    return new ArrayList(); 
  }
  
  
  public ArrayList getBrainstorm(int n)
  {
    try
   {  
      ObjectCleaner cleaner = new ObjectCleaner();
       
      JSONArray responseArray = new JSONArray(makeCall("getBrainstorm", Integer.toString(n)));
      ArrayList finalArray = new ArrayList();
      
      String cleanResult;
      JSONObject it;
      for(int i=0; i<responseArray.length(); i++)
      {
          it = responseArray.getJSONObject(i);
          cleanResult = cleaner.cleanTextObject(it.getString("message"));
          long timestamp = Long.parseLong(it.getString("timestamp").trim());
          int id = Integer.parseInt(it.getString("ID").trim());
          finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
      }
      return finalArray;
   }catch (Exception ex)
   {
       System.out.println("Server Comunication Exception: " + ex.getMessage());
   }
    return new ArrayList(); 
  }
  
  public ArrayList getUsersFirstBrainstorm()
  {
    if(this.loggedIn())
    {
       try
       {  
          ObjectCleaner cleaner = new ObjectCleaner();
           
          JSONArray responseArray = new JSONArray(makeCall("getUsersFirstBrainstorm", this.user));
          ArrayList finalArray = new ArrayList();
          
          String cleanResult;
          JSONObject it;
          for(int i=1; i<responseArray.length(); i++)
          {
              it = responseArray.getJSONObject(i);
              cleanResult = cleaner.cleanTextObject(it.getString("message"));
              long timestamp = Long.parseLong(it.getString("timestamp").trim());
              int id = Integer.parseInt(it.getString("ID").trim());
              finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
          }
          
          it = responseArray.getJSONObject(0);
          int bsId = Integer.parseInt(it.getString("ID").trim());
          
          this.setBrainstorm(bsId); 
          
          return finalArray;
       }catch (Exception ex)
       {
           System.out.println("Server Comunication Exception: " + ex.getMessage());
       }
        return new ArrayList();
    }
    else
    {
      return new ArrayList();
    }
  }
  
  public void addObjectToCurrentBrainstorm(Thing ob)
  {
    if(this.brainstorm != 0)
    {
      this.addExistingObjectToBrainstorm(ob.getId(), this.brainstorm);
    }
  }
  
  public boolean addExistingObjectToBrainstorm(int object, int brainstorm)
  {
    if(object == 0 || brainstorm == 0)
    {
       return false; 
    }
    ArrayList attr = new ArrayList();
    attr.add(object);
    attr.add(brainstorm);
    
    String resultString = makeCall("addExistingObjectToBrainstorm", attr);
    if(resultString.substring(1, 5).equals("true"))
    { 
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public boolean login(String username, String password)
  {
    ArrayList attr = new ArrayList();
    attr.add(username);
    try
    {
      attr.add(AeSimpleSHA1.SHA1(password));
    }catch(Exception ex)
    {
      System.out.println("Error encrypting password:" + ex.getMessage());
    }
    String resultString = makeCall("authenticate", attr);
    if(resultString != null && resultString.substring(1, 5).equals("true"))
    { 
      this.isAnon = false;
      this.user = username;

      return true;
    }
    else
    {
      return false;
    }
  }
  
  public boolean createUser(String username, String password)
  {
    System.out.println("Creating user");
    ArrayList attr = new ArrayList();
    attr.add(username);
    try
    {
      attr.add(AeSimpleSHA1.SHA1(password));
    }catch(Exception ex)
    {
      System.out.println("Error encrypting password:" + ex.getMessage());
    }
    String resultString = makeCall("createUser", attr);
    if(resultString.substring(1, 5).equals("true"))
    { 
      this.isAnon = false;
      this.user = username;

      return true;
    }
    else
    {
      System.out.println(resultString);
      return false;
    }
  }
  
  public boolean loggedIn()
  {
     return !this.isAnon;   
  }
  
  public String getUser()
  {
    return this.user;
  }
  
  public long getCurrentTime()
  {
    String resultString = makeCall("getCurrentServerTime", "None");
    
    try {
         long seconds = Long.parseLong(resultString.trim());
         return seconds;
      } catch (NumberFormatException nfe) {
         System.out.println("NumberFormatException: " + nfe.getMessage());
         return 0;
      }
  }
  
  public ArrayList getNewObjectsSince(long sinceTime)
  { 
    try
       {  
          ObjectCleaner cleaner = new ObjectCleaner();
           
          JSONArray responseArray = new JSONArray(makeCall("getNewTextObjectsSince", Long.toString(sinceTime)));
          ArrayList finalArray = new ArrayList();
          
          String cleanResult;
          JSONObject it;
          for(int i=0; i<responseArray.length(); i++)
          {
              it = responseArray.getJSONObject(i);
              cleanResult = cleaner.cleanTextObject(it.getString("message"));
              long timestamp = Long.parseLong(it.getString("timestamp").trim());
              int id = Integer.parseInt(it.getString("ID").trim());
              finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
          }
          return finalArray;
       }catch (Exception ex)
       {
           System.out.println("Server Comunication Exception: " + ex.getMessage());
       }
       return new ArrayList(); 
  }
  
  public ArrayList searchThoughts(String search)
  {
    try
   {  
      ObjectCleaner cleaner = new ObjectCleaner();
       
      JSONArray responseArray = new JSONArray(makeCall("search", search.replace(" ", "+")));
      ArrayList finalArray = new ArrayList();
      
      String cleanResult;
      JSONObject it;
      for(int i=0; i<responseArray.length(); i++)
      {
          it = responseArray.getJSONObject(i);
          cleanResult = cleaner.cleanTextObject(it.getString("message"));
          long timestamp = Long.parseLong(it.getString("timestamp").trim());
          int id = Integer.parseInt(it.getString("ID").trim());
          finalArray.add(new TextObject(context, cleanResult, it.getString("user"), timestamp, id));
      }
      
      return finalArray;
    }catch (Exception ex)
   {
       System.out.println("Server Comunication Exception: " + ex.getMessage());
   }
   
   return new ArrayList();
  }
}


