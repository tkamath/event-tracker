package tracker;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.stream.IntStream;

import static tracker.EventTracker.LIMIT;


@RunWith(PowerMockRunner.class)
@PrepareForTest(EventTracker.class)
@PowerMockRunnerDelegate(Parameterized.class)
public class EventTrackerTest {
	
	EventTracker eventTracker;
	
	@Parameter
	public int expectedEventCount;
	
	@Parameter(1)
	public int numSecondsInTimeInterval;

	
	@Before
	public void init() {
		eventTracker = new EventTracker(LIMIT);
	}
	
	// When invoked with negative argument x, getNumOccurrences method should 
	// throw an IllegalArgumentException
	@Test(expected=IllegalArgumentException.class)
	public void testGetNumOccurrencesForInvalidNegativeInput() {
		eventTracker.getNumOccurrences(-1);
	}
	
	// When invoked with argument x > LIMIT, getNumOccurrences method
	// should throw an IllegalArgumentException
	@Test(expected=IllegalArgumentException.class)
	public void testGetNumOccurrencesForInvalidPositiveInput() {
		eventTracker.getNumOccurrences(LIMIT*2);
	}
	
	// When invoked with argument 0, getNumOccurrences method should return 0
	@Test
	public void testGetNumOccurrencesForInputArgZero() {
		assertEquals(0, eventTracker.getNumOccurrences(0));
	}
	
	// When invoked with valid positive argument x > current time t, 
	// getNumOccurrences method should return getNumOccurrences(t)
	@Test
	public void testGetNumOccurrencesForInputArgHigherThanCurrentTime() {
		List<Integer> testEventCountList = getEmptyEventCountList();
		testEventCountList.set(10, 100);
		try {
			// Simulate user-signaling at time t = 10 s
			PowerMockito.field(EventTracker.class, "eventCountList").set(eventTracker, testEventCountList);
			// Simulate scenario where current time t = 15 s
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, 15);
			assertEquals(100, eventTracker.getNumOccurrences(30));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	// This test simulates user-signaling without invoking signalEventOccurrence method
	@Test
	public void testGetNumOccurrencesWithPopulatedEventCountArray() {
		List<Integer> testEventCountList = getEmptyEventCountList();
		testEventCountList.set(0, 10);
		testEventCountList.set(60, 15);
		testEventCountList.set(180, 7);
		testEventCountList.set(240, 0);
		testEventCountList.set(299, 2);
		
		try {
			// Simulate user-signaling at various points in time
			PowerMockito.field(EventTracker.class, "eventCountList").set(eventTracker, testEventCountList);
			// Simulate scenario where current time = <LIMIT> s
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, LIMIT-1);
			assertEquals(expectedEventCount, eventTracker.getNumOccurrences(numSecondsInTimeInterval));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{0, 0},
			{2, 60},
			{9, 180},
			{24, 240},
			{34, 300}
		});	
	}
	
	// This test simulates user-signaling by invoking signalEventOccurrence method
	@Test
	public void testSignalEventOccurrence() {
		try {
			eventTracker = PowerMockito.mock(EventTracker.class);
			PowerMockito.field(EventTracker.class, "eventCountList").set(eventTracker, getEmptyEventCountList());
			PowerMockito.doCallRealMethod().when(eventTracker).signalEventOccurrence();
			PowerMockito.doCallRealMethod().when(eventTracker).getNumOccurrences(Matchers.anyInt());

			// Simulate 10 incidents of user-signaling
			IntStream.range(0, 10).forEach(i -> eventTracker.signalEventOccurrence());
			// Simulate scenario where current time = 4 s
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, 4);
			assertEquals(10, eventTracker.getNumOccurrences(5));

			// Simulate 10 more incidents of user-signaling
			// (for a total of 20 incidents from start time)
			IntStream.range(0, 10).forEach(i -> eventTracker.signalEventOccurrence());
			// Simulate scenario where current time = 9 s
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, 9);
			assertEquals(20, eventTracker.getNumOccurrences(10));
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	// Test helper method used to mock private field <eventCountList> in EventTracker
	// instance in test methods
	private List<Integer> getEmptyEventCountList() {
		return Arrays.stream(new int[LIMIT]).boxed().collect(Collectors.toList());
	}
}
