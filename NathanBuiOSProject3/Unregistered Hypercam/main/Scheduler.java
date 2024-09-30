package schedule;

import java.util.LinkedList;
import java.util.Scanner;

public abstract class Scheduler 
{
    public final String NAME;
    private int completedProcesses = 0;

    // Process info
    protected Process runningProcess = null;
    protected LinkedList<Process> blockedProcesses = new LinkedList<Process>();

    // Setup
    public Scheduler(String name) 
    {
        NAME = name;
    }

    public abstract void initialize(Scanner settings);

    // Process handling
    public abstract void enqueue(Process process, int clock);

    public abstract Process dequeue(int clock);

    protected abstract void onProcessStep(int clock);

    public void step(int clock) 
    {
        // Update the blocked list
        for (int i = 0; i < blockedProcesses.size(); i++) {
            Process blockedProcess = blockedProcesses.pop();
            blockedProcess.step();

            switch (blockedProcess.state) 
            {
                case BLOCKING:
                    blockedProcesses.addLast(blockedProcess);
                    break;

                case READY:
                    enqueue(blockedProcess, clock);
                    break;

                case COMPLETE:
                    completedProcesses++;
                    runningProcess.setEndTime(clock);

                    if (Main.PROJ_DEBUG)
                        System.out.printf("[%04d] - Finished Process [%s]\n", Main.clock, blockedProcess);
                    break;

                default:
                    System.out.println("ERROR: Invalid State");
                    break;
            }
        }

        // Check if a process is running or nop
        if (runningProcess == null)
            return;

        // Advance the process and check state
        runningProcess.step();
        switch (runningProcess.state) 
        {
            case RUNNING:
                onProcessStep(clock);
                break;

            case BLOCKING:
                blockedProcesses.addLast(runningProcess);
                if (Main.PROJ_DEBUG)
                    System.out.printf("[%04d] - Blocked  Process [%s]\n", Main.clock, runningProcess);

                runningProcess = dequeue(clock);
                if (runningProcess != null && Main.PROJ_DEBUG)
                    System.out.printf("[%04d] - Running  Process [%s]\n", Main.clock, runningProcess);
                break;

            case COMPLETE:
                completedProcesses++;
                runningProcess.setEndTime(clock);

                if (Main.PROJ_DEBUG)
                    System.out.printf("[%04d] - Finished Process [%s]\n", Main.clock, runningProcess);

                runningProcess = dequeue(clock);
                if (runningProcess != null && Main.PROJ_DEBUG)
                    System.out.printf("[%04d] - Running  Process [%s]\n", Main.clock, runningProcess);
                break;

            default:
                System.out.println("ERROR: Invalid State");
                break;
        }
    }

    // Comparison & info
    public int getCompletedProcesses() 
    {
        return completedProcesses;
    }

    public boolean equals(String name) 
    {
        return NAME.equals(name);
    }
}