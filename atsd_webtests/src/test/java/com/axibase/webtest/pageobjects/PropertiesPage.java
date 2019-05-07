package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.axibase.webtest.CommonActions.createNewURL;

public class PropertiesPage {
    private final String BASE_URL = "/properties";
    private WebDriver driver;

    public PropertiesPage(WebDriver driver, String url, String entityName, String[] paramKeys, String[] paramValues) {
        this.driver = driver;
        driver.get(createNewURL(url + "/entities/" + entityName + BASE_URL, paramKeys, paramValues));
    }

    public String getTagsAndKeys() {
        return driver.findElement(By.id("property-widget")).getText();
    }

}
