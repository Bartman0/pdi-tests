package nl.inergy.pdi.unittest.tests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import nl.inergy.pdi.unittest.db.Credentials;
import nl.inergy.pdi.unittest.db.operations.Product;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class TestBase {
    // the tracker is static because JUnit uses a separate Test instance for every test method.
    protected static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    //private static Credentials credentials = new Credentials().initContinuousIntegrationAWS("led");
    protected static Credentials credentials = new Credentials();

    @BeforeAll
    static void initAll() throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        InputStream dbPropertiesStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("db.properties");
        properties.load(dbPropertiesStream);
        Class.forName(properties.getProperty("db.driver"));
        credentials.set(properties.getProperty("dwa.url"),
                properties.getProperty("dwa.user"),
                properties.getProperty("dwa.password"));

        Operation initialization =
                sequenceOf(
                        Product.TRUNCATE_DSA,
                        Product.TRUNCATE_DWA,
                        Product.INSERT_DSA
                );

        DbSetup dbSetup = getDbSetup(credentials, initialization);

        // use the tracker to launch the DbSetup.
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    private static DbSetup getDbSetup(Credentials credentials, Operation initialization) {
        String dbUrl = credentials.getDbUrl();
        String dbUser = credentials.getDbUser();
        String dbPassword = credentials.getDbPassword();
        return new DbSetup(new DriverManagerDestination(dbUrl, dbUser, dbPassword), initialization);
    }

    @AfterAll
    static void tearDownAll() {
    }
}
