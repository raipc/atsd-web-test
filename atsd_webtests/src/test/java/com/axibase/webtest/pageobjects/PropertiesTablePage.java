package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Collectors;

import static com.axibase.webtest.CommonActions.createNewURL;

public class PropertiesTablePage {
    private final String BASE_URL = "/properties";
    private WebDriver driver;

    public PropertiesTablePage(WebDriver driver, String url, String entityName) {
        this.driver = driver;
        driver.get(createNewURL(url + "/entities/" + entityName + BASE_URL));
    }

    public boolean isPropertyPresent(String propertyName) {
        return driver.findElement(By.id("property-types-table"))
                .findElements(By.cssSelector("tbody > tr >td:nth-child(3n)"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList()).toString().contains(propertyName);
    }

}
