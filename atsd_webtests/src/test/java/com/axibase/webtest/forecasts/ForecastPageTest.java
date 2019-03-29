package com.axibase.webtest.forecasts;

import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ForecastPageTest extends AtsdTest {
    private static String parserXml = CSVImportParserAsSeriesTest.class.getResource("csv-parsers.xml").getFile();
    private static String dataCSV = CSVImportParserAsSeriesTest.class.getResource("csv-parser-test.csv").getFile();
    private static boolean isDataMissing = true;

    @Before
    public void setUp() {
        this.login();

        if (isDataMissing) {
            importParser();
            importData();
            isDataMissing = false;
        }
        goToForecastPage();
    }

    private void importData() {
        driver.get(url + "/data/csv/parsers/parser/csv-parser-test");
        driver.findElement(By.id("file")).sendKeys(dataCSV);
        driver.findElement(By.id("upload-btn")).click();

    }

    private void importParser() {
        driver.get(url + "/csv/configs/import");
        driver.findElement(By.id("putTable")).findElement(By.xpath(".//input[@type='file']")).sendKeys(parserXml);
        driver.findElement(By.xpath(".//input[@type='submit']")).click();
    }

    @Test
    public void testForecastURLParams() {
        try {
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
            testPeriods("Wrong period", "period-unit", period, "period-count");
            testPeriods("Wrong horizon interval", "horizon-unit", horizonInterval, "horizon-count");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }

    }

    @Test
    public void testPresenceOfForecasts() {
        try {

            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));

            int countInPic = driver.
                    findElements(By.cssSelector("#widget-container > svg > g > g > rect")).size();

            int countInSummary = driver.
                    findElements(By.cssSelector("#summary-container > table > thead > tr > th")).size() - 1;

            assertEquals("Wrong count of forecasts", countInPic, countInSummary);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testAddAndRemoveBtns() {
        try {
            WebElement addButton = driver.findElement(By.id("add-group-btn"));
            WebElement removeButton = driver.findElement(By.id("remove-group-btn"));

            assertVisibility("Remove button is broken", false, "remove-group-btn");

            checkCountOfForecasts(1);
            addButton.click();
            assertVisibility("Remove button is broken", true, "remove-group-btn");
            assertVisibility("Add button is broken", true, "add-group-btn");
            addButton.click();
            removeButton.click();
            checkCountOfForecasts(2);
            addButton.click();
            addButton.click();
            addButton.click();
            checkCountOfForecasts(5);
            assertVisibility("Add button is broken", false, "add-group-btn");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }

    }

    @Test
    public void testClickableUnits() {
        try {
            testUnitsChange("Wrong period unit", "week", "//*[@id='group-editor']/section[1]//button");
            testUnitsChange("Wrong horizon unit", "week", "//*[@id='settings']//button");
            testUnitsChange("Wrong score interval unit", "year", "//*[@id='group-editor']/section[4]//button");
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testChangeActiveSummary() {
        try {
            String countOfGroups = "11";
            setCountOfGroups(countOfGroups);
            setNumberParam("decompose-limit", "19");
            clickSubmitButton();

            List<WebElement> forecasts = driver.findElements(By.xpath("//*/a[text()='(√λ)']"));
            WebElement componentsWindow = driver.findElement(By.id("singular-values-container"));

            for (WebElement forecast : forecasts) {
                forecast.click();
                checkNameOfForecastInComponentsWindow(forecast, componentsWindow);
                checkActiveComponents(forecast, componentsWindow, forecasts.indexOf(forecast));
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
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

            driver.findElement(By.id("add-group-btn")).click();

            checkValueById("Wrong threshold", "10", "decompose-threshold");
            checkValueById("Wrong limit", "12", "decompose-limit");
            checkValueById("Wrong length", "44", "decompose-length");
            checkValueById("Wrong avgFunction name", "median".toUpperCase(), "reconstruct-avg");
            assertEquals("Wrong clustering param", "Novosibirsk".toUpperCase(),
                    driver.findElement(By.id("grouping-auto-clustering-method")).getAttribute("value"));

        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
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

            driver.findElement(By.id("add-group-btn")).click();

            driver.findElement(By.id("grouping-type-none")).click();
            setDecomposeParameters("20", "32", "34");
            setSelectionOption("reconstruct-avg", "Average");


            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(0).click();
            checkValueById("Wrong threshold", "10", "decompose-threshold");
            checkValueById("Wrong limit", "12", "decompose-limit");
            checkValueById("Wrong length", "44", "decompose-length");
            checkValueById("Wrong avgFunction name", "median".toUpperCase(), "reconstruct-avg");
            assertEquals("Wrong clustering param", "Novosibirsk".toUpperCase(),
                    driver.findElement(By.id("grouping-auto-clustering-method")).getAttribute("value"));


            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(1).click();
            checkValueById("Wrong threshold", "20", "decompose-threshold");
            checkValueById("Wrong limit", "32", "decompose-limit");
            checkValueById("Wrong length", "34", "decompose-length");
            checkValueById("Wrong avgFunction name", "avg".toUpperCase(), "reconstruct-avg");
            assertTrue("Wrong clustering param", driver.findElement(By.id("grouping-type-none")).isSelected());


        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testCountOfGroups() {
        try {
            String countOfGroups = "11";
            setCountOfGroups(countOfGroups);
            setNumberParam("decompose-limit", "19");
            clickSubmitButton();

            int countInPic = driver.
                    findElements(By.cssSelector("#widget-container > svg > g > g > rect")).size();

            assertEquals("Wrong count of groups on a chart", countOfGroups, Integer.toString(countInPic));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }

    }

    @Test
    public void testPresenceOfHistoryCharts() {
        try {
            driver.findElement(By.id("add-group-btn")).click();
            setNumberParam("period-count", "20");
            setSelectionOption("aggregation", "sum");
            setSelectionOption("interpolation", "prev");
            driver.findElement(By.id("group-save-btn")).click();

            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));

            int countInTabs = driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).size();

            assertEquals("Wrong count of history charts", 2, countInTabs);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testPresenceOfHistoryChartsInSummary() {
        try {
            driver.findElement(By.id("add-group-btn")).click();
            clickSubmitButton();

            int countInSummary = driver.
                    findElements(By.cssSelector("#summary-container > table > thead > tr > th")).size() - 1;

            assertEquals("Wrong count of history charts in summary", 2, countInSummary);

        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testPresenceOfHistoryChartsInPic() {
        try {
            driver.findElement(By.id("add-group-btn")).click();
            setNumberParam("period-count", "20");
            setSelectionOption("aggregation", "sum");
            setSelectionOption("interpolation", "prev");
            clickSubmitButton();

            int countInPic = driver.
                    findElements(By.cssSelector("#widget-container > svg > g > g > circle")).size();

            int countInSummary = driver.
                    findElements(By.cssSelector("#summary-container > table > thead > tr > th")).size() - 1;

            assertEquals("Wrong count of history charts in pic", 2, countInPic);
            assertEquals("Wrong count of history charts in summary", 2, countInSummary);

        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testNamesInSummary() {
        try {
            driver.findElement(By.id("add-group-btn")).click();
            driver.findElement(By.id("add-group-btn")).click();
            driver.findElements(By.xpath("//*[@id='group-toggle-list']/li")).get(1).click();
            driver.findElement(By.id("remove-group-btn")).click();
            clickSubmitButton();

            String[] names = driver.findElement(By.id("group-toggle-list")).getText().split("\n");
            List<WebElement> forecasts = driver.findElements(By.xpath("//*[@id='summary-container']/table/thead/tr/th"));
            forecasts.remove(0);
            for (int i = 0; i < forecasts.size(); i++) {
                assertTrue("Wrong name of forecast", forecasts.get(i).getText().contains(names[i]));
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }

    }


    private void checkValueById(String errorMessage, String correctValue, String id) {
        assertEquals(errorMessage, correctValue,
                driver.findElement(By.id(id)).getAttribute("value"));
    }

    //What is a bug in Selenium. Without sendKeys option doesn't select;
    private void setSelectionOption(String selectId, String newValue) {
        WebElement elem = driver.findElement(By.id(selectId));
        elem.sendKeys(newValue);
//        driver.findElement(By.id("group-modal")).click();
    }

    private void setDecomposeParameters(String threshold, String componentCount, String windowLen) {
        setNumberParam("decompose-threshold", threshold);
        setNumberParam("decompose-limit", componentCount);
        setNumberParam("decompose-length", windowLen);
    }

    private void setNumberParam(String elementId, String elementValue) {
        WebElement element = driver.findElement(By.id(elementId));
        element.clear();
        element.sendKeys(elementValue);
    }

    private void setCountOfGroups(String groupCount) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setNumberParam("grouping-auto-count", groupCount);
    }

    private void clickSubmitButton() {
        driver.findElement(By.id("group-save-btn")).click();

        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/section[@id='summary-container']/table")));
    }

    private void checkActiveComponents(WebElement forecast, WebElement componentsWindow, int numberOfForecast) {
        int countOfActiveComponents = getCountOfActiveComponents(forecast, numberOfForecast);
        int countOfGreenComponents = componentsWindow.findElements(By.xpath("//*[@fill='green' and not(@class)]")).size();
        assertEquals("Wrong count of green components", countOfActiveComponents, countOfGreenComponents);
    }

    private int getCountOfActiveComponents(WebElement forecast, int numberOfForecast) {
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

    private void testUnitsChange(String errorMessage, String newUnit, String path) {
        driver.findElement(By.xpath(path)).click();
        driver.findElement(By.xpath(path.replace("button", "") +
                "ul/li/a[@data-value='" + newUnit + "']")).click();
        assertEquals(errorMessage, newUnit,
                driver.findElement(By.xpath(path)).getText());
    }

    private void assertVisibility(String errorMessage, boolean isVisible, String elementId) {
        if (driver.findElements(By.cssSelector(elementId)).size() != 0) {
            assertEquals(errorMessage, isVisible, driver.findElement(By.id(elementId)).isDisplayed());
        }
    }

    private void checkCountOfForecasts(int count) {
        assertEquals("Wrong count of forecasts", count,
                driver.findElement(By.id("group-toggle-list")).findElements(By.xpath("./li")).size());
    }

    private void testPeriods(String errorMessage, String unitType, String expectedValue, String countType) {
        String newPeriod = driver.findElement(By.id(countType)).getAttribute("value") + "-" +
                driver.findElement(By.id(unitType)).getAttribute("value");
        assertEquals(errorMessage, expectedValue.toLowerCase(), newPeriod.toLowerCase());

    }

    private void testStartDate(String startDate) {
        String newDate = driver.findElement(By.id("startdate")).getAttribute("value") +
                driver.findElement(By.id("starttime")).getAttribute("value");
        assertEquals("Wrong start date", startDate.replace("T", ""), newDate);
    }

    private void goToForecastPage() {
        WebDriverWait wait = new WebDriverWait(driver, 5);

        goToMetricPage();
        goToSeriesPage(wait);
        driver.findElement(By.xpath("//*/a[@data-original-title='Statistics']")).click();
        clickAndSwitch(driver.findElement(By.xpath("//*/a[text()='Forecast']")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("forecasts-container")));
    }

    private void goToSeriesPage(WebDriverWait wait) {
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
}
