package com.axibase.webtest;

import com.axibase.webtest.cases.CreateEntityAndMetriclTest;
import com.axibase.webtest.cases.CreatePortalTest;
import com.axibase.webtest.service.*;
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
        CreatePortalTest.class
})
public class TestSuiteCheckCleanAtsd {
    @BeforeClass
    public static void createUser() {
        JUnitCore.runClasses(CreateAccountServiceTest.class);
    }
}
