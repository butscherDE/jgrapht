package storage;

import java.io.IOException;
import java.util.List;

public class CsvColumnImporter implements CsvImporter {
    private final String path;
    private final char delimiter;

    private String[] headers;
    private List<Object>[] elements;

    public CsvColumnImporter(final String path, final char delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }

    public List<List<Object>> importData() throws IOException {
        return null;
    }

    public String[] getHeaders() {
        return null;
    }
}
