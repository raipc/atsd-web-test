package com.axibase.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonActions {

    /**
     * Find CodeMirror editor window and send text to it
     *
     * @param relatedTextArea - next to the CodeMirror element
     * @param text            - text to send
     */
    public static void sendTextToCodeMirror(WebElement relatedTextArea, String text) {
        if (!relatedTextArea.getTagName().equals("textarea")) {
            throw new IllegalStateException("his is not a textarea");
        }
        WebDriver driver = ElementUtils.getConnectedDriver(relatedTextArea);
        Actions builder = new Actions(driver);
        builder.sendKeys(relatedTextArea.findElement(By.xpath("./following-sibling::*[contains(@class,CodeMirror)]")),
                text).build().perform();
    }

    /**
     * Clear element's window and send into it value
     *
     * @param value   - value to be set
     * @param element - selected element
     */
    public static void setValueOption(String value, WebElement element) {
        element.clear();
        element.sendKeys(value);
    }

    /**
     * Set one of the element's selection option
     *
     * @param value   - value to be set
     * @param element - selected element
     */
    public static void setSelectionOption(String value, WebElement element) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    /**
     * Find checkbox by its value and select it
     *
     * @param driver - current web driver
     * @param value  - value of the checkbox
     */
    public static void clickCheckboxByValueAttribute(WebDriver driver, String value) {
        String xpath = String.format("//*/input[@type='checkbox' and @value='%s']", value);
        driver.findElement(By.xpath(xpath)).click();
    }

    /**
     * Delete elements wih selected checkboxes
     *
     * @param driver- current web driver
     */
    public static void dropCheckedRecords(WebDriver driver) {
        driver.findElement(By.xpath("//*/div[@class='table-controls']//*[@data-toggle='dropdown']")).click();
        driver.findElement(By.xpath("//*/div[@class='table-controls']//a[text()='Delete']")).click();
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("confirm-modal")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirm-modal']//button[text()='Yes']")));
        driver.findElement(By.xpath("//*[@id='confirm-modal']//button[text()='Yes']")).click();
    }

}
