package com.axibase.webtest.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.axibase.webtest.CommonActions.createNewURL;
import static com.axibase.webtest.CommonActions.sendTextToCodeMirror;

public class DataEntryPage {
    private final String BASE_URL = "/metrics/entry";
    private WebDriver driver;

    private By sendButton = By.cssSelector("button[value=send]");

    public DataEntryPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL));
    }

    public DataEntryPage typeCommands(String command) {
        sendTextToCodeMirror(driver.findElement(By.name("commands")), command);
        return this;
    }

    public DataEntryPage sendCommands() {
        driver.findElement(sendButton).click();
        return this;
    }

}
