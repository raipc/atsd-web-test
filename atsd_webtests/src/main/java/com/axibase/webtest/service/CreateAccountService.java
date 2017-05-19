package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 30.01.15.
 */
public class CreateAccountService extends Service {
    public CreateAccountService(WebDriver driver) {
        super(driver);
    }

    public boolean createUser(String login, String password) {
            driver.findElement(By.id("userBean.username")).clear();
            driver.findElement(By.id("userBean.username")).sendKeys(login);
            driver.findElement(By.id("userBean.password")).clear();
            driver.findElement(By.id("userBean.password")).sendKeys(password);
            driver.findElement(By.id("repeatPassword")).clear();
            driver.findElement(By.id("repeatPassword")).sendKeys(password);
            driver.findElement(By.xpath("//input[@type='submit']")).click();
            return true;
    }
}
