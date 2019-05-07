package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Collectors;

import static com.axibase.webtest.CommonActions.createNewURL;

public class MetricIDsPage implements IDsPage {
    private static final String BASE_URL = "/admin/metrics/uids";
    private WebDriver driver;

    public MetricIDsPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL));
    }

    @Override
    public String getValuesInTable() {
        return driver.findElement(By.id("buildInfo"))
                .findElements(By.cssSelector("tbody > tr > td:nth-child(1n)"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList())
                .toString();
    }

}
