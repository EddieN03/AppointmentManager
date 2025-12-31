# Option 2: Simple Calendar Application
Create an application for a simple Calendar and appointment management. The user
should be able to create an event by specifying its title, start time, and end time. The
application should prevent overlapping events.
The applications should support:
• Listing all events for the day
• List all remaining events for the day
• List all events for any specified day
• Provide the next available slot of a specified size for today or the specified day.
All data can be kept in memory or in a file. A database is not necessary.
Use any programming language to build the application. The application can be
command-line-based; a UI is not necessary, but it can be included if preferred.
Submit your response by putting it in a public GitHub repo.

# Prerequisities
Ensure the following are installed on your system:

1) Java JDK 19 or newer, you can verify with ```java -version```

2) Apache Maven 3.9+, you can verify with ```mvn -version```

If running with VS Code make sure to have the Java Extension Pack

# Building the Project

From the project root directory (where pom.xml is located), run:

```mvn clean compile```

# Running the Application

After compiling, run the application using Maven:

```mvn exec:java -Dexec.mainClass="com.eddien03.simplecalendar.SimpleCalendarApp"```

If you want to run with Jave after compilation:

```java -cp target/classes com.eddien03.simplecalendar.SimpleCalendarApp```

# Using the Application

Follow the prompts to interact with the calendar.

Input Formats

* Date: yyyy-MM-dd

* Date & Time: yyyy-MM-dd HH:mm

* Duration: Number of minutes (integer)

# Data Persistence

* Events are automatically loaded from events.csv on startup (if present)

* Events are saved back to events.csv when you choose Save and Exit

* events.csv has already been included in the project root

# Running Tests

Automated tests are written using JUnit 5. To run them all:

```mvn test```

# Limitations

* This is a command-line application
* Time based tests dpeend on the current system clock
* Events are stored locally in CSV format

# License

This project is inteded for educational purposes.

# Author

Developed by Edward Navarro
