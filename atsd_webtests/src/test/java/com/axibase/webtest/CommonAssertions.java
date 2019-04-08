package com.axibase.webtest;

import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonAssertions {
    public static void assertValid(String errorMessage, List<WebElement> elements) {
        assertTrue(errorMessage, !elements.isEmpty());
    }

    public static void assertInvalid(String errorMessage, List<WebElement> elements) {
        assertFalse(errorMessage, !elements.isEmpty());
    }
}
