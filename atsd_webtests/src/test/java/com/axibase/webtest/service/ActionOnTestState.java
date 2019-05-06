package com.axibase.webtest.service;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ActionOnTestState extends TestWatcher {

    private AtsdTest testClassObject;

    public ActionOnTestState(AtsdTest testClassObject) {
        this.testClassObject = testClassObject;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        if (AtsdTest.driver != null) { // driver = null for ExportServiceTest methods
            String clazz = description.getTestClass().getSimpleName();
            String method = description.getMethodName();
            String classAndMethod = clazz + "_" + method;
            takeScreenshot(classAndMethod);
        }
    }

    @Override
    protected void finished(Description description) {
        if (AtsdTest.driver != null) { // driver = null for ExportServiceTest methods
            testClassObject.cleanup();
            LoginService ls = new LoginService(AtsdTest.driver);
            ls.logout();
        }
    }

    @Override
    protected void starting(Description description) {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless");
        opts.addArguments("--no-sandbox");
        opts.addArguments("--window-size=1280,720");
        if (AtsdTest.driver == null) { // driver = null for ExportServiceTest methods
            AtsdTest.driver = new ChromeDriver(opts);
            AtsdTest.driver.manage().window().setSize(new Dimension(1280, 720));
            AtsdTest.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            AtsdTest.driver.navigate().to(AtsdTest.url);
        }
    }

    private void takeScreenshot(String classAndMethod) {
        String filepath = AtsdTest.screenshotDir + "/"
                + classAndMethod + "_"
                + System.currentTimeMillis()
                + ".png";
        System.out.println(AtsdTest.driver);
        int height = AtsdTest.driver.findElement(By.xpath("/html/body")).getSize().getHeight();
        int width = AtsdTest.driver.findElement(By.xpath("/html/body")).getSize().getWidth();
        AtsdTest.driver.manage().window().setSize(new Dimension(width, height));
        File scrFile = ((TakesScreenshot) AtsdTest.driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(filepath), true);
            System.out.println("Screenshot saved to '" + filepath + "'");
        } catch (IOException ex) {
            System.out.println("Can't save screenshot to '" + filepath + "'");
        }
        AtsdTest.driver.manage().window().setSize(new Dimension(1280, 720));
    }

}