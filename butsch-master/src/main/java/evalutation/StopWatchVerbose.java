package evalutation;

import org.jgrapht.util.StopWatchGraphhopper;

public class StopWatchVerbose {
    private final StopWatchGraphhopper sw;

    public StopWatchVerbose(final String name) {
        this.sw = new StopWatchGraphhopper(name).start();
    }


    public void printTimingIfVerbose() {
        System.out.println(sw.stop().toString());
    }
}
