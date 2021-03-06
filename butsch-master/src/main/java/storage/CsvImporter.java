package storage;

import java.io.IOException;
import java.util.List;

public interface CsvImporter {
    List<List<String>> importData() throws IOException;

    String[] getHeaders();
}
