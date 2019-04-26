package com.axibase.webtest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

public class ElementUtils {

    /**
     * Execute JavaScript with element as a parameter
     *
     * @param element - element that will be used in script
     * @param script  - script to be executed
     * @param <T>     - type of returned expression
     * @return - returned expression from executed script
     */
    @SuppressWarnings("unchecked")
    public static <T> T executeWithElement(WebElement element, String script) {
        WebDriver driver = getConnectedDriver(element);
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalArgumentException("WebDriver of element can not execute script");
        }
        String iifeScript = "return (function (element) {" + script + ";})(arguments[0])";
        return (T) ((JavascriptExecutor) driver).executeScript(iifeScript, element);
    }

    /**
     * Get connected to element Web Driver
     *
     * @param element - element to get WebDriver
     * @return - connected to element WebDriver
     */
    public static WebDriver getConnectedDriver(WebElement element) {
        if (!(element instanceof WrapsDriver)) {
            throw new IllegalArgumentException("Element does not contain WebDriver");
        }
        return ((WrapsDriver) element).getWrappedDriver();
    }

}
