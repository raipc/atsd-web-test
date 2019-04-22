package com.axibase.webtest.cases;

import com.axibase.webtest.service.AtsdTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CreateEntityAndMetricTest extends AtsdTest {

    @Before
    public void setUp(){
        login();
    }

    @Test
    public void createEntityAndMetric() {
        driver.findElement(By.xpath("//a/span[normalize-space(text())='Data']/..")).click();
        boolean submenuVisible = driver.findElement(By.xpath("//h4[normalize-space(text())='Data']")).isDisplayed();
        assertTrue(generateAssertMessage("Submenu should be visible"), submenuVisible);

        driver.findElement(By.xpath("//a[normalize-space(text())='Data Entry']")).click();
        assertEquals(generateAssertMessage("Title should be 'Data Entry'"), "Data Entry", AtsdTest.driver.getTitle());

        driver.findElement(By.id("seriesType")).click();
        driver.findElement(By.name("entity")).sendKeys("my-entity");
        driver.findElement(By.name("metric")).sendKeys("my-metric");

        driver.findElement(By.name("value")).sendKeys("50");
        driver.findElement(By.name("series")).click();
        WebElement successMessage = driver.findElement(By.cssSelector("form.series .form-status>.alert-success"));
        assertEquals(generateAssertMessage("Wrong success message"), "Series inserted successfully", successMessage.getText().trim());

        driver.findElement(By.name("value")).sendKeys("150");
        driver.findElement(By.name("series")).click();
        successMessage = driver.findElement(By.cssSelector("form.series .form-status>.alert-success"));
        assertEquals(generateAssertMessage("Wrong success message"), "Series inserted successfully", successMessage.getText().trim());

        driver.navigate().to(AtsdTest.url + "/portals/series?entity=my-entity&metric=my-metric");
        List<WebElement> widgets = driver.findElements(By.className("widget"));
        assertNotEquals(generateAssertMessage("No widgets for portal"), 0, widgets.size());
    }
}
