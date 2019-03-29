package com.axibase.webtest;

import com.axibase.webtest.cases.CreateEntityAndMetriclTest;
import com.axibase.webtest.cases.CreatePortalTest;
import com.axibase.webtest.cases.CreateUserGroupTest;
import com.axibase.webtest.cases.DataEntryCommandsTest;
import com.axibase.webtest.forecasts.ForecastPageTest;
import com.axibase.webtest.service.*;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sild on 03.02.15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AdminServiceTest.class,
        EntitiesServiceTest.class,
        ExportServiceTest.class,
        LoginServiceTest.class,
        MetricsServiceTest.class,
        CreateEntityAndMetriclTest.class,
        CreatePortalTest.class,
        CreateUserGroupTest.class,
        CSVImportParserAsSeriesTest.class,
        ForecastPageTest.class,
        DataEntryCommandsTest.class
})
public class TestSuiteCheckCleanAtsd {
    @BeforeClass
    public static void createUser() {
        JUnitCore.runClasses(AccountServiceTest.class);
    }
}
