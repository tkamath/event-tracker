package tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class EventTracker {

	private Timer timer;
	
	private static int NUM_SECONDS_IN_MINUTE = 60;
	// Upper bound (inclusive) on number of seconds in user-requested time interval
	protected static int LIMIT = 5 * NUM_SECONDS_IN_MINUTE;
	
	// Record of number of occurrences of event at each time t_i seconds,
	// where 0 <= i < <LIMIT>
	private List<Integer> eventCountList = new ArrayList<>();
			
	
	public EventTracker(int seconds) {
		timer = new Timer();
		// Schedule task to run every second 
		timer.schedule(new EventTrackerTask(), 0, seconds * 1000); // delay in milliseconds
	}
	
	// Increment current time t by 1 s with each passing second
	// (t is zero-indexed, to align with indexing of <eventCountList>)
	class EventTrackerTask extends TimerTask {
		@Override
		public void run() {
			if (eventCountList.size() == LIMIT) {
				// Remove first entry of <eventCountList>
				int firstElement = eventCountList.remove(0); 
			}
			// Append empty entry to <eventCountList>
			eventCountList.add(0); 
		}
	}
	
	public static void main(String[] args) {
		// Create timer that operates every second
		EventTracker eventTracker = new EventTracker(1);
	}
	
	// User can call this method to signal that event has occurred
	public void signalEventOccurrence() {
		if (eventCountList.isEmpty()) {
			eventCountList.add(0);
		}
		int lastIndex = eventCountList.size() - 1;
		eventCountList.set(lastIndex, eventCountList.get(lastIndex) + 1);
	}
	
	// User can call this method to get number of occurrences of event
	// in past <numSecondsInInterval> seconds
	public int getNumOccurrences(int numSecondsInInterval) {
		if (numSecondsInInterval < 0 || numSecondsInInterval > LIMIT) {
			throw new IllegalArgumentException(
					"Number of seconds must be between 0 and " + LIMIT + " (inclusive)");
		}
		if (numSecondsInInterval == 0 || eventCountList.isEmpty()) {
			return 0;
		}

		int rangeEndIndex = eventCountList.size();
		// Set <rangeStartIndex> to 0 if number of seconds in user-requested time interval 
		// exceeds number of entries in <eventCountList>
		int rangeStartIndex = Math.max(0, rangeEndIndex - numSecondsInInterval);
		return eventCountList.subList(rangeStartIndex, rangeEndIndex).stream().mapToInt(z -> z).sum();
	}
	
}
