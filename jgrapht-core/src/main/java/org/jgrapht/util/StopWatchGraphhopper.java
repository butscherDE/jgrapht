package org.jgrapht.util;

/**
 * Make simple speed measurements possible.
 * <p>
 *
 * @author Peter Karich
 */
public class StopWatchGraphhopper {
    private long lastTime;
    private long elapsedNanos;
    private final String name;

    public StopWatchGraphhopper(String name) {
        this.name = name;
    }

    public StopWatchGraphhopper start() {
        lastTime = System.nanoTime();
        return this;
    }

    public StopWatchGraphhopper stop() {
        if (lastTime < 0)
            return this;

        elapsedNanos += System.nanoTime() - lastTime;
        lastTime = -1;
        return this;
    }

    private float getSeconds() {
        return elapsedNanos / 1e9f;
    }

    @Override
    public String toString() {
        return name + " time:" + getSeconds();
    }
}
