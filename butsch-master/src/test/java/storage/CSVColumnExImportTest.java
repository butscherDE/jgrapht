package storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CSVColumnExImportTest {
    private static String TEST_PATH = System.getProperty("user.dir");

    @Test
    public void exAndImport() {
        final String[] headers = new String[] {"test_int", "test_double", "test_string", "test_abstract"};
        final List<List<Object>> objects = new ArrayList<>();
    }

    @Test
    public void pathDoesNotExist() {
        final CsvColumnImporter importer = new CsvColumnImporter("C:\\liewrhjkh\\wlkiehn\\iej.csv", ',');

        Assertions.assertThrows(FileNotFoundException.class, () -> importer.importData());
    }
}
