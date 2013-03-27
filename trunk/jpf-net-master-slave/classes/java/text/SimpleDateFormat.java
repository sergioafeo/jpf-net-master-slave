package java.text;


import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * (incomplete) model class for java.text.SimpleDate. See Format for details
 * about the native formatter delegation
 */
public class SimpleDateFormat extends DateFormat {
	
  // see DecimalFormat comments why we use explicit init0()'s

  private native void init0();
  private native void init0(String pattern);
  private native void init0(int timeStyle, int dateStyle);

  public SimpleDateFormat () {
    init0();
  }

  public SimpleDateFormat (String pattern) {
    init0(pattern);
  }
  
  public SimpleDateFormat (String pattern, Locale locale) {
	    init0(pattern);
  }  

  SimpleDateFormat (int timeStyle, int dateStyle, Locale locale){
    init0(timeStyle, dateStyle);
  }

  // unfortunately we can't override the DateFormat.format(String) because
  // it is final, and hence the compiler can do a INVOKE_SPECIAL
  native String format0 (long dateTime);
  
  public StringBuffer format (Date date, StringBuffer sb, FieldPosition pos) {
    // we don't do FieldPositions yet

    return new StringBuffer(format0(0));
  }


  public Date parse (String arg0, ParsePosition arg1) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void setTimeZone(TimeZone zone) {  
  }

}
