package schedule.schedulers;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import schedule.Scheduler;
import schedule.Main;
import schedule.Process;

public class SPN extends Scheduler
{
    // Queue to store processes
    private LinkedList<Process> queue = new LinkedList<>();

    // Flag to determine if service time is given
    private boolean service = false;

    // Alpha parameter for estimating process duration
    private double alpha = 1.0D;

    // Constructor
    public SPN()
    {
        super("SPN");
    }

    // Initialize method to set configuration parameters from settings
    public void initialize(Scanner settings)
    {
        while (settings.hasNextLine())
        {
            // Bounds checking
            if (!settings.hasNext())
                break;

            // Read key and value
            String key = settings.next();
            String val = settings.next();

            // Set appropriate value based on the key
            if (key.equals("service_given"))
                service = Boolean.valueOf(val);
            else if (key.equals("alpha"))
                alpha = Double.valueOf(val);
            else
                System.out.printf("WARNING: Unrecognized key %s with value %s\n", key, val);
        }
    }

    // Comparator for comparing processes based on estimated duration
    private Comparator<Process> processComparator = (process1, process2) ->
    {
        if (service)
            return process1.estimateDuration() - process2.estimateDuration();

        double delta = process1.estimateDuration(alpha) - process2.estimateDuration(alpha);
        return Double.compare(delta, 0D);
    };

    // Enqueue a process for execution
    public void enqueue(Process process, int clock)
    {
        if (runningProcess == null)
        {
            // Run the process immediately if no other process is running
            process.setStartTime(clock);
            process.state = Process.State.RUNNING;
            runningProcess = process;

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Running Process [%s]\n", Main.clock, process);
        }
        else
        {
            // Enqueue the process for later execution
            process.setEnqueueTime(clock);
            process.state = Process.State.READY;
            queue.addLast(process);
            queue.sort(processComparator);

            if (Main.PROJ_DEBUG)
                System.out.printf("[%04d] - Enqueued Process [%s]\n", Main.clock, process);
        }
    }

    // Dequeue the next process for execution
    public Process dequeue(int clock)
    {
        if (queue.isEmpty())
            return null;

        // Dequeue the process with the shortest estimated duration
        Process process = queue.pop();
        process.setStartTime(clock);
        process.setDequeueTime(clock);
        process.state = Process.State.RUNNING;

        if (Main.PROJ_DEBUG)
            System.out.printf("[%04d] - Dequeued Process [%s]\n", Main.clock, process);

        return process;
    }

    // Unused method for handling process steps
    @Override
    public void onProcessStep(int clock)
    {
        // This method is not used in the SPN scheduler
    }
}