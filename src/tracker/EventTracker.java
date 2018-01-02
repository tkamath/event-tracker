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
		// Add empty entry to <eventCountList>
		eventCountList.add(0);
		timer = new Timer();
		// Schedule task to run every second 
		timer.schedule(new EventTrackerTask(), 0, seconds * 1000); // delay in milliseconds
	}
	
	// Until <LIMIT> seconds have passed since the start of the program,
	// add one empty entry to <eventCountList> with each passing second
	// Once <eventCountList> has reached <LIMIT> in size, with each passing
	// second, remove the first entry and add an empty entry to the end
	class EventTrackerTask extends TimerTask {
		@Override
		public void run() {
			// If <eventCountList> size is <LIMIT>, remove first entry
			if (eventCountList.size() == LIMIT) {
				eventCountList.remove(0);
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
	public synchronized void signalEventOccurrence() {
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
		// Set <rangeStartIndex> to 0 if number of seconds in user-requested time interval 
		// exceeds number of entries in <eventCountList>
		if (numSecondsInInterval == 0) {
			return 0;
		}
		synchronized (this) {
			int rangeEndIndex = eventCountList.size();
			if (rangeEndIndex == 0) {
				return 0;
			}
			// Set <rangeStartIndex> to 0 if number of seconds in user-requested time interval 
			// exceeds number of entries in <eventCountList>
			int rangeStartIndex = Math.max(0, rangeEndIndex - numSecondsInInterval);
			return eventCountList.subList(rangeStartIndex, rangeEndIndex).stream().mapToInt(z -> z).sum();
		}
		
	}
	
}
