package com.axibase.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CommonActions {

    /**
     * Find CodeMirror editor window and send text to it
     *
     * @param driver - web driver
     * @param siblingElement - next to the CodeMirror element
     * @param text - text to send
     */
    public static void sendTextToCodeMirror(WebDriver driver, WebElement siblingElement, String text) {
        Actions builder = new Actions(driver);
        builder.sendKeys(siblingElement.findElement(By.xpath("./following-sibling::*[contains(@class,CodeMirror)]")),
                text).build().perform();
    }

}
