
class ObjectCleaner
{
  private int textNumWordsPerLine;

  ObjectCleaner()
  {
    this.textNumWordsPerLine = 5;
  }

  ObjectCleaner(int textNumWordsPerLine)
  {
    this.textNumWordsPerLine = textNumWordsPerLine;
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

  public String cleanTextObject(String txt)
  {
    txt = replace(txt,"\n", "");
    String [] words = txt.split(" ");
    String result = "";
    int wordCount = 0;
    int lines = (words.length/this.textNumWordsPerLine)+1;

    for(int word=0; word<words.length; word++)
    {
      if (words[word].matches("http.*"))
      {
        words[word] = "[URL]";
      }
    }

    for(int i=0; i<lines; i++)
    {
      for(int j=0; j<this.textNumWordsPerLine; j++)
      {
        if(wordCount < words.length)
        {
          if(wordCount == words.length-1)
          {
            result += words[wordCount];
          }else
          {
            result += words[wordCount] + " ";
          }
          wordCount ++;
        }
        else
        {
          break;
        }
      }
      if(i!=lines-1)
      {
        result += "\n";
      }
    }

    return result;
  }
}

