package com.axibase.webtest.forecasts;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CSVDataUploaderService;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ForecastPageTest extends AtsdTest {
    private static final String DATA_CSV = CSVImportParserAsSeriesTest.class.getResource("csv-parser-test.csv").getFile();
    private static CSVDataUploaderService csvDataUploaderService;

    @Before
    public void setUp() {
        this.login();
        if (csvDataUploaderService == null) {
            csvDataUploaderService = new CSVDataUploaderService(AtsdTest.driver, AtsdTest.url);
            csvDataUploaderService.uploadWithParser(DATA_CSV, "test-atsd-import-series-parser");
        }
        goToForecastPage();
    }

    @Test
    public void testForecastURLParams() throws URISyntaxException {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("entity", "nurswgvml007");
            params.put("metric", "forecastpagetest");
            params.put("startDate", "2019-03-17T08:11:22");
            params.put("endDate", "2019-03-18T08:11:22");
            params.put("horizonInterval", "1-day");
            params.put("period", "25-minute");
            params.put("scoreInterval", "1-MINUTE");
            params.put("componentThreshold", "10");
            params.put("windowLength", "45");
            params.put("componentCount", "100");
            params.put("aggregation", "PERCENTILE_75");
            params.put("interpolation", "PREVIOUS");


            String newURL = createNewURL(params);
            driver.navigate().to(newURL);

            assertStartDate("URLParams:Wrong Start Date", params.get("startDate"), "start");
            assertStartDate("URLParams:Wrong End Date", params.get("endDate"), "end");
            assertIntervalEquals("URLParams:Wrong period", params.get("period"), "period");
            assertIntervalEquals("URLParams:Wrong horizon interval", params.get("horizonInterval"), "horizon");
            assertIntervalEquals("URLParams:Wrong score interval", params.get("scoreInterval"), "forecast-score-interval");
            checkValueById("URLParams:Wrong threshold", params.get("componentThreshold"), "decompose-threshold");
            checkValueById("URLParams:Wrong window length", params.get("windowLength"), "decompose-length");
            checkValueById("URLParams:Wrong component count", params.get("componentCount"), "decompose-limit");
            checkValueById("URLParams:Wrong aggregation", params.get("aggregation"), "aggregation");
            checkValueById("URLParams:Wrong interpolation", params.get("interpolation"), "interpolation");
        } catch (AssertionError | URISyntaxException err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }

    }

    @Test
    public void testPresenceOfForecasts() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));

            int forecastCountInChart = driver.
                    findElements(By.cssSelector("#widget-container > svg > g > g > rect")).size();

            int forecastCountInSummary = driver.
                    findElements(By.cssSelector("#summary-container > table > thead > tr > th")).size() - 1;

            assertEquals("Different count of forecasts in the chart and in the summary",
                    forecastCountInChart, forecastCountInSummary);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testAddAndRemoveButtons() {
        try {
            checkAddAndRemoveButtonsInnerCase();
            checkFromMinToMaxTabsCase();
            checkFromMaxToMinTabsCase();
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testClickableUnits() {
        try {
            testUnitsChange("Wrong period unit", "week", "period");
            testUnitsChange("Wrong horizon unit", "week", "horizon");
            testUnitsChange("Wrong score interval unit", "year", "forecast-score-interval");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testChangeActiveSummary() {
        try {
            String countOfGroups = "11";
            setCountOfGroups(countOfGroups);
            setNumberParamById("decompose-limit", "19");
            clickSubmitButton();

            List<WebElement> forecasts = driver.findElements(By.xpath("//*/a[text()='(√λ)']"));
            WebElement componentsWindow = driver.findElement(By.id("singular-values-container"));

            for (WebElement forecast : forecasts) {
                forecast.click();
                checkNameOfForecastInComponentsWindow(forecast, componentsWindow);
                checkActiveComponents(componentsWindow, forecasts.indexOf(forecast));
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }

    }

    @Test
    public void testActiveOptionsCloning() {
        try {
            setCountOfGroups("10");
            setDecomposeParameters("10", "12", "44");
            setSelectionOption("reconstruct-avg", "Median");
            setSelectionOption("grouping-auto-clustering-method", "Novosibirsk");

            getAddButton().click();

            checkValueById("Wrong threshold after cloning", "10", "decompose-threshold");
            checkValueById("Wrong limit after cloning", "12", "decompose-limit");
            checkValueById("Wrong length after cloning", "44", "decompose-length");
            checkValueById("Wrong avgFunction name after cloning", "median".toUpperCase(), "reconstruct-avg");
            checkValueById("Wrong clustering param after cloning", "Novosibirsk".toUpperCase(), "grouping-auto-clustering-method");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testSwitchTabsOptions() {
        try {
            setCountOfGroups("10");
            setDecomposeParameters("10", "12", "44");
            setSelectionOption("reconstruct-avg", "Median");
            setSelectionOption("grouping-auto-clustering-method", "Novosibirsk");

            getAddButton().click();

            driver.findElement(By.id("grouping-type-none")).click();
            setDecomposeParameters("20", "32", "34");
            setSelectionOption("reconstruct-avg", "Average");

            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(0).click();
            checkValueById("Wrong threshold in first tab", "10", "decompose-threshold");
            checkValueById("Wrong limit in first tab", "12", "decompose-limit");
            checkValueById("Wrong length in first tab", "44", "decompose-length");
            checkValueById("Wrong avgFunction name in first tab", "median".toUpperCase(), "reconstruct-avg");
            checkValueById("Wrong clustering param in first tab", "Novosibirsk".toUpperCase(), "grouping-auto-clustering-method");

            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(1).click();
            checkValueById("Wrong threshold in second tab", "20", "decompose-threshold");
            checkValueById("Wrong limit in second tab", "32", "decompose-limit");
            checkValueById("Wrong length in second tab", "34", "decompose-length");
            checkValueById("Wrong avgFunction name in second tab", "avg".toUpperCase(), "reconstruct-avg");
            assertTrue("Wrong clustering param in second tab", driver.findElement(By.id("grouping-type-none")).isSelected());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testCountOfGroups() {
        try {
            String countOfGroups = "11";
            setCountOfGroups(countOfGroups);
            setNumberParamById("decompose-limit", "19");
            clickSubmitButton();

            int countInPic = driver.findElements(By.cssSelector("#widget-container > svg > g > g > rect")).size();

            assertEquals("Wrong count of groups on the chart", countOfGroups, Integer.toString(countInPic));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testPresenceOfHistoryChartsInSummary() {
        try {
            getAddButton().click();
            clickSubmitButton();

            int forecastCountInSummary = driver.
                    findElements(By.cssSelector("#summary-container > table > thead > tr > th")).size() - 1;

            assertEquals("Wrong count of history charts in summary", 2, forecastCountInSummary);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testPresenceOfHistoryChartsInPic() {
        try {
            getAddButton().click();
            setNumberParamById("period-count", "20");
            setSelectionOption("aggregation", "sum");
            setSelectionOption("interpolation", "prev");
            clickSubmitButton();

            int forecastCountInChart = driver.
                    findElements(By.cssSelector("#widget-container > svg > g > g > circle")).size();
            assertEquals("Wrong count of history charts in chart", 2, forecastCountInChart);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testNamesInSummary() {
        try {
            getAddButton().click();
            getAddButton().click();
            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(1).click();
            getRemoveButton().click();
            clickSubmitButton();

            String[] names = driver.findElement(By.id("group-toggle-list")).getText().split("\n");
            List<WebElement> forecasts = driver.findElements(By.xpath("//*[@id='summary-container']//thead//th"));
            forecasts.remove(0);
            for (int i = 0; i < forecasts.size(); i++) {
                assertTrue("Wrong name of forecast", forecasts.get(i).getText().contains(names[i]));
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void componentThresholdAndScoreIntervalAccessTest() {
        try {
            WebElement threshold = driver.findElement(By.id("decompose-threshold"));
            WebElement intervalScore = driver.findElement(By.id("forecast-score-interval-count"));

            assertTrue("The score interval should be displayed before the Component Threshold is filled",
                    intervalScore.isDisplayed());

            threshold.sendKeys("5");
            assertFalse("The score interval shouldn't be displayed after the Component Threshold is filled",
                    intervalScore.isDisplayed());
            threshold.click();
            threshold.sendKeys(Keys.DELETE);
            threshold.sendKeys(Keys.BACK_SPACE);

            assertTrue("The Component Threshold should be enabled before the Score Interval if filled",
                    threshold.isEnabled());
            intervalScore.sendKeys("5");
            driver.findElement(By.xpath("//body")).click();
            assertFalse("The Component Threshold shouldn't be enabled after the Score Interval if filled",
                    threshold.isEnabled());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void componentCountAndGroupCountComparisonTest() {
        try {
            WebElement componentCount = driver.findElement(By.id("decompose-limit"));
            driver.findElement(By.id("grouping-type-auto")).click();
            WebElement groupCount = driver.findElement(By.id("grouping-auto-count"));


            CommonAssertions.assertValid("The Group Count is validated but it have not to",
                    driver.findElements(By.cssSelector("#grouping-auto-count:valid")));
            componentCount.sendKeys("3");
            groupCount.sendKeys("10");
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("The Group Count is not validated but it have to",
                    driver.findElements(By.cssSelector("#grouping-auto-count:valid")));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void componentThresholdBoundValidationTest() {
        try {
            CommonAssertions.assertValid("The Component Threshold is validated but it have not to",
                    driver.findElements(By.cssSelector("#decompose-threshold:valid")));
            setNumberParamById("decompose-threshold", "100");
            CommonAssertions.assertInvalid("The Component Threshold is not validated but it have to",
                    driver.findElements(By.cssSelector("#decompose-threshold:valid")));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    private void checkFromMaxToMinTabsCase() {
        getRemoveButton();

        getRemoveButton().click();
        getRemoveButton().click();
        getRemoveButton().click();
        getRemoveButton().click();
        assertInvisibility("Remove button is broken:", "remove-group-btn");
    }

    private void checkFromMinToMaxTabsCase() {
        getAddButton();
        assertInvisibility("Remove button is broken:", "remove-group-btn");
        assertCountOfForecasts(1);
        getAddButton().click();
        getAddButton().click();
        getAddButton().click();
        getAddButton().click();
        assertCountOfForecasts(5);
        assertInvisibility("Add button is broken:", "add-group-btn");
    }

    private void checkAddAndRemoveButtonsInnerCase() {
        getAddButton();
        getRemoveButton();

        getAddButton().click();

        assertVisibility("Remove button is broken:", "remove-group-btn");
        assertVisibility("Add button is broken:", "add-group-btn");
        getAddButton().click();
        getRemoveButton().click();
        assertCountOfForecasts(2);
        getRemoveButton().click();
        assertCountOfForecasts(1);
    }

    private WebElement getRemoveButton() {
        return driver.findElement(By.id("remove-group-btn"));
    }

    private WebElement getAddButton() {
        return driver.findElement(By.id("add-group-btn"));
    }

    private void checkValueById(String errorMessage, String correctValue, String id) {
        assertEquals(errorMessage, correctValue, driver.findElement(By.id(id)).getAttribute("value"));
    }

    //What is a bug in Selenium. Without sendKeys option doesn't select;
    private void setSelectionOption(String selectId, String newValue) {
        WebElement elem = driver.findElement(By.id(selectId));
        elem.sendKeys(newValue);
    }

    private void setDecomposeParameters(String threshold, String componentCount, String windowLen) {
        setNumberParamById("decompose-threshold", threshold);
        setNumberParamById("decompose-limit", componentCount);
        setNumberParamById("decompose-length", windowLen);
    }

    private void setNumberParamById(String elementId, String elementValue) {
        WebElement element = driver.findElement(By.id(elementId));
        element.clear();
        element.sendKeys(elementValue);
    }

    private void setCountOfGroups(String groupCount) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setNumberParamById("grouping-auto-count", groupCount);
    }

    private void clickSubmitButton() {
        driver.findElement(By.id("group-save-btn")).click();

        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));
    }

    private void checkActiveComponents(WebElement componentsWindow, int numberOfForecast) {
        int countOfActiveComponents = getCountOfActiveComponents(numberOfForecast);
        int countOfGreenComponents = componentsWindow.findElements(By.xpath("//*[@fill='green' and not(@class)]")).size();
        assertEquals("Wrong count of green components", countOfActiveComponents, countOfGreenComponents);
    }

    private int getCountOfActiveComponents(int numberOfForecast) {
        int count = 0;
        String componentsAsString = driver.findElement(By.xpath(String.
                format("//*[@id='summary-container']//tbody/tr[2]/td[%d]", numberOfForecast + 1))).getText();

        for (String str : componentsAsString.split(";")) {
            String[] components = str.split("-");
            if (Integer.parseInt(components[0]) > 20) break;
            if (components.length > 1) {
                if (Integer.parseInt(components[1]) > 20) break;
                count += (Integer.parseInt(components[1]) - Integer.parseInt(components[0]) + 1);
            } else {
                count++;
            }
        }
        return count;
    }

    private void checkNameOfForecastInComponentsWindow(WebElement forecast, WebElement componentsWindow) {
        String name = forecast.findElement(By.xpath("../..")).getText().replace("\n(√λ)", "");
        assertTrue("Wrong forecast name in components window", componentsWindow.getText().contains(name));
    }

    private void testUnitsChange(String errorMessage, String newUnit, String name) {
        driver.findElement(By.id(name + "-count")).findElement(By.xpath("..//button")).click();
        driver.findElement(By.id(name + "-count")).
                findElement(By.xpath("..//ul/li/a[@data-value='" + newUnit + "']")).click();

        assertEquals(errorMessage, newUnit,
                driver.findElement(By.id(name + "-count")).findElement(By.xpath("..//button")).getText());
    }

    private void assertVisibility(String errorMessage, String elementId) {
        if (!driver.findElements(By.id(elementId)).isEmpty()) {
            assertTrue(errorMessage + "should be visible but it is not", driver.findElement(By.id(elementId)).isDisplayed());
        }
    }

    private void assertInvisibility(String errorMessage, String elementId) {
        if (!driver.findElements(By.id(elementId)).isEmpty()) {
            assertFalse(errorMessage + "should be not visible but it is", driver.findElement(By.id(elementId)).isDisplayed());
        }
    }

    private void assertCountOfForecasts(int count) {
        assertEquals("Wrong count of forecasts", count,
                driver.findElement(By.id("group-toggle-list")).findElements(By.xpath("./li")).size());
    }

    private void assertIntervalEquals(String errorMessage, String expectedValue, String typeOfInterval) {
        String newPeriod = driver.findElement(By.id(typeOfInterval + "-count")).getAttribute("value") + "-" +
                driver.findElement(By.id(typeOfInterval + "-unit")).getAttribute("value");
        assertEquals(errorMessage, expectedValue.toLowerCase(), newPeriod.toLowerCase());

    }

    private void assertStartDate(String errorMessage, String sendedDate, String typeOfDate) {
        String newDate = driver.findElement(By.id(typeOfDate + "date")).getAttribute("value") +
                driver.findElement(By.id(typeOfDate + "time")).getAttribute("value");
        assertEquals(errorMessage, sendedDate.replace("T", ""), newDate);
    }

    private void goToForecastPage() {
        WebDriverWait wait = new WebDriverWait(driver, 5);

        goToMetricPage();
        goToSeriesPage();
        driver.findElement(By.xpath("//*/a[@data-original-title='Statistics']")).click();
        clickAndSwitch(driver.findElement(By.xpath("//*/a[text()='Forecast']")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("forecasts-container")));
    }

    private void goToSeriesPage() {
        String seriesName = "forecast**";
        WebElement searchField = driver.findElement(By.xpath("//*/form[@id='search']/input[@type='search']"));
        searchField.clear();
        searchField.sendKeys(seriesName);
        driver.findElement(By.xpath("//*/form[@id='search']/input[@type='submit']")).click();
        driver.findElement(By.xpath("//*[@id='metricsList']//a[@data-original-title='Series']")).click();
    }

    private void goToMetricPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Metric')]")).click();
        assertEquals("Wrong Page", url + "/metrics", driver.getCurrentUrl());
    }

    private void clickAndSwitch(WebElement link) {
        assertNotEquals("This is not a link", link.getAttribute("target"), null);
        link.click();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.get(1));
    }

    private String createNewURL(Map<String, String> params) throws URISyntaxException {
        String URIprefix = AtsdTest.url + "/series/forecast";
        List<NameValuePair> paramsForEncoding = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsForEncoding.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return new URIBuilder(URIprefix).addParameters(paramsForEncoding).build().toString();
    }
}
