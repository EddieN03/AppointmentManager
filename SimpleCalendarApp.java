/*
 * Simple Calendar Application
 * ===========================
 * This application should support:
 * 1) Listing all events for the day
 * 2) List all remaining events for the day
 * 3) List all events for any specified day
 * 4) Provide the next available slot of a specified size for today OR the specified day.
 * 
 * Current Issues:
 * ==============
 * - Actually setting up this main file
 * - Presentation
 * 
 * Stretch Goals:
 * ==============
 * 1) Test cases (Semi Done)
 * 2) GUI
 *
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NavigableSet;
import java.util.Scanner;

public class SimpleCalendarApp {

    private static final String CSV_FILE = "events.csv";

    public static void main(String[] args) {

        AppointmentManager manager = new AppointmentManager();
        manager.loadFromCSV(CSV_FILE);

        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.println("Welcome to the Simple Calendar App!");
        boolean running = true;

        while (running) {
            
            System.out.println("\nSelect an option:");
            System.out.println("\n1) Add an event:");
            System.out.println("\n2) List ALL events for today:");
            System.out.println("\n3) List all REMAINING events for today:");
            System.out.println("\n4) List ALL events for ANY day:");
            System.out.println("\n5) Find the next available slot of a given size on any day:");
            System.out.println("\n6) Save and Exit");
            System.out.println("==============================================================");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addEventOnCLI(manager, scanner, formatter);
                    break;
                case "2":
                    checkDayCLI(manager, scanner, true);
                    break;
                case "3":
                    checkRemainingCLI(manager);
                    break;
                case "4":
                    checkDayCLI(manager, scanner, false);
                    break;
                case "5":
                    findNextSlotCLI(manager, scanner);
                    break;   
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");;
            }
        }

        manager.saveToCSV(CSV_FILE);
        System.out.println("Events saved. Goodbye!");
        scanner.close();
        
    }

    //Helper Functions

    /*
     * This function allows the User to create an event for their calendar.
     */
    private static void addEventOnCLI(AppointmentManager manager, Scanner scanner, DateTimeFormatter formatter) {
        String title;
        LocalDateTime start;
        LocalDateTime end;

        //Loop for Title
        while (true) {
            System.out.println("Please note \",\" will be replaced with \"-\"");
            System.out.println("Enter event title (blank to cancel): ");

            //We can just use title here since its also a string
            title = scanner.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println("Cancelling...");
                return;
            }

            //Change commas to dashes since comma is for the CSV
            title = title.replace(",", "-");
            break;
        }

        //Loop for Start Time
        while (true) {
            System.out.println("Please note the format is: yyyy-MM-dd HH:mm");
            System.out.println("Enter start time (blank to cancel): ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Cancelling...");
                return;
            }

            try {
                start = LocalDateTime.parse(input, formatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Try again.");
            }
        }

        //Loop for End Time
        while (true) {
            System.out.println("Please note the format is: yyyy-MM-dd HH:mm");
            System.out.println("Enter end time (blank to cancel): ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Cancelling...");
                return;
            }

            try {
                end = LocalDateTime.parse(input, formatter);
                if (end.isBefore(start)) {
                    System.out.println("End must be after start.");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Try again.");
            }
        }

        //Finally commit the valid event
        try {
            manager.addEvent(title, start, end);
            System.out.println("Event added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add event: " + e.getMessage());
        }

    }

    /*
     * This function connects the CLI to listADaysEvents to get the events of either
     * today or any day the user chooses. It's used in both option 2 and 4.
     */
    private static void checkDayCLI(AppointmentManager manager, Scanner scanner, boolean isToday) {
        
        //Since we need something for all of today's events just use this to skip all the inputting
        if (isToday) {
            var events = manager.listADaysEvents(LocalDate.now());

            if (events.isEmpty()) {
                    
                    System.out.println("No events today.");
                
                } else {

                    System.out.println("Here are today's events:");
                    for (Event event : events) {
                        System.out.println(" - " + event.getTitle()
                                            + " " + event.getStartTime()
                                            + "-" + event.getEndTime());
                    }

                }

            return;

        }

        //Loop for the day they need to choose now
        while (true) {
            System.out.println("Please note the format is: yyyy-MM-dd");
            System.out.println("Enter the day (blank to cancel): ");
            

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {

                System.out.println("Cancelling...");
                return;      

            }

            try {

                LocalDate date = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
                var events = manager.listADaysEvents(date);

                if (events.isEmpty()) {
                    System.out.println("No events on " + date);
                } else {
                    System.out.println("Events on " + date + ":");
                    for (Event event : events) {
                        System.out.println(" - " + event.getTitle()
                                            + " " + event.getStartTime()
                                            + "-" + event.getEndTime());
                    }
                    
                }

                return;
                
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Try again.");
            }
        }

    }

    /*
     * This function connects the CLI to be able to find all remaining events
     * left for today.
     */
    private static void checkRemainingCLI(AppointmentManager manager) {

        NavigableSet<Event> remaining = manager.listTodaysRemainingEvents();
        
        if (remaining.isEmpty()) {

            System.out.println("There are no remaining events for today.");
        
        } else {
        
            System.out.println("Here are today's remaining events:");

                    for (Event event : remaining) {
                        System.out.println(" - " + event.getTitle()
                                            + " " + event.getStartTime()
                                            + "-" + event.getEndTime());
                    }
        
        }

    }

    /*
     * This functions connects the CLI to be able to find the next available time slot
     * for an event.
     */
    private static void findNextSlotCLI(AppointmentManager manager, Scanner scanner) {

        LocalDate day;
        Duration duration;

        //Loop to get the date
        while (true) {
            System.out.println("Please note the format is: yyyy-MM-dd");
            System.out.println("Enter the day (blank to cancel): ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Cancelling...");
                return;                
            }

            try {
                day = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Try again.");
            }
        }

        //Loop to get the duration
        while (true) {
            System.out.println("Enter the duration in minutes (as a number): ");

            String input = scanner.nextLine().trim();

            try {

                long minutes = Long.parseLong(input);

                //Prevent a fake number
                if (minutes <= 0) {
                    System.out.println("Duration must be positive.");
                    continue;
                }

                duration = Duration.ofMinutes(minutes);
                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }

        //Now that we have every, time to look for the timeslot
        var slot = manager.findNextAvailableSlot(day, duration);

        //Tell the user if there was a slot or not
        if (slot.isPresent()) {
            LocalTime[] timeSlot = slot.get();
            System.out.println("The next available slot on " + day + ": " + timeSlot[0] + " to " + timeSlot[1]);            
        } else {
            System.out.println("There was no available slot of that duration on " + day);
        }

    }

}
