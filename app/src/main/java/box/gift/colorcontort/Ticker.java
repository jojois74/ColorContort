package box.gift.colorcontort;

/**
 * Created by Joseph on 10/11/2017.
 */

public class Ticker
{
    Ticked obj;
    int delayTicks;
    int currentTicks = 0;

    public Ticker(int delayTicks, Ticked obj)
    {
        this.obj = obj;
        this.delayTicks = delayTicks;
    }

    public void tick()
    {
        currentTicks++;
        if (currentTicks > delayTicks)
        {
            currentTicks = 0;
            obj.activate();
        }
    }
}