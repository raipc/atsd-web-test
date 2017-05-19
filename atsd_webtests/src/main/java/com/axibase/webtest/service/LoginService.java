package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 30.01.15.
 */
public class LoginService extends Service {
    public static final String title = "Login";

    public LoginService(WebDriver driver) {
        super(driver);
    }

    public boolean login(String login, String password) {
        String field_atsd_user = "//*[@id='atsd_user']";
        String field_atsd_pwd = "//*[@id='atsd_pwd']";
        String btn_login = "//*[@name='commit']";
            driver.findElement(By.xpath(field_atsd_user)).sendKeys(login);
            driver.findElement(By.xpath(field_atsd_pwd)).sendKeys(password);
            driver.findElement(By.xpath(btn_login)).click();
            if ( driver.getTitle().equals("Axibase Time Series Database")) {
                return true;
            } else {
                return false;
            }
    }

    public boolean logout() {
        String btn_logout = "//a[@title='Logout']";
        if(driver.findElements(By.xpath(btn_logout)).size() == 1) {
            driver.findElement(By.xpath(btn_logout)).click();
            return true;
        } else {
            return false;
        }
    }
}
