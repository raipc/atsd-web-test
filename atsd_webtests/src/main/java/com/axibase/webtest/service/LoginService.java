package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sild on 30.01.15.
 */
public class LoginService extends Service {
    public static final String title = "Login";
    private final String LOGOUT_URL;

    public LoginService(WebDriver driver) {
        super(driver);
        try {
            URL url = new URL(driver.getCurrentUrl());
            LOGOUT_URL = new URL(url.getProtocol(), url.getHost(), url.getPort(), "/logout").toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean loginAsAdmin() {
        final Config config = Config.getInstance();
        return login(config.getLogin(), config.getPassword());
    }

    public boolean login(String login, String password) {
        String field_atsd_user = "//*[@id='atsd_user']";
        String field_atsd_pwd = "//*[@id='atsd_pwd']";
        String btn_login = "//*[@name='commit']";
        driver.findElement(By.xpath(field_atsd_user)).sendKeys(login);
        driver.findElement(By.xpath(field_atsd_pwd)).sendKeys(password);
        driver.findElement(By.xpath(btn_login)).click();
        return driver.getTitle().equals("Axibase Time Series Database");
    }

    public boolean logout() {
        driver.get(LOGOUT_URL);
        try {
            return "/login".equals(new URL(driver.getCurrentUrl()).getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
