package nl.inergy.pdi.unittest.tests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import nl.inergy.pdi.unittest.db.Credentials;

public class TestBase {
    // the tracker is static because JUnit uses a separate Test instance for every test method.
    protected static DbSetupTracker dbSetupTrackerDSA = new DbSetupTracker();
    protected static DbSetupTracker dbSetupTrackerDWA = new DbSetupTracker();
    //private static Credentials credentialsDSA = new Credentials().initContinuousIntegrationAWS("led");
    //private static Credentials credentialsDSA = new Credentials().initContinuousIntegrationAWS("led");
    protected static Credentials credentialsDSA = new Credentials();
    protected static Credentials credentialsDWA = new Credentials();

    protected static DbSetup getDbSetup(Credentials credentials, Operation initialization) {
        String dbUrl = credentials.getDbUrl();
        String dbUser = credentials.getDbUser();
        String dbPassword = credentials.getDbPassword();
        return new DbSetup(new DriverManagerDestination(dbUrl, dbUser, dbPassword), initialization);
    }
}
