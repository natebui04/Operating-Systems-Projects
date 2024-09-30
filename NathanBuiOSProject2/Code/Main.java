package project;

import java.util.Random;

public class Main {
    // Global variables
    public static Random random;

    public static Waiter[] waitStaff = new Waiter[3];
    public static Customer[] servedCustomers = new Customer[3];
    public static int[] unservedTables = {0, 0, 0};

    // Entry point
    public static void main(String[] args) throws Exception 
    {
        random = new Random(System.nanoTime());

        // Create waiters
        Thread[] waiterThreads = new Thread[3];
        for (int i = 0; i < waiterThreads.length; i++) 
        {
            waitStaff[i] = new Waiter(i);

            waiterThreads[i] = new Thread(waitStaff[i]);
            waiterThreads[i].start();
        }

        // Create customer threads
        Thread[] customerThreads = new Thread[40];
        for (int i = 0; i < customerThreads.length; i++) 
        {
            customerThreads[i] = new Thread(new Customer(i));
            customerThreads[i].start();
        }

        // Wait for all customer threads to finish
        for (Thread customerThread : customerThreads)
            customerThread.join();

        // Perform leaving routine for waiters and wait for their threads to finish
        for (int i = 0; i < 3; i++) 
        {
            waitStaff[i].performLeavingRoutine();
            waiterThreads[i].join();
        }
    }

    // Synchronized methods

    public static synchronized void incrementUnservedTables(int index) 
    {
        unservedTables[index]++;
    }

    public static synchronized void decrementUnservedTables(int index) 
    {
        unservedTables[index]--;
    }
}