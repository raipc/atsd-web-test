package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MetricsSeriesTablePage {
    private WebDriver driver;

    private By metricList = By.id("metricList");

    public MetricsSeriesTablePage(WebDriver driver, String url, String metricName) {
        this.driver = driver;
        driver.get(url + "/metrics/" + metricName + "/series");
    }

    public boolean isSeriesPresent() {
        return driver.findElement(metricList).findElements(By.cssSelector("tbody > tr")).size() > 0;
    }

}
