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
 * 3) GUI
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
                    break;   
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, try again");;
            }
        }

        manager.saveToCSV(CSV_FILE);
        System.out.println("Events saved. Goodbye!");
        scanner.close();
        
    }

    //Helper Functions

    private static void addEventOnCLI(AppointmentManager manager, Scanner scanner, DateTimeFormatter formatter) {
        String title;
        LocalDateTime start;
        LocalDateTime end;

        //Loop for Title
        while (true) {
            System.out.println("Enter event title (blank to cancel): ");
            System.out.println("Please note \",\" will be replaced with \"-\"\n");

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
            System.out.println("Enter start time (blank to cancel): ");
            System.out.println("Please note the format is: yyyy-MM-dd HH:mm\n");

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
            System.out.println("Enter end time (blank to cancel): ");
            System.out.println("Please note the format is: yyyy-MM-dd HH:mm\n");

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
                    return;
                }

        }

        //Loop for the day they need to choose now
        while (true) {
            System.out.println("Enter end time (blank to cancel): ");
            System.out.println("Please note the format is: yyyy-MM-dd\n");

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
                    return;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Try again.");
            }
        }

    }

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
    


    
      
}
