package com.newgen.ws.util;

public class XMLParser {
	private String parseString;
	  private String copyString;
	  private int IndexOfPrevSrch;

	  public XMLParser()
	  {
	  }

	  public XMLParser(String paramString)
	  {
	    this.copyString = new String(paramString);
	    this.parseString = toUpperCase(this.copyString, 0, 0);
	  }

	  public void setInputXML(String paramString)
	  {
	    if (paramString != null)
	    {
	      this.copyString = new String(paramString);
	      this.parseString = toUpperCase(this.copyString, 0, 0);
	      this.IndexOfPrevSrch = 0;
	    }
	    else
	    {
	      this.parseString = null;
	      this.copyString = null;
	      this.IndexOfPrevSrch = 0;
	    }
	  }

	  public String getServiceName()
	  {
	    try
	    {
	      return new String(this.copyString.substring(this.parseString.indexOf(toUpperCase("<Option>", 0, 0)) + new String(toUpperCase("<Option>", 0, 0)).length(), this.parseString.indexOf(toUpperCase("</Option>", 0, 0))));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	      throw localStringIndexOutOfBoundsException;
	    }
	  }

	  public String getServiceName(char paramChar)
	  {
	    try
	    {
	      if (paramChar == 'A')
	        return new String(this.copyString.substring(this.parseString.indexOf("<AdminOption>".toUpperCase()) + new String("<AdminOption>".toUpperCase()).length(), this.parseString.indexOf("</AdminOption>".toUpperCase())));
	      return "";
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "NoServiceFound";
	  }

	  public boolean validateXML()
	  {
	    try
	    {
	      return (this.parseString.indexOf("<?xml version=\"1.0\"?>".toUpperCase()) == -1);
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return false;
	  }

	  public String getValueOf(String paramString)
	  {
	    try
	    {
	      return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">") + paramString.length() + 2, this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">")));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String getValueOf(String paramString1, String paramString2)
	  {
	    try
	    {
	      if (paramString2.equalsIgnoreCase("Binary"))
	      {
	        int i = this.copyString.indexOf("<" + paramString1 + ">");
	        if (i == -1)
	          return "";
	        int j = this.copyString.lastIndexOf("</" + paramString1 + ">");
	        i += new String("<" + paramString1 + ">").length();
	        return this.copyString.substring(i, j);
	      }
	      return "";
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String getValueOf(String paramString, boolean paramBoolean)
	  {
	    try
	    {
	      if (paramBoolean)
	        return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">") + paramString.length() + 2, this.parseString.lastIndexOf("</" + toUpperCase(paramString, 0, 0) + ">")));
	      return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">") + paramString.length() + 2, this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">")));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String getValueOf(String paramString, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramInt1 >= 0)
	      {
	        int i = this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">", paramInt1);
	        if ((i > paramInt1) && (((paramInt2 == 0) || (paramInt2 >= i))))
	          return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">", paramInt1) + paramString.length() + 2, i));
	      }
	      return "";
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public int getStartIndex(String paramString, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramInt1 >= 0)
	      {
	        int i = this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">", paramInt1);
	        if ((i >= paramInt1) && (((paramInt2 == 0) || (paramInt2 >= i))))
	          return (i + paramString.length() + 2);
	      }
	      return -1;
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return -1;
	  }

	  public int getEndIndex(String paramString, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramInt1 >= 0)
	      {
	        int i = this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">", paramInt1);
	        if ((i > paramInt1) && (((paramInt2 == 0) || (paramInt2 >= i))))
	          return i;
	      }
	      return -1;
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return -1;
	  }

	  public int getTagStartIndex(String paramString, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramInt1 >= 0)
	      {
	        int i = this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">", paramInt1);
	        if ((i >= paramInt1) && (((paramInt2 == 0) || (paramInt2 >= i))))
	          return i;
	      }
	      return -1;
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return -1;
	  }

	  public int getTagEndIndex(String paramString, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramInt1 >= 0)
	      {
	        int i = this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">", paramInt1);
	        if ((i > paramInt1) && (((paramInt2 == 0) || (paramInt2 >= i))))
	          return (i + paramString.length() + 3);
	      }
	      return -1;
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return -1;
	  }

	  public String getFirstValueOf(String paramString)
	  {
	    try
	    {
	      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">");
	      return new String(this.copyString.substring(this.IndexOfPrevSrch + paramString.length() + 2, this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">")));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String getFirstValueOf(String paramString, int paramInt)
	  {
	    try
	    {
	      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">", paramInt);
	      return new String(this.copyString.substring(this.IndexOfPrevSrch + paramString.length() + 2, this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">", paramInt)));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String getNextValueOf(String paramString)
	  {
	    try
	    {
	      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(paramString, 0, 0) + ">", this.IndexOfPrevSrch + paramString.length() + 2);
	      return new String(this.copyString.substring(this.IndexOfPrevSrch + paramString.length() + 2, this.parseString.indexOf("</" + toUpperCase(paramString, 0, 0) + ">", this.IndexOfPrevSrch)));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public int getNoOfFields(String paramString)
	  {
	    int i = 0;
	    int j = 0;
	    try
	    {
	      paramString = toUpperCase(paramString, 0, 0) + ">";
	      while (this.parseString.indexOf("<" + paramString, j) != -1)
	      {
	        ++i;
	        j = this.parseString.indexOf("</" + paramString, j);
	        if (j == -1)
	          break;
	        j += paramString.length() + 2;
	      }
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return i;
	  }

	  public int getNoOfFields(String paramString, int paramInt1, int paramInt2)
	  {
	    int i = 0;
	    int j = paramInt1;
	    try
	    {
	      paramString = toUpperCase(paramString, 0, 0) + ">";
	     
	      do
	      {
	        j = this.parseString.indexOf("</" + paramString, j) + paramString.length() + 2;
	        if ((j != -1) && (((j <= paramInt2) || (paramInt2 == 0))))
	          ++i;
	        label91: if (this.parseString.indexOf("<" + paramString, j) == -1)
	          break;
	      }
	      while ((j < paramInt2) || (paramInt2 == 0));
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return i;
	  }

	  public String convertToSQLString(String paramString)
	  {
	    try
	    {
	      for (int i = paramString.indexOf("["); i != -1; i = paramString.indexOf("[", i + 2))
	        paramString = paramString.substring(0, i) + "[[]" + paramString.substring(i + 1, paramString.length());
	    }
	    catch (Exception localException1)
	    {
	    }
	    try
	    {
	      for (int j = paramString.indexOf("_"); j != -1; j = paramString.indexOf("_", j + 2))
	        paramString = paramString.substring(0, j) + "[_]" + paramString.substring(j + 1, paramString.length());
	    }
	    catch (Exception localException2)
	    {
	    }
	    try
	    {
	      for (int k = paramString.indexOf("%"); k != -1; k = paramString.indexOf("%", k + 2))
	        paramString = paramString.substring(0, k) + "[%]" + paramString.substring(k + 1, paramString.length());
	    }
	    catch (Exception localException3)
	    {
	    }
	    paramString = paramString.replace('?', '_');
	    return paramString;
	  }

	  public String getValueOf(String paramString1, String paramString2, int paramInt1, int paramInt2)
	  {
	    try
	    {
	      if (paramString2.equalsIgnoreCase("Binary"))
	      {
	        int i = this.copyString.indexOf("<" + paramString1 + ">", paramInt1);
	        if (i == -1)
	          return "";
	        int j = this.copyString.indexOf("</" + paramString1 + ">", paramInt1);
	        if (j > paramInt2)
	          return "";
	        i += new String("<" + paramString1 + ">").length();
	        return this.copyString.substring(i, j);
	      }
	      return "";
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	    }
	    return "";
	  }

	  public String toUpperCase(String paramString, int paramInt1, int paramInt2)
	    throws StringIndexOutOfBoundsException
	  {
	    String str = "";
	    try
	    {
	      int i = paramString.length();
	      char[] arrayOfChar = new char[i];
	      paramString.getChars(0, i, arrayOfChar, 0);
	      while (i-- > 0)
	        arrayOfChar[i] = Character.toUpperCase(arrayOfChar[i]);
	      str = new String(arrayOfChar);
	    }
	    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
	    {
	    }
	    return str;
	  }

	  public String changeValue(String paramString1, String paramString2, String paramString3)
	  {
	    try
	    {
	      String str1 = paramString1.toUpperCase();
	      String str2 = new String("<" + paramString2 + ">").toUpperCase();
	      int i = str1.indexOf(str2) + str2.length();
	      int j = str1.indexOf(new String("</" + paramString2 + ">").toUpperCase());
	      String str3 = paramString1.substring(0, i);
	      str3 = str3 + paramString3 + paramString1.substring(j);
	      return str3;
	    }
	    catch (Exception localException)
	    {
	    }
	    return "";
	  }

	  public void changeValue(String paramString1, String paramString2)
	  {
	    try
	    {
	      int j;
	      String str2;
	      String str1 = "<" + paramString1 + ">".toUpperCase();
	      int i = this.parseString.indexOf(str1);
	      if (i > -1)
	      {
	        i += str1.length();
	        j = this.parseString.indexOf("</" + paramString1 + ">".toUpperCase());
	        str2 = this.copyString.substring(0, i);
	        this.copyString = str2 + paramString2 + this.copyString.substring(j);
	      }
	      else
	      {
	        j = i = this.parseString.lastIndexOf("</");
	        str2 = this.copyString.substring(0, i);
	        this.copyString = str2 + "<" + paramString1 + ">" + paramString2 + "</" + paramString1 + ">" + this.copyString.substring(j);
	      }
	      this.parseString = toUpperCase(this.copyString, 0, 0);
	    }
	    catch (Exception localException)
	    {
	    }
	  }

	  public String toString()
	  {
	    return this.copyString;
	  }

	  public String ParseFieldValue(String paramString1, String paramString2, String paramString3)
	  {
	    try
	    {
	      int i = this.parseString.indexOf("<" + toUpperCase(paramString1, 0, 0) + ">" + "<" + toUpperCase(paramString2, 0, 0) + ">" + toUpperCase(paramString3, 0, 0) + "</" + toUpperCase(paramString2, 0, 0) + ">");
	      if (i != -1)
	      {
	        i += paramString1.length() + 2;
	        return new String(this.copyString.substring(i, this.parseString.indexOf("</" + toUpperCase(paramString1, 0, 0) + ">", i)));
	      }
	      return "";
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	      localStringIndexOutOfBoundsException.printStackTrace();
	    }
	    return "";
	  }

	  public String getValueListXml(String paramString)
	  {
	    try
	    {
	      int i = this.parseString.indexOf("<NAME>" + paramString.toUpperCase() + "</NAME>");
	      if (i != -1)
	      {
	        int j = this.parseString.indexOf("<VALUELIST>", i);
	        if (j != -1)
	        {
	          int k = this.parseString.indexOf("</FIELD>", i);
	          if ((k != -1) && (k > j))
	            return this.copyString.substring(j, k);
	        }
	      }
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	      localStringIndexOutOfBoundsException.printStackTrace();
	    }
	    return null;
	  }

	  public String getFieldValue(String paramString)
	  {
	    try
	    {
	      StringBuffer localStringBuffer = new StringBuffer("<NAME>");
	      localStringBuffer.append(paramString.toUpperCase());
	      localStringBuffer.append("</NAME>");
	      int i = this.parseString.indexOf(localStringBuffer.toString());
	      if (i != -1)
	      {
	        i = this.parseString.indexOf("<VALUE>", i);
	        String str = this.copyString.substring(i + 7, this.parseString.indexOf("</VALUE>", i));
	        return str;
	      }
	    }
	    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
	    {
	      localStringIndexOutOfBoundsException.printStackTrace();
	    }
	    return null;
	  }
}
