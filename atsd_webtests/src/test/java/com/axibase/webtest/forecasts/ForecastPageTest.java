package com.axibase.webtest.forecasts;

import com.axibase.webtest.service.AtsdTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class ForecastPageTest extends AtsdTest {

    @Before
    public void setUp() {
        login();
        this.goToForecastPage();
    }

    @Test
    public void testForecastURLParams() {
        String horizonInterval = "1-day";
        String period = "25-minute";
        String startDate = "2019-03-17T08:11:22";

        String newURL = String.format(driver.getCurrentUrl().split("startDate")[0] +
                        "startDate=%s&" +
                        "horizonInterval=%s&" +
                        "period=%s",
                startDate, horizonInterval, period);
        driver.navigate().to(newURL);

        testStartDate(startDate);
        testPeriods("period-count", "period-unit", period, "Wrong period");
        testPeriods("horizon-count", "horizon-unit", horizonInterval, "Wrong horizon interval");
    }

    @Test
    public void testPresenceOfForecasts(){
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));

        int countInPic = driver.
                findElements(By.cssSelector("#widget-container > svg > g > g > rect")).size();
        int countInSummary = driver.
                findElements((By.cssSelector("#summary-container > table > thead > tr > th"))).size() -1;

        Assert.assertEquals("Wrong count of forecasts",countInPic,countInSummary);
    }

    @Test
    public void testAddAndRemoveBtns() {

        WebElement addButton = driver.findElement(By.id("add-group-btn"));
        WebElement removeButton = driver.findElement(By.id("remove-group-btn"));

        checkIsVisible("remove-group-btn", false, "Remove button is broken");
        checkCountOfForecasts(1);
        addButton.click();
        checkIsVisible("remove-group-btn", true, "Remove button is broken");
        checkIsVisible("add-group-btn", true, "Add button is broken");
        addButton.click();
        removeButton.click();
        checkCountOfForecasts(2);
        addButton.click();
        addButton.click();
        addButton.click();
        checkCountOfForecasts(5);
        checkIsVisible("add-group-btn", false, "Add button is broken");

    }

    @Test
    public void testUnitsClickable() {
        testUnitsChange("//*[@id='group-editor']/section[1]//button", "week", "Wrong period unit");
        testUnitsChange("//*[@id='settings']//button", "week", "Wrong horizon unit");
        testUnitsChange("//*[@id='group-editor']/section[4]//button", "year", "Wrong score interval unit");
    }

    private void testUnitsChange(String path, String newUnit, String errorMessage) {
        driver.findElement(By.xpath(path)).click();
        driver.findElement(By.xpath(path.replace("button", "") +
                "ul/li/a[@data-value='" + newUnit + "']")).click();
        Assert.assertEquals(errorMessage, newUnit + "s",
                driver.findElement(By.xpath(path)).getText());
    }

    private void checkIsVisible(String selector, boolean isVisible, String errorMessage) {
        Assert.assertEquals(errorMessage, isVisible,
                driver.findElement(By.id(selector)).isDisplayed());
    }

    private void checkCountOfForecasts(int count) {
        Assert.assertEquals("Wrong count of forecasts", count,
                driver.findElement(By.id("group-toggle-list")).findElements(By.xpath("./li")).size());
    }

    private void testPeriods(String countType, String unitType, String expectedValue, String errorMessage) {
        String newPeriod = driver.findElement(By.id(countType)).getAttribute("value") + "-" +
                driver.findElement(By.id(unitType)).getAttribute("value");
        Assert.assertEquals(errorMessage, expectedValue.toLowerCase(), newPeriod.toLowerCase());

    }

    private void testStartDate(String startDate) {
        String newDate = driver.findElement(By.id("startdate")).getAttribute("value") +
                driver.findElement(By.id("starttime")).getAttribute("value");
        Assert.assertEquals("Wrong start date", startDate.replace("T", ""), newDate);
    }


    private void goToForecastPage() {
        WebDriverWait wait = new WebDriverWait(driver, 5);

        goToSeriesPage();
        goToStatisticPage(wait);

        clickAndSwitch(driver.findElement(By.xpath("//*/a[text()='Forecast']")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("forecasts-container")));
    }

    private void goToStatisticPage(WebDriverWait wait) {
        String seriesName = "cpu_*";

        WebElement searchField = driver.findElement(By.xpath("//*/div[@id='searchResults_filter']//input[@type='search']"));
        searchField.clear();
        searchField.sendKeys(seriesName);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/a[@class='stats']")));

        List<WebElement> seriesTable = driver.findElement(By.id("searchResults")).findElements(By.xpath("./tbody/tr"));
        Assert.assertTrue("There is no such series", seriesTable.size() != 0);

        clickAndSwitch(seriesTable.get(0).findElement(By.xpath("//*/a[@class='stats']")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/a[text()='Forecast']")));
    }

    private void goToSeriesPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Series')]")).click();
        Assert.assertEquals("Wrong Page", driver.getCurrentUrl(), url + "/series/search");
    }

    private void clickAndSwitch(WebElement link) {
        Assert.assertNotEquals("This is not a link", link.getAttribute("target"), null);
        link.click();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.get(1));
    }
}
