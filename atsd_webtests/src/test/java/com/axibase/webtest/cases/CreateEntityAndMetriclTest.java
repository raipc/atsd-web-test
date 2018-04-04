package com.axibase.webtest.cases;

import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.LoginService;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CreateEntityAndMetriclTest extends AtsdTest {

    @Test
    public void createEntityAndMetric() {
        try {
            Assert.assertEquals(generateAssertMessage("Should get login page"), driver.getTitle(), LoginService.title);
            LoginService ls = new LoginService(AtsdTest.driver);
            Assert.assertTrue(generateAssertMessage("Can't login"), ls.login(AtsdTest.login, AtsdTest.password));
            AtsdTest.driver.navigate().to(AtsdTest.url);

            driver.findElement(By.xpath("//a/span[normalize-space(text())='Data']/..")).click();
            boolean submenuVisible = driver.findElement(By.xpath("//h4[normalize-space(text())='Data']")).isDisplayed();
            Assert.assertTrue(generateAssertMessage("Submenu should be visible"), submenuVisible);

            driver.findElement(By.xpath("//a[normalize-space(text())='Data Entry']")).click();
            Assert.assertEquals(generateAssertMessage("Title should be 'Data Entry'"), "Data Entry", AtsdTest.driver.getTitle());

            driver.findElement(By.id("seriesType")).click();
            driver.findElement(By.name("entity")).sendKeys("my-entity");
            driver.findElement(By.name("metric")).sendKeys("my-metric");

            driver.findElement(By.name("value")).sendKeys("50");
            driver.findElement(By.name("series")).click();
            WebElement successMessage = driver.findElement(By.cssSelector("form.series .form-status>.alert-success"));
            Assert.assertEquals(generateAssertMessage("Wrong success message"), "Series inserted successfully", successMessage.getText().trim());

            driver.findElement(By.name("value")).sendKeys("150");
            driver.findElement(By.name("series")).click();
            successMessage = driver.findElement(By.cssSelector("form.series .form-status>.alert-success"));
            Assert.assertEquals(generateAssertMessage("Wrong success message"), "Series inserted successfully", successMessage.getText().trim());

            driver.navigate().to(AtsdTest.url + "/portals/series?entity=my-entity&metric=my-metric");
            List<WebElement> widgets = driver.findElements(By.className("widget"));
            Assert.assertNotEquals(generateAssertMessage("No widgets for portal"), 0, widgets.size());
        } catch (Throwable err) {
            String filepath = AtsdTest.screenshotDir + "/" + this.getClass().getSimpleName() + "_"
                    + Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + System.currentTimeMillis()
                    + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }
}