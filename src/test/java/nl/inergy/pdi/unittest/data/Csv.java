package nl.inergy.pdi.unittest.data;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CachedResultSetTable;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTable;
import org.dbunit.dataset.CachedTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Csv {
    private String filename;
    private String separator;

    public Csv(String filename) throws ClassNotFoundException {
        new Csv(filename, ";");
    }

    public Csv(String filename, String separator) throws ClassNotFoundException {
        this.filename = filename;
        this.separator = separator;
        Class.forName("org.relique.jdbc.csv.CsvDriver");
    }

    public CachedTable read(String tableName) throws SQLException, DatabaseUnitException {
        Properties props = new Properties();
        props.put("separator", this.separator);
        Connection connection = DriverManager.getConnection("jdbc:relique:csv:" + this.filename, props);
        CachedResultSetTable cachedResultSetTable = new CachedResultSetTable(
                new ForwardOnlyResultSetTable(tableName, String.format("select * from {0}", tableName), new DatabaseConnection(connection)));
        connection.close();
        return cachedResultSetTable;
    }
}
