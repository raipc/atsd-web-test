package com.axibase.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CommonActions {

    /**
     * Find CodeMirror editor window and send text to it
     *
     * @param relatedTextArea - next to the CodeMirror element
     * @param text            - text to send
     */
    public static void sendTextToCodeMirror(WebElement relatedTextArea, String text) {
        if (!relatedTextArea.getTagName().equals("textarea")) {
            throw new IllegalStateException("this is not a textarea");
        }
        WebDriver driver = ElementUtils.getConnectedDriver(relatedTextArea);
        Actions builder = new Actions(driver);
        builder.sendKeys(relatedTextArea.findElement(By.xpath("./following-sibling::*[contains(@class,CodeMirror)]")),
                text).build().perform();
    }

}
