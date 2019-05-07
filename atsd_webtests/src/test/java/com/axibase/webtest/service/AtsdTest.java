package com.axibase.webtest.service;

import org.junit.Rule;
import org.openqa.selenium.WebDriver;

public abstract class AtsdTest {
    @Deprecated
    protected static WebDriver driver;
    protected static String url = Config.getInstance().getUrl();

    @Rule
    public final ActionOnTestState action = new ActionOnTestState(this);

    protected String generateAssertMessage(String thread) {
        String message = "";
        String currentUrl = "driver is null, can't get last url";
        String pageSrc = "driver is null, can't get page source";
        if (null != driver) { // driver = null for ExportServiceTest methods
            currentUrl = driver.getCurrentUrl();
            pageSrc = driver.getPageSource();
        }
        message += thread + "\n" +
                "url: " + currentUrl + "\n";
//                "page source:\n" + pageSrc;
        return message;
    }

    protected void login() {
        if (driver.getTitle().equals(LoginService.title)) {
            LoginService ls = new LoginService(driver);
            if (ls.loginAsAdmin()) {
                driver.navigate().to(url);
            } else {
                throw new BadLoginException("Can not login");
            }
        } else {
            throw new BadLoginException("Expected login page title is '" + LoginService.title + "' but there is '" + driver.getTitle());
        }
    }
}
