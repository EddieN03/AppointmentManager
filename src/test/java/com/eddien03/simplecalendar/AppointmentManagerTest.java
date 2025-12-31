package com.eddien03.simplecalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NavigableSet;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit Tests for the Core 4 Features needed for this assignment 
 */

public class AppointmentManagerTest {

    private AppointmentManager manager;

    @BeforeEach
    void setUp() {
        manager = new AppointmentManager();
    }

    @Test
    void testAddAndListAllEventsForADay() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 31, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 11, 0);

        manager.addEvent("Meeting", start, end);

        NavigableSet<Event> events = manager.listADaysEvents(LocalDate.of(2025, 12, 31));
        assertEquals(1, events.size());
        Event event = events.first();
        assertEquals("Meeting", event.getTitle());
        assertEquals(LocalTime.of(10, 0), event.getStartTime());
        assertEquals(LocalTime.of(11, 0), event.getEndTime());
    }

    @Test
    void testListTodaysRemainingEvents() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 10, 0);
        LocalDateTime end = start.plusHours(1);

        manager.addEvent("Morning Meeting", start, end);

        NavigableSet<Event> remaining = manager.listTodaysRemainingEvents();
        // Depending on current time, there may be 0 remaining events
        if (LocalTime.now().isBefore(end.toLocalTime())) {
            assertFalse(remaining.isEmpty());
            Event e = remaining.first();
            assertEquals("Morning Meeting", e.getTitle());
        }
    }

    @Test
    void testListEventsForSpecificDay() {
        LocalDateTime start1 = LocalDateTime.of(2025, 12, 31, 9, 0);
        LocalDateTime end1 = start1.plusHours(1);
        LocalDateTime start2 = LocalDateTime.of(2025, 12, 31, 14, 0);
        LocalDateTime end2 = start2.plusHours(1);

        manager.addEvent("Morning Event", start1, end1);
        manager.addEvent("Afternoon Event", start2, end2);

        NavigableSet<Event> events = manager.listADaysEvents(LocalDate.of(2025, 12, 31));
        assertEquals(2, events.size());
    }

    @Test
    void testFindNextAvailableSlot() {
        LocalDateTime start1 = LocalDateTime.of(2025, 12, 31, 9, 0);
        LocalDateTime end1 = start1.plusHours(1);
        LocalDateTime start2 = LocalDateTime.of(2025, 12, 31, 11, 0);
        LocalDateTime end2 = start2.plusHours(1);

        manager.addEvent("Event 1", start1, end1);
        manager.addEvent("Event 2", start2, end2);

        // Looking for a 30-min slot on the same day
        var slotOpt = manager.findNextAvailableSlot(LocalDate.of(2025, 12, 31), Duration.ofMinutes(30));
        assertTrue(slotOpt.isPresent());

        LocalTime[] slot = slotOpt.get();
        // It should be before 9:00 or between 10:00-11:00
        assertTrue(slot[1].isBefore(LocalTime.of(9, 0)) ||
                   (slot[0].isAfter(LocalTime.of(10, 0)) && slot[1].isBefore(LocalTime.of(11, 0))) ||
                   slot[0].isAfter(LocalTime.of(12, 0)));
    }

    @Test
    void testOverlappingEventsThrowsException() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 31, 10, 0);
        LocalDateTime end = start.plusHours(1);

        manager.addEvent("Event 1", start, end);

        // Overlapping event
        LocalDateTime overlapStart = LocalDateTime.of(2025, 12, 31, 10, 30);
        LocalDateTime overlapEnd = LocalDateTime.of(2025, 12, 31, 11, 30);

        assertThrows(IllegalArgumentException.class, () -> {
            manager.addEvent("Overlap Event", overlapStart, overlapEnd);
        });
    }
    
}
