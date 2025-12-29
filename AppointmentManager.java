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

    private final Map<LocalDate, List<Event>> eventsEachDay;

    public AppointmentManager() {
        this.eventsEachDay = new HashMap<>();
    }

    //TODO: Add events to the Calendar, will have the splitting logic
    public void AddEvent() {
        Event newEvent = new Event(null, null, null);
    }

    /*
     * Get the events of a given day without checking if the time has passed for said events
     * 1) Listing all events for the day -> Since today is also a day
     * 3) List all events for any specified day
     */
    public List<Event> listADaysEvents(LocalDate aDay) {
        return eventsEachDay.get(aDay);
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
