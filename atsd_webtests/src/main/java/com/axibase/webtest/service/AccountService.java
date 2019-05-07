package com.axibase.webtest.service;


import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sild on 30.01.15.
 */
public class AccountService extends Service {
    public static final String CREATE_ACCOUNT_TITLE = "Create Account";
    public AccountService(WebDriver driver) {
        super(driver);
    }

    public boolean createAdmin() {
        final Config config = Config.getInstance();
        return createUser(config.getLogin(), config.getPassword());
    }

    public boolean createUser(String login, String password) {
        driver.findElement(By.id("userBean.username")).clear();
        driver.findElement(By.id("userBean.username")).sendKeys(login);
        driver.findElement(By.id("userBean.password")).clear();
        driver.findElement(By.id("userBean.password")).sendKeys(password);
        driver.findElement(By.id("repeatPassword")).clear();
        driver.findElement(By.id("repeatPassword")).sendKeys(password);
        driver.findElement(By.xpath("//input[@type='submit']")).click();
        final List<WebElement> elements = driver.findElements(By.cssSelector(".field__error"));
        final String errors = elements.stream()
                .map(WebElement::getText)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
        if (StringUtils.isNotEmpty(errors)) {
            throw new IllegalStateException(errors);
        }
        return true;
    }

    // Flaky
    @SneakyThrows
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
                Thread.sleep(200);
                yes = driver.findElement(By.xpath("//button[normalize-space(text())='Yes']"));
            } while (yes.getText().isEmpty()); // Button "Yes" can be find but probably not initialized

            try {
                yes.click();
            } catch (Exception e) {
                Thread.sleep(200);
                yes = driver.findElement(By.xpath("//button[normalize-space(text())='Yes']"));
                yes.click();
            }
            return true;
        }
        return false;
    }
}
