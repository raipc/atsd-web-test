package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.axibase.webtest.CommonActions.createNewURL;

public class EntitiesTablePage implements Table {
    private final String BASE_URL = "/entities";
    private WebDriver driver;

    private By searchQuery = By.id("searchQuery");

    public EntitiesTablePage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL));
    }

    @Override
    public boolean isRecordPresent(String name) {
        String xpathToEntity = String.format("//*[@id='entitiesList']//a[text()='%s']", name);
        WebDriverWait wait = new WebDriverWait(driver, 1);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToEntity)));
        } catch (TimeoutException exception) {
            driver.navigate().refresh();
        }
        return driver.findElements(By.xpath(xpathToEntity)).size() != 0;
    }

    @Override
    public void searchRecordByName(String name) {
        WebElement searchQueryElement = driver.findElement(searchQuery);
        CommonActions.setValueOption(name, searchQueryElement);
        searchQueryElement.submit();
    }

}
