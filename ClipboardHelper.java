
import java.awt.datatransfer.*;
import java.awt.Toolkit; 

public class ClipboardHelper
{
  private Clipboard clipboard;
  private Transferable content;
  
  ClipboardHelper()
  {
    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  }
  
  public String getClipboardContents()
  {
    try
    {
      Transferable content = clipboard.getContents(null);
      if (content != null)
      {
        Object obj = content.getTransferData(DataFlavor.stringFlavor);
        return (String) obj;
      }
      else
      {
         return "";
      }
    }
    catch(Exception e)
    {
       return ""; 
    }
  }
  
}
