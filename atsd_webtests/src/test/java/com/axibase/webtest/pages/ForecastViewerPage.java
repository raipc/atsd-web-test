package com.axibase.webtest.pages;

import lombok.Builder;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ForecastViewerPage {
    private WebDriver driver;

    private By breadcrumb = By.className("breadcrumb");
    private By forecastTabsPanel = By.id("group-toggle-list");
    private By componentContainer = By.id("singular-values-container");
    private By summaryContainer = By.id("summary-container");

    private By forecastSettings = By.id("save-forecast-btn");
    private By portal = By.id("save-config-btn");
    private By downloadingPortal = By.id("get-config-btn");
    private By statistics = By.xpath("//*[@id='settings']//footer//*[@class='icon-sum']");

    private By submitButton = By.id("group-save-btn");
    private By removeButton = By.id("remove-group-btn");
    private By addButton = By.id("add-group-btn");

    private By aggregation = By.id("aggregation");
    private By interpolation = By.id("interpolation");
    private By periodCount = By.id("period-count");
    private By periodUnit = By.id("period-unit");

    private By threshold = By.id("decompose-threshold");
    private By componentCount = By.id("decompose-limit");
    private By windowLength = By.id("decompose-length");

    private By groupOff = By.id("grouping-type-none");
    private By groupAuto = By.id("grouping-type-auto");
    private By groupCount = By.id("grouping-auto-count");
    private By clustering = By.id("grouping-auto-clustering-method");
    private By stack = By.xpath("//*[@id='grouping-auto-stack']/parent::div");
    private By groupUnion1 = By.id("grouping-auto-union-0");
    private By groupUnion2 = By.id("grouping-auto-union-1");
    private By groupUnion3 = By.id("grouping-auto-union-2");
    private By groupParameterV = By.id("grouping-auto-clustering-v");
    private By groupParameterC = By.id("grouping-auto-clustering-c");
    private By groupManual = By.id("grouping-type-manual");
    private By groupComponentIndex1 = By.id("grouping-manual-groups-0");
    private By groupComponentIndex2 = By.id("grouping-manual-groups-1");
    private By groupComponentIndex3 = By.id("grouping-manual-groups-2");

    private By averagingFunction = By.id("reconstruct-avg");
    private By scoreIntervalCount = By.id("forecast-score-interval-count");
    private By scoreIntervalUnit = By.id("forecast-score-interval-unit");

    private By startDate = By.id("startdate");
    private By startTime = By.id("starttime");
    private By endDate = By.id("enddate");
    private By endTime = By.id("endtime");
    private By forecastHorizonCount = By.id("horizon-count");
    private By forecastHorizonUnit = By.id("horizon-unit");

    private By groupingByURL = By.cssSelector("#settings > .controls");

    public ForecastViewerPage(WebDriver driver) {
        this.driver = driver;
    }

    public void scheduleForecast() {
        driver.findElement(forecastSettings).click();
    }

    public void savePortal() {
        driver.findElement(portal).click();
    }

    public WebElement getBreadcrumbElement(int elementsNumber) {
        return driver.findElement(breadcrumb).findElements(By.tagName("li")).get(elementsNumber);
    }

    public void submitFormAndWait(int countOfSeconds) {
        driver.findElement(submitButton).click();
        waitUntilSummaryTableIsLoaded(countOfSeconds);
    }

    public ForecastViewerPage clickSubmitButton() {
        driver.findElement(submitButton).click();
        return this;
    }

    public void waitUntilSummaryTableIsLoaded(int countOfSeconds) {
        Wait<WebDriver> wait = new WebDriverWait(driver, countOfSeconds).
                withMessage("Chart's loading time (" + countOfSeconds + " seconds) is over");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='summary-container']/table")));
        } catch (TimeoutException e) {
            Assert.fail("Chart's loading time (" + countOfSeconds + " seconds) is over");
        }
    }

    public boolean isWidgetContainerLoading() {
        WebDriverWait wait = new WebDriverWait(driver, 1);
        boolean isLoading = false;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='widget-container']/" +
                            "*[@class='axi-tooltip axi-tooltip-info']/" +
                            "*[@class='axi-tooltip-inner']/" +
                            "*[text()='Loaded']")));
        } catch (TimeoutException e) {
            isLoading = true;
        }
        return isLoading;
    }

    public ForecastViewerPage removeForecastTab() {
        driver.findElement(removeButton).click();
        return this;
    }

    public ForecastViewerPage addForecastTab() {
        driver.findElement(addButton).click();
        return this;
    }

    public ForecastViewerPage switchForecastTab(String nameOfForecast) {
        driver.findElement(forecastTabsPanel).findElement(By.linkText(nameOfForecast)).click();
        return this;
    }

    public ForecastViewerPage switchStack() {
        driver.findElement(stack).click();
        return this;
    }

    public void waitUntilTooltipIsShown(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
        Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(500, MILLISECONDS);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("tooltip")));
        } catch (TimeoutException e) {
            Assert.fail("Tooltip's time is over");
        }
        //that is for tooltip does not cover the button after tooltip is shown
        action.moveToElement(getSubmitButton()).perform();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("tooltip")));
    }

    public ForecastViewerPage setDecomposeOptions(String threshold, String componentCount, String windowLength) {
        setThreshold(threshold);
        setComponentCount(componentCount);
        setWindowLength(windowLength);
        return this;
    }

    public ForecastViewerPage setRegularizeOptions(String aggregation, String interpolation, String periodCount, String periodUnit) {
        setAggregation(aggregation);
        setInterpolation(interpolation);
        setPeriodCount(periodCount);
        setPeriodUnit(periodUnit);
        return this;
    }

    public ForecastViewerPage setGroupAutoOptions(String countOfGroups, String clustering, String union1, String union2, String union3) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setGroupCount(countOfGroups);
        setClustering(clustering);
        switchStack();
        setGroupUnion1(union1);
        setGroupUnion2(union2);
        setGroupUnion3(union3);
        return this;
    }

    public ForecastViewerPage setGroupManualOptions(String group1, String group2, String group3) {
        driver.findElement(groupManual).click();
        setGroupComponentIndex1(group1);
        setGroupComponentIndex2(group2);
        setGroupComponentIndex3(group3);
        return this;
    }

    public ForecastViewerPage setForecastOptions(String average, String scoreIntervalCount, String scoreIntervalUnit) {
        setAveragingFunction(average);
        setScoreIntervalCount(scoreIntervalCount);
        setScoreIntervalUnit(scoreIntervalUnit);
        return this;
    }

    public String getGroupedByURLTags() {
        return driver.findElement(groupingByURL).findElement(By.tagName("ul")).getText();
    }

    public String getGroupedByURLText() {
        return driver.findElement(groupingByURL).getText();

    }

    public int getCountOfForecastsInWidgetContainer() {
        return driver.findElements(By.cssSelector("#widget-container rect.axi-legend-button")).size();
    }

    public int getCountOfHistoryChartsInWidgetContainer() {
        return driver.findElements(By.cssSelector("#widget-container circle.axi-legend-button")).size();
    }

    public int getCountOfActiveComponentsInComponentContainer() {
        return driver.findElement(componentContainer).findElements(By.xpath("//*[@fill='green' and not(@class)]")).size();
    }

    public int getCountOfPassiveComponentsInComponentContainer() {
        return driver.findElement(componentContainer).findElements(By.xpath("//*[@fill='silver' and not(@class)]")).size();
    }

    public String getNameOfForecastInSummaryTable(WebElement forecast) {
        return forecast.findElement(By.xpath("../..")).getText().replace("\n(√λ)", "");
    }

    public String[] getForecastTabNames() {
        return driver.findElement(forecastTabsPanel).getText().split("\n");
    }

    public WebElement getComponentContainer() {
        return driver.findElement(componentContainer);
    }

    public List<WebElement> getSummaryContainerForecastsSingularValueLinks() {
        return driver.findElement(summaryContainer).findElements(By.xpath("//a[text()='(√λ)']"));
    }

    public List<String> getSummaryContainerForecastNames() {
        return driver.findElement(summaryContainer).findElements(By.xpath("//thead//th")).stream().
                map(WebElement::getText).collect(Collectors.toList());
    }

    public List<WebElement> getForecastsTabs() {
        return driver.findElement(forecastTabsPanel).findElements(By.tagName("li"));
    }

    public boolean isForecastAddButtonPresent() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0]!=null",
                driver.findElement(addButton));
    }

    public boolean isForecastAddButtonVisible() {
        return driver.findElement(addButton).isDisplayed();
    }

    public boolean isForecastRemoveButtonPresent() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0]!=null",
                driver.findElement(removeButton));
    }

    public boolean isForecastRemoveButtonVisible() {
        return driver.findElement(removeButton).isDisplayed();
    }

    public int getCountOfActiveComponentsInSingularValueContainer(int forecastNumber) {
        int count = 0;
        int maxCountOfActiveComponents = 20;
        String componentsAsString = driver.findElement(By.xpath(String.
                format("//*[@id='summary-container']//tbody/tr[2]/td[%d]", forecastNumber + 1))).getText();
        for (String str : componentsAsString.split(";")) {
            String[] components = str.split("-");
            if (Integer.parseInt(components[0]) > maxCountOfActiveComponents) break;
            if (components.length > 1) {
                if (Integer.parseInt(components[1]) > maxCountOfActiveComponents) break;
                count += (Integer.parseInt(components[1]) - Integer.parseInt(components[0]) + 1);
            } else {
                count++;
            }
        }
        return count;
    }

    public WebElement getDownloadingPortal() {
        return driver.findElement(downloadingPortal);
    }

    public WebElement getStatistics() {
        return driver.findElement(statistics);
    }

    public WebElement getForecastSettings() {
        return driver.findElement(forecastSettings);
    }

    public WebElement getPortal() {
        return driver.findElement(portal);
    }

    public WebElement getStackElement() {
        return driver.findElement(By.id("grouping-auto-stack"));
    }

    public WebElement getGroupAutoElement() {
        return driver.findElement(groupAuto);
    }

    public WebElement getGroupManualElement() {
        return driver.findElement(groupManual);
    }

    public WebElement getGroupParameterV() {
        return driver.findElement(groupParameterV);
    }

    public ForecastViewerPage setGroupParameterV(String value) {
        setNumericOption(value, driver.findElement(groupParameterV));
        return this;
    }

    public WebElement getGroupParameterC() {
        return driver.findElement(groupParameterC);
    }

    public ForecastViewerPage setGroupParameterC(String value) {
        setNumericOption(value, driver.findElement(groupParameterC));
        return this;
    }

    public WebElement getSubmitButton() {
        return driver.findElement(submitButton);
    }

    public WebElement getAggregation() {
        return driver.findElement(aggregation);
    }

    public ForecastViewerPage setAggregation(String value) {
        setSelectionOption(value, driver.findElement(aggregation));
        return this;
    }

    public WebElement getInterpolation() {
        return driver.findElement(interpolation);
    }

    public ForecastViewerPage setInterpolation(String value) {
        setSelectionOption(value, driver.findElement(interpolation));
        return this;
    }

    public WebElement getPeriodCount() {
        return driver.findElement(periodCount);
    }

    public ForecastViewerPage setPeriodCount(String value) {
        setNumericOption(value, driver.findElement(periodCount));
        return this;
    }

    public WebElement getPeriodUnit() {
        return driver.findElement(periodUnit);
    }

    public ForecastViewerPage setPeriodUnit(String value) {
        WebElement intervalInput = driver.findElement(periodUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
        return this;
    }

    public WebElement getThreshold() {
        return driver.findElement(threshold);
    }

    public ForecastViewerPage setThreshold(String value) {
        setNumericOption(value, driver.findElement(threshold));
        return this;
    }

    public WebElement getComponentCount() {
        return driver.findElement(componentCount);
    }

    public ForecastViewerPage setComponentCount(String value) {
        setNumericOption(value, driver.findElement(componentCount));
        return this;
    }

    public WebElement getWindowLength() {
        return driver.findElement(windowLength);
    }

    public ForecastViewerPage setWindowLength(String value) {
        setNumericOption(value, driver.findElement(windowLength));
        return this;
    }

    public boolean getGroupOff() {
        return driver.findElement(groupOff).isSelected();
    }

    public ForecastViewerPage setGroupOff() {
        driver.findElement(groupOff).click();
        return this;
    }

    public boolean getGroupAuto() {
        return driver.findElement(groupAuto).isSelected();
    }

    public ForecastViewerPage setGroupAuto() {
        driver.findElement(groupAuto).click();
        return this;
    }

    public WebElement getGroupCount() {
        return driver.findElement(groupCount);
    }

    public ForecastViewerPage setGroupCount(String value) {
        setNumericOption(value, driver.findElement(groupCount));
        return this;
    }

    public WebElement getClustering() {
        return driver.findElement(clustering);
    }

    public ForecastViewerPage setClustering(String value) {
        setSelectionOption(value, driver.findElement(clustering));
        return this;
    }

    public WebElement getGroupUnion1() {
        return driver.findElement(groupUnion1);
    }

    public ForecastViewerPage setGroupUnion1(String value) {
        setNumericOption(value, driver.findElement(groupUnion1));
        return this;
    }

    public WebElement getGroupUnion2() {
        return driver.findElement(groupUnion2);
    }

    public ForecastViewerPage setGroupUnion2(String value) {
        setNumericOption(value, driver.findElement(groupUnion2));
        return this;
    }

    public WebElement getGroupUnion3() {
        return driver.findElement(groupUnion3);
    }

    public ForecastViewerPage setGroupUnion3(String value) {
        setNumericOption(value, driver.findElement(groupUnion3));
        return this;
    }

    public boolean getGroupManual() {
        return driver.findElement(groupManual).isSelected();
    }

    public ForecastViewerPage setGroupManual() {
        driver.findElement(groupManual).click();
        return this;
    }

    public WebElement getGroupComponentIndex1() {
        return driver.findElement(groupComponentIndex1);
    }

    public ForecastViewerPage setGroupComponentIndex1(String value) {
        setNumericOption(value, driver.findElement(groupComponentIndex1));
        return this;
    }

    public WebElement getGroupComponentIndex2() {
        return driver.findElement(groupComponentIndex2);
    }

    public ForecastViewerPage setGroupComponentIndex2(String value) {
        setNumericOption(value, driver.findElement(groupComponentIndex2));
        return this;
    }

    public WebElement getGroupComponentIndex3() {
        return driver.findElement(groupComponentIndex3);
    }

    public ForecastViewerPage setGroupComponentIndex3(String value) {
        setNumericOption(value, driver.findElement(groupComponentIndex3));
        return this;
    }

    public WebElement getAveragingFunction() {
        return driver.findElement(averagingFunction);
    }

    public ForecastViewerPage setAveragingFunction(String value) {
        setSelectionOption(value, driver.findElement(averagingFunction));
        return this;
    }

    public WebElement getScoreIntervalCount() {
        return driver.findElement(scoreIntervalCount);
    }

    public ForecastViewerPage setScoreIntervalCount(String value) {
        setNumericOption(value, driver.findElement(scoreIntervalCount));
        return this;
    }

    public WebElement getScoreIntervalUnit() {
        return driver.findElement(scoreIntervalUnit);
    }

    public ForecastViewerPage setScoreIntervalUnit(String value) {
        WebElement intervalInput = driver.findElement(scoreIntervalUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
        return this;
    }

    public WebElement getStartDate() {
        return driver.findElement(startDate);
    }

    public ForecastViewerPage setStartDate(String value) {
        setNumericOption(value, driver.findElement(startDate));
        return this;
    }

    public WebElement getStartTime() {
        return driver.findElement(startTime);
    }

    public ForecastViewerPage setStartTime(String value) {
        setNumericOption(value, driver.findElement(startTime));
        return this;
    }

    public WebElement getEndDate() {
        return driver.findElement(endDate);
    }

    public ForecastViewerPage setEndDate(String value) {
        setNumericOption(value, driver.findElement(endDate));
        return this;
    }

    public WebElement getEndTime() {
        return driver.findElement(endTime);
    }

    public ForecastViewerPage setEndTime(String value) {
        setNumericOption(value, driver.findElement(endTime));
        return this;
    }

    public WebElement getForecastHorizonCount() {
        return driver.findElement(forecastHorizonCount);
    }

    public ForecastViewerPage setForecastHorizonCount(String value) {
        setNumericOption(value, driver.findElement(forecastHorizonCount));
        return this;
    }

    public WebElement getForecastHorizonUnit() {
        return driver.findElement(forecastHorizonUnit);
    }

    public ForecastViewerPage setForecastHorizonUnit(String value) {
        WebElement intervalInput = driver.findElement(forecastHorizonUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
        return this;
    }

    public ForecastViewerPage setNumericOption(String value, WebElement element) {
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public ForecastViewerPage setSelectionOption(String value, WebElement element) {
        Select select = new Select(element);
        select.selectByValue(value);
        return this;
    }

}
