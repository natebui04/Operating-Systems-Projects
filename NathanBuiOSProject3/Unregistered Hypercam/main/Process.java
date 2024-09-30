package schedule;

import java.util.LinkedList;
import java.util.Scanner;

public class Process 
{
    private static int nextPID = 0;

    public final int PID;
    public final int ARRIVAL, DURATION;
    private LinkedList<Timer> activities = new LinkedList<Timer>();

    public enum State { READY, RUNNING, BLOCKING, COMPLETE };
    public State state = State.READY;

    // Stats
    private int startTime = -1, endTime = -1;
    private int enqueueTime, totalResponseTime = 0;
    private int serviceTime, serviceCount = 0;

    public Process(Scanner info) 
    {
        PID = nextPID++;
        ARRIVAL = info.nextInt();

        for (int i = 1; info.hasNext(); i++) 
        {
            Timer timer = new Timer(info.nextInt());
            activities.addLast(timer);

            serviceTime += timer.getValue() * (i % 2);
            serviceCount += (i % 2);
        }

        DURATION = activities.getFirst().getValue();
        burst = (double) DURATION;
    }

    // Perform a step
    public void step() 
    {
        Timer timer = activities.getFirst();
        timer.tick();

        if (timer.reachedZero())
        {
            activities.removeFirst();

            if (activities.size() == 0)
                state = State.COMPLETE;
            else
                switch (state) 
                {
                    case RUNNING:
                        state = State.BLOCKING;
                        break;

                    case BLOCKING:
                        state = State.READY;
                        break;

                    default:
                        System.out.println("ERROR: Invalid State");
                        break;
                };
        }
    }

    // Process time estimation
    private double burst;

    public int estimateDuration()
    {
        return DURATION;
    }

    public double estimateDuration(double alpha) 
    {
        double curBurst = burst;
        burst = (alpha * activities.getFirst().getValue()) + ((1D - alpha) * burst);
        return curBurst;
    }

    // Stats
    public void setStartTime(int clock) 
    {
        startTime = (startTime == -1) ? clock : startTime;
    }

    public void setEndTime(int clock)
    {
        endTime = clock;
    }

    public void setEnqueueTime(int clock) 
    {
        enqueueTime = clock;
    }

    public void setDequeueTime(int clock) 
    {
        totalResponseTime += (clock - enqueueTime);
    }

    // ToString
    public void printStats() 
    {
        System.out.printf("Process %02d\n\t"
                        + "Start Time: %03d\n\t"
                        + "Finish Time: %03d\n\t"
                        + "Service Time: %03d\n\t"
                        + "Turnaround Time: %03d\n\t"
                        + "Turnaround Time (Normalized): %03.3f\n\t"
                        + "Average Response Time: %.3f\n",
                PID,
                startTime,
                endTime,
                serviceTime,
                endTime - startTime,
                (double) (endTime - startTime) / (double) serviceTime,
                (double) totalResponseTime / (double) serviceCount
        );
    }

    public String toString() 
    {
        final int time = (activities.size() == 0) ? 0 : activities.peek().getValue();
        return String.format("ID: %02d - TA: %03d - CA: %03d", PID, ARRIVAL, time);
    }
}