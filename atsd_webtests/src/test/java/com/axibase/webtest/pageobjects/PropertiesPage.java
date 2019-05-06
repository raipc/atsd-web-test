package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PropertiesPage {
    private final String BASE_URL = "/properties";
    private WebDriver driver;

    public PropertiesPage(WebDriver driver, String url, String entityName, String propertyType) {
        this.driver = driver;
        driver.get(url + "/entities/" + entityName + BASE_URL + "?type=" + propertyType);
    }

    public String getTagsAndKeys() {
        return driver.findElement(By.id("property-widget")).getText();
    }

}
