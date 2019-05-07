package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.stream.Collectors;

import static com.axibase.webtest.CommonActions.createNewURL;

public class MessagesPage {
    private static final String BASE_URL = "/messages";
    private WebDriver driver;


    private By type = By.id("type");
    private By source = By.id("source");
    private By entity = By.id("entity");
    private By severity = By.id("severity");
    private By messagesList = By.id("messagesList");
    private By search = By.xpath("//*/button[text()='Search']");
    private By tableHeader = By.className("panel__header");

    public MessagesPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL));
    }

    public void search() {
        driver.findElement(search).click();
    }

    public MessagesPage openFilterPanel() {
        if (driver.findElement(tableHeader).getAttribute("class").contains("collapsed")) {
            driver.findElement(tableHeader).click();
        }
        return this;
    }

    public int getCountOfMessages() {
        return driver.findElement(messagesList).findElements(By.xpath("./tbody/tr")).size();
    }

    public MessagesPage setEntity(String name) {
        CommonActions.setValueOption(name, driver.findElement(entity));
        return this;
    }

    public String[] getMessagesSeverity() {
        return driver.findElements(By.xpath("//td[contains(@id,'severity')]"))
                .stream()
                .map(element->element.getAttribute("data-value"))
                .toArray(String[]::new);
    }

    public MessagesPage setSeverity(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(severity));
        return this;
    }

    public MessagesPage setSource(String value) {
        CommonActions.setValueOption(value, driver.findElement(source));
        return this;
    }

    public MessagesPage setType(String value) {
        CommonActions.setSelectionOption(value, driver.findElement(type));
        return this;
    }

}
