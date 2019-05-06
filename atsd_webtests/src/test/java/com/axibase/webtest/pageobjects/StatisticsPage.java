package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class StatisticsPage {
    private final String BASE_URL = "/series/statistics?";
    private WebDriver driver;

    public StatisticsPage(WebDriver driver, String url, String entityName, String metricName, String[] tagNames, String[] tagValues) {
        this.driver = driver;
        StringBuilder tags = new StringBuilder();
        for (int i = 0; i < tagNames.length; i++) {
            tags.append("&").append(tagNames[i]).append("=").append(tagValues[i]);
        }
        driver.get(url + BASE_URL + "entity=" + entityName + "&metric=" + metricName + tags.toString());
    }

    public String getSeriesTags() {
        return driver.findElement(By.xpath("//*/caption[contains(text(),'Series')]")).findElement(By.xpath("./..")).getText();
    }

    public void getSampleDataTab() {
        driver.findElement(By.xpath("//*/a[text()='Sample Data']")).click();
    }

    public String getSampleDataTableText() {
        return driver.findElement(By.xpath("//*[@id='sample-data']/table")).getText();
    }

}
