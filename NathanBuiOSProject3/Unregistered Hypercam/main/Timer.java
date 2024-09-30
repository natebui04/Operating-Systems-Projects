package schedule;

public class Timer 
{
    private int counter;

    // Constructor
    public Timer(int initialValue) 
    {
        counter = initialValue;
    }

    // Decrease the counter by one
    public void tick() 
    {
        counter--;
    }

    // Check if the counter has reached zero
    public boolean reachedZero() 
    {
        return counter == 0;
    }

    // Get the current value of the counter
    public int getValue() 
    {
        return counter;
    }
}