//generateTime is a method to give us the current time of day using the Joda library
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTest {

	public static void main(String[] args) {
		generateTime();
	}

	// Change this to return a date
	public static void generateTime() {
		DateTime currentTime = new DateTime();
		String fakeInput = "20:48:00";
		ArrayList<String> timeInputs = new ArrayList<String>();
		ArrayList<DateTime> dateTimeInputs = new ArrayList<DateTime>();
		
		// Format the time so there are two digits for Hour/Minute/Seconds to
		// meet metro pattern
		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
		DateTime fakeTime = DateTime.parse(fakeInput,
				DateTimeFormat.forPattern("HH:mm:ss"));

		System.out.println("Current Time: " + fmt.print(currentTime));
		System.out.println("Fake Time: " + fmt.print(fakeTime));
		isDuringOrAfter(currentTime, fakeTime);
		// System.out.println("Fake Time Before Current Time? " +
		// fakeTime.isBefore(currentTime));
	}

	public static boolean isDuringOrAfter(DateTime currentTime,
			DateTime targetTime) {
		// result == 1 currentTime is after targetTime. result == 0 current ==
		// target result == -1, current is before target
		int result = DateTimeComparator.getTimeOnlyInstance().compare(
				currentTime, targetTime);
		switch (result) {
		case 0:
		case 1:
			System.out.println("Current time is after or equal to target time.");
			return true;
		default:
			System.out.println("Current time is less than target.");
			return false;
		}

	}
}
