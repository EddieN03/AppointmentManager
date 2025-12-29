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
    
}
