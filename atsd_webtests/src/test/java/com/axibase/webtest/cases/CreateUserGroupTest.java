package com.axibase.webtest.cases;

import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CreateAccountService;
import com.axibase.webtest.service.LoginService;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CreateUserGroupTest extends AtsdTest {


    private void createTestUser() {
        AtsdTest.driver.navigate().to(AtsdTest.url + "/admin/users/edit.xhtml");
        CreateAccountService cas = new CreateAccountService(AtsdTest.driver);
        cas.createUser("axiuser", "axipass");
    }

    /*
    Issue 5179
    */
    @Test
    public void createUserGroup() {
        Assert.assertEquals(generateAssertMessage("Should get login page"), driver.getTitle(), LoginService.title);
        LoginService ls = new LoginService(AtsdTest.driver);
        Assert.assertTrue(generateAssertMessage("Can't login"), ls.login(AtsdTest.login, AtsdTest.password));

        createTestUser();

        AtsdTest.driver.navigate().to(AtsdTest.url);

        driver.findElement(By.xpath("//a/span[normalize-space(text())='Settings']/..")).click();
        boolean submenuVisible = driver.findElement(By.xpath("//h4[normalize-space(text())='Settings']")).isDisplayed();
        Assert.assertTrue(generateAssertMessage("Submenu should be visible"), submenuVisible);

        driver.findElement(By.xpath("//a[normalize-space(text())='User Groups']")).click();
        Assert.assertEquals(generateAssertMessage("Title should be 'User Groups'"), "User Groups", driver.getTitle());

        driver.findElement(By.xpath("//a[@href='/admin/users/groups/edit.xhtml']")).click();
        Assert.assertEquals(generateAssertMessage("Title should be 'New User Group'"), "New User Group", driver.getTitle());

        driver.findElement(By.id("userGroup.name")).sendKeys("Test Group");
        driver.findElement(By.name("save")).click();

        driver.findElement(By.xpath("//a[@href='#members']")).click();
        driver.findElement(By.id("members0.groupMember")).click();

        driver.findElement(By.xpath("//a[@href='#entity-permissions']")).click();
        driver.findElement(By.id("allEntityGroupsRead")).click();

        driver.findElement(By.name("update")).click();

        WebElement allEntitiesReadCheckbox = driver.findElement(By.id("allEntityGroupsRead"));
        Assert.assertTrue(generateAssertMessage("'All Entities Read' should be enabled"), allEntitiesReadCheckbox.isSelected());

        WebElement allEntitiesWriteCheckbox = driver.findElement(By.id("allEntityGroupsWrite"));
        Assert.assertFalse(generateAssertMessage("'All Entities Write' should be disabled"), allEntitiesWriteCheckbox.isSelected());

        driver.findElement(By.xpath("//a[@href='#members']")).click();
        WebElement userCheckbox = driver.findElement(By.id("members0.groupMember"));
        Assert.assertTrue(generateAssertMessage("User check box should be enabled"), userCheckbox.isSelected());
    }
}

