package com.axibase.webtest;

import com.axibase.webtest.cases.CreateEntityAndMetricTest;
import com.axibase.webtest.cases.CreatePortalTest;
import com.axibase.webtest.cases.CreateUserGroupTest;
import com.axibase.webtest.forecasts.ForecastPageTestDependingOnTheData;
import com.axibase.webtest.forecasts.ForecastPageTestRegardlessOfData;
import com.axibase.webtest.service.*;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Created by sild on 03.02.15.
 */
@RunWith(Suite.class)
@SuiteClasses({
        AdminServiceTest.class,
        EntitiesServiceTest.class,
        ExportServiceTest.class,
        LoginServiceTest.class,
        MetricsServiceTest.class,
        CreateEntityAndMetricTest.class,
        CreatePortalTest.class,
        CreateUserGroupTest.class,
        CSVImportParserAsSeriesTest.class,
        ForecastPageTestDependingOnTheData.class,
        ForecastPageTestRegardlessOfData.class
})
public class TestSuiteCheckCleanAtsd {
    @BeforeClass
    public static void createUser() {
        JUnitCore.runClasses(AccountServiceTest.class);
    }
}
