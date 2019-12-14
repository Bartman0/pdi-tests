package nl.inergy.pdi.unittest.data;

import nl.inergy.pdi.unittest.db.Credentials;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CachedResultSetTable;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTable;
import org.dbunit.dataset.CachedTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final Credentials credentials;

    public Database(String driver, Credentials credentials) throws ClassNotFoundException {
        this.credentials = credentials;
        Class.forName(driver);
    }

    public CachedTable read(String tableName) throws SQLException, DatabaseUnitException {
        return read(tableName, String.format("select * from {0}", tableName));
    }

    public CachedTable read(String tableName, String query) throws SQLException, DatabaseUnitException {
        Connection connection = DriverManager.getConnection(credentials.getDbUrl(), credentials.getDbUser(), credentials.getDbPassword());
        CachedResultSetTable cachedResultSetTable = new CachedResultSetTable(
                new ForwardOnlyResultSetTable(tableName, query, new DatabaseConnection(connection)));
        connection.close();
        return cachedResultSetTable;
    }
}
