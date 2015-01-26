package mcp.mobius.opis.events;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class EventTimer {

    private long interval;
    private long lastTick = System.nanoTime();

    public EventTimer(long interval)
    {
        this.interval = (interval * 1000L * 1000L);
    }

    public boolean isDone()
    {
        long time = System.nanoTime();
        long delta = time - this.lastTick - this.interval;
        boolean done = delta >= 0L;
        if (!done) {
            return false;
        }
        this.lastTick = (time - delta);
        return true;
    }

}
