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

public class Event implements Comparable<Event>{

    private final String title;
    private final LocalTime startTime;
    private final LocalTime endTime;

    //Constructor
    public Event(String title, LocalTime startTime, LocalTime endTime) {

        //Error handling
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("The starting time must be before the ending time.");
        }

        this.title = title;
        this.startTime = startTime;
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

    //Actual helper fucntion to determine if a collision exists between events
    public boolean overlaps(Event other) {
        return (this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime));
    }

    //Since I'm changing from an ArrayList to a TreeSet for optimization,
    //we now need this compareTo function
    @Override
    public int compareTo(Event other) {
        int cmp = this.startTime.compareTo(other.startTime);
        return (cmp != 0) ? cmp : this.endTime.compareTo(other.endTime);
    }
    
}
