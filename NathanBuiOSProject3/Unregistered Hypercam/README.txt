1. Main Class:
This is the main class that orchestrates the entire simulation. It reads input files to configure the scheduler and processes, then runs the simulation, and finally prints out the statistics for each process.

2. Process Class:
Represents a simulated process in the scheduling simulation. It contains information about the process, such as arrival time, duration, and a list of timers representing the activities the process will perform. It also includes methods for simulating the progress of the process, estimating duration, and printing process statistics.

3. Scheduler Class:
An abstract class that serves as the base for different scheduling algorithms. It defines the common structure and methods that each specific scheduler (e.g., FCFS, RR, SPN) must implement. It manages the queue of processes and their states.

4. Timer Class:
A simple class representing a timer that counts down from an initial value. Used in the simulation to model the duration of activities within a process.

5. FCFS, RR, SPN Classes:
Concrete classes that extend the Scheduler class, implementing specific scheduling algorithms (First-Come-First-Serve, Round Robin, Shortest Process Next). They provide the logic for enqueuing, dequeuing, and handling process steps according to their respective scheduling algorithms.

Compilation and Execution:
To compile and run the entire project, follow these steps:

Compilation:
Open a terminal or command prompt.
Navigate to the directory containing your Java files.
Compile all Java files using the javac command. 

For example:
javac schedule/*.java schedule/schedulers/*.java

Running the Program:
After successful compilation, run the program using the java command. Provide the scheduler file and process file as command-line arguments. 

For example:
java schedule.Main scheduler.txt process.txt
Replace scheduler.txt and process.txt with the actual names of your scheduler and process files.
Ensure that the input files (scheduler.txt and process.txt) are correctly formatted and placed in the same directory as your Java files. Adjust the file paths and names accordingly.

Note: Make sure you have the Java Development Kit (JDK) installed on your system to compile and run Java programs.