package evalutation;

@SuppressWarnings("SpellCheckingInspection")
public class Config {
    public final static boolean VERBOSE = false;

    public final static String PBF_FILES = "C:\\pbffiles\\";
    // ERP style data
//    public final static String ERP_PATH = "C:\\Users\\Daniel\\Documents\\ger.txt";
    public final static String ERP_PATH = "C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d.txt";
    public final static String ERP_LUXEMBOURG = "C:\\pbffiles\\luxembourg.txt";
    public final static String ERP_TUEBINGEN = PBF_FILES + "tuebingen.txt";

    // Polygons
    public final static String IMG_PATH = "C:\\Users\\Daniel\\Documents\\jpanelimg\\";

    // PBF files
    public static final String PBF_FREIBURG = PBF_FILES + "freiburg-regbez-latest.osm.pbf";
    public static final String PBF_TUEBINGEN = PBF_FILES + "tuebingen-regbez-latest.osm.pbf";
    public static final String PBF_ANDORRA = PBF_FILES + "andorra-latest.osm.pbf";
    public static final String PBF_LUXEMBOURG = PBF_FILES + "luxembourg-latest.osm.pbf";
    public static final String PBF_LUXEMBOURG_STATS = PBF_FILES + "luxembourgstats.csv";
    public static final String PBF_BAWU = PBF_FILES + "baden-wuerttemberg-latest.osm.pbf";
    public static final String PBF_GERMANY = PBF_FILES + "germany-latest.osm.pbf";

    // Preproc files
    public static final String PREPROC = PBF_FILES + "preproc\\";
    public static final String[] PRE_TUEBINGEN = new String[] {
            PREPROC + "tuebingen_graph.txt",
            PREPROC + "tuebingen_vcs.txt",
            PREPROC + "tuebingen_index.txt",
            PREPROC + "tuebingen_polygons.txt"
    };

    // Results
    public static final String RESULTS = PBF_FILES + "results\\";
    public final static String POLYGON_PATH = RESULTS + "preprocessedPolygons\\";
    public static final String POLY_SIMPLIFICATION = RESULTS + "simplify\\";
}
