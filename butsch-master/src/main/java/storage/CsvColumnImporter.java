package storage;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvColumnImporter implements CsvImporter {
    private final String path;
    private final char delimiter;

    private String[] headers = null;
    private List<Object>[] elements = null;

    public CsvColumnImporter(final String path, final char delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }

    public List<List<Object>> importData() throws IOException {
        final FileReader fileReader = new FileReader(path);



        fileReader.close();

        return null;
    }

    public String[] getHeaders() {
        if (headers == null) {
            throw new IllegalStateException("Call importData() first");
        }

        return headers;
    }
}
