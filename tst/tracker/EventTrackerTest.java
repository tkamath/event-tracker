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
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetNumOccurrencesForInvalidNegativeInput() {
		eventTracker.getNumOccurrences(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetNumOccurrencesForInvalidPositiveInput() {
		eventTracker.getNumOccurrences(1000);
	}
	
	@Test
	public void testGetNumOccurrencesAtStart() {
		assertEquals(0, eventTracker.getNumOccurrences(5));
	}
	
	@Test
	public void testGetNumOccurrencesWithPopulatedEventCountArray() {
		List<Integer> testEventCountList = getEmptyEventCountList();
		testEventCountList.set(0, 10);
		testEventCountList.set(60, 15);
		testEventCountList.set(180, 7);
		testEventCountList.set(240, 0);
		testEventCountList.set(299, 2);
		
		try {
			PowerMockito.field(EventTracker.class, "eventCountList").set(eventTracker, testEventCountList);
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, LIMIT);
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
	
	@Test
	public void testSignalEventOccurrence() {
		try {
			eventTracker = PowerMockito.mock(EventTracker.class);
			PowerMockito.field(EventTracker.class, "eventCountList").set(eventTracker, getEmptyEventCountList());
			PowerMockito.doCallRealMethod().when(eventTracker).signalEventOccurrence();
			PowerMockito.doCallRealMethod().when(eventTracker).getNumOccurrences(Matchers.anyInt());

			IntStream.range(0, 10).forEach(i -> eventTracker.signalEventOccurrence());

			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, 5);
			assertEquals(10, eventTracker.getNumOccurrences(5));

			IntStream.range(0, 10).forEach(i -> eventTracker.signalEventOccurrence());
			PowerMockito.field(EventTracker.class, "numSecondsSinceStartTime").set(eventTracker, 10);
			
			assertEquals(20, eventTracker.getNumOccurrences(10));
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private List<Integer> getEmptyEventCountList() {
		return Arrays.stream(new int[LIMIT]).boxed().collect(Collectors.toList());
	}
}
