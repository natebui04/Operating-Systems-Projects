package project;

import java.util.concurrent.Semaphore;


public class Customer implements Runnable {

	// customer only amenities
	private static String[] tabletype = {"Seasfood", "Steak", "Pasta"};
	private static Semaphore[] table = {new Semaphore(4), new Semaphore(4), new Semaphore(4)};
	private static Semaphore[] place = {new Semaphore(1), new Semaphore(1), new Semaphore(1)};
	private static Semaphore[] queue = {new Semaphore(1), new Semaphore(1), new Semaphore(1)};
	private static int[] queueCount = {0, 0, 0};
	
	private static Semaphore door = new Semaphore(2);
	private static Semaphore register = new Semaphore(1);
	
	// customer info
	private final int id;
	private final int primary, secondary;
	private final int eatingTime;
	
	// order signaling
	public final Semaphore order = new Semaphore(1);
	
	public Customer(int givenID) 
	{
		// customer info
		id = givenID;
		primary = Main.random.nextInt(3);
		secondary = Main.random.nextInt(3);
		eatingTime = 200 + Main.random.nextInt(801);
		
		// order signaling
		try {
			order.acquire();
		}
		catch(Exception e) { 
			e.printStackTrace(); 
		}
	}

	public void run() 
	{
		try {
			System.out.printf("Customer %d : Wants %s\n", id, tabletype[primary]);
			
			// enter restaurant
			System.out.printf("Customer %d : Wants to enter the restaurant\n", id);
			door.acquire();
			System.out.printf("Customer %d : Entered Through Door\n", id);
			door.release();

			// pick a line
			Semaphore selectedQueue;
			int selectedIndex;
			
			if(queueCount[primary] >= 7 && queueCount[secondary] < 7) {
				selectedQueue = queue[secondary];
				selectedIndex = secondary;
			}
			else {
				selectedQueue = queue[primary];
				selectedIndex = primary;
			}

			// queue in selected line
			System.out.printf("Customer %d : Goes to Table %c\n", id, 'A' + selectedIndex);
			incQueueCount(selectedIndex);
			selectedQueue.acquire();
			System.out.printf("Customer %d : Queued in line of Table %c\n", id, 'A' + selectedIndex);
			
			table[selectedIndex].acquire();
			
			Main.incUnserved(selectedIndex);
			
			System.out.printf("Customer %d : Seated at Table %c\n", id, 'A' + selectedIndex);
			selectedQueue.release();
			decQueueCount(selectedIndex);


			// place order with waiter and eat
			System.out.printf("Customer %d : Calls the Waiter\n", id);
			place[selectedIndex].acquire();
			Main.served[selectedIndex] = this;
			System.out.printf("Customer %d : Orders food\n", id);
			txOrder(selectedIndex);
			System.out.printf("Customer %d : Waiting for thier food\n", id);
			rxOrder();
			place[selectedIndex].release();
			
			System.out.printf("Cutsomer %d : Began Eating\n", id);
			Thread.sleep(eatingTime);
			System.out.printf("Cutsomer %d : Finished Eating\n", id);
			
			table[selectedIndex].release();
			System.out.printf("Customer %d : Left Table %c\n", id, 'A' + selectedIndex);

			// claim the register, when free; and then leave the table
			register.acquire();
			System.out.printf("Customer %d : Is paying\n", id);
			register.release();
			System.out.printf("Customer %d : Has Paid\n", id);

			// claim door, when free; leave the register; and then leave the building
			door.acquire();
			System.out.printf("Customer %d : left the restaurant\n", id);
			door.release();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	// customer-waiter signaling
	
	private void txOrder(int index) 
	{
		Main.servers[index].order.release();
	}
	
	public void rxOrder() 
	{
		try { order.acquire(); }
		catch(Exception e) { e.printStackTrace(); }
	}

	// customer info
	
	public int getID() 
	{
		return id;
	}
	
	// syncronized methods
	
	private static synchronized void incQueueCount(int index) 
	{
		queueCount[index]++;
	}

	private static synchronized void decQueueCount(int index) 
	{
		queueCount[index]--;
	}
}
