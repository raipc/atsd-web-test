package com.axibase.webtest.service;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anna Striganova on 25.05.18
 */
public class ActionOnTestState extends TestWatcher {

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
            LoginService ls = new LoginService(AtsdTest.driver);
            ls.logout();
        }
    }

    @Override
    protected void starting(Description description) {
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{"--webdriver-loglevel=WARN"};
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
//
//        System.setProperty("webdriver.chrome.driver", "/opt/chromedriver/chromedriver");
//        dcap.setCapability("chrome.switches", Lists.newArrayList("--homepage=about:blank",
//                "--no-first-run"));
        if (AtsdTest.driver == null) { // driver = null for ExportServiceTest methods
            AtsdTest.driver = new PhantomJSDriver(dcap);
            AtsdTest.driver.manage().window().setSize(new Dimension(1280, 720));
//            driver = new ChromeDriver(dcap);
            AtsdTest.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            AtsdTest.driver.navigate().to(AtsdTest.url);
        }
    }

    public void takeScreenshot(String classAndMethod) {
        String filepath = AtsdTest.screenshotDir + "/"
                + classAndMethod + "_"
                + System.currentTimeMillis()
                + ".png";
        System.out.println(AtsdTest.driver);
        File scrFile = ((TakesScreenshot) AtsdTest.driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(filepath), true);
            System.out.println("Screenshot saved to '" + filepath + "'");
        } catch (IOException ex) {
            System.out.println("Can't save screenshot to '" + filepath + "'");
        }
    }
}