package com.axibase.webtest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PortalPage {
    private WebDriver driver;
    private By contentWrapper = By.id("content-wrapper");

    public PortalPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getContentWrapperText() {
        return driver.findElement(contentWrapper).getText();
    }

}
