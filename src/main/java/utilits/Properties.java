package utilits;

public class Properties {
    public static boolean debug = true;
    public static String DB_CONNECT_PATH = "jdbc:h2:file:./src/main/resources/bankapi_database;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE";
    public static String DB_MEM_CONNECT_PATH = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    public static final String SQL_SCHEMA_FILE_PATH = "src/main/resources/sql_schema_data/sql_schema.sql";
    public static final String SQL_TEST_DATA_FILE_PATH = "src/main/resources/sql_schema_data/test_data.sql";
    public static final int HTTP_SERVER_PORT = 8000;
}
