package nl.inergy.pdi.unittest.tests;

import nl.inergy.pdi.unittest.data.Csv;
import nl.inergy.pdi.unittest.data.Database;
import nl.inergy.pdi.unittest.exec.Pdi;
import nl.inergy.pdi.unittest.tags.Fast;
import nl.inergy.pdi.unittest.tags.Slow;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DefaultTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;

import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.dbunit.Assertion.assertEqualsIgnoreCols;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("Test product dimension")
public class ProductTests extends TestBase {

    @BeforeEach
    void init() {
    }

    @Slow
    @Test
    void productBaseTest() throws DatabaseUnitException, IOException, SQLException, ClassNotFoundException {
        dbSetupTracker.skipNextLaunch();

        Pdi.runJob("pdi/productJob");

        DefaultTable expected = new Csv("productExpected.csv").read("WTDA_PRODUCT");
        DefaultTable actual = new Database("org.netezza.Driver", credentials).read("WTDA_PRODUCT");

        assertEqualsIgnoreCols(expected, actual,
                new String[]{"INSERTDT", "UPDATEDT", "TA_STATUS_CODE", "TA_METADATA", "TA_INSERT_DATETIME", "TA_UPDATE_DATETIME"});
    }

    @Fast
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
}
