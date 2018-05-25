package com.axibase.webtest;

import com.axibase.webtest.cases.CreateEntityAndMetriclTest;
import com.axibase.webtest.cases.CreatePortalTest;
import com.axibase.webtest.cases.CreateUserGroupTest;
import com.axibase.webtest.service.*;
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
        CreateEntityAndMetriclTest.class,
        CreatePortalTest.class,
        CreateUserGroupTest.class
})
public class TestSuiteCheckCleanAtsd {
    @BeforeClass
    public static void createUser() {
        JUnitCore.runClasses(AccountServiceTest.class);
    }
}
