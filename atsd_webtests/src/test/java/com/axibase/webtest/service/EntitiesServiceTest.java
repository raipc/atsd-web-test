package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

public class EntitiesServiceTest extends AtsdTest {
    @Test
    public void checkDefaultEntity() {
        try {
            Assert.assertEquals(this.generateAssertMessage("Should get login page"), driver.getTitle(), LoginService.title);
            LoginService ls = new LoginService(AtsdTest.driver);
            Assert.assertTrue(this.generateAssertMessage("Can't login"), ls.login(AtsdTest.login, AtsdTest.password));
            AtsdTest.driver.navigate().to(AtsdTest.url + "/admin/system-information");
            Assert.assertEquals(this.generateAssertMessage("Should get system-information page"), driver.getTitle(), SystemInfoService.title);
            SystemInfoService sis = new SystemInfoService(AtsdTest.driver);
            String hostname = sis.getSystemInfoValue("hostname");
            Assert.assertNotNull("Can't find hostname value", hostname);
            AtsdTest.driver.navigate().to(AtsdTest.url + "/entities");
            Assert.assertEquals(generateAssertMessage("Title should be 'Entities'"), AtsdTest.driver.getTitle(), EntitiesService.title);
            EntitiesService es = new EntitiesService(AtsdTest.driver);
            Assert.assertTrue(generateAssertMessage("Should be more than 1 row in entities table"), es.getEntitiesCount() > 1);
            Assert.assertNotNull(generateAssertMessage("Can't find 'atsd' cell"), es.getEntityByName("atsd"));
            //Assert.assertNotNull(generateAssertMessage("Can't find 'entity-1' cell"), es.getEntityByName("entity-1"));
            Assert.assertNotNull(generateAssertMessage("Can't find " + hostname + " cell"), es.getEntityByName(hostname.toLowerCase()));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }
}
