package com.axibase.webtest.cases;

import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.AccountService;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Anna Striganova on 11.04.18.
 */

public class CreateUserGroupTest extends AtsdTest {

    private String testUser = "axiuser";
    private AccountService as;

    @Before
    public void createTestUser() throws Exception {
        login();
        driver.navigate().to(url + "/admin/users/edit.xhtml");
        as = new AccountService(AtsdTest.driver);
        as.createUser(testUser, testUser);
    }

    /**
     * Issue 5179
     */
    @Test
    public void createUserGroup() {
        try {
            driver.navigate().to(url);

            driver.findElement(By.xpath("//a/span[normalize-space(text())='Settings']/..")).click();
            boolean submenuVisible = driver.findElement(By.xpath("//h4[normalize-space(text())='Settings']")).isDisplayed();
            Assert.assertTrue(generateAssertMessage("Submenu should be visible"), submenuVisible);

            driver.findElement(By.xpath("//a[normalize-space(text())='User Groups']")).click();
            Assert.assertEquals(generateAssertMessage("Title should be 'User Groups'"), "User Groups", driver.getTitle());


            driver.findElement(By.xpath("//a[@href='/admin/users/groups/edit.xhtml']")).click();
            Assert.assertEquals(generateAssertMessage("Title should be 'New User Group'"), "New User Group", driver.getTitle());

            driver.findElement(By.id("userGroup.name")).sendKeys("Test Group");
            driver.findElement(By.name("save")).click();

            driver.findElement(By.cssSelector("a[href='#members']")).click();
            driver.findElement(By.id("members0.groupMember")).click(); // Select the first user

            driver.findElement(By.cssSelector("a[href='#entity-permissions']")).click();
            driver.findElement(By.id("allEntityGroupsRead")).click();

            driver.findElement(By.name("update")).click();

            // Check that configuration saved correctly
            WebElement allEntitiesReadCheckbox = driver.findElement(By.id("allEntityGroupsRead"));
            Assert.assertTrue(generateAssertMessage("'All Entities Read' should be enabled"), allEntitiesReadCheckbox.isSelected());

            WebElement allEntitiesWriteCheckbox = driver.findElement(By.id("allEntityGroupsWrite"));
            Assert.assertFalse(generateAssertMessage("'All Entities Write' should be disabled"), allEntitiesWriteCheckbox.isSelected());

            driver.findElement(By.cssSelector("a[href='#members']")).click();
            WebElement userCheckbox = driver.findElement(By.id("members0.groupMember"));
            Assert.assertTrue(generateAssertMessage("Check box for user '" + testUser + "' should be enabled"), userCheckbox.isSelected());

            // Add additional settings
            driver.findElement(By.cssSelector("a[href='#portal-permissions']")).click();
            driver.findElement(By.id("portalPermissionsModels0.accessGranted")).click(); // Select the first portal

            driver.findElement(By.cssSelector("a[href='#entity-permissions']")).click();
            driver.findElement(By.id("entityGroupPermissionModels1.write")).click(); // Select the second entity group

            driver.findElement(By.name("update")).click();

            // Check that configuration saved correctly
            allEntitiesReadCheckbox = driver.findElement(By.id("allEntityGroupsRead"));
            Assert.assertTrue(generateAssertMessage("'All Entities Read' should be enabled"), allEntitiesReadCheckbox.isSelected());

            allEntitiesWriteCheckbox = driver.findElement(By.id("allEntityGroupsWrite"));
            Assert.assertFalse(generateAssertMessage("'All Entities Write' should be disabled"), allEntitiesWriteCheckbox.isSelected());

            WebElement secondEntityGroupWriteCheckbox = driver.findElement(By.id("entityGroupPermissionModels1.write"));
            Assert.assertTrue(generateAssertMessage("Write checkbox for the second Entity Group should be enabled"), secondEntityGroupWriteCheckbox.isSelected());

            driver.findElement(By.cssSelector("a[href='#members']")).click();
            userCheckbox = driver.findElement(By.id("members0.groupMember"));
            Assert.assertTrue(generateAssertMessage("Check box for user '" + testUser + "' should be enabled"), userCheckbox.isSelected());

            driver.findElement(By.cssSelector("a[href='#portal-permissions']")).click();
            WebElement firstPortalCheckbox = driver.findElement(By.id("portalPermissionsModels0.accessGranted"));
            Assert.assertTrue(generateAssertMessage("Check box for first portal should be enabled"), firstPortalCheckbox.isSelected());
        } catch (Throwable e) {
            throw e; // Error should be processed by ActionOnTestState to take screenshot
        } finally { // Instead of @After method
            // Configure ATSD as it was before test
            driver.navigate().to(url + "/admin/users/edit.xhtml?user=" + testUser);
            as.deleteUser(testUser);
        }
    }
}

