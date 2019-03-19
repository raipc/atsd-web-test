package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by sild on 30.01.15.
 */
public class AccountService extends Service {
    public AccountService(WebDriver driver) {
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

    public boolean deleteUser(String login) {
        if (driver.getTitle().equals("User " + login)) {
            final WebElement deleteButton = driver.findElement(By.name("delete"));
            final List<WebElement> dropdownToggle = deleteButton.findElements(By.xpath("../../../button[@data-toggle='dropdown']"));
            if (dropdownToggle.size() > 0) {
                dropdownToggle.get(0).click();
            }
            deleteButton.click();
            WebElement yes;
            // Waiting for the confirmation window to be loaded
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                yes = driver.findElement(By.xpath("//button[normalize-space(text())='Yes']"));
            } while (yes.getText().isEmpty()); // Button "Yes" can be find but probably not initialized

            yes.click();
            return true;
        }
        return false;
    }
}
