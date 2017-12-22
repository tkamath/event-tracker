# Event Tracker

This project is a Java program that helps track the number of events that happened during a specified window of time. (In this particular program, time granularity is in seconds, and the upper bound of the time window is 5 minutes.)

## Dependencies

This project runs on Java 8, and the `lib` directory contains the JAR files that need to be added to the project build path.

## Source Code

In the `EventTracker` class, the constructor initializes a `Timer` instance that schedules the task of incrementing the time counter to run every second (i.e. every 1000 milliseconds).
That is, the inner class `EventTrackerTask` implements the abstract `TimerTask` class, overriding the `run` method to increment the time counter by 1, and, once the time counter has reached the upper bound `LIMIT`, to reset the time counter to 0 and clear the record of event occurrences.

The user can signal that the event has occurred, by invoking the public method `signalEventOccurrence()`. The record of occurrences of the event is stored in the private variable, `eventCountList`, which is initialized as an empty `List` of size `LIMIT`, and updated as the user signals that the event has 
occurred. For example, if the user signals 5 times at time t = 0 s and 20 times at t = 10 s, the element at index 0 of `eventCountList` is set to 5, and the element at index 10 is set to 20.

The user can request the number of event occurrences in the past *x* seconds by making the method call `getNumOccurrences(x)`, where *x* can be up to `LIMIT` (inclusive). 


## Test Class

The test class `EventTrackerTest`, located in the `tst` directory, uses JUnit and PowerMock to test the event-tracking program. This project uses PowerMock specifically because of the ability of PowerMock to mock out private fields, which is very helpful in simulating different values of the current time and different values of the elements in `eventCountList` in the test instance of `EventTracker`.
In order to simulate the process of invoking `signalEventOccurrence()` multiple times, `EventTrackerTest` also applies the Stream API, available from Java 8 onwards.



