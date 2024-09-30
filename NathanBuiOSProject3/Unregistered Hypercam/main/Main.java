package schedule;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import schedule.schedulers.*;

public class Main 
{
    public static final boolean PROJ_DEBUG = false;

    public static int clock = 0;
    private static final Scheduler[] AVAILABLE_SCHEDULERS = 
    {
            new FCFS(),
            new RR(),
            new SPN()
    };

    public static void main(String[] args) 
    {
        if (args.length < 2) 
        {
            System.out.println("ERROR: Require a path for both scheduler and process files");
            System.exit(0);
        }

        try 
        {
            // Initialize the scheduler from file
            Scanner schedulerFile = new Scanner(new File(args[0]));
            schedulerFile.useDelimiter("[=\n]");

            Scheduler scheduler = null;
            String name = schedulerFile.nextLine();

            for (Scheduler s : AVAILABLE_SCHEDULERS)
                if (s.equals(name))
                    scheduler = s;

            if (scheduler == null) 
            {
                System.out.printf("WARNING: No scheduler with name %s using FCFS\n", name);
                scheduler = AVAILABLE_SCHEDULERS[0];
            }

            scheduler.initialize(schedulerFile);
            schedulerFile.close();

            // Read in processes from file and queue them
            LinkedList<Process> processes = new LinkedList<>();

            Scanner procFile = new Scanner(new File(args[1]));
            while (procFile.hasNextLine()) {
                Scanner procInfo = new Scanner(procFile.nextLine());
                procInfo.useDelimiter("\\s+");

                processes.add(new Process(procInfo));
            }
            procFile.close();

            // Pulse the scheduler forward one time unit at a time
            int currentIndex = 0;
            final int PROCESS_COUNT = processes.size();
            while (PROCESS_COUNT != scheduler.getCompletedProcesses()) 
            {
                // Check the process list for processes to queue
                if (currentIndex < processes.size() && processes.get(currentIndex).ARRIVAL == clock)
                    scheduler.enqueue(processes.get(currentIndex++), clock);

                // Advance the scheduler 1 time unit
                scheduler.step(clock);

                // Advance the clock
                clock++;
            }

            // Print out process stats
            for (Process p : processes)
                p.printStats();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}