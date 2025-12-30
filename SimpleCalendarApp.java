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
import java.util.NavigableSet;


public class SimpleCalendarApp {

    //TODO: All of this
    public static void main(String[] args) {

        AppointmentManager manager = new AppointmentManager();

        //TODO: THIS IS ALL TEMP, move to a JUnit Test File at some point

        // Test Case 1: Add a single-day event
        LocalDateTime start1 = LocalDateTime.of(2025, 12, 31, 10, 0);
        LocalDateTime end1   = LocalDateTime.of(2025, 12, 31, 11, 0);

        manager.addEvent("Meeting", start1, end1);
        System.out.println("Added single-day event: Meeting");

        // List events for Dec 31
        NavigableSet<Event> dec31Events = manager.listADaysEvents(LocalDate.of(2025, 12, 31));
        System.out.println("Events on 2025-12-31:");
        dec31Events.forEach(e -> System.out.println(e.getTitle() + " " + e.getStartTime() + "-" + e.getEndTime()));

        // Test Case 2: Add multi-day event
        LocalDateTime start2 = LocalDateTime.of(2025, 12, 31, 22, 0);
        LocalDateTime end2   = LocalDateTime.of(2026, 1, 1, 2, 0);

        manager.addEvent("Overnight", start2, end2);
        System.out.println("\nAdded multi-day event: Overnight");

        // List events for Dec 31
        System.out.println("Events on 2025-12-31 after Overnight:");
        dec31Events = manager.listADaysEvents(LocalDate.of(2025, 12, 31));
        dec31Events.forEach(e -> System.out.println(e.getTitle() + " " + e.getStartTime() + "-" + e.getEndTime()));

        // List events for Jan 1
        NavigableSet<Event> jan1Events = manager.listADaysEvents(LocalDate.of(2026, 1, 1));
        System.out.println("Events on 2026-01-01:");
        jan1Events.forEach(e -> System.out.println(e.getTitle() + " " + e.getStartTime() + "-" + e.getEndTime()));

        // Test Case 3: Attempt overlapping event
        LocalDateTime start3 = LocalDateTime.of(2025, 12, 31, 10, 30);
        LocalDateTime end3   = LocalDateTime.of(2025, 12, 31, 11, 30);

        try {
            manager.addEvent("OverlapTest", start3, end3);
            System.out.println("ERROR: Overlapping event was added!");
        } catch (IllegalArgumentException ex) {
            System.out.println("\nCorrectly caught overlap: " + ex.getMessage());
        }

        // Test Case 4: List remaining events for today
        NavigableSet<Event> remaining = manager.listTodaysRemainingEvents();
        System.out.println("\nRemaining events for today (" + LocalDate.now() + "):");
        if (remaining.isEmpty()) {
            System.out.println("No remaining events.");
        } else {
            remaining.forEach(e -> System.out.println(e.getTitle() + " " + e.getStartTime() + "-" + e.getEndTime()));
        }


    }
      
}
