package com.axibase.webtest.forecasts;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.pages.ForecastSettingsPage;
import com.axibase.webtest.pages.ForecastViewerPage;
import com.axibase.webtest.pages.PortalPage;
import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CSVDataUploaderService;
import com.axibase.webtest.service.CommonSelects;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ForecastPageTest extends AtsdTest {
    private static final String PAGE_URL = url + "/series/forecast";
    private static final String URL_FOR_GROUPING_WITHOUT_TAGS = PAGE_URL + "?entity=entity-forecast-viewer-test&" +
            "metric=metric-forecast-viewer-test&_g&" +
            "startDate=2019-04-12T14:17:23.000Z";
    private static final String URL_FOR_GROUPING_WITH_TAGS = URL_FOR_GROUPING_WITHOUT_TAGS + "&host=A4AF797F3737&name=entityById&";
    private static final String START_URL = PAGE_URL + "?entity=nurswgvml007&" +
            "metric=forecastpagetest&tag_name1=forecastPageTest&tag_name2=forecastPageTest&" +
            "startDate=2019-03-16T08:11:22.000Z&horizonInterval=12-HOUR&period=5-MINUTE";
    private static final String DATA_CSV = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-data.csv").getFile();
    private CSVDataUploaderService csvDataUploaderService;
    private long TIME_ZONE_HOURS = 0;
    private ForecastViewerPage forecastViewerPage;

    @Before
    public void setUp() {
        this.login();
        csvDataUploaderService = new CSVDataUploaderService(AtsdTest.driver, AtsdTest.url);
        csvDataUploaderService.uploadWithParser(DATA_CSV, "test-atsd-import-series-parser");
        setTimeZone();
        driver.get(START_URL);
        forecastViewerPage = new ForecastViewerPage(driver);
    }

    @Test
    public void testEigenvaluesWithZeroThresholdOnSmallSample() {
        try {
            forecastViewerPage.setStartDate("2019-03-16");
            forecastViewerPage.setStartTime("16:11:00");
            forecastViewerPage.setEndDate("2019-03-16");
            forecastViewerPage.setEndTime("17:11:00");
            forecastViewerPage.setThreshold("0");
            forecastViewerPage.setPeriodCount("15");
            forecastViewerPage.setPeriodUnit("minute");
            forecastViewerPage.setForecastHorizonCount("1");
            forecastViewerPage.setForecastHorizonUnit("hour");
            forecastViewerPage.submitFormAndWait(15);

            assertEquals("There has to be only one silver component", 1,
                    forecastViewerPage.getCountOfPassiveComponentsInComponentContainer());
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
    public void testUnionFieldInvalid() {
        try {
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setGroupCount("1");
            forecastViewerPage.switchStack();
            String[] variants = {"Aa", "A1", "A.2", "a", ".A", "1", "и", "AA-", "A:", "A,"};
            for (String variant : variants) {
                forecastViewerPage.setGroupUnion2(variant);
                CommonAssertions.assertInvalid("Invalid variant:" + variant + " is accepted",
                        driver, forecastViewerPage.getGroupUnion2());
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
    public void testUnionFieldValid() {
        try {
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setGroupCount("1");
            forecastViewerPage.switchStack();
            String[] variants = {"AA", "A", "AA;A", "A-AA", "A;B-A"};
            for (String variant : variants) {
                forecastViewerPage.setGroupUnion2(variant);
                CommonAssertions.assertValid("Valid variant:" + variant + " is not accepted",
                        driver, forecastViewerPage.getGroupUnion2());
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
    public void testComponentIndexesFieldInvalid() {
        try {
            forecastViewerPage.setGroupManual();
            String[] variants = {"a", ".A", "и", "3F", "3a", "3;A", "AA", "A", "AA;A", "A-AA", "A;B-A", "B-A"};
            for (String variant : variants) {
                forecastViewerPage.setGroupComponentIndex1(variant);
                CommonAssertions.assertInvalid("Invalid variant:" + variant + " is accepted",
                        driver, forecastViewerPage.getGroupComponentIndex1());
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
    public void testComponentIndexesFieldValid() {
        try {
            forecastViewerPage.setGroupManual();
            String[] variants = {"11", "2", "32;5", "1-23", "1;3-4"};
            for (String variant : variants) {
                forecastViewerPage.setGroupComponentIndex1(variant);
                CommonAssertions.assertValid("Valid variant:" + variant + " is not accepted",
                        driver, forecastViewerPage.getGroupComponentIndex1());
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
    public void testGroupURLParameterWithTagsOnViewerPage() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
            driver.get(URL_FOR_GROUPING_WITH_TAGS);
            String tags = forecastViewerPage.getGroupedByURLTags();
            assertTrue("There is a missing tag in the grouping", tags.contains("name = entityById"));
            assertTrue("There is a missing tag in the grouping", tags.contains("host = A4AF797F3737"));
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
    public void testGroupURLParameterWithoutTagsOnViewerPage() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
            driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
            assertEquals("There is no sign of presence of grouping", "Grouped by all tags",
                    forecastViewerPage.getGroupedByURLText());
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
    public void testGroupURLParameterWithTagsOnSettingsPage() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");

            driver.get(URL_FOR_GROUPING_WITH_TAGS);
            forecastViewerPage.scheduleForecast();
            switchToWindowTab(1);
            ForecastSettingsPage forecastSettings = new ForecastSettingsPage(driver);
            assertEquals("Wrong type of grouping in forecast settings", "Metric - Entity - Defined Tags",
                    forecastSettings.getGroupingType());
            assertEquals("Wrong tags in grouping in forecast settings", "host name",
                    forecastSettings.getGroupingTags());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        } finally {
            if (driver.getWindowHandles().size() > 1) {
                driver.close();
                switchToWindowTab(0);
            }
        }
    }

    @Test
    public void testGroupURLParameterWithoutTagsOnSettingsPage() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
            driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
            forecastViewerPage.scheduleForecast();
            switchToWindowTab(1);
            ForecastSettingsPage forecastSettings = new ForecastSettingsPage(driver);
            assertEquals("Wrong type of grouping in forecast settings", "Metric - Entity",
                    forecastSettings.getGroupingType());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        } finally {
            if (driver.getWindowHandles().size() > 1) {
                driver.close();
                switchToWindowTab(0);
            }
        }
    }

    @Test
    public void testGroupURLParameterWithTagsInPortal() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
            driver.get(URL_FOR_GROUPING_WITH_TAGS);
            forecastViewerPage.savePortal();
            switchToWindowTab(1);
            PortalPage portalPage = new PortalPage(driver);
            assertTrue("There is no tags section", portalPage.getContentWrapperText().contains("[tags]"));
            assertTrue("There is no host tag", portalPage.getContentWrapperText().contains("host = A4AF797F3737"));
            assertTrue("There is no name tag", portalPage.getContentWrapperText().contains("name = entityById"));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        } finally {
            if (driver.getWindowHandles().size() > 1) {
                driver.close();
                switchToWindowTab(0);
            }
        }
    }

    @Test
    public void testGroupURLParameterWithoutTagsInPortal() {
        try {
            loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
            driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
            forecastViewerPage.savePortal();
            switchToWindowTab(1);
            PortalPage portalPage = new PortalPage(driver);
            assertFalse("There is tag section without tags", portalPage.getContentWrapperText().contains("[tags]"));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        } finally {
            if (driver.getWindowHandles().size() > 1) {
                driver.close();
                switchToWindowTab(0);
            }
        }
    }

    @Test
    public void testStrictTagsAndDataMapping() {
        try {
            driver.get(removeURLParameter(driver.getCurrentUrl(), "tag_name1"));
            assertFalse("There is some data but with not full set of tags there shouldn't be any data",
                    forecastViewerPage.isWidgetContainerLoading());
            assertEquals("There are some charts but it shouldn't be", 0,
                    forecastViewerPage.getCountOfForecastsInWidgetContainer());
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
    public void testCorrectnessOfLinkClicks() {
        try {
            forecastViewerPage.getBreadcrumbElement(2).click();
            assertEquals("Wrong page while click on entity link in breadcrumb",
                    "Entity: nurswgvml007", driver.getTitle());
            driver.navigate().back();
            forecastViewerPage.getBreadcrumbElement(1).click();
            assertEquals("Wrong page while click on metric link in breadcrumb",
                    "Metric: forecastpagetest", driver.getTitle());
            driver.navigate().back();

            assertEquals("Wrong tag parameters in breadcrumb",
                    "tag_name1=\"forecastPageTest\", tag_name2=\"forecastPageTest\"",
                    forecastViewerPage.getBreadcrumbElement(3).getText());
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
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getAggregation()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getInterpolation()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getPeriodCount()));
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
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getThreshold()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getComponentCount()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getWindowLength()));
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
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setGroupCount("1");
            forecastViewerPage.switchStack();

            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getGroupCount()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getClustering()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getStackElement()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getGroupUnion1()));

            forecastViewerPage.setGroupManual();
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getGroupComponentIndex1()));
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
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getAveragingFunction()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getScoreIntervalCount()));
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
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getStartDate()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getEndDate()));
            forecastViewerPage.waitUntilTooltipIsShown(CommonSelects.getElementTooltipByFor(forecastViewerPage.getForecastHorizonCount()));
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
            forecastViewerPage.waitUntilTooltipIsShown(forecastViewerPage.getStatistics());
            forecastViewerPage.waitUntilTooltipIsShown(forecastViewerPage.getPortal());
            forecastViewerPage.waitUntilTooltipIsShown(forecastViewerPage.getDownloadingPortal());
            forecastViewerPage.waitUntilTooltipIsShown(forecastViewerPage.getForecastSettings());
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
            forecastViewerPage.setThreshold("100");
            forecastViewerPage.addForecastTab();
            assertEquals("New tab is created but there is an error in the form", 1,
                    forecastViewerPage.getForecastTabNames().length);
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
            storeCurrentWidgetContainerInJS();
            forecastViewerPage.setThreshold("100");
            forecastViewerPage.clickSubmitButton();
            isStoredWidgetContainerEqualsNew();
            assertTrue("Submit button was submitted but there is an error in the form",
                    isStoredWidgetContainerEqualsNew());
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
            storeCurrentWidgetContainerInJS();
            forecastViewerPage.setWindowLength("100");
            forecastViewerPage.clickSubmitButton();
            assertTrue("Submit button was submitted but there is an error in the form",
                    isStoredWidgetContainerEqualsNew());
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
            storeCurrentWidgetContainerInJS();
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setComponentCount("1");
            forecastViewerPage.setGroupCount("10");
            forecastViewerPage.clickSubmitButton();
            assertTrue("Submit button was submitted but there is an error in the form",
                    isStoredWidgetContainerEqualsNew());
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
            storeCurrentWidgetContainerInJS();
            forecastViewerPage.setGroupManual();
            forecastViewerPage.getGroupComponentIndex1().clear();
            forecastViewerPage.getGroupComponentIndex2().clear();
            forecastViewerPage.getGroupComponentIndex3().clear();
            forecastViewerPage.clickSubmitButton();
            assertTrue("Submit button was submitted but there is an error in the form",
                    isStoredWidgetContainerEqualsNew());
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
            checkHighlightOfSelectionElement(forecastViewerPage.getAggregation(), "SUM");
            checkHighlightOfSelectionElement(forecastViewerPage.getInterpolation(), "PREVIOUS");
            checkHighlightOfNumericElement(forecastViewerPage.getPeriodCount(), "2");
            checkHighlightOfNumericElement(forecastViewerPage.getThreshold(), "1");
            forecastViewerPage.getThreshold().sendKeys(Keys.BACK_SPACE);
            forecastViewerPage.getThreshold().sendKeys(Keys.DELETE);
            checkHighlightOfNumericElement(forecastViewerPage.getComponentCount(), "12");
            checkHighlightOfNumericElement(forecastViewerPage.getWindowLength(), "1");
            checkHighlightOfSelectionElement(forecastViewerPage.getAveragingFunction(), "MEDIAN");
            checkHighlightOfNumericElement(forecastViewerPage.getScoreIntervalCount(), "11");
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
            Map<String, String> params = prepareURLParams();
            String newURL = createNewURL(AtsdTest.url + "/series/forecast", params);
            driver.navigate().to(newURL);

            assertRegularizeOptionValues(params.get("aggregation"), params.get("interpolation"), "25",
                    "minute", "URL params test");
            assertDecomposeOptionValues(params.get("componentThreshold"), params.get("componentCount"),
                    params.get("windowLength"), "URL params test");
            assertForecastOptions("AVG", "1", "minute", "URL params test");
            assertStartDate("URLParams: Wrong Start Date", params.get("startDate"));
            assertEndDate("URLParams: Wrong End Date", params.get("endDate"));
            assertIntervalEquals("URLParams: Wrong horizon interval", params.get("horizonInterval"),
                    CommonSelects.getFormattedInterval(forecastViewerPage.getForecastHorizonCount(),
                            forecastViewerPage.getForecastHorizonUnit()));
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
    public void testPresenceOfForecastsUntilSubmit() {
        try {
            forecastViewerPage.waitUntilSummaryTableIsLoaded(20);
            int forecastCountInChart = forecastViewerPage.getCountOfForecastsInWidgetContainer();
            int forecastCountInSummary = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks().size();
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
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();

            assertCountOfForecasts(5);
            forecastViewerPage.removeForecastTab();
            forecastViewerPage.removeForecastTab();
            forecastViewerPage.removeForecastTab();
            forecastViewerPage.removeForecastTab();
            assertCountOfForecasts(1);
            assertInvisibility("Remove button is broken:", forecastViewerPage.isForecastRemoveButtonPresent(),
                    forecastViewerPage.isForecastRemoveButtonVisible());
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
            assertInvisibility("Remove button is broken:", forecastViewerPage.isForecastRemoveButtonPresent(),
                    forecastViewerPage.isForecastRemoveButtonVisible());
            assertCountOfForecasts(1);
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            assertCountOfForecasts(5);
            assertInvisibility("Add button is broken:", forecastViewerPage.isForecastAddButtonPresent(),
                    forecastViewerPage.isForecastAddButtonVisible());
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
            forecastViewerPage.addForecastTab();
            assertCountOfForecasts(2);
            assertVisibility("Remove button is broken:", forecastViewerPage.isForecastRemoveButtonPresent(),
                    forecastViewerPage.isForecastRemoveButtonVisible());
            assertVisibility("Add button is broken:", forecastViewerPage.isForecastAddButtonPresent(),
                    forecastViewerPage.isForecastAddButtonVisible());
            forecastViewerPage.addForecastTab();
            forecastViewerPage.removeForecastTab();
            assertCountOfForecasts(2);
            forecastViewerPage.removeForecastTab();
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
            forecastViewerPage.setPeriodUnit("week");
            assertEquals("Wrong period unit", "week",
                    forecastViewerPage.getPeriodUnit().getAttribute("value"));
            forecastViewerPage.setForecastHorizonUnit("week");
            assertEquals("Wrong horizon unit", "week",
                    forecastViewerPage.getForecastHorizonUnit().getAttribute("value"));
            forecastViewerPage.setScoreIntervalUnit("year");
            assertEquals("Wrong score interval unit", "year",
                    forecastViewerPage.getScoreIntervalUnit().getAttribute("value"));
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
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setGroupCount("11");
            forecastViewerPage.setComponentCount("19");
            forecastViewerPage.submitFormAndWait(20);

            List<WebElement> forecastSingularValues = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks();
            WebElement componentContainer = forecastViewerPage.getComponentContainer();

            for (WebElement forecastSingularValue : forecastSingularValues) {
                forecastSingularValue.click();
                assertNameOfForecastInComponentsWindowAndNameInSummary(forecastSingularValue, componentContainer);
                assertCountOfGreenComponents(forecastSingularValues.indexOf(forecastSingularValue));
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
            forecastViewerPage.setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute");
            forecastViewerPage.addForecastTab();
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
            forecastViewerPage.setDecomposeOptions("10", "12", "44");
            forecastViewerPage.addForecastTab();
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
            forecastViewerPage.setForecastOptions("MEDIAN", "10", "year");
            forecastViewerPage.addForecastTab();
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
            forecastViewerPage.setGroupOff();
            forecastViewerPage.addForecastTab();
            assertTrue("Wrong grouping mode", forecastViewerPage.getGroupOff());
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
            forecastViewerPage.setGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D");
            forecastViewerPage.setGroupParameterV("0.9");
            forecastViewerPage.setGroupParameterC("0.8");
            forecastViewerPage.addForecastTab();
            assertTrue("Wrong grouping mode", forecastViewerPage.getGroupAuto());
            assertGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D", "cloning");
            assertValueAttributeOfElement("Wrong v parameter after cloning", "0.9",
                    forecastViewerPage.getGroupParameterV());
            assertValueAttributeOfElement("Wrong c parameter after cloning", "0.8",
                    forecastViewerPage.getGroupParameterC());
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
            forecastViewerPage.setGroupManualOptions("2-3", "", "2-4");
            forecastViewerPage.addForecastTab();
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
            forecastViewerPage.setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute");
            forecastViewerPage.addForecastTab();

            String[] names = forecastViewerPage.getForecastTabNames();
            assertNotEquals("Forecast names in tabs are equals but they shouldn't be", names[0], names[1]);

            forecastViewerPage.setRegularizeOptions("SUM", "LINEAR", "10", "hour");
            forecastViewerPage.switchForecastTab("Forecast 1");
            assertRegularizeOptionValues("PERCENTILE_99", "PREVIOUS", "20", "minute", "switching");
            forecastViewerPage.switchForecastTab("Forecast 2");
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
            forecastViewerPage.setDecomposeOptions("10", "12", "44");
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setDecomposeOptions("20", "15", "2");
            forecastViewerPage.switchForecastTab("Forecast 1");
            assertDecomposeOptionValues("10", "12", "44", "switching");
            forecastViewerPage.switchForecastTab("Forecast 2");
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
            forecastViewerPage.setForecastOptions("MEDIAN", "10", "year");
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setForecastOptions("AVG", "11", "minute");
            forecastViewerPage.switchForecastTab("Forecast 1");
            assertForecastOptions("MEDIAN", "10", "year", "switching");
            forecastViewerPage.switchForecastTab("Forecast 2");
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
            forecastViewerPage.setGroupOff();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D");
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setGroupManualOptions("2-3", "", "2-4");
            forecastViewerPage.switchForecastTab("Forecast 1");
            assertTrue("Wrong grouping mode", forecastViewerPage.getGroupOff());
            forecastViewerPage.switchForecastTab("Forecast 2");
            assertTrue("Wrong grouping mode", forecastViewerPage.getGroupAuto());
            assertGroupAutoOptions("10", "NOVOSIBIRSK", "A", "", "B-C;D", "switching");
            forecastViewerPage.switchForecastTab("Forecast 3");
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

    @Test
    public void testCountOfGroups() {
        try {
            String countOfGroups = "11";
            forecastViewerPage.setGroupAuto();
            forecastViewerPage.setGroupCount(countOfGroups);
            forecastViewerPage.setComponentCount("19");
            forecastViewerPage.submitFormAndWait(20);
            int countInPic = forecastViewerPage.getCountOfForecastsInWidgetContainer();
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
    public void testPresenceOfForecastsInSummary() {
        try {
            forecastViewerPage.addForecastTab();
            forecastViewerPage.submitFormAndWait(20);
            int forecastCountInSummary = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks().size();
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
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setPeriodCount("20");
            forecastViewerPage.submitFormAndWait(20);

            int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
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
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setAggregation("SUM");
            forecastViewerPage.submitFormAndWait(20);
            int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
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
            forecastViewerPage.addForecastTab();
            forecastViewerPage.setInterpolation("PREVIOUS");
            forecastViewerPage.submitFormAndWait(20);

            int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
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
            forecastViewerPage.addForecastTab();
            forecastViewerPage.addForecastTab();
            forecastViewerPage.switchForecastTab("Forecast 2");
            forecastViewerPage.removeForecastTab();
            forecastViewerPage.submitFormAndWait(25);

            String[] names = forecastViewerPage.getForecastTabNames();
            List<String> forecastNames = forecastViewerPage.getSummaryContainerForecastNames();
            forecastNames.remove(0);
            for (int i = 0; i < forecastNames.size(); i++) {
                assertTrue("Wrong name of forecast in summary table", forecastNames.get(i).contains(names[i]));
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
            WebElement threshold = forecastViewerPage.getThreshold();
            WebElement intervalScore = forecastViewerPage.getScoreIntervalCount();

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
            threshold.click();
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
            forecastViewerPage.setGroupAuto();
            CommonAssertions.assertValid("The Group Count is validated but it have not to", driver,
                    forecastViewerPage.getGroupCount());
            forecastViewerPage.getComponentCount().sendKeys("3");
            forecastViewerPage.getGroupCount().sendKeys("10");
            forecastViewerPage.clickSubmitButton();
            CommonAssertions.assertInvalid("The Group Count is not validated but it have to", driver,
                    forecastViewerPage.getGroupCount());
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
                    forecastViewerPage.getThreshold());
            forecastViewerPage.setThreshold("100");
            CommonAssertions.assertInvalid("The Component Threshold is not validated but it have to", driver,
                    forecastViewerPage.getThreshold());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    private boolean isStoredWidgetContainerEqualsNew() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById(\"widget-container\").__innerWidget__===self.widgetContainerForAtsdTest");
    }

    private void storeCurrentWidgetContainerInJS() {
        ((JavascriptExecutor) driver).executeScript(
                "self.widgetContainerForAtsdTest =  document.getElementById(\"widget-container\").__innerWidget__");
    }

    private void assertVisibility(String errorMessage, boolean isPresent, boolean isVisible) {
        assertTrue("there is no such element on the page", isPresent);
        assertTrue(errorMessage + "should be visible but it is not", isVisible);
    }

    private void assertInvisibility(String errorMessage, boolean isPresent, boolean isVisible) {
        if (isPresent) {
            assertFalse(errorMessage + "should be not visible but it is", isVisible);
        }
    }

    private void assertValueAttributeOfElement(String errorMessage, String correctValue, WebElement element) {
        assertEquals(errorMessage, correctValue, element.getAttribute("value"));
    }

    private void assertCountOfGreenComponents(int forecastNumber) {
        int countOfActiveComponents = forecastViewerPage.getCountOfActiveComponentsInSingularValueContainer(forecastNumber);
        int countOfGreenComponents = forecastViewerPage.getCountOfActiveComponentsInComponentContainer();
        assertEquals("Wrong count of green components", countOfActiveComponents, countOfGreenComponents);
    }

    private void assertNameOfForecastInComponentsWindowAndNameInSummary(WebElement forecast, WebElement componentContainer) {
        String name = forecastViewerPage.getNameOfForecastInSummaryTable(forecast);
        assertTrue("Wrong forecast name in components window", componentContainer.getText().contains(name));
    }

    private void assertCountOfForecasts(int count) {
        assertEquals("Wrong count of forecasts", count, forecastViewerPage.getForecastsTabs().size());
    }

    private void assertIntervalEquals(String errorMessage, String expectedInterval, String elementInterval) {
        assertEquals(errorMessage, expectedInterval, elementInterval);
    }

    private void assertEndDate(String errorMessage, String sendedDate) {
        String newDate = forecastViewerPage.getEndDate().getAttribute("value") + "T" +
                forecastViewerPage.getEndTime().getAttribute("value");
        assertEquals(errorMessage, getTranslatedDate(sendedDate).toString(), newDate);
    }

    private LocalDateTime getTranslatedDate(String oldDate) {
        LocalDateTime sended = LocalDateTime.parse(oldDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        sended = sended.plusHours(TIME_ZONE_HOURS);
        return sended;
    }

    private void assertStartDate(String errorMessage, String sendedDate) {
        String newDate = forecastViewerPage.getStartDate().getAttribute("value") + "T" +
                forecastViewerPage.getStartTime().getAttribute("value");
        assertEquals(errorMessage, getTranslatedDate(sendedDate).toString(), newDate);
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

    private void checkHighlightOfSelectionElement(WebElement element, String value) {
        assertLackOfHighlight(element);
        setSelectionOption(element, value);
        assertPresenceOfHighlight(element);
    }

    private void setSelectionOption(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    private void checkHighlightOfNumericElement(WebElement element, String value) {
        assertLackOfHighlight(element);
        setNumericOption(element, value);
        assertPresenceOfHighlight(element);
    }

    private void setNumericOption(WebElement element, String value) {
        element.clear();
        element.sendKeys(value);
    }

    private void assertPresenceOfHighlight(WebElement element) {
        assertTrue("Parameter with id: " + element.getAttribute("id") + "is not highlighted but it should be",
                element.getAttribute("class").contains("highlight"));
    }

    private void assertLackOfHighlight(WebElement element) {
        assertFalse("Parameter with id: " + element.getAttribute("id") + "is highlighted but it shouldn't",
                element.getAttribute("class").contains("highlight"));
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
                forecastViewerPage.getAggregation());
        assertValueAttributeOfElement("Wrong interpolation after " + testType, interpolationType,
                forecastViewerPage.getInterpolation());
        assertIntervalEquals("Wrong period in the regularize section",
                periodCount + "-" + periodUnit,
                CommonSelects.getFormattedInterval(forecastViewerPage.getPeriodCount(),
                        forecastViewerPage.getPeriodUnit()));
    }

    private void assertDecomposeOptionValues(String threshold, String componentCount, String windowLen, String testType) {
        assertValueAttributeOfElement("Wrong threshold after " + testType, threshold,
                forecastViewerPage.getThreshold());
        assertValueAttributeOfElement("Wrong component count after " + testType, componentCount,
                forecastViewerPage.getComponentCount());
        assertValueAttributeOfElement("Wrong window length after " + testType, windowLen,
                forecastViewerPage.getWindowLength());
    }

    private void assertForecastOptions(String average, String intervalCount, String intervalUnit, String testType) {
        assertValueAttributeOfElement("Wrong average name after " + testType, average,
                forecastViewerPage.getAveragingFunction());
        assertIntervalEquals("Wrong score interval in the Forecast section",
                intervalCount + "-" + intervalUnit,
                CommonSelects.getFormattedInterval(forecastViewerPage.getScoreIntervalCount(),
                        forecastViewerPage.getScoreIntervalUnit()));
    }

    private void assertGroupAutoOptions(String countOfGroups, String clustering, String union1,
                                        String union2, String union3, String testType) {
        assertTrue("Wrong grouping mode", forecastViewerPage.getGroupAuto());
        assertValueAttributeOfElement("Wrong count of groups after " + testType, countOfGroups,
                forecastViewerPage.getGroupCount());
        assertValueAttributeOfElement("Wrong clustering after " + testType, clustering,
                forecastViewerPage.getClustering());
        assertValueAttributeOfElement("Wrong union value after " + testType, union1,
                forecastViewerPage.getGroupUnion1());
        assertValueAttributeOfElement("Wrong union value after " + testType, union2,
                forecastViewerPage.getGroupUnion2());
        assertValueAttributeOfElement("Wrong union value after " + testType, union3,
                forecastViewerPage.getGroupUnion3());
    }

    private void assertGroupManualOptions(String group1, String group2, String group3, String testType) {
        assertTrue("Wrong grouping mode", forecastViewerPage.getGroupManual());
        assertValueAttributeOfElement("Wrong union value after " + testType, group1,
                forecastViewerPage.getGroupComponentIndex1());
        assertValueAttributeOfElement("Wrong union value after " + testType, group2,
                forecastViewerPage.getGroupComponentIndex2());
        assertValueAttributeOfElement("Wrong union value after " + testType, group3,
                forecastViewerPage.getGroupComponentIndex3());
    }

    private void setTimeZone() {
        String url = AtsdTest.url + "/api/v1/version";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        String authorizationParams = AtsdTest.login + ":" + AtsdTest.password;
        try {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encodeBase64(
                    authorizationParams.getBytes(StandardCharsets.ISO_8859_1))));
            HttpResponse response = client.execute(request);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            String resultJSON = result.toString();
            Matcher matcher = Pattern.compile("\"offsetMinutes\":(\\d+)").matcher(resultJSON);
            if (matcher.find()) {
                TIME_ZONE_HOURS = Long.parseLong(matcher.group(1)) / 60;
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get time zone", e);
        }
    }

    private void switchToWindowTab(int numberOfTab) {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(numberOfTab));
    }

    private void loadDataAndParserByNames(String parserName, String dataName) {
        String pathToCSVFile = CSVImportParserAsSeriesTest.class.getResource(dataName + ".csv").getFile();
        csvDataUploaderService.uploadWithParser(pathToCSVFile, parserName);
    }

    private Map<String, String> prepareURLParams() {
        Map<String, String> params = new HashMap<>();
        params.put("entity", "nurswgvml007");
        params.put("metric", "forecastpagetest");
        params.put("startDate", "2019-03-17T08:11:22.000Z");
        params.put("endDate", "2019-03-18T08:11:22.000Z");
        params.put("horizonInterval", "1-day");
        params.put("period", "25-minute");
        params.put("scoreInterval", "1-MINUTE");
        params.put("componentThreshold", "10");
        params.put("windowLength", "45");
        params.put("componentCount", "100");
        params.put("aggregation", "PERCENTILE_75");
        params.put("interpolation", "PREVIOUS");
        params.put("tag_name1", "forecastPageTest");
        return params;
    }

}
