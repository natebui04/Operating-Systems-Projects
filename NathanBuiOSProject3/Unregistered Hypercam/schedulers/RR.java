package schedule.schedulers;

import java.util.LinkedList;
import java.util.Scanner;
import schedule.Scheduler;
import schedule.Main;
import schedule.Process;

public class RR extends Scheduler {
    // Queue to store processes
    private LinkedList<Process> queue = new LinkedList<>();

    // Timer and quantum for Round Robin scheduling
    private int timer, quantum = 100;

    // Constructor
    public RR() {
        super("RR");
    }

    // Initialize method to set quantum from settings
    public void initialize(Scanner settings) {
        // Bounds checking
        if (!settings.hasNext())
            return;

        // Read key and value, and check if it's a valid key
        String key = settings.next();
        String val = settings.next();

        if (!key.equals("quantum")) {
            System.out.printf("WARNING: Unrecognized key %s with value %s\n", key, val);
            System.exit(0);
        }

        // Read in the value of the quantum
        quantum = Integer.valueOf(val);
    }

    // Enqueue a process for execution
    public void enqueue(Process process, int clock) {
        if (runningProcess == null) {
            // Initialize timer and set the process to running
            timer = quantum;
            process.setStartTime(clock);
            process.state = Process.State.RUNNING;
            runningProcess = process;

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Running Process [%s]\n", Main.clock, process);
        } else {
            // Enqueue the process for later execution
            process.state = Process.State.READY;
            process.setEnqueueTime(clock);
            queue.addLast(process);

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Enqueued Process [%s]\n", Main.clock, process);
        }
    }

    // Dequeue the next process for execution
    public Process dequeue(int clock) {
        if (queue.size() == 0)
            return null;

        // Reset timer and dequeue the next process
        timer = quantum;
        Process process = queue.pop();
        process.setStartTime(clock);
        process.setDequeueTime(clock);
        process.state = Process.State.RUNNING;

        if (Main.PROJ_DEBUG)
            System.out.printf("[%04d] - Dequeued Process [%s]\n", Main.clock, process);

        return process;
    }

    // Method to handle process steps
    public void onProcessStep(int clock) {
        timer--;

        if (timer == 0) {
            // Timeout: enqueue the running process and get the next one
            enqueue(runningProcess, clock);

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Timedout Process [%s]\n", Main.clock, runningProcess);

            runningProcess = dequeue(clock);

            if (runningProcess != null && Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Running Process [%s]\n", Main.clock, runningProcess);
        }
    }
}