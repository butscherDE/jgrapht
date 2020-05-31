package storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CSVColumnExImportTest {
    private static final String TEST_PATH = System.getProperty("user.dir") + "\\csvimportexporttest.csv";

    @Test
    public void exAndImport() {
        final String[] headers = getHeaders();
        final List<List<Object>> elements = getElements();
        dump(headers, elements);
        reImportAndCheck(headers, elements);
    }

    public String[] getHeaders() {
        return new String[]{"test_int", "test_double", "test_string", "test_abstract"};
    }

    public List<List<Object>> getElements() {
        final List<Object> integers = Arrays.asList(1, 2, 3);
        final List<Object> doubles = Arrays.asList(0.4, 2.1, 3.7);
        final List<Object> strings = Arrays.asList("abc", "def", "ghi");
        final List<Object> abstracts = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2));
        return Arrays.asList(integers, doubles, strings, abstracts);
    }

    public void dump(final String[] headers, final List<List<Object>> elements) {
        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ';');
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void reImportAndCheck(final String[] headers, final List<List<Object>> elements) {
        final CsvColumnImporter importer = new CsvColumnImporter(TEST_PATH, ';');
        try {
            final List<List<String>> reImportedElements = importer.importData();
            final String[] reImportedHeaders = importer.getHeaders();

            final List<List<Object>> reImportedElementsConverted = convertReimportedElements(reImportedElements);

            assertArrayEquals(headers, reImportedHeaders);
            assertEquals(elements, reImportedElementsConverted);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public List<List<Object>> convertReimportedElements(final List<List<String>> reImportedElements) {
        return Arrays.asList(reImportedElements.get(0).stream().map(Integer::valueOf).collect(Collectors.toList()),
                             reImportedElements.get(1).stream().map(Double::valueOf).collect(Collectors.toList()),
                             reImportedElements.get(2).stream().collect(Collectors.toList()),
                             reImportedElements.get(3).stream().map((a) -> parseCoordinate(a)).collect(Collectors.toList()));
    }

    @Test
    public void deleteThis() {
        System.out.println(new Coordinate(0.0, 1.2));
    }

    private Coordinate parseCoordinate(final String coordinateString) {
        final String cutString = coordinateString.substring(1, coordinateString.length() - 1);

        final String[] split = cutString.split(", ");
        final List<Double> doubleCoords = Arrays.stream(split).map(Double::valueOf).collect(Collectors.toList());

        return new Coordinate(doubleCoords.get(0), doubleCoords.get(1), doubleCoords.get(2));
    }

    @Test
    public void getHeadersCalledToEarly() {
        prepareDummyFile();

        final CsvColumnImporter importer = new CsvColumnImporter(TEST_PATH, ',');
        assertThrows(IllegalStateException.class, () -> importer.getHeaders());
    }

    public void prepareDummyFile() {
        final String[] headers = new String[]{""};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList(""));

        dumpDummyFile(headers, elements);
    }

    public void dumpDummyFile(final String[] headers, final List<List<Object>> elements) {
        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ';');
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unequalSizedObjects() {
        final String[] headers = new String[]{"test1", "test2"};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList(0, 1), Arrays.asList(1));

        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ';');
        assertThrows(IllegalArgumentException.class, () -> dumper.dump());
    }

    @Test
    public void unequalHeadersVsColumnNumbers() {
        final String[] headers = new String[]{"abc"};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList(0, 1), Arrays.asList(1, 2));

        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ';');
        assertThrows(IllegalArgumentException.class, () -> dumper.dump());
    }

    @Test
    public void pathDoesNotExist() {
        final CsvColumnImporter importer = new CsvColumnImporter("C:\\liewrhjkh\\wlkiehn\\iej.csv", ';');

        Assertions.assertThrows(FileNotFoundException.class, () -> importer.importData());
    }

    @AfterEach
    public void clearTestFile() {
        final File f = new File(TEST_PATH);
        f.delete();
    }
}
