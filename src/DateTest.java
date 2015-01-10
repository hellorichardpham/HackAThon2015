//generateTime is a method to give us the current time of day using the Joda library
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class DateTest {

	public static void main(String[] args) {
		generateTime();
	}

	//Change this to return a date
	public static void generateTime() {
		DateTime dt = new DateTime();
		//Format the time so there are two digits for Hour/Minute/Seconds to meet metro pattern
		DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss");
		System.out.println(fmt.print(dt));
	}

}
