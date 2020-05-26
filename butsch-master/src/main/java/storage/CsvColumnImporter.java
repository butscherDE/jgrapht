package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;

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
        final int numColumns = readHeader(fileReader);
        readElements(fileReader, numColumns);
        fileReader.close();

        return elements;
    }

    public int readHeader(final BufferedReader fileReader) throws IOException {
        final String headerLine = fileReader.readLine();
        headers = headerLine.split(String.valueOf(delimiter));
        return headers.length;
    }

    public void readElements(final BufferedReader fileReader, final int numColumns) throws IOException {
        getElementsDataStructure(numColumns);
        addDataFromFile(fileReader, numColumns);
    }

    public void getElementsDataStructure(final int numColumns) {
        elements = new ArrayList<>(numColumns);
        for (int i = 0; i < numColumns; i++) {
            elements.add(new LinkedList<>());
        }
    }

    public void addDataFromFile(final BufferedReader fileReader, final int numColumns) throws IOException {
        String currentLine = fileReader.readLine();
        do {
            final String[] splitLine = currentLine.split(String.valueOf(delimiter));

            failOnLineWithWrongNumberOfColumns(numColumns, splitLine);
            addLineData(splitLine);

            currentLine = fileReader.readLine();
        } while (isFileNotEnded(currentLine));
    }

    public void failOnLineWithWrongNumberOfColumns(final int numColumns, final String[] splitLine) {
        if (splitLine.length != numColumns) {
            throw new InputMismatchException("Mismatch between number of headers and number of elements in a row");
        }
    }

    public void addLineData(final String[] splitLine) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).add(splitLine[i]);
        }
    }

    public boolean isFileNotEnded(final String currentLine) {
        return currentLine != null && !currentLine.equals("");
    }

    public String[] getHeaders() {
        if (headers == null) {
            throw new IllegalStateException("Call importData() first");
        }

        return headers;
    }
}
