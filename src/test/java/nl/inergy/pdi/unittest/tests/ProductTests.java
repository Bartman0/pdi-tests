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
import static org.dbunit.Assertion.assertEquals;
import static org.dbunit.Assertion.assertEqualsIgnoreCols;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("Test product dimension")
public class ProductTests extends PdiTestBase {

    static Properties properties;

    @BeforeAll
    static void initAll() throws IOException, ClassNotFoundException {

        initialization();

       Operation initializationDSA =
                sequenceOf(
                        Product.TRUNCATE_DSA,   // truncate DSA table
                        Product.INSERT_DSA      // insert base input dataset
                );
        Operation initializationDWA =
                sequenceOf(
                        Product.TRUNCATE_DWA    // truncate DWA table
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

    @Slow
    @Test
    void productBaseTest() throws DatabaseUnitException, IOException, SQLException, ClassNotFoundException, InterruptedException {
        // run PDI transformation
        Pdi.runTransformation("pdi/wtda_product.ktr");

        // define actual table, use a query to guarantee ordering
        DefaultTable actual = new Database(properties.getProperty("db.driver"), credentialsDWA)
                .read("WTDA_PRODUCT", "select * from wtda_product order by dn_prodcd");
        // retrieve column data types to set the CSV data types accordingly
        String columnTypes = getColumnTypesAsString(actual);
        // read the expected data from a CSV file
        DefaultTable expected = new Csv("expected").read("WTDA_PRODUCT", columnTypes);

        // compare the expected and the actual results, ignoring system columns
        assertEqualsIgnoreCols(expected, actual,
                new String[]{"S1_PRODUCT", "TA_INSERT_DATETIME", "TA_UPDATE_DATETIME", "TA_HASH", "TA_RUNID_PCR"});
    }

    @Fast
    @Test
    void productNaturalKeyTest() throws DatabaseUnitException, SQLException, ClassNotFoundException {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();

        // count rows per natural key
        String query = "select dn_prodcd, count(*) as cnt from wtda_product group by dn_prodcd order by dn_prodcd";
        // on Netezza count(*) gets a type of BigInteger which has no equivalent in the reference dataset
        String nzQuery = "select dn_prodcd, cast(count(*) as integer) as cnt from wtda_product group by dn_prodcd order by dn_prodcd";

        // define actual table, use a query to group by and guarantee ordering
        DefaultTable actual = new Database(properties.getProperty("db.driver"), credentialsDWA)
                .read("WTDA_PRODUCT", nzQuery);
        // retrieve column data types to set the CSV data types accordingly
        String columnTypes = getColumnTypesAsString(actual);
        // read the expected data from a CSV file using a query to group on natural key
        DefaultTable expected = new Csv("expected").read("WTDA_PRODUCT", columnTypes, query);

        // compare the expected and the actual results
        assertEquals(expected, actual);
    }

    @Fast
    @Test
    void failingTest() {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();
        fail("a failing test");
    }

    @Slow
    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        dbSetupTrackerDSA.skipNextLaunch();
        dbSetupTrackerDWA.skipNextLaunch();
        // not executed
    }

    @Fast
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

    private static void initialization() throws IOException, ClassNotFoundException {
        properties = new Properties();
        InputStream dbPropertiesStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("db.properties");
        properties.load(dbPropertiesStream);
        Class.forName(properties.getProperty("db.driver"));

        // set credentials based on properties
        credentialsDSA.set(properties.getProperty("dsa.url"),
                properties.getProperty("dsa.user"),
                properties.getProperty("dsa.password"));
        credentialsDWA.set(properties.getProperty("dwa.url"),
                properties.getProperty("dwa.user"),
                properties.getProperty("dwa.password"));
    }
}
