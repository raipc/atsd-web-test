package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Collectors;

public class MessagesPage {
    private static final String BASE_URL = "/messages";
    private WebDriver driver;

    private By entity = By.id("entity");
    private By search = By.xpath("//*/button[text()='Search']");

    public MessagesPage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(url + BASE_URL);
    }

    public MessagesPage setEntity(String name) {
        CommonActions.setValueOption(name, driver.findElement(entity));
        return this;
    }

    public void search() {
        driver.findElement(search).click();
    }

    public int getCountOfMessages() {
        return driver.findElements(By.xpath("//*[@id='messagesList']/tbody/tr")).size();
    }

    public String getMessagesTime() {
        return driver.findElement(By.id("messagesList"))
                .findElements(By.cssSelector("tbody > tr > td:nth-child(2n)"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList())
                .toString();
    }

}
