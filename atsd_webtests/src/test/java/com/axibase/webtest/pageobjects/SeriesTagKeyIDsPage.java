package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Collectors;

public class SeriesTagKeyIDsPage implements IDsPage {
    private static final String BASE_URL = "/admin/tags/series_tag_key/uids";
    private WebDriver driver;

    public SeriesTagKeyIDsPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(url + BASE_URL);
    }

    @Override
    public String getValuesInTable() {
        return driver.findElement(By.id("buildInfo"))
                .findElements(By.cssSelector("tbody > tr > td:nth-child(2n)"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList())
                .toString();
    }

}
