package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class EntitiesServiceTest extends AtstTest {
    @Test
    public void checkDefaultEntity() {
        try {
            Assert.assertEquals(this.generateAssertMessage("Should get login page"), this.driver.getTitle(), LoginService.title);
            LoginService ls = new LoginService(AtstTest.driver);
            Assert.assertTrue(this.generateAssertMessage("Can't login"), ls.login(AtstTest.login, AtstTest.password));
            AtstTest.driver.navigate().to(AtstTest.url + "/admin/system-information");
            Assert.assertEquals(this.generateAssertMessage("Should get system-information page"), this.driver.getTitle(), SystemInfoService.title);
            SystemInfoService sis = new SystemInfoService(AtstTest.driver);
            String hostname = sis.getSystemInfoValue("hostname");
            Assert.assertNotNull("Can't find hostname value", hostname);
            AtstTest.driver.navigate().to(AtstTest.url + "/entities");
            Assert.assertEquals(generateAssertMessage("Title should be 'Entities'"), AtstTest.driver.getTitle(), EntitiesService.title);
            EntitiesService es = new EntitiesService(AtstTest.driver);
            Assert.assertTrue(generateAssertMessage("Should be more then 2 rows in entities table"), es.getEntitiesCount() > 2);
            Assert.assertNotNull(generateAssertMessage("Can't find 'atsd' cell"), es.getEntityByName("atsd"));
            Assert.assertNotNull(generateAssertMessage("Can't find 'entity-1' cell"), es.getEntityByName("entity-1"));
            Assert.assertNotNull(generateAssertMessage("Can't find " + hostname + " cell"), es.getEntityByName(hostname.toLowerCase()));
        } catch (AssertionError err) {
            String filepath = AtstTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            System.out.println(err.toString());
            Assert.fail();

        }
    }
}
