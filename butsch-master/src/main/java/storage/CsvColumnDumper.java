package storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvColumnDumper implements CsvDumper {
    private final String path;
    private final String[] headers;
    private final List<List<Object>> elements;
    private final char delimiter;

    private FileWriter fileWriter;

    public CsvColumnDumper(final String path, final String[] headers, final List<List<Object>> elements, final char delimiter) {
        this.path = path;
        this.headers = headers;
        this.elements = elements;
        this.delimiter = delimiter;
    }

    public void dump() throws IOException {
        failOnHeaderLengthUnequalToElementsLength();
        failOnUnequalSizedColumns();

        writeData();
    }

    public void writeData() throws IOException {
        fileWriter = new FileWriter(path);

        dumpHeader();
        dumpElements();

        fileWriter.close();
    }

    public void failOnHeaderLengthUnequalToElementsLength() {
        if (headers.length != elements.size()) {
            throw new IllegalArgumentException("Number of headers must be equal to number of element columns");
        }
    }

    public void failOnUnequalSizedColumns() {
        final int sizeOfFirstColumn = elements.get(0).size();
        for (final List<Object> element : elements) {
            if (element.size() != sizeOfFirstColumn) {
                throw new IllegalArgumentException("All columns must have equal number of elements");
            }
        }
    }

    public void dumpHeader() throws IOException {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.length - 1; i++) {
            sb.append(headers[i] + delimiter);
        }
        sb.append(headers[headers.length - 1]);
        fileWriter.write(sb.toString() + "\n");
    }

    public void dumpElements() throws IOException {
        final List<Iterator<Object>> elementIterators = buildIteratorForEachColumn();
        writeElements(elementIterators);
    }

    public List<Iterator<Object>> buildIteratorForEachColumn() {
        final List<Iterator<Object>> elementIterators = new ArrayList<>(elements.size());
        for (final List<Object> element : elements) {
            elementIterators.add(element.iterator());
        }
        return elementIterators;
    }

    public void writeElements(final List<Iterator<Object>> elementIterators) throws IOException {
        final Iterator<Object> iteratorProbe = elementIterators.get(0);
        final int numColumns = elementIterators.size();
        while (iteratorProbe.hasNext()) {
            writeElementLine(elementIterators, numColumns);
        }
    }

    public void writeElementLine(final List<Iterator<Object>> elementIterators,
                                 final int numColumns) throws IOException {
        final StringBuilder elementSb = new StringBuilder();
        for (int i = 0; i < numColumns - 1; i++) {
            elementSb.append(elementIterators.get(i).next().toString() + delimiter);
        }
        elementSb.append(elementIterators.get(numColumns - 1).next().toString());
        fileWriter.write(elementSb.toString() + "\n");
    }
}
