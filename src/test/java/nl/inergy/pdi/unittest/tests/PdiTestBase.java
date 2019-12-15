package nl.inergy.pdi.unittest.tests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import nl.inergy.pdi.unittest.db.Credentials;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PdiTestBase {
    // the tracker is static because JUnit uses a separate Test instance for every test method.
    protected static DbSetupTracker dbSetupTrackerDSA = new DbSetupTracker();
    protected static DbSetupTracker dbSetupTrackerDWA = new DbSetupTracker();
    //private static Credentials credentialsDSA = new Credentials().initContinuousIntegrationAWS("led");
    //private static Credentials credentialsDWA = new Credentials().initContinuousIntegrationAWS("led");
    protected static Credentials credentialsDSA = new Credentials();
    protected static Credentials credentialsDWA = new Credentials();

    protected static DbSetup getDbSetup(Credentials credentials, Operation initialization) {
        String dbUrl = credentials.getDbUrl();
        String dbUser = credentials.getDbUser();
        String dbPassword = credentials.getDbPassword();
        return new DbSetup(new DriverManagerDestination(dbUrl, dbUser, dbPassword), initialization);
    }

    public static ArrayList<String> getColumnTypes(DefaultTable actual) throws DataSetException {
        Column[] columns = actual.getTableMetaData().getColumns();
        return Arrays.stream(columns)
                .map(c -> c.getDataType().getTypeClass().getSimpleName())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static String getColumnTypesAsString(DefaultTable actual) throws DataSetException {
        return String.join(", ", getColumnTypes(actual));
    }
}
