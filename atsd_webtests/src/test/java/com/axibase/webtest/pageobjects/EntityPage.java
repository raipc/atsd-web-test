package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.axibase.webtest.CommonActions.createNewURL;

public class EntityPage {
    private final String BASE_URL = "/entities/";
    private WebDriver driver;

    private By entityName = By.id("entityName");
    private By label = By.id("label");
    private By tagNames = By.className("tag-key");
    private By tagValues = By.className("tag-value");
    private By addTag = By.className("add-tag-button");

    private By enabledSwitch = By.id("enabled");
    private By interpolation = By.id("interpolate");
    private By timeZone = By.id("timeZone");

    public EntityPage(WebDriver driver, String url, String entityName) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL + entityName));
    }

    public EntityPage openSettingsPanel() {
        By settingsPanel = By.xpath("//*[contains(@data-target,'#settingsPanel')]");
        if (driver.findElement(settingsPanel).getAttribute("class").contains("collapsed")) {
            driver.findElement(settingsPanel).click();
        }
        return this;
    }

    public EntityPage addTag(String tagName, String tagValue) {
        driver.findElement(addTag).click();
        List<WebElement> tagNamesList = driver.findElements(tagNames);
        CommonActions.setValueOption(tagName, tagNamesList.get(tagNamesList.size() - 1));
        List<WebElement> tagValuesList = driver.findElements(tagValues);
        CommonActions.setValueOption(tagValue, tagValuesList.get(tagValuesList.size() - 1));
        return this;
    }

    public WebElement getEntityName() {
        return driver.findElement(entityName);
    }

    public WebElement getLabel() {
        return driver.findElement(label);
    }

    public EntityPage setLabel(String value) {
        CommonActions.setValueOption(value, driver.findElement(label));
        return this;
    }

    public String getTagNames() {
        return driver.findElements(tagNames).stream().
                map(element -> element.getAttribute("value")).
                collect(Collectors.toList()).toString();
    }

    public String getTagValues() {
        return driver.findElements(tagValues).stream().
                map(element -> element.getAttribute("value")).
                collect(Collectors.toList()).toString();
    }

    public WebElement getEnabledSwitch() {
        return driver.findElement(enabledSwitch);
    }

    public EntityPage toggleEnabledSwitch() {
        driver.findElement(enabledSwitch).click();
        return this;
    }

    public WebElement getInterpolation() {
        return driver.findElement(interpolation);
    }

    public EntityPage setInterpolation(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(interpolation));
        return this;
    }

    public WebElement getTimeZone() {
        return driver.findElement(timeZone);
    }

    public EntityPage setTimeZone(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(timeZone));
        return this;

    }

}
