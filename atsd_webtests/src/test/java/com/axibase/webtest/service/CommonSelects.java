package com.axibase.webtest.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommonSelects {
    public static WebElement selectElementTooltip(WebElement element) {
        return element.findElement(By.xpath("ancestor::label|" +
                "//*/label[@for='aggregation']")) ;
    }
}
