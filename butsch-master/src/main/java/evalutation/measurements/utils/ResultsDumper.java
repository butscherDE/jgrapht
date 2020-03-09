package evalutation.measurements.utils;

import java.io.FileWriter;
import java.io.IOException;

class ResultsDumper {
    private final FileWriter fileWriter;
    private final Result[][] results;

    public ResultsDumper(final String path, final Result[][] results) throws IOException {
        fileWriter = new FileWriter(path);
        this.results = results;
    }

    public void dump() throws IOException {
        dumpHeadline();
        dumpData();
        fileWriter.close();
    }

    private void dumpHeadline() throws IOException {
        fileWriter.write("Algorithm,StartNode,EndNode,RunningTime,Cost,SettledNodes\n");
    }

    private void dumpData() throws IOException {
        for (Result[] algorithmResult : results) {
            for (Result result : algorithmResult) {
                dumpLine(result);
            }
        }
    }

    private void dumpLine(final Result result) throws IOException {
        fileWriter.write(result.toString() + "\n");
    }
}
