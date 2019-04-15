package com.axibase.webtest.forecasts;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CSVDataUploaderService;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;

public class ForecastPageTest extends AtsdTest {
    private final String START_URL = "/series/forecast?entity=nurswgvml007&" +
            "metric=forecastpagetest&tag_name1=forecastPageTest&tag_name2=forecastPageTest&" +
            "startDate=2019-03-16T08:11:22.000Z&horizonInterval=12-HOUR&period=5-MINUTE";
    private static final String DATA_CSV = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-data.csv").getFile();
    private static CSVDataUploaderService csvDataUploaderService;

    @Before
    public void setUp() {
        this.login();
        if (csvDataUploaderService == null) {
            csvDataUploaderService = new CSVDataUploaderService(AtsdTest.driver, AtsdTest.url);
            csvDataUploaderService.uploadWithParser(DATA_CSV, "test-atsd-import-series-parser");
        }
        driver.get(AtsdTest.url + START_URL);
    }

    @Test
    public void testStrictTagsAndDataMapping() {
        driver.get(removeURLParameter(driver.getCurrentUrl(), "tag_name1"));

        assertNotEquals("There is some data but with not full set of tags there shouldn't be any data", "Loading...",
                driver.findElement(By.cssSelector("#widget-container > .axi-tooltip.axi-tooltip-info > .axi-tooltip-inner"))
                        .getText());
        assertTrue("There are some charts but there shouldn't be",
                driver.findElements(By.cssSelector("#widget-container > svg > g > g > circle")).isEmpty());
    }

    @Test
    public void testCorrectnessOfLinkClicks() {
        try {
            driver.findElement(By.className("breadcrumb")).findElement(By.linkText("nurswgvml007")).click();
            assertEquals("Wrong page while click on entity link in breadcrumb",
                    "Entity: nurswgvml007", driver.getTitle());
            driver.navigate().back();
            driver.findElement(By.className("breadcrumb")).findElement(By.linkText("forecastpagetest")).click();
            assertEquals("Wrong page while click on metric link in breadcrumb",
                    "Metric: forecastpagetest", driver.getTitle());
            driver.navigate().back();
            assertEquals("Wrong tag parameters in breadcrumb",
                    "tag_name1=\"forecastPageTest\", tag_name2=\"forecastPageTest\"",
                    driver.findElement(By.className("breadcrumb")).findElement(By.partialLinkText("tag")).getText());
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
    public void testPresenceOfTooltipsInRegularizeBlock() {
        try {
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='aggregation']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='interpolation']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='period-count']")));
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
    public void testPresenceOfTooltipsInDecomposeBlock() {
        try {
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='decompose-threshold']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='decompose-limit']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='decompose-length']")));
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
    public void testPresenceOfTooltipsInGroupBlock() {
        try {
            setCountOfGroups("1");
            System.out.println();
            clickOnStackSwitch();

            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='grouping-auto-count']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='grouping-auto-clustering-method']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='grouping-auto-stack']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='grouping-auto-union']")));

            driver.findElement(By.id("grouping-type-manual")).click();
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='grouping-manual-groups']")));
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
    public void testPresenceOfTooltipsInForecastBlock() {
        try {
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='reconstruct-avg']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='forecast-score-interval-count']")));
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
    public void testPresenceOfTooltipsInSettingsBlock() {
        try {
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='startdate']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='enddate']")));
            waitUntilTooltipIsShown(driver.findElement(By.xpath("//*[@for='horizon-count']")));
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
    public void testPresenceOfTooltipsInSettingsButtonsBlock() {
        try {
            WebElement settingsBlock = driver.findElement(By.id("settings"));
            waitUntilTooltipIsShown(settingsBlock.findElement(By.tagName("footer")).findElement(By.className("icon-sum")));
            waitUntilTooltipIsShown(driver.findElement(By.id("get-config-btn")));
            waitUntilTooltipIsShown(driver.findElement(By.id("save-config-btn")));
            waitUntilTooltipIsShown(driver.findElement(By.id("save-forecast-btn")));
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
    public void testCloneButtonWithErrorInForm() {
        try {
            setValueParamById("decompose-threshold", "100");
            getAddButton().click();
            assertEquals("New tab is created but there is an error in the form", 1,
                    driver.findElements(By.cssSelector("#group-toggle-list li")).size());
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
    public void testSubmitButtonWithThresholdErrorInForm() {
        try {
            setValueParamById("decompose-threshold", "100");
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("Submit button was submitted but there is an error in the form",
                    driver, driver.findElement(By.id("decompose-threshold")));
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
    public void testSubmitButtonWithWindowLengthErrorInForm() {
        try {
            setValueParamById("decompose-length", "100");
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("Submit button was submitted but there is an error in the form",
                    driver, driver.findElement(By.id("decompose-length")));
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
    public void testSubmitButtonWithComponentCountAndCountOfGroupsErrorInForm() {
        try {
            setValueParamById("decompose-limit", "1");
            setCountOfGroups("10");
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("Submit button was submitted but there is an error in the form",
                    driver, driver.findElement(By.id("grouping-auto-count")));
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
    public void testSubmitButtonWithManualGroupErrorInForm() {
        try {
            driver.findElement(By.id("grouping-type-manual")).click();
            driver.findElement(By.id("grouping-manual-groups-0")).clear();
            driver.findElement(By.id("grouping-manual-groups-1")).clear();
            driver.findElement(By.id("grouping-manual-groups-2")).clear();
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("Submit button was submitted but there is an error in the form",
                    driver, driver.findElement(By.id("grouping-manual-groups-0")));
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
    public void testChangeColorOfChangedParameters() {
        try {
            checkHighlightOfSelectionElement("aggregation", "SUM");
            checkHighlightOfSelectionElement("interpolation", "PREVIOUS");
            checkHighlightOfNumericElement("period-count", "2");
            checkHighlightOfNumericElement("decompose-threshold", "1");
            driver.findElement(By.id("decompose-threshold")).sendKeys(Keys.BACK_SPACE);
            driver.findElement(By.id("decompose-threshold")).sendKeys(Keys.DELETE);
            checkHighlightOfNumericElement("decompose-limit", "12");
            checkHighlightOfNumericElement("decompose-length", "1");
            checkHighlightOfSelectionElement("reconstruct-avg", "MEDIAN");
            checkHighlightOfNumericElement("forecast-score-interval-count", "11");
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
    public void testForecastURLParams() {
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
            params.put("tag_name1", "forecastPageTest");

            String newURL = createNewURL(AtsdTest.url + "/series/forecast", params);
            driver.navigate().to(newURL);

            assertRegularizeOptionValues(params.get("aggregation"), params.get("interpolation"), "25",
                    "minute", "URL params test");
            assertDecomposeOptionValues(params.get("componentThreshold"), params.get("componentCount"),
                    params.get("windowLength"), "URL params test");
            assertForecastOptions("AVG", "1", "minute", "URL params test");
            assertStartDate("URLParams: Wrong Start Date", params.get("startDate"), "start");
            assertStartDate("URLParams: Wrong End Date", params.get("endDate"), "end");
            assertIntervalEquals("URLParams: Wrong horizon interval", params.get("horizonInterval"),
                    "horizon");
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
    public void testPresenceOfChartsUntilSubmit() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 20);
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
    public void testFromMaxToMinTabsButtonsCase() {
        try {
            getAddButton().click();
            getAddButton().click();
            getAddButton().click();
            getAddButton().click();

            assertCountOfForecasts(5);
            getRemoveButton().click();
            getRemoveButton().click();
            getRemoveButton().click();
            getRemoveButton().click();
            assertCountOfForecasts(1);
            assertInvisibility("Remove button is broken:", "remove-group-btn");
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
    public void testFromMinToMaxTabsButtonsCase() {
        try {
            assertInvisibility("Remove button is broken:", "remove-group-btn");
            assertCountOfForecasts(1);
            getAddButton().click();
            getAddButton().click();
            getAddButton().click();
            getAddButton().click();
            assertCountOfForecasts(5);
            assertInvisibility("Add button is broken:", "add-group-btn");
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
    public void testInBetweenButtonsCase() {
        try {
            assertCountOfForecasts(1);
            getAddButton().click();
            assertCountOfForecasts(2);
            assertVisibility("Remove button is broken:", "remove-group-btn");
            assertVisibility("Add button is broken:", "add-group-btn");
            getAddButton().click();
            getRemoveButton().click();
            assertCountOfForecasts(2);
            getRemoveButton().click();
            assertCountOfForecasts(1);
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
            setUnitParameter("period", "week");
            assertUnitChange("Wrong period unit", "week", "period");
            setUnitParameter("horizon", "week");
            assertUnitChange("Wrong horizon unit", "week", "horizon");
            setUnitParameter("forecast-score-interval", "year");
            assertUnitChange("Wrong score interval unit", "year", "forecast-score-interval");
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
            setValueParamById("decompose-limit", "19");
            submitFormAndWait();

            List<WebElement> forecasts = driver.findElements(By.xpath("//*[@id='summary-container']//a[text()='(√λ)']"));
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
    public void testActiveRegularizeOptionsCloning() {
        try {
            setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute");
            getAddButton().click();
            assertRegularizeOptionValues("PERCENTILE_99", "PREVIOUS", "20", "minute", "cloning");
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
    public void testActiveDecomposeOptionsCloning() {
        try {
            setDecomposeOptions("10", "12", "44");
            getAddButton().click();
            assertDecomposeOptionValues("10", "12", "44", "cloning");
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
    public void testActiveForecastOptionsCloning() {
        try {
            setForecastOptions("MEDIAN", "10", "year");
            getAddButton().click();
            assertForecastOptions("MEDIAN", "10", "year", "cloning");
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
    public void testActiveGroupOffOptionsCloning() {
        try {
            driver.findElement(By.id("grouping-type-none")).click();
            getAddButton().click();
            assertSelected(driver.findElement(By.id("grouping-type-none")));
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
    public void testActiveGroupAutoOptionsCloning() {
        try {
            setGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D");
            setValueParamById("grouping-auto-clustering-v", "0.9");
            setValueParamById("grouping-auto-clustering-c", "0.8");

            getAddButton().click();

            assertSelected(driver.findElement(By.id("grouping-type-auto")));
            assertGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D", "cloning");
            assertValueAttributeOfElement("Wrong v parameter after cloning", "0.9",
                    driver.findElement(By.id("grouping-auto-clustering-v")));
            assertValueAttributeOfElement("Wrong c parameter after cloning", "0.8",
                    driver.findElement(By.id("grouping-auto-clustering-c")));
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
    public void testActiveGroupManualOptionsCloning() {
        try {
            setGroupManualOptions("2-3", "", "2-4");
            getAddButton().click();
            assertGroupManualOptions("2-3", "", "2-4", "cloning");
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
    public void testSwitchTabsRegularizeOptions() {
        try {
            setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute");
            getAddButton().click();

            String[] names = driver.findElement(By.id("group-toggle-list")).getText().split("\n");
            assertNotEquals("Forecast names in tabs are equals but they shouldn't be", names[0], names[1]);

            setRegularizeOptions("SUM", "LINEAR", "10", "hour");
            switchForecastTabByName("Forecast 1");
            assertRegularizeOptionValues("PERCENTILE_99", "PREVIOUS", "20", "minute", "switching");
            switchForecastTabByName("Forecast 2");
            assertRegularizeOptionValues("SUM", "LINEAR", "10", "hour", "switching");
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
    public void testSwitchTabsDecomposeOptions() {
        try {
            setDecomposeOptions("10", "12", "44");
            getAddButton().click();
            setDecomposeOptions("20", "15", "2");

            switchForecastTabByName("Forecast 1");
            assertDecomposeOptionValues("10", "12", "44", "switching");
            switchForecastTabByName("Forecast 2");
            assertDecomposeOptionValues("20", "15", "2", "switching");
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
    public void testSwitchTabsForecastOptions() {
        try {
            setForecastOptions("MEDIAN", "10", "year");
            getAddButton().click();
            setForecastOptions("AVG", "11", "minute");
            switchForecastTabByName("Forecast 1");
            assertForecastOptions("MEDIAN", "10", "year", "switching");
            switchForecastTabByName("Forecast 2");
            assertForecastOptions("AVG", "11", "minute", "switching");
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
    public void testSwitchTabsGroupOffOptions() {
        try {
            driver.findElement(By.id("grouping-type-none")).click();
            getAddButton().click();
            setGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D");
            getAddButton().click();
            setGroupManualOptions("2-3", "", "2-4");

            switchForecastTabByName("Forecast 1");
            assertSelected(driver.findElement(By.id("grouping-type-none")));
            switchForecastTabByName("Forecast 2");
            assertSelected(driver.findElement(By.id("grouping-type-auto")));
            assertGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D", "switching");
            switchForecastTabByName("Forecast 3");
            assertGroupManualOptions("2-3", "", "2-4", "switching");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    private void assertSelected(WebElement element) {
        assertTrue("Wrong grouping mode", element.isSelected());
    }

    @Test
    public void testCountOfGroups() {
        try {
            String countOfGroups = "11";
            setCountOfGroups(countOfGroups);
            setValueParamById("decompose-limit", "19");
            submitFormAndWait();

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
            submitFormAndWait();

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
    public void testPresenceOfHistoryChartsInPicWithDifferentPeriods() {
        try {
            getAddButton().click();
            setValueParamById("period-count", "20");
            submitFormAndWait();

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
    public void testPresenceOfHistoryChartsInPicWithDifferentAggregation() {
        try {
            getAddButton().click();
            setSelectionOption("aggregation", "SUM");
            submitFormAndWait();

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
    public void testPresenceOfHistoryChartsInPicWithDifferentInterpolation() {
        try {
            getAddButton().click();
            setSelectionOption("interpolation", "PREVIOUS");
            submitFormAndWait();

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
            switchForecastTabByName("Forecast 2");
            getRemoveButton().click();
            submitFormAndWait();

            String[] names = driver.findElement(By.id("group-toggle-list")).getText().split("\n");
            List<WebElement> forecasts = driver.findElements(By.xpath("//*[@id='summary-container']//thead//th"));
            forecasts.remove(0);
            for (int i = 0; i < forecasts.size(); i++) {
                assertTrue("Wrong name of forecast in summary table", forecasts.get(i).getText().contains(names[i]));
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

            CommonAssertions.assertValid("The Group Count is validated but it have not to", driver,
                    driver.findElement(By.id("grouping-auto-count")));
            componentCount.sendKeys("3");
            groupCount.sendKeys("10");
            driver.findElement(By.id("group-save-btn")).click();
            CommonAssertions.assertInvalid("The Group Count is not validated but it have to", driver,
                    driver.findElement(By.id("grouping-auto-count")));
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
            CommonAssertions.assertValid("The Component Threshold is validated but it have not to", driver,
                    driver.findElement(By.id("decompose-threshold")));
            setValueParamById("decompose-threshold", "100");
            CommonAssertions.assertInvalid("The Component Threshold is not validated but it have to", driver,
                    driver.findElement(By.id("decompose-threshold")));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    private WebElement getRemoveButton() {
        return driver.findElement(By.id("remove-group-btn"));
    }

    private WebElement getAddButton() {
        return driver.findElement(By.id("add-group-btn"));
    }

    private void assertValueAttributeOfElement(String errorMessage, String correctValue, WebElement element) {
        assertEquals(errorMessage, correctValue, element.getAttribute("value"));
    }

    private void setSelectionOption(String selectId, String newValue) {
        Select select = new Select(driver.findElement(By.id(selectId)));
        select.selectByValue(newValue);
    }

    private void setDecomposeOptions(String threshold, String componentCount, String windowLen) {
        setValueParamById("decompose-threshold", threshold);
        setValueParamById("decompose-limit", componentCount);
        setValueParamById("decompose-length", windowLen);
    }

    private void setValueParamById(String elementId, String elementValue) {
        WebElement element = driver.findElement(By.id(elementId));
        element.clear();
        element.sendKeys(elementValue);
    }

    private void setCountOfGroups(String groupCount) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setValueParamById("grouping-auto-count", groupCount);
    }

    private void submitFormAndWait() {
        driver.findElement(By.id("group-save-btn")).click();
        waitUntilSummaryTableIsLoaded();
    }

    private void waitUntilSummaryTableIsLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='summary-container']/table")));
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

    private void assertUnitChange(String errorMessage, String newUnit, String elementName) {
        assertEquals(errorMessage, newUnit,
                driver.findElement(By.id(elementName + "-count")).findElement(By.xpath(".."))
                        .findElement(By.tagName("button")).getText());
    }

    private void setUnitParameter(String elementName, String newUnit) {
        WebElement intervalInput = driver.findElement(By.id(elementName + "-count")).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(newUnit)).click();
    }

    private void assertVisibility(String errorMessage, String elementId) {
        assertTrue("there is no such element on the page", !driver.findElements(By.id(elementId)).isEmpty());
        assertTrue(errorMessage + "should be visible but it is not", driver.findElement(By.id(elementId)).isDisplayed());
    }

    private void assertInvisibility(String errorMessage, String elementId) {
        if (!driver.findElements(By.id(elementId)).isEmpty()) {
            assertFalse(errorMessage + "should be not visible but it is", driver.findElement(By.id(elementId)).isDisplayed());
        }
    }

    private void assertCountOfForecasts(int count) {
        assertEquals("Wrong count of forecasts", count,
                driver.findElements(By.cssSelector("#group-toggle-list > li")).size());
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

    private String createNewURL(String URLPrefix, Map<String, String> params) {
        List<NameValuePair> paramsForEncoding = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsForEncoding.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            return new URIBuilder(URLPrefix).addParameters(paramsForEncoding).build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Wrong URI", e);
        }
    }

    private void checkHighlightOfSelectionElement(String elementId, String value) {
        assertLackOfHighlight(elementId);
        setSelectionOption(elementId, value);
        assertPresenceOfHighlight(elementId);
    }

    private void checkHighlightOfNumericElement(String elementId, String value) {
        assertLackOfHighlight(elementId);
        setValueParamById(elementId, value);
        assertPresenceOfHighlight(elementId);
    }

    private void assertPresenceOfHighlight(String elementId) {
        assertTrue("Parameter with id: " + elementId + "is not highlighted but it should be",
                driver.findElement(By.id(elementId)).getAttribute("class").contains("highlight"));
    }

    private void assertLackOfHighlight(String elementId) {
        assertFalse("Parameter with id: " + elementId + "is highlighted but it shouldn't",
                driver.findElement(By.id(elementId)).getAttribute("class").contains("highlight"));
    }

    private void clickOnStackSwitch() {
        driver.findElement(By.xpath("//*[@id='grouping-auto-stack']")).findElement(By.xpath("..")).click();
    }

    private void waitUntilTooltipIsShown(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();

        Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(500, MILLISECONDS);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("tooltip")));

        //that is for tooltip does not cover the button after tooltip is shown
        action.moveToElement(driver.findElement(By.id("group-save-btn"))).perform();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("tooltip")));
    }

    private String removeURLParameter(String url, String parameterName) {
        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(url);
            List<NameValuePair> queryParameters = uriBuilder.getQueryParams();
            for (NameValuePair queryPair : queryParameters) {
                if (queryPair.getName().equals(parameterName)) {
                    queryParameters.remove(queryPair);
                    break;
                }
            }
            uriBuilder.setParameters(queryParameters);
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Wrong current URL", e);
        }
    }

    private void assertRegularizeOptionValues(String aggregationType, String interpolationType,
                                              String periodCount, String periodUnit, String testType) {
        assertValueAttributeOfElement("Wrong aggregation after " + testType, aggregationType,
                driver.findElement(By.id("aggregation")));
        assertValueAttributeOfElement("Wrong interpolation after " + testType, interpolationType,
                driver.findElement(By.id("interpolation")));
        assertIntervalEquals("Wrong period in the regularize section",
                periodCount + "-" + periodUnit, "period");
    }

    private void setRegularizeOptions(String aggregationType, String interpolationType,
                                      String periodCount, String periodUnit) {
        setSelectionOption("aggregation", aggregationType);
        setSelectionOption("interpolation", interpolationType);
        setValueParamById("period-count", periodCount);
        setUnitParameter("period", periodUnit);
    }

    private void assertDecomposeOptionValues(String threshold, String componentCount, String windowLen, String testType) {
        assertValueAttributeOfElement("Wrong threshold after " + testType, threshold,
                driver.findElement(By.id("decompose-threshold")));
        assertValueAttributeOfElement("Wrong component count after " + testType, componentCount,
                driver.findElement(By.id("decompose-limit")));
        assertValueAttributeOfElement("Wrong window length after " + testType, windowLen,
                driver.findElement(By.id("decompose-length")));
    }

    private void setForecastOptions(String average, String scoreIntervalCount, String scoreIntervalUnit) {
        setSelectionOption("reconstruct-avg", average);
        setValueParamById("forecast-score-interval-count", scoreIntervalCount);
        setUnitParameter("forecast-score-interval", scoreIntervalUnit);
    }

    private void assertForecastOptions(String average, String intervalCount, String intervalUnit, String testType) {
        assertValueAttributeOfElement("Wrong average name after " + testType, average,
                driver.findElement(By.id("reconstruct-avg")));
        assertIntervalEquals("Wrong score interval in the Forecast section",
                intervalCount + "-" + intervalUnit, "forecast-score-interval");
    }

    private void assertGroupAutoOptions(String countOfGroups, String clustering, String union1,
                                        String union2, String union3, String testType) {
        assertValueAttributeOfElement("Wrong clustering after " + testType, countOfGroups,
                driver.findElement(By.id("grouping-auto-count")));
        assertValueAttributeOfElement("Wrong clustering after " + testType, clustering,
                driver.findElement(By.id("grouping-auto-clustering-method")));
        assertValueAttributeOfElement("Wrong union value after " + testType, union1,
                driver.findElement(By.id("grouping-auto-union-0")));
        assertValueAttributeOfElement("Wrong union value after " + testType, union2,
                driver.findElement(By.id("grouping-auto-union-1")));
        assertValueAttributeOfElement("Wrong union value after " + testType, union3,
                driver.findElement(By.id("grouping-auto-union-2")));
    }

    private void setGroupAutoOptions(String countOfGroups, String clustering, String union1, String union2, String union3) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setCountOfGroups(countOfGroups);
        setSelectionOption("grouping-auto-clustering-method", clustering);
        clickOnStackSwitch();
        setValueParamById("grouping-auto-union-0", union1);
        setValueParamById("grouping-auto-union-1", union2);
        setValueParamById("grouping-auto-union-2", union3);
    }

    private void assertGroupManualOptions(String group1, String group2, String group3, String testType) {
        assertSelected(driver.findElement(By.id("grouping-type-manual")));
        assertValueAttributeOfElement("Wrong union value after " + testType, group1,
                driver.findElement(By.id("grouping-manual-groups-0")));
        assertValueAttributeOfElement("Wrong union value after " + testType, group2,
                driver.findElement(By.id("grouping-manual-groups-1")));
        assertValueAttributeOfElement("Wrong union value after " + testType, group3,
                driver.findElement(By.id("grouping-manual-groups-2")));
    }

    private void setGroupManualOptions(String group1, String group2, String group3) {
        driver.findElement(By.id("grouping-type-manual")).click();
        setValueParamById("grouping-manual-groups-0", group1);
        setValueParamById("grouping-manual-groups-1", group2);
        setValueParamById("grouping-manual-groups-2", group3);
    }

    private void switchForecastTabByName(String s) {
        driver.findElement(By.id("group-toggle-list")).findElement(By.partialLinkText(s)).click();
    }
}
