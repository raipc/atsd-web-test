package com.axibase.webtest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonAssertions {

    public static void assertValid(String errorMessage, WebDriver driver, WebElement element) {
        assertTrue(errorMessage, (Boolean) ((JavascriptExecutor) driver).
                executeScript("return arguments[0].checkValidity()", element));
    }

    public static void assertInvalid(String errorMessage, WebDriver driver, WebElement element) {
        assertFalse(errorMessage, (Boolean) ((JavascriptExecutor) driver).
                executeScript("return arguments[0].checkValidity()", element));
    }
}
