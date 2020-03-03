package evalutation;

import org.jgrapht.util.StopWatch;

public class StopWatchVerbose {
    private final StopWatch stopWatch;

    public StopWatchVerbose(final String name) {
        this.stopWatch = new StopWatch(name).start();
    }

    public void printTimingIfVerbose() {
        if (Config.VERBOSE) {
            System.out.println(stopWatch.stop().toString());
        }
    }
}
