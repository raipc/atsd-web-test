package com.axibase.webtest.service;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;

public class ScreenshotSaver {

    private WebDriver driver;

    public ScreenshotSaver(WebDriver driver) {
        this.driver = driver;
    }

    public void saveScreenshot(String filepath) {
        int height = driver.findElement(By.xpath("/html/body")).getSize().getHeight();
        int width = driver.findElement(By.xpath("/html/body")).getSize().getWidth();
        driver.manage().window().setSize(new Dimension(width, height));
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(filepath), true);
            System.out.println("screenshot saved to '" + filepath + "'");
        } catch (IOException e) {
            System.out.println("Can't save screenshot to '" + filepath + "'");
        }
        driver.manage().window().setSize(new Dimension(1280, 720));
    }
}
