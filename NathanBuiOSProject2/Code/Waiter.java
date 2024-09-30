package project;

import java.util.concurrent.*;

public class Waiter implements Runnable {
    // Waiter-only amenities
    private static Semaphore kitchenSemaphore = new Semaphore(1);

    // Waiter info
    private int waiterId;
    private boolean hasServed;

    // Order signaling
    public Semaphore orderSemaphore = new Semaphore(1);

    public Waiter(int givenWaiterId) 
    {
        // Waiter info
        waiterId = givenWaiterId;
        hasServed = false;

        // Order signaling
        try 
        {
            orderSemaphore.acquire();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    public void run() 
    {
        System.out.printf("Waiter %d : Assigned to Table %c\n", waiterId, 'A' + waiterId);
        while (!hasServed || Main.unservedTables[waiterId] > 0) {
            try 
            {
                System.out.printf("Waiter %d : Awaiting Table %c\n", waiterId, 'A' + waiterId);

                receiveOrder();
                System.out.printf("Waiter %d : Called to Table %c\n", waiterId, 'A' + waiterId);

                int customerId = Main.servedCustomers[waiterId].getCustomerId();
                System.out.printf("Waiter %d : Took Customer %d's order\n", waiterId, customerId);

                // Go to the kitchen to start order
                System.out.printf("Waiter %d : Placing Customer %d's order\n", waiterId, customerId);
                kitchenSemaphore.acquire();
                System.out.printf("Waiter %d : Enters the Kitchen\n", waiterId);
                Thread.sleep(100 + Main.random.nextInt(401));
                kitchenSemaphore.release();

                // Wait for the order to be complete
                System.out.printf("Waiter %d : Exits the Kitchen and Awaits Customer %d's order\n", waiterId, customerId);
                Thread.sleep(300 + Main.random.nextInt(701));

                // Go in and get the customer's order
                kitchenSemaphore.acquire();
                System.out.printf("Waiter %d : Enters the Kitchen & Retrieves Customer %d's order\n", waiterId, customerId);
                Thread.sleep(100 + Main.random.nextInt(401));
                System.out.printf("Waiter %d : Exiting the Kitchen\n", waiterId);
                kitchenSemaphore.release();
                System.out.printf("Waiter %d : Delivering Customer %d's order\n", waiterId, customerId);

                // Present the customer with the order
                System.out.printf("Waiter %d : Presenting Customer %d their order\n", waiterId, customerId);
                Main.decrementUnservedTables(waiterId);
                transmitOrder();
                
                hasServed = true;
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    public void leavingRoutine() 
    {
        System.out.printf("Waiter %d : Cleaned Table %c and left\n", waiterId, 'A' + waiterId);
    }

    // Customer-waiter signaling

    private void transmitOrder() 
    {
        Main.servedCustomers[waiterId].orderSemaphore.release();
    }

    public void receiveOrder() 
    {
        try 
        {
            orderSemaphore.acquire();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}