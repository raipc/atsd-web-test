package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 02.02.15.
 */
public class SystemInfoService extends Service {
    public static final String title = "System Information";
    public SystemInfoService(WebDriver driver) {
        super(driver);
    }


    public String getSystemInfoValue(String key) {
        return driver.findElement(By.xpath("//*[@id='serverProperties']/descendant::tr[td//text()='" + key + "']/td[2]")).getText();
    }
}
