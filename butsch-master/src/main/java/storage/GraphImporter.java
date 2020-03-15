package storage;

import data.RoadGraph;

import java.io.FileNotFoundException;

public interface GraphImporter {
    RoadGraph createGraph() throws FileNotFoundException;
}
