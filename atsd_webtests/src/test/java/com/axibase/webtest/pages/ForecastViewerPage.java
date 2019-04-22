package com.axibase.webtest.pages;

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

    public void gotoForecastSettings() {
        driver.findElement(forecastSettings).click();
    }

    public void gotoPortalPage() {
        driver.findElement(portal).click();
    }

    public void gotoBreadcrumpLink(String linkName) {
        driver.findElement(breadcrumb).findElement(By.linkText(linkName)).click();
    }

    public String getBreadcrumpTags() {
        return driver.findElement(breadcrumb).findElement(By.partialLinkText("tag")).getText();
    }

    public void submitFormAndWait(int countOfSeconds) {
        driver.findElement(submitButton).click();
        waitUntilSummaryTableIsLoaded(countOfSeconds);
    }

    public void submitForm() {
        driver.findElement(submitButton).click();
    }

    public void waitUntilSummaryTableIsLoaded(int countOfSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, countOfSeconds);
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
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector("#widget-container > .axi-tooltip.axi-tooltip-info > .axi-tooltip-inner"),
                    "Loaded"));
        } catch (TimeoutException e) {
            isLoading = true;
        }
        return isLoading;
    }

    public void removeForecastTab() {
        driver.findElement(removeButton).click();
    }

    public void addForecastTab() {
        driver.findElement(addButton).click();
    }

    public void switchForecastTab(String nameOfForecast) {
        driver.findElement(forecastTabsPanel).findElement(By.partialLinkText(nameOfForecast)).click();
    }

    public void switchStack() {
        driver.findElement(stack).click();
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

    public String getPeriodInterval() {
        return getPeriodCount().getAttribute("value") + "-" + getPeriodUnit().getAttribute("value");
    }

    public String getScoreInterval() {
        return getScoreIntervalCount().getAttribute("value") + "-" + getScoreIntervalUnit().getAttribute("value");
    }

    public String getForecastHorizonInterval() {
        return getForecastHorizonCount().getAttribute("value") + "-" + getForecastHorizonUnit().getAttribute("value");
    }

    public void setDecomposeOptions(String threshold, String componentCount, String windowLength) {
        setThreshold(threshold);
        setComponentCount(componentCount);
        setWindowLength(windowLength);
    }

    public void setRegularizeOptions(String aggregation, String interpolation, String periodCount, String periodUnit) {
        setAggregation(aggregation);
        setInterpolation(interpolation);
        setPeriodCount(periodCount);
        setPeriodUnit(periodUnit);
    }

    public void setGroupAutoOptions(String countOfGroups, String clustering, String union1, String union2, String union3) {
        driver.findElement(By.id("grouping-type-auto")).click();
        setGroupCount(countOfGroups);
        setClustering(clustering);
        switchStack();
        setGroupUnion1(union1);
        setGroupUnion2(union2);
        setGroupUnion3(union3);
    }

    public void setGroupManualOptions(String group1, String group2, String group3) {
        driver.findElement(groupManual).click();
        setGroupComponentIndex1(group1);
        setGroupComponentIndex2(group2);
        setGroupComponentIndex3(group3);
    }

    public void setForecastOptions(String average, String scoreIntervalCount, String scoreIntervalUnit) {
        setAveragingFunction(average);
        setScoreIntervalCount(scoreIntervalCount);
        setScoreIntervalUnit(scoreIntervalUnit);
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
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].offsetParent!=null",
                driver.findElement(addButton));
    }

    public boolean isForecastRemoveButtonPresent() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0]!=null",
                driver.findElement(removeButton));
    }

    public boolean isForecastRemoveButtonVisible() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].offsetParent!=null",
                driver.findElement(removeButton));
    }

    public int getCountOfActiveComponentsOfForecast(int forecastNumber) {
        int count = 0;
        String componentsAsString = driver.findElement(By.xpath(String.
                format("//*[@id='summary-container']//tbody/tr[2]/td[%d]", forecastNumber + 1))).getText();
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

    public void setGroupParameterV(String value) {
        driver.findElement(groupParameterV).clear();
        driver.findElement(groupParameterV).sendKeys(value);
    }

    public WebElement getGroupParameterC() {
        return driver.findElement(groupParameterC);
    }

    public void setGroupParameterC(String value) {
        driver.findElement(groupParameterC).clear();
        driver.findElement(groupParameterC).sendKeys(value);
    }

    public WebElement getSubmitButton() {
        return driver.findElement(submitButton);
    }

    public WebElement getAggregation() {
        return driver.findElement(aggregation);
    }

    public void setAggregation(String value) {
        Select select = new Select(driver.findElement(aggregation));
        select.selectByValue(value);
    }

    public WebElement getInterpolation() {
        return driver.findElement(interpolation);
    }

    public void setInterpolation(String value) {
        Select select = new Select(driver.findElement(interpolation));
        select.selectByValue(value);
    }

    public WebElement getPeriodCount() {
        return driver.findElement(periodCount);
    }

    public void setPeriodCount(String value) {
        driver.findElement(periodCount).clear();
        driver.findElement(periodCount).sendKeys(value);
    }

    public WebElement getPeriodUnit() {
        return driver.findElement(periodUnit);
    }

    public void setPeriodUnit(String value) {
        WebElement intervalInput = driver.findElement(periodUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
    }

    public WebElement getThreshold() {
        return driver.findElement(threshold);
    }

    public void setThreshold(String value) {
        driver.findElement(threshold).clear();
        driver.findElement(threshold).sendKeys(value);
    }

    public WebElement getComponentCount() {
        return driver.findElement(componentCount);
    }

    public void setComponentCount(String value) {
        driver.findElement(componentCount).clear();
        driver.findElement(componentCount).sendKeys(value);
    }

    public WebElement getWindowLength() {
        return driver.findElement(windowLength);
    }

    public void setWindowLength(String value) {
        driver.findElement(windowLength).clear();
        driver.findElement(windowLength).sendKeys(value);
    }

    public boolean getGroupOff() {
        return driver.findElement(groupOff).isSelected();
    }

    public void setGroupOff() {
        driver.findElement(groupOff).click();
    }

    public boolean getGroupAuto() {
        return driver.findElement(groupAuto).isSelected();
    }

    public void setGroupAuto() {
        driver.findElement(groupAuto).click();
    }

    public WebElement getGroupCount() {
        return driver.findElement(groupCount);
    }

    public void setGroupCount(String value) {
        driver.findElement(groupCount);
        driver.findElement(groupCount).sendKeys(value);
    }

    public WebElement getClustering() {
        return driver.findElement(clustering);
    }

    public void setClustering(String value) {
        Select select = new Select(driver.findElement(clustering));
        select.selectByValue(value);
    }


    public WebElement getGroupUnion1() {
        return driver.findElement(groupUnion1);
    }

    public void setGroupUnion1(String value) {
        driver.findElement(groupUnion1).clear();
        driver.findElement(groupUnion1).sendKeys(value);
    }

    public WebElement getGroupUnion2() {
        return driver.findElement(groupUnion2);
    }

    public void setGroupUnion2(String value) {
        driver.findElement(groupUnion2).clear();
        driver.findElement(groupUnion2).sendKeys(value);
    }

    public WebElement getGroupUnion3() {
        return driver.findElement(groupUnion3);
    }

    public void setGroupUnion3(String value) {
        driver.findElement(groupUnion3).clear();
        driver.findElement(groupUnion3).sendKeys(value);
    }

    public boolean getGroupManual() {
        return driver.findElement(groupManual).isSelected();
    }

    public void setGroupManual() {
        driver.findElement(groupManual).click();
    }

    public WebElement getGroupComponentIndex1() {
        return driver.findElement(groupComponentIndex1);
    }

    public void setGroupComponentIndex1(String value) {
        driver.findElement(groupComponentIndex1).clear();
        driver.findElement(groupComponentIndex1).sendKeys(value);
    }

    public WebElement getGroupComponentIndex2() {
        return driver.findElement(groupComponentIndex2);
    }

    public void setGroupComponentIndex2(String value) {
        driver.findElement(groupComponentIndex2).clear();
        driver.findElement(groupComponentIndex2).sendKeys(value);
    }

    public WebElement getGroupComponentIndex3() {
        return driver.findElement(groupComponentIndex3);
    }

    public void setGroupComponentIndex3(String value) {
        driver.findElement(groupComponentIndex3).clear();
        driver.findElement(groupComponentIndex3).sendKeys(value);
    }

    public WebElement getAveragingFunction() {
        return driver.findElement(averagingFunction);
    }

    public void setAveragingFunction(String value) {
        Select select = new Select(driver.findElement(averagingFunction));
        select.selectByValue(value);
    }

    public WebElement getScoreIntervalCount() {
        return driver.findElement(scoreIntervalCount);
    }

    public void setScoreIntervalCount(String value) {
        driver.findElement(scoreIntervalCount).clear();
        driver.findElement(scoreIntervalCount).sendKeys(value);
    }

    public WebElement getScoreIntervalUnit() {
        return driver.findElement(scoreIntervalUnit);
    }

    public void setScoreIntervalUnit(String value) {
        WebElement intervalInput = driver.findElement(scoreIntervalUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
    }

    public WebElement getStartDate() {
        return driver.findElement(startDate);
    }

    public void setStartDate(String value) {
        driver.findElement(startDate).clear();
        driver.findElement(startDate).sendKeys(value);
    }

    public WebElement getStartTime() {
        return driver.findElement(startTime);
    }

    public void setStartTime(String value) {
        driver.findElement(startTime).clear();
        driver.findElement(startTime).sendKeys(value);
    }

    public WebElement getEndDate() {
        return driver.findElement(endDate);
    }

    public void setEndDate(String value) {
        driver.findElement(endDate).clear();
        driver.findElement(endDate).sendKeys(value);
    }

    public WebElement getEndTime() {
        return driver.findElement(endTime);
    }

    public void setEndTime(String value) {
        driver.findElement(endTime).clear();
        driver.findElement(endTime).sendKeys(value);
    }

    public WebElement getForecastHorizonCount() {
        return driver.findElement(forecastHorizonCount);
    }

    public void setForecastHorizonCount(String value) {
        driver.findElement(forecastHorizonCount).clear();
        driver.findElement(forecastHorizonCount).sendKeys(value);
    }

    public WebElement getForecastHorizonUnit() {
        return driver.findElement(forecastHorizonUnit);
    }

    public void setForecastHorizonUnit(String value) {
        WebElement intervalInput = driver.findElement(forecastHorizonUnit).findElement(By.xpath(".."));
        intervalInput.findElement(By.tagName("button")).click();
        intervalInput.findElement(By.className("dropdown-menu")).findElement(By.partialLinkText(value)).click();
    }
}
