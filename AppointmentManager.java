/*
 * Structure:
 * =========
 * Key = Dates
 * Value = ArrayList of Events of the Day
 * Events should NOT overlap
 * 
 */


import java.util.*;
import java.time.*;

public class AppointmentManager {

    /*
     * This is a small helper object used for event validation
     */
    private static class TempEvent {
        final LocalDate date;
        final LocalTime start;
        final LocalTime end;

        TempEvent(LocalDate date, LocalTime start, LocalTime end) {
            this.date = date;
            this.start = start;
            this.end = end;
        }
    
    }

    private final Map<LocalDate, List<Event>> eventsEachDay;

    public AppointmentManager() {
        this.eventsEachDay = new HashMap<>();
    }

    /*
     * This fucntion is the main driver for adding events to the calendar.
     * It utilizes various helper functions as well as the TempEvent object
     */
    public void addEvent(String title, LocalDateTime start, LocalDateTime end) {

        //Error handling
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Start must be before end");
        }

        //Do the splitting in a helper function
        List<TempEvent> segments = buildSegments(start, end);

        //Check if all the split events are legal to include
        validateSegments(segments);

        //TODO: Commit them all at once
        for (TempEvent event : segments) {
            
        }
    }

    /*
     * Helper fucntion to split an event across multiple days if needed
     */
    private List<TempEvent> buildSegments(LocalDateTime start, LocalDateTime end) {

        List<TempEvent> result = new ArrayList<>();

        //Logic for splitting the days
        LocalDate currentDate = start.toLocalDate();
        LocalDate lastDate = end.toLocalDate();

        //Use NOT isAfter instead of isBefore to include the end of the event
        while (!currentDate.isAfter(lastDate)) {

            LocalTime currentStart;
            LocalTime currentEnd;

            //Check to see if the current day matches with the start
            //If so set it to the proper time
            //Otherwise set it to midnight
            if (currentDate.isEqual(start.toLocalDate())) {
                currentStart = start.toLocalTime();
            } else {
                currentStart = LocalTime.MIDNIGHT;
            }

            //Check to see if the current day matches with the end
            //If so set it to the proper time
            //Otherwise set it to 23:59:99
            if (currentDate.isEqual(end.toLocalDate())) {
                currentEnd = end.toLocalTime();
            } else {
                currentEnd = LocalTime.MAX;
            }

            //Insert the new temp event into the temp array and increment the day
            result.add(new TempEvent(currentDate, currentStart, currentEnd));

            currentDate = currentDate.plusDays(1);
        }

        return result;

    }

    /*
     * Another helper function, this time to validate all the events
     * on the arraylist to see if they don't overlap with current events
     */
    private void validateSegments(List<TempEvent> segments) {

        for (TempEvent event : segments) {
            List<Event> eventsOfTheDay = listADaysEvents(event.date);

            Event probe = new Event("probe", event.start, event.end);

            //TODO: Check events of that day and see if probe fits, if not throw error
        }

    }

    /*
     * Get the events of a given day without checking if the time has passed for said events
     * 1) Listing all events for the day -> Since today is also a day
     * 3) List all events for any specified day
     */
    public List<Event> listADaysEvents(LocalDate aDay) {
        //Using default of empty set to avoid null
        return eventsEachDay.getOrDefault(aDay, List.of());
    }

    /*
     * 2) List all remaining events for the day
     */
    public List<Event> listTodaysRemainingEvents() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        //Have to convert to a stream in order to use the filter where endTime is after the current time
        return eventsEachDay.get(today).stream().filter(e -> e.getEndTime().isAfter(now)).toList();
    }

    /*
     * 4) Provide the next available slot of a specified size for today OR the specified day.
     */
    public Optional<LocalTime[]> findNextAvailableSlot(LocalDate aDay, Duration howLong) {

        //Variables for the function
        List<Event> eventsToCheck;
        boolean isToday = false;
        //Predefine If there are no events & not today, just suggest midnight + duration (as this is the first possible time)
        LocalTime[] result = {LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plus(howLong)};

        //Cut the events to be only what is currently possible
        if (aDay.isEqual(LocalDate.now())) {
            eventsToCheck= listTodaysRemainingEvents();
            isToday = true;
        } else {
            eventsToCheck = listADaysEvents(aDay);
        }

        
        if (eventsToCheck.isEmpty()) {
            //This could be an &&, but I also wanted to make it so that we avoid the main algo if a future day is empty
            if (isToday) {
                result[0] = LocalTime.now();
                result[1] = LocalTime.now().plus(howLong);
            }
            return Optional.of(result);
        }

        /*
        * For each remaining event:
        *   if there does NOT exist an event where (the event's endtime + howLong) does NOT intersect the next event 
        *       return that time slot
        *
        * if nothing has been returned, return empty set
        */

        for (Event event : eventsToCheck) {
            //TODO: finish the logic in here
        }

        
        return Optional.empty();
    }
    
}
