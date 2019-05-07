package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.axibase.webtest.CommonActions.createNewURL;

public class StatisticsPage {
    private final String BASE_URL = "/series/statistics";
    private WebDriver driver;

    public StatisticsPage(WebDriver driver, String url, String[] paramKeys, String[] paramValues) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL, paramKeys, paramValues));
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
