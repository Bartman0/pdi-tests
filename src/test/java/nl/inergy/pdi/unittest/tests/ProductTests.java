package nl.inergy.pdi.unittest.tests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.operation.Operation;
import nl.inergy.pdi.unittest.data.Csv;
import nl.inergy.pdi.unittest.data.Database;
import nl.inergy.pdi.unittest.db.operations.Product;
import nl.inergy.pdi.unittest.exec.Pdi;
import nl.inergy.pdi.unittest.tags.Fast;
import nl.inergy.pdi.unittest.tags.Slow;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DefaultTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.dbunit.Assertion.assertEqualsIgnoreCols;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("Test product dimension")
public class ProductTests extends TestBase {

    @BeforeAll
    static void initAll() throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        InputStream dbPropertiesStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("db.properties");
        properties.load(dbPropertiesStream);
        Class.forName(properties.getProperty("db.driver"));
        credentialsDSA.set(properties.getProperty("dsa.url"),
                properties.getProperty("dsa.user"),
                properties.getProperty("dsa.password"));
        credentialsDWA.set(properties.getProperty("dwa.url"),
                properties.getProperty("dwa.user"),
                properties.getProperty("dwa.password"));

        Operation initializationDSA =
                sequenceOf(
                        Product.TRUNCATE_DSA,
                        Product.INSERT_DSA
                );
        Operation initializationDWA =
                sequenceOf(
                        Product.TRUNCATE_DWA
                );

        DbSetup dbSetupDSA = getDbSetup(credentialsDSA, initializationDSA);
        DbSetup dbSetupDWA = getDbSetup(credentialsDWA, initializationDWA);

        // use the tracker to launch the DbSetup.
        dbSetupTrackerDSA.launchIfNecessary(dbSetupDSA);
        dbSetupTrackerDWA.launchIfNecessary(dbSetupDWA);
    }

    @BeforeEach
    void init() {
    }

    @Test
    void productBaseTest() throws DatabaseUnitException, IOException, SQLException, ClassNotFoundException {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();

        Pdi.runJob("pdi/productJob");

        DefaultTable expected = new Csv("productExpected.csv").read("WTDA_PRODUCT");
        DefaultTable actual = new Database("org.netezza.Driver", credentialsDWA).read("WTDA_PRODUCT");

        assertEqualsIgnoreCols(expected, actual,
                new String[]{"INSERTDT", "UPDATEDT", "TA_STATUS_CODE", "TA_METADATA", "TA_INSERT_DATETIME", "TA_UPDATE_DATETIME"});
    }

    @Fast
    @Test
    @Disabled("for demonstration purposes")
    void failingTest() {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();
        fail("a failing test");
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();
        // not executed
    }

    @Test
    void abortedTest() {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();
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
