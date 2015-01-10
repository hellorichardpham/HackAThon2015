//generateTime is a method to give us the current time of day using the Joda library
import java.util.ArrayList;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTest {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		ArrayList<String> timeInputs = new ArrayList<String>();
		ArrayList<DateTime> dateTimeInputs = new ArrayList<DateTime>(); 
		
		//Get user input for test times as Strings
		for(int i =0; i < 4; i++) {
			timeInputs.add(scan.next());
		}
		
		//Turn strings into DateTime Objects.
		dateTimeInputs = generateTime(timeInputs);
		
		System.out.println(findNextBusTime(dateTimeInputs) + " wow");
	}

	// Change this to return a date
	//Fills dateTimeInputs with test times that we will compare with current time.
	public static ArrayList<DateTime> generateTime(ArrayList<String> timeInputs) {
		ArrayList<DateTime> dateTimeInputs = new ArrayList<DateTime>();
		DateTime fakeTime;
		for(int i =0; i < 4; i++) {
			dateTimeInputs.add(fakeTime = DateTime.parse(timeInputs.get(i),
					DateTimeFormat.forPattern("HH:mm:ss")));
		}
		return dateTimeInputs;
	}
	
	public static String findNextBusTime(ArrayList<DateTime> dateTimeInputs) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
		DateTime currentTime = new DateTime();
		
		for(int i =0; i < dateTimeInputs.size(); i++) {
			if(isDuringOrAfter(currentTime, dateTimeInputs.get(i))) {
				return fmt.print(dateTimeInputs.get(i));
			}
		}
		
		return "Error: Bus Not Found";
	}

	public static boolean isDuringOrAfter(DateTime currentTime,
			DateTime targetTime) {
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
		
		System.out.println("curr: " + fmt.print(currentTime));
		System.out.println("target: " + fmt.print(targetTime));

		int result = DateTimeComparator.getTimeOnlyInstance().compare(
				targetTime,currentTime);

		switch (result) {
		case 0:
			System.out.println("Current time is equal to target time. " + fmt.print(targetTime));
			return true;
		case 1:
			System.out.println("Current time is after or equal to target time. " + fmt.print(targetTime));
			return true;
		case -1:
			System.out.println("returning false for " + fmt.print(targetTime));
			return false;
			default:
				System.out.println("wow");
				return false;
		}

	}
}
