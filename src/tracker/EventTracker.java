package tracker;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class EventTracker {

	private Timer timer;
	private int numSecondsSinceStartTime = -1;
	
	private static int NUM_SECONDS_IN_MINUTE = 60;
	protected static int LIMIT = 5 * NUM_SECONDS_IN_MINUTE;
	private List<Integer> eventCountList = 
			Arrays.stream(new int[LIMIT]).boxed().collect(Collectors.toList());
	
	public EventTracker(int seconds) {
		timer = new Timer();
		timer.schedule(new EventTrackerTask(), 0, seconds*1000);
	}
	
	class EventTrackerTask extends TimerTask {
		@Override
		public void run() {
			if ((numSecondsSinceStartTime += 1) == LIMIT) {
				numSecondsSinceStartTime = 0;
				eventCountList.clear();
			}
		}
	}
	
	public static void main(String[] args) {
		EventTracker eventTracker = new EventTracker(NUM_SECONDS_IN_MINUTE);
	}
	
	public void signalEventOccurrence() {
		int currentSecond = Math.max(0, numSecondsSinceStartTime);
		int numEventsInCurrentSecond = eventCountList.get(currentSecond);
		eventCountList.set(currentSecond, numEventsInCurrentSecond + 1);
	}
	
	public int getNumOccurrences(int x) {
		if (x < 0 || x > LIMIT) {
			throw new IllegalArgumentException("Number of minutes must be between 0 and " + LIMIT + ", inclusive");
		}
		if (x == 0) {
			return 0;
		}
		int currentSecond = Math.max(0, numSecondsSinceStartTime);
		int startTime = Math.max(0, currentSecond - x);
		return eventCountList.subList(startTime, currentSecond).stream().mapToInt(z -> z).sum();
	}
	
}
