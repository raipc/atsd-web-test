package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
            driver.findElement(By.name("delete")).click();
            WebElement yes;
            // Waiting for the confirmation window to be loaded
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                yes = driver.findElement(By.xpath("//button[normalize-space(text())='Yes']"));
            } while (yes.getText().isEmpty());

            yes.click();
            return true;
        }
        return false;
    }
}
