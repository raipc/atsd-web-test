package com.axibase.webtest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class ForecastSettingsPage {
    private WebDriver driver;
    private By groupingType = By.id("groupingType");
    private By groupingTags = By.id("settings.requiredTagKeys");

    public ForecastSettingsPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getGroupingType() {
        return new Select(driver.findElement(groupingType)).getFirstSelectedOption().getText();
    }

    public String getGroupingTags() {
        return driver.findElement(groupingTags).getAttribute("value");
    }

}
