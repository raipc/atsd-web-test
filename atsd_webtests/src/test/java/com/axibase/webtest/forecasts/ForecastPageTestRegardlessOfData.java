package com.axibase.webtest.forecasts;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.pages.ForecastViewerPage;
import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CommonSelects;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class ForecastPageTestRegardlessOfData extends AtsdTest {
    private ForecastViewerPage forecastViewerPage;
    private static final String START_PAGE = url+ "/series/forecast?entity=entity-for-regardless-of-data-test&" +
            "metric=metric-for-regardless-of-data-test&" +
            "startDate=2015-03-04T14:24:40.000Z";

    @Before
    public void setUp() {
        this.login();
        loadData();
        forecastViewerPage = new ForecastViewerPage(driver);
        driver.get(START_PAGE);
    }

    private void loadData() {
        driver.get(AtsdTest.url + "/metrics/entry");
        driver.findElement(By.xpath("//*/form[1]/div[1]/div/div[2]/div[6]")).click();
        driver.findElement(By.xpath("//*/form[1]/div[1]/div/div[2]/div[1]/textarea")).sendKeys(
                "<#list 1..5 as i>\n" +
                        "series s:${1425482080 - i * 600} e:entity-for-regardless-of-data-test m:metric-for-regardless-of-data-test=${60 - 2*i}\n" +
                        "</#list>");
        driver.findElement(By.cssSelector("button[value=send]")).click();
        ;
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
            CommonAssertions.assertValueAttributeOfElement("Wrong v parameter after cloning", "0.9",
                    forecastViewerPage.getGroupParameterV());
            CommonAssertions.assertValueAttributeOfElement("Wrong c parameter after cloning", "0.8",
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
    public void testSwitchTabsGroupsOptions() {
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

    private void checkHighlightOfSelectionElement(WebElement element, String value) {
        assertLackOfHighlight(element);
        forecastViewerPage.setSelectionOption(value, element);
        assertPresenceOfHighlight(element);
    }

    private void checkHighlightOfNumericElement(WebElement element, String value) {
        assertLackOfHighlight(element);
        forecastViewerPage.setNumericOption(value, element);
        assertPresenceOfHighlight(element);
    }

    private void assertPresenceOfHighlight(WebElement element) {
        assertTrue("Parameter with id: " + element.getAttribute("id") + "is not highlighted but it should be",
                element.getAttribute("class").contains("highlight"));
    }

    private void assertLackOfHighlight(WebElement element) {
        assertFalse("Parameter with id: " + element.getAttribute("id") + "is highlighted but it shouldn't",
                element.getAttribute("class").contains("highlight"));
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

    private void assertCountOfForecasts(int count) {
        assertEquals("Wrong count of forecasts", count, forecastViewerPage.getForecastsTabs().size());
    }

    private void assertGroupAutoOptions(String countOfGroups, String clustering, String union1,
                                        String union2, String union3, String testType) {
        assertTrue("Wrong grouping mode", forecastViewerPage.getGroupAuto());
        CommonAssertions.assertValueAttributeOfElement("Wrong count of groups after " + testType, countOfGroups,
                forecastViewerPage.getGroupCount());
        CommonAssertions.assertValueAttributeOfElement("Wrong clustering after " + testType, clustering,
                forecastViewerPage.getClustering());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, union1,
                forecastViewerPage.getGroupUnion1());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, union2,
                forecastViewerPage.getGroupUnion2());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, union3,
                forecastViewerPage.getGroupUnion3());
    }

    private void assertGroupManualOptions(String group1, String group2, String group3, String testType) {
        assertTrue("Wrong grouping mode", forecastViewerPage.getGroupManual());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, group1,
                forecastViewerPage.getGroupComponentIndex1());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, group2,
                forecastViewerPage.getGroupComponentIndex2());
        CommonAssertions.assertValueAttributeOfElement("Wrong union value after " + testType, group3,
                forecastViewerPage.getGroupComponentIndex3());
    }

}
