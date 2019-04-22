package com.axibase.webtest.service;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sild on 02.02.15.
 */
public class AtsdTest {

    protected static WebDriver driver;
    protected static final String propertypath = "atsd.properties";
    protected static String login;
    protected static String password;
    protected static String url;
    protected static String screenshotDir;

    @Rule
    public final ActionOnTestState action = new ActionOnTestState();

    @BeforeClass
    public static void readConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(propertypath)));
            url = properties.getProperty("url");
            login = properties.getProperty("login");
            password = properties.getProperty("password");
            screenshotDir = properties.getProperty("screenshot_directory");
            if (url == null || login == null || password == null || screenshotDir == null) {
                System.out.println("Can't read required properties");
                System.exit(1);
            }

            String phantomjsBinary = properties.getProperty("phantomjs.binary.path");
            if (phantomjsBinary != null) {
                System.setProperty("phantomjs.binary.path", phantomjsBinary);
            }
        } catch (IOException e) {
            System.out.println("Can't read property file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void cleanup() {
        if (driver != null) {
            driver.close();
        }
    }

    protected String generateAssertMessage(String thread) {
        String message = "";
        String currentUrl = "driver is null, can't get last url";
        String pageSrc = "driver is null, can't get page source";
        if (null != driver) { // driver = null for ExportServiceTest methods
            currentUrl = driver.getCurrentUrl();
            pageSrc = driver.getPageSource();
        }
        message += thread + "\n" +
                "url: " + currentUrl + "\n" +
                "page source:\n" + pageSrc;
        return message;
    }

    protected void login() {
        if (driver.getTitle().equals(LoginService.title)) {
            LoginService ls = new LoginService(driver);
            if (ls.login(login, password)) {
                driver.navigate().to(url);
            } else {
                throw new BadLoginException("Can not login");
            }
        } else {
            throw new BadLoginException("Expected login page title is '" + LoginService.title + "' but there is '" + driver.getTitle());
        }
    }

    public static class BadLoginException extends RuntimeException {
        public BadLoginException(String message) {
            super(message);
        }
    }
}
