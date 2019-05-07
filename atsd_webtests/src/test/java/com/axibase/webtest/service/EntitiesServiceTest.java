package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

public class EntitiesServiceTest extends AtsdTest {

    @Test
    public void checkDefaultEntity() {
        Assert.assertEquals(generateAssertMessage("Should get login page"), driver.getTitle(), LoginService.title);
        LoginService ls = new LoginService(driver);
        Assert.assertTrue(generateAssertMessage("Can't login"), ls.loginAsAdmin());
        driver.navigate().to(url + "/admin/system-information");
        Assert.assertEquals(generateAssertMessage("Should get system-information page"), driver.getTitle(), SystemInfoService.title);
        SystemInfoService sis = new SystemInfoService(driver);
        String hostname = sis.getSystemInfoValue("hostname");
        Assert.assertNotNull("Can't find hostname value", hostname);
        driver.navigate().to(url + "/entities");
        Assert.assertEquals(generateAssertMessage("Title should be 'Entities'"), driver.getTitle(), EntitiesService.title);
        EntitiesService es = new EntitiesService(driver);
        Assert.assertTrue(generateAssertMessage("Should be more than 1 row in entities table"), es.getEntitiesCount() > 1);
        Assert.assertNotNull(generateAssertMessage("Can't find 'atsd' cell"), es.getEntityByName("atsd"));
        //Assert.assertNotNull(generateAssertMessage("Can't find 'entity-1' cell"), es.getEntityByName("entity-1"));
        Assert.assertNotNull(generateAssertMessage("Can't find " + hostname + " cell"), es.getEntityByName(hostname.toLowerCase()));
    }
}
