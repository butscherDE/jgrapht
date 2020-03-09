package evalutation.measurements.utils;

import evalutation.Config;
import org.jgrapht.util.StopWatchGraphhopper;

public class StopWatchVerbose {
    final StopWatchGraphhopper sw;

    public StopWatchVerbose(final String name) {
        sw = new StopWatchGraphhopper(name).start();
    }

    public void printTimingIfVerbose() {
        if (Config.VERBOSE) {
            System.out.println(sw.stop().toString());
        }
    }
}
