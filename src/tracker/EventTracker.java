package tracker;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class EventTracker {

	private Timer timer;
	
	// Number of seconds (zero-indexed) since program was started 
	// or since timer last reset
	private int numSecondsSinceStartTime = -1;
	
	private static int NUM_SECONDS_IN_MINUTE = 60;
	// Upper bound (inclusive) on number of seconds in user-requested time interval
	protected static int LIMIT = 5 * NUM_SECONDS_IN_MINUTE;
	
	// Record of number of occurrences of event at each time t_i seconds,
	// where 0 <= i < <LIMIT>
	private List<Integer> eventCountList = 
			Arrays.stream(new int[LIMIT]).boxed().collect(Collectors.toList());
	
	public EventTracker(int seconds) {
		timer = new Timer();
		// Schedule task to run every second 
		timer.schedule(new EventTrackerTask(), 0, seconds*1000); // delay in milliseconds
	}
	
	// Increment current time t by 1 s with each passing second
	// (t is zero-indexed, to align with indexing of <eventCountList>)
	class EventTrackerTask extends TimerTask {
		@Override
		public void run() {
			// If t has reached <LIMIT>, reset t to 0 and clear the record of events
			if ((numSecondsSinceStartTime += 1) > LIMIT) {
				numSecondsSinceStartTime = 0;
				eventCountList.clear();
			}
		}
	}
	
	public static void main(String[] args) {
		// Create timer that operates every second
		EventTracker eventTracker = new EventTracker(1);
	}
	
	// User can call this method to signal that event has occurred
	public void signalEventOccurrence() {
		// If current time t is still at -1, set <currentSecond> to 0;
		// otherwise, set <currentSecond> to t
		int currentSecond = Math.max(0, numSecondsSinceStartTime);
		int numEventsInCurrentSecond = eventCountList.get(currentSecond);
		eventCountList.set(currentSecond, numEventsInCurrentSecond + 1);
	}
	
	// User can call this method to get number of occurrences of event
	// in past <numSecondsInInterval> seconds
	public int getNumOccurrences(int numSecondsInInterval) {
		if (numSecondsInInterval < 0 || numSecondsInInterval > LIMIT) {
			throw new IllegalArgumentException(
					"Number of seconds must be between 0 and " + LIMIT + " (inclusive)");
		}
		if (numSecondsInInterval == 0) {
			return 0;
		}
		// If current time t is still at -1, operate as if t = 0
		int rangeEndIndex = Math.max(0, numSecondsSinceStartTime) + 1;
		// Set <startTime> to 0 if number of seconds in user-requested time interval 
		// exceeds current time t
		int rangeStartIndex = Math.max(0, rangeEndIndex - numSecondsInInterval);
		return eventCountList.subList(rangeStartIndex, rangeEndIndex).stream().mapToInt(z -> z).sum();
	}
	
}
