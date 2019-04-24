package com.axibase.webtest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class CommonAssertions {

    /**
     * Check that element is valid
     *
     * @param errorMessage - message that will be shown if element is invalid
     * @param driver       - web driver
     * @param element      - element that will be checked
     */
    public static void assertValid(String errorMessage, WebDriver driver, WebElement element) {
        assertTrue(errorMessage, (Boolean) ((JavascriptExecutor) driver).
                executeScript("return arguments[0].checkValidity()", element));
    }

    /**
     * Check that element is invalid
     *
     * @param errorMessage - message that will be shown if element is valid
     * @param driver       - web driver
     * @param element      - element that will be checked
     */
    public static void assertInvalid(String errorMessage, WebDriver driver, WebElement element) {
        assertFalse(errorMessage, (Boolean) ((JavascriptExecutor) driver).
                executeScript("return arguments[0].checkValidity()", element));
    }

    /**
     * Compare element's value and expected value
     *
     * @param errorMessage - message that will be shown if element is valid
     * @param correctValue - expected value
     * @param -            element that will be checked
     */
    public static void assertValueAttributeOfElement(String errorMessage, String correctValue, WebElement element) {
        assertEquals(errorMessage, correctValue, element.getAttribute("value"));
    }

}
