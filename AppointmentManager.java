/*
 * Structure:
 * =========
 * Key = Dates
 * Value = TreeSet of Events of the Day
 * Events should NOT overlap
 * 
 */


import java.util.*;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class AppointmentManager {

    //Instance Variable & Constructor

    private final Map<LocalDate, NavigableSet<Event>> eventsEachDay;

    public AppointmentManager() {
        this.eventsEachDay = new HashMap<>();
    }

    //Core Requirements

    /*
     * Get a COPY of the events of a given day without checking if the time has passed for said events
     * 1) Listing all events for the day -> Since today is also a day
     * 3) List all events for any specified day
     */
    public NavigableSet<Event> listADaysEvents(LocalDate aDay) {
        //Using default of empty list to avoid null
        return new TreeSet<>(eventsEachDay.getOrDefault(aDay, new TreeSet<>()));
    }

    /*
     * 2) List all remaining events for the day
     */
    public NavigableSet<Event> listTodaysRemainingEvents() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        //Have to convert to a stream in order to use the filter where endTime is after the current time
        //Just like listADaysEvents also have a default to empty list
        return eventsEachDay.getOrDefault(today, new TreeSet<>())
                            .stream()
                            .filter(e -> e.getEndTime().isAfter(now))
                            .collect(Collectors.toCollection(TreeSet::new));
    }

    /*
     * 4) Provide the next available slot of a specified size for today OR the specified day.
     */
    public Optional<LocalTime[]> findNextAvailableSlot(LocalDate aDay, Duration howLong) {

        //Variables for the function
        NavigableSet<Event> eventsToCheck;
        boolean isToday = false;
        //Predefine If there are no events & not today, just suggest midnight + duration (as this is the first possible time)
        LocalTime[] result = {LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plus(howLong)};

        //Cut the events to be only what is currently possible
        if (aDay.isEqual(LocalDate.now())) {
            eventsToCheck = listTodaysRemainingEvents();
            isToday = true;
        } else {
            eventsToCheck = listADaysEvents(aDay);
        }

        //First sanity check if the day to check is empty if so we can add it whenever
        if (eventsToCheck.isEmpty()) {
            //This could be an &&, but I also wanted to make it so that we avoid the main algo if a future day is empty
            if (isToday) {
                result[0] = LocalTime.now();
                result[1] = LocalTime.now().plus(howLong);
            }

            //Check in both cases if the duration is too long, if so return nothing
            if (result[1].isAfter(LocalTime.MAX)) {
                return Optional.empty();
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
            //If the end is before the next event's start we are done
            if (result[0].plus(howLong).isBefore(event.getStartTime())) {

                //Another overcap check here
                if (result[0].plus(howLong).isAfter(LocalTime.MAX)) {
                    return Optional.empty();
                }

                return Optional.of(result);
            }

            //Move result to 1 minute after the last checked event since the edges can't overlap 
            result[0] = event.getEndTime().plusMinutes(1);
            result[1] = result[0].plus(howLong);

            //Last and final overcap check
            if (result[1].isAfter(LocalTime.MAX)) {
                return Optional.empty();
            }

        }
        
        return Optional.empty();
    }

    //Remaining Public API Methods

    /*
     * This function is the main driver for adding events to the calendar.
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

        //Commit all parts of an event at once
        for (TempEvent event : segments) {

            //If there wasn't any events on that day just start that section with
            //computeIfAbsent
            NavigableSet<Event> eventsOfTheDay = eventsEachDay.computeIfAbsent(event.date, d -> new TreeSet<>());
            
            //Then add it to whatever we just got
            eventsOfTheDay.add(new Event(title, event.start, event.end));
        }

    }

    //Persistence Functionality

    /*
     * This function saves all the events on the calendar.
     */
    public void saveToCSV(String filename) {

        //Get the formatting set up for the file
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        //Check if the file name is valid
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            //If so then for every day there are events we write then one by one into the file
            for (var entry : eventsEachDay.entrySet()) {


                for (Event event : entry.getValue()) {

                    String line = String.join(",",
                                                event.getTitle(),
                                                event.getStartTime().format(formatter),
                                                event.getEndTime().format(formatter));
                    writer.write(line);
                    writer.newLine();

                }
                
            }
        } catch (IOException e) {
            //This shouldn't happen since the code will start with events.csv, but just in case
            e.printStackTrace();
        }
    }

    /*
     * This function loads all the events onto the calendar.
     */
    public void loadFromCSV(String filename) {

        //Get the formatting set up for the file
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            //As long as something exists on the line, get it and put it into the calendar
            String line;
            while ((line = reader.readLine()) != null) {

                //If there somehow is a misalignment skip it
                String[] parts = line.split(",", 3);
                if (parts.length != 3) continue;

                //Parse the split, then add the event to the correct date
                String title = parts[0];
                LocalDateTime start = LocalDateTime.parse(parts[1], formatter);
                LocalDateTime end = LocalDateTime.parse(parts[2], formatter);

                addEvent(title, start, end);

            }

        } catch (IOException e) {
            //This shouldn't happen since the code will start with events.csv, but just in case
            e.printStackTrace();
        }

    }


    //Helper Functions

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

            NavigableSet<Event> eventsOfTheDay = listADaysEvents(event.date);

            Event probe = new Event("probe", event.start, event.end);

            Event lowerEvent = eventsOfTheDay.lower(probe);
            Event higherEvent = eventsOfTheDay.higher(probe);


            //Check events of that day and see if probe fits, if not throw error
            if ((lowerEvent != null && lowerEvent.overlaps(probe)) || 
                (higherEvent != null && probe.overlaps(higherEvent))) {

                    throw new IllegalArgumentException("Event overlaps on " + event.date);
                
            }

        }

    }

    //Inner Class

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
    
}
