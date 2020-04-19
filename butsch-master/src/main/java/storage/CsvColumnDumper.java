package storage;

import java.io.IOException;
import java.util.List;

public class CsvColumnDumper implements CsvDumper {
    private final String path;
    private final String[] headers;
    private final List<List<Object>> elements;
    private final char delimiter;

    public CsvColumnDumper(final String path, final String[] headers, final List<List<Object>> elements, final char delimiter) {
        this.path = path;
        this.headers = headers;
        this.elements = elements;
        this.delimiter = delimiter;
    }

    public void dump() throws IOException {
        if (headers.length != elements.size()) {
            throw new IllegalArgumentException("Number of headers must be equal to number of element columns");
        }

        final int sizeOfFirstColumn = elements.get(0).size();
        for (final List<Object> element : elements) {
            if (element.size() != sizeOfFirstColumn) {
                throw new IllegalArgumentException("All columns must have equal number of elements");
            }
        }
    }
}
