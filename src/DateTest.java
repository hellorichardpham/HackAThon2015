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
		System.out.println(getTimeOfDay());
	}
	
	public static int getTimeOfDay() {
		
		DateTime today = new DateTime();
		String day = today.dayOfWeek().getAsText().toLowerCase();
		
		switch(day) {
		case "sunday":
			return 2;
		case "saturday":
			return 1;
		case "monday":
			return 0;
		case "tuesday":
			return 0;
		case "wednesday":
			return 0;
		case "thursday":
			return 0;
		case "friday":
			return 0;
		}
		return 0;
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
