package com.axibase.webtest.service;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by sild on 02.02.15.
 */
abstract public class AtstTest {
    protected static WebDriver driver = null;
    protected static final String propertypath = "atsd.properties";
    protected static String login;
    protected static String password;
    protected static String url;
    protected static String screenshotDir;

    @BeforeClass
    public static void readConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(propertypath)));
            url = properties.getProperty("url");
            login = properties.getProperty("login");
            password = properties.getProperty("password");
            screenshotDir = properties.getProperty("screenshot_directory");
            if (null == url || null == login || null == password || null == screenshotDir) {
                System.out.println("Can't read required properties");
                System.exit(1);
            }
        } catch (IOException e) {
            System.out.println("Can't read property file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void cleanup() {
        if(driver != null) {
            driver.close();
        }
    }



    @Before
    public void init() {
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new  String[] {"--webdriver-loglevel=WARN"};
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
//
//        System.setProperty("webdriver.chrome.driver", "/opt/chromedriver/chromedriver");
//        dcap.setCapability("chrome.switches", Lists.newArrayList("--homepage=about:blank",
//                "--no-first-run"));
        if(driver == null) {
            driver = new PhantomJSDriver(dcap);
//            driver = new ChromeDriver(dcap);
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            driver.navigate().to(url);
        }
    }

    @After
    public void reset() {
        if (null != driver) {
            LoginService ls = new LoginService(driver);
            ls.logout();
        }
    }

    protected String generateAssertMessage(String thread) {
        String message = "";
        String currentUrl = "driver is null, can't get last url";
        String pageSrc = "driver is null, can't get page source";
        if(null != driver) {
            currentUrl = driver.getCurrentUrl();
            pageSrc = driver.getPageSource();
        }
        message += thread + "\n" +
                "url: " + currentUrl + "\n" +
                "page source:\n" + pageSrc;
        return message;
    }

    protected void saveScreenshot(String filepath) {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(filepath), true);
            System.out.println("screenshot saved to '" + filepath + "'");
        } catch (IOException e) {
            System.out.println("Can't save screenshot to '" + filepath + "'");
        }

    }

}
