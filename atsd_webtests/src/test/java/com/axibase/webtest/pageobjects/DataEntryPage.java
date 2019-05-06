package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DataEntryPage {
    private final String BASE_URL = "/metrics/entry";
    private WebDriver driver;

    private By sendButton = By.cssSelector("button[value=send]");

    public DataEntryPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(url + BASE_URL);
    }

    public DataEntryPage typeCommands(String command) {
        CommonActions.sendTextToCodeMirror(driver.findElement(By.name("commands")), command);
        return this;
    }

    public DataEntryPage sendCommands() {
        driver.findElement(sendButton).click();
        return this;
    }

}
