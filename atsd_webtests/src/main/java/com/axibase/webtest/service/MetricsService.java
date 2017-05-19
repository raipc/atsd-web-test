package com.axibase.webtest.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 30.01.15.
 */
public class MetricsService extends Service {
    public static final String title = "Metrics";
    public MetricsService(WebDriver driver) {
        super(driver);
    }
    public boolean elementExist(String xpath) {
        if (driver.findElements(By.xpath(xpath)).size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean metricsListExist() {
       return driver.findElements(By.id("metricsList")).size() == 1;
    }

    public int getMetricsCount() {
        return driver.findElements(By.xpath("//*[@id='metricsList']/tbody/tr")).size();
    }

    public String getMetricByName(String name) {
            return driver.findElement(By.xpath("//*[@id='metricsList']/descendant::tr[td//text()='" + name + "']")).getText();
    }


}
