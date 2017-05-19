package com.axibase.webtest.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 02.02.15.
 */
public class AdminService extends Service {
    public AdminService(WebDriver driver) {
        super(driver);
    }

    public String getTime() {
        return driver.findElement(By.xpath("//*[@id=\"serverTime\"]/tbody/tr[1]/td[2]")).getText();
    }
}
