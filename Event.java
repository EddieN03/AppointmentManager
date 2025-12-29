/*
 * Structure:
 * =========
 * String title;
 * LocalTime starttime;
 * LocalTime endtime;
 * Events should NOT overlap
 * 
 */

import java.time.LocalTime;

public class Event {

    private final String title;
    private final LocalTime startTime;
    private final LocalTime endTime;

    //Constructor
    public Event(String title, LocalTime starTime, LocalTime endTime) {

        //Error handling
        if (starTime.isAfter(endTime)) {
            throw new IllegalArgumentException("The starting time must be before the ending time.");
        }

        this.title = title;
        this.startTime = starTime;
        this.endTime = endTime;
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    
}
