package storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CSVColumnExImportTest {
    private static String TEST_PATH = System.getProperty("user.dir") + "\\csvimportexporttest.csv";

    @Test
    public void exAndImport() {
        final String[] headers = new String[] {"test_int", "test_double", "test_string", "test_abstract"};

        final List<Object> integers = Arrays.asList(1, 2, 3);
        final List<Object> doubles = Arrays.asList(0.4, 2.1, 3.7);
        final List<Object> strings = Arrays.asList("abc", "def", "ghi");
        final List<Object> abstracts = Arrays.asList(new Coordinate(0, 0),
                                                     new Coordinate(1,1),
                                                     new Coordinate(2,2));
        final List<List<Object>> elements = Arrays.asList(integers, doubles, strings, abstracts);

        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ',');
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        final CsvColumnImporter importer = new CsvColumnImporter(TEST_PATH, ',');
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
        final List<Object> reImportedIntegers = new LinkedList<>();
        for (final String reImportedElement : reImportedElements.get(0)) {
            reImportedIntegers.add(Integer.valueOf(reImportedElement));
        }
        final List<Object> reImportedDoubles = new LinkedList<>();
        for (final String reImportedElement : reImportedElements.get(1)) {
            reImportedDoubles.add(Double.valueOf(reImportedElement));
        }
        final List<Object> reImportedStrings = new LinkedList<>();
        for (final String reImportedElement : reImportedElements.get(1)) {
            reImportedDoubles.add(reImportedElement);
        }
        final List<Object> reImportedCoordinates = new LinkedList<>();
        for (final String reImportedElement : reImportedElements.get(3)) {
            reImportedDoubles.add(parseCoordinate(reImportedElement));
        }
        return Arrays.asList(reImportedIntegers,
                             reImportedDoubles,
                             reImportedStrings,
                             reImportedCoordinates);
    }

    @Test
    public void deleteThis() {
        System.out.println(new Coordinate(0.0, 1.2));
    }

    private Coordinate parseCoordinate(final String coordinateString) {
        final String cutString = coordinateString.substring(0, coordinateString.length() - 1);

        final String[] coordinatesString = cutString.split(", ");
        final double[] coordinates = new double[] {Double.valueOf(coordinatesString[0]),
                                                   Double.valueOf(coordinatesString[1]),
                                                   Double.valueOf(coordinatesString[2])};

        return new Coordinate(coordinates[0], coordinates[1], coordinates[2]);
    }

    @Test
    public void getHeadersCalledToEarly() {
        final String[] headers = new String[] {"abc"};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList("abc"));
        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ',');
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(TEST_PATH);
        final CsvColumnImporter importer = new CsvColumnImporter(TEST_PATH, ',');
        assertThrows(IllegalStateException.class, () -> importer.importData());
    }

    @Test
    public void unequalSizedObjects() {
        final String[] headers = new String[] {"test1", "test2"};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList(0,1), Arrays.asList(1));

        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ',');
        assertThrows(IllegalArgumentException.class, () -> dumper.dump());
    }

    @Test
    public void unequalHeadersVsColumnNumbers() {
        final String[] headers = new String[] {"abc"};
        final List<List<Object>> elements = Arrays.asList(Arrays.asList(0,1), Arrays.asList(1,2));

        final CsvColumnDumper dumper = new CsvColumnDumper(TEST_PATH, headers, elements, ',');
        assertThrows(IllegalArgumentException.class, () -> dumper.dump());
    }

    @Test
    public void pathDoesNotExist() {
        final CsvColumnImporter importer = new CsvColumnImporter("C:\\liewrhjkh\\wlkiehn\\iej.csv", ',');

        Assertions.assertThrows(FileNotFoundException.class, () -> importer.importData());
    }

    @AfterEach
    public void clearTestFile() {
        final File f = new File(TEST_PATH);
        f.delete();
    }
}
