package nl.inergy.pdi.unittest.data;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CachedResultSetTable;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTable;
import org.dbunit.dataset.CachedTable;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Csv {
    private final URL resourcesPath;
    private String filename;
    private String separator;

    public Csv(String filename) throws ClassNotFoundException {
        this(filename, ";");
    }

    public Csv(String resourcePathName, String separator) throws ClassNotFoundException {
        this.resourcesPath = Thread.currentThread().getContextClassLoader().getResource(resourcePathName);
        this.separator = separator;
        Class.forName("org.relique.jdbc.csv.CsvDriver");
    }

    public CachedTable read(String tableName, String columnTypes) throws SQLException, DatabaseUnitException {
        return read(tableName, columnTypes, String.format("select * from %s", tableName));
    }

    public CachedTable read(String tableName, String columnTypes, String query) throws SQLException, DatabaseUnitException {
        Properties props = new Properties();
        props.put("separator", this.separator);
        props.put("columnTypes", columnTypes);
        Connection connection = DriverManager.getConnection("jdbc:relique:csv:" + this.resourcesPath.getFile(), props);
        CachedResultSetTable cachedResultSetTable = new CachedResultSetTable(
                new ForwardOnlyResultSetTable(tableName, query, new DatabaseConnection(connection)));
        connection.close();
        return cachedResultSetTable;
    }

}
