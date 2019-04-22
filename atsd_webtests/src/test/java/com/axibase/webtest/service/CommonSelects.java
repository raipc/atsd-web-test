package com.axibase.webtest.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommonSelects {

    public static WebElement getElementTooltipByFor(WebElement element) {
        String elementsTag = element.getTagName();
        if (!(elementsTag.equals("input") || elementsTag.equals("select") || elementsTag.equals("textarea"))) {
            throw new IllegalStateException("Wrong selector");
        }
        return element.findElement(By.xpath("ancestor::label|" +
                "//*/label[@for='" + element.getAttribute("id") + "']"));

    }

    public static String getFormattedInterval(WebElement countElement, WebElement unitElement) {
        return countElement.getAttribute("value") + "-" + unitElement.getAttribute("value");
    }

}
