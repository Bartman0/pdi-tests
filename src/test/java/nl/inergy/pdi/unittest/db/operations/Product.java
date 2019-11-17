package nl.inergy.pdi.unittest.db.operations;

import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;
import java.time.LocalTime;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;

public class Product {
    public static final Operation TRUNCATE_DSA = truncate("MAILINFO_ST..RTCRA_PRODUCT");
    public static final Operation TRUNCATE_DWA = truncate("MAILINFO_WT..WTDA_PRODUCT");
    public static final Operation INSERT_DSA = insertInto("MAILINFO_ST..RTCRA_PRODUCT")
            .withGeneratedValue("PRODUCTCODE", ValueGenerators.sequence().startingAt(1L))
            .withGeneratedValue("OMSCHRIJVING", ValueGenerators.stringSequence("Omschrijving-").startingAt(1L))
            .columns("USERID",
                    "INSERTDT",
                    "UPDATEDT",
                    "TN_SOURCESYSTEM",
                    "TA_STATUS_CODE",
                    "TA_METADATA",
                    "TA_INSERT_DATETIME",
                    "TA_UPDATE_DATETIME",
                    "TA_HASH",
                    "TA_RUNID")
            .repeatingValues("TEST", LocalTime.now(), LocalTime.now(), "TST", 0, 0, LocalTime.now(), LocalTime.now(), 1, 1).times(10)
            .build();
}
