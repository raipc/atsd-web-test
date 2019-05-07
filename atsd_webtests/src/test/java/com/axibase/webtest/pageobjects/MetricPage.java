package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.axibase.webtest.CommonActions.createNewURL;

public class MetricPage {
    private final String BASE_URL = "/metrics/metric.xhtml";
    private WebDriver driver;

    private By name = By.id("metric.name");
    private By label = By.id("metric.label");
    private By description = By.id("metric.description");
    private By units = By.id("metric.units");
    private By minValue = By.id("metric.minValue");
    private By maxValue = By.id("metric.maxValue");
    private By tagNames = By.className("tag-key");
    private By tagValues = By.className("tag-value");
    private By addTag = By.className("add-tag-button");

    private By enabledSwitch = By.id("metric.enabled");
    private By persistentSwitch = By.id("metric.persistent");
    private By persistentFilter = By.id("metric.filter");
    private By retentionIntervalDays = By.id("metric.retentionIntervalDays");
    private By seriesRetentionDays = By.id("metric.seriesRetentionDays");
    private By dataType = By.id("metric.dataType");
    private By timeZone = By.id("metric.timeZone");
    private By versioningSwitch = By.id("metric.versioning");
    private By invalidAction = By.id("metric.invalidValueAction");
    private By interpolation = By.id("metric.interpolate");

    public MetricPage(WebDriver driver, String url, String[] paramKeys, String[] paramValues) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL, paramKeys, paramValues));
    }

    public MetricPage openSettingsPanel() {
        By settingsPanel = By.xpath("//*[contains(@data-target,'#settingsPanel')]");
        if (driver.findElement(settingsPanel).getAttribute("class").contains("collapsed")) {
            driver.findElement(settingsPanel).click();
        }
        return this;
    }

    public MetricPage addTag(String tagName, String tagValue) {
        driver.findElement(addTag).click();
        List<WebElement> tagNamesList = driver.findElements(tagNames);
        CommonActions.setValueOption(tagName, tagNamesList.get(tagNamesList.size() - 1));
        List<WebElement> tagValuesList = driver.findElements(tagValues);
        CommonActions.setValueOption(tagValue, tagValuesList.get(tagValuesList.size() - 1));
        return this;
    }

    public WebElement getName() {
        return driver.findElement(name);
    }

    public WebElement getLabel() {
        return driver.findElement(label);
    }

    public MetricPage setLabel(String value) {
        CommonActions.setValueOption(value, driver.findElement(label));
        return this;
    }

    public WebElement getDescription() {
        return driver.findElement(description);
    }

    public MetricPage setDescription(String value) {
        CommonActions.setValueOption(value, driver.findElement(description));
        return this;
    }

    public WebElement getUnits() {
        return driver.findElement(units);
    }

    public MetricPage setUnits(String value) {
        CommonActions.setValueOption(value, driver.findElement(units));
        return this;
    }

    public WebElement getMinValue() {
        return driver.findElement(minValue);
    }

    public MetricPage setMinValue(String value) {
        CommonActions.setValueOption(value, driver.findElement(minValue));
        return this;
    }

    public WebElement getMaxValue() {
        return driver.findElement(maxValue);
    }

    public MetricPage setMaxValue(String value) {
        CommonActions.setValueOption(value, driver.findElement(maxValue));
        return this;
    }

    public String getTagNames() {
        return driver.findElements(tagNames).stream().
                map(element -> element.getAttribute("value")).
                collect(Collectors.joining(","));
    }

    public String getTagValues() {
        return driver.findElements(tagValues).stream().
                map(element -> element.getAttribute("value")).
                collect(Collectors.joining(","));
    }

    public WebElement getEnabledSwitch() {
        return driver.findElement(enabledSwitch);
    }

    public MetricPage toggleEnabledSwitch(String value) {
        driver.findElement(enabledSwitch).click();
        return this;
    }

    public WebElement getPersistentSwitch() {
        return driver.findElement(persistentSwitch);
    }

    public MetricPage setPersistentSwitch(String value) {
        driver.findElement(persistentSwitch).click();
        return this;
    }

    public WebElement getPersistentFilter() {
        return driver.findElement(persistentFilter);
    }

    public MetricPage setPersistentFilter(String value) {
        CommonActions.setValueOption(value, driver.findElement(persistentFilter));
        return this;
    }

    public WebElement getRetentionIntervalDays() {
        return driver.findElement(retentionIntervalDays);
    }

    public MetricPage setRetentionIntervalDays(String value) {
        CommonActions.setValueOption(value, driver.findElement(retentionIntervalDays));
        return this;
    }

    public WebElement getSeriesRetentionDays() {
        return driver.findElement(seriesRetentionDays);
    }

    public MetricPage setSeriesRetentionDays(String value) {
        CommonActions.setValueOption(value, driver.findElement(seriesRetentionDays));
        return this;
    }

    public WebElement getDataType() {
        return driver.findElement(dataType);
    }

    public MetricPage setDataType(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(dataType));
        return this;
    }

    public WebElement getTimeZone() {
        return driver.findElement(timeZone);
    }

    public MetricPage setTimeZone(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(timeZone));
        return this;
    }

    public WebElement getVersioningSwitch() {
        return driver.findElement(versioningSwitch);
    }

    public MetricPage setVersioningSwitch(String value) {
        driver.findElement(versioningSwitch).click();
        return this;
    }

    public WebElement getInvalidAction() {
        return driver.findElement(invalidAction);
    }

    public MetricPage setInvalidAction(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(invalidAction));
        return this;
    }

    public WebElement getInterpolation() {
        return driver.findElement(interpolation);
    }

    public MetricPage setInterpolation(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(interpolation));
        return this;
    }

}
