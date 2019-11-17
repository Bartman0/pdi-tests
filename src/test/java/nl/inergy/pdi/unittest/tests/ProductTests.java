package nl.inergy.pdi.unittest.tests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import nl.inergy.pdi.unittest.data.Csv;
import nl.inergy.pdi.unittest.data.Database;
import nl.inergy.pdi.unittest.db.Credentials;
import nl.inergy.pdi.unittest.db.operations.Product;
import nl.inergy.pdi.unittest.exec.Pdi;
import nl.inergy.pdi.unittest.tags.Fast;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DefaultTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.dbunit.Assertion.assertEqualsIgnoreCols;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("Test product dimension")
public class ProductTests {

    // the tracker is static because JUnit uses a separate Test instance for every test method.
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    //private static Credentials credentials = new Credentials().initContinuousIntegrationAWS("led");
    private static Credentials credentials = new Credentials();

    @BeforeAll
    static void initAll() throws IOException, URISyntaxException {
        Properties properties = new Properties();
        InputStream dbPropertiesStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("db.properties");
        properties.load(dbPropertiesStream);
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

    @BeforeEach
    void init() {
    }

    @Fast
    @Test
    void productTest() throws DatabaseUnitException, IOException, SQLException, ClassNotFoundException {
        dbSetupTracker.skipNextLaunch();

        Pdi.runJob("pdi/productJob");

        DefaultTable expected = new Csv("productExpected.csv").read("WTDA_PRODUCT");
        DefaultTable actual = new Database("org.netezza.Driver", credentials).read("WTDA_PRODUCT");

        assertEqualsIgnoreCols(expected, actual,
                new String[]{"INSERTDT", "UPDATEDT", "TA_STATUS_CODE", "TA_METADATA", "TA_INSERT_DATETIME", "TA_UPDATE_DATETIME"});
    }

    @Test
    void failingTest() {
        dbSetupTracker.skipNextLaunch();
        fail("a failing test");
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        dbSetupTracker.skipNextLaunch();
        // not executed
    }

    @Test
    void abortedTest() {
        dbSetupTracker.skipNextLaunch();
        assumeTrue("abc".contains("Z"));
        fail("test should have been aborted");
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }
}
