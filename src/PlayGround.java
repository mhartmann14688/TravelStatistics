import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class PlayGround {
	 private static SimpleDateFormat dateFormat = new SimpleDateFormat ("ha");
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date d = new Date(32400000);
		System.out.println(d.toString());
		System.out.println(d.getTimezoneOffset());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println( dateFormat.format(new Date(32400000)));

		DateTime x = new DateTime();
		System.out.println(x.secondOfDay().get());
		
		/*try {
			//System.out.println(DateUtils.parseDate("2015/05/05 09:39:00.000 GMT", Locale.GERMANY, "yyyy/MM/dd HH:mm:ss.SSS z"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
