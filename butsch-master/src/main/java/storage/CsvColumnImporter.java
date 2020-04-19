package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class CsvColumnImporter implements CsvImporter {
    private final String path;
    private final char delimiter;

    private String[] headers = null;
    private List<List<String>> elements = null;

    public CsvColumnImporter(final String path, final char delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }

    public List<List<String>> importData() throws IOException {
        final BufferedReader fileReader = new BufferedReader(new FileReader(path));

        final String headerLine = fileReader.readLine();
        headers = headerLine.split(String.valueOf(delimiter));
        final int numColumns = headers.length;

        elements = new ArrayList<>(numColumns);
        for (int i = 0; i < numColumns; i++) {
            elements.add(new LinkedList<>());
        }

        String currentLine = fileReader.readLine();;
        do {
            final String[] splitLine = currentLine.split(String.valueOf(delimiter));

            if (splitLine.length != numColumns) {
                throw new InputMismatchException("Mismatch between number of headers and number of elements in a row");
            }

            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).add(splitLine[i]);
            }

            currentLine = fileReader.readLine();;
        } while (currentLine != null && !currentLine.equals(""));

        fileReader.close();

        return elements;
    }

    public String[] getHeaders() {
        if (headers == null) {
            throw new IllegalStateException("Call importData() first");
        }

        return headers;
    }
}
