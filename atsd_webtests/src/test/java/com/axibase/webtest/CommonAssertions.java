package com.axibase.webtest;

import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class CommonAssertions {

    /**
     * Check that element passes HTML validation
     *
     * @param errorMessage - message that will be shown if element is invalid
     * @param element      - element that will be checked
     */
    public static void assertValid(String errorMessage, WebElement element) {
        String script = "return element.checkValidity()";
        Boolean result = ElementUtils.executeWithElement(element, script);
        assertTrue(errorMessage, result);
    }

    /**
     * Check that element doesn't passes HTML validation
     *
     * @param errorMessage - message that will be shown if element is valid
     * @param element      - element that will be checked
     */
    public static void assertInvalid(String errorMessage, WebElement element) {
        String script = "return element.checkValidity()";
        Boolean result = ElementUtils.executeWithElement(element, script);
        assertFalse(errorMessage, result);
    }

    /**
     * Compare element's value and expected value
     *
     * @param errorMessage  - message that will be shown if element is valid
     * @param expectedValue - expected value
     * @param -             element that will be checked
     */
    public static void assertValueAttributeOfElement(String errorMessage, String expectedValue, WebElement element) {
        assertEquals(errorMessage, expectedValue, element.getAttribute("value"));
    }

    /**
     * Compare current url with what should be
     *
     * @param expectedUrl - url that should be
     * @param currentUrl  - driver current url
     */
    public static void assertPageUrl(String expectedUrl, String currentUrl) {
        assertEquals("Wrong page", expectedUrl, currentUrl);
    }

}
