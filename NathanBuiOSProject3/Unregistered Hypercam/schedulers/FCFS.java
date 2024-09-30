package schedule.schedulers;

import java.util.LinkedList;
import schedule.Scheduler;
import schedule.Main;
import schedule.Process;

public class FCFS extends Scheduler {
    // Queue to store processes
    private LinkedList<Process> queue = new LinkedList<>();

    // Constructor
    public FCFS() {
        super("FCFS");
    }

    // Enqueue a process for execution
    public void enqueue(Process process, int clock) {
        if (runningProcess == null) {
            // Run the process immediately if no other process is running
            process.setStartTime(clock);
            process.state = Process.State.RUNNING;
            runningProcess = process;

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Running Process [%s]\n", Main.clock, process);
        } else {
            // Enqueue the process for later execution
            process.setEnqueueTime(clock);
            process.state = Process.State.READY;
            queue.addLast(process);

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Enqueued Process [%s]\n", Main.clock, process);
        }
    }

    // Dequeue the next process for execution
    public Process dequeue(int clock) {
        if (queue.size() == 0)
            return null;

        // Dequeue the next process in the queue
        Process process = queue.pop();
        process.setStartTime(clock);
        process.setDequeueTime(clock);
        process.state = Process.State.RUNNING;

        if (Main.PROJ_DEBUG)
            System.out.printf("[%04d] - Dequeued Process [%s]\n", Main.clock, process);

        return process;
    }

    // Unused methods
    @Override
    public void initialize(java.util.Scanner settings) {}

    @Override
    public void onProcessStep(int clock) {}
}