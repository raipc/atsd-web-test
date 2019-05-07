package com.axibase.webtest.pageobjects;

import com.axibase.webtest.CommonActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.axibase.webtest.CommonActions.createNewURL;

public class MetricsTablePage implements Table {
    private final String BASE_URL = "/metrics";
    private WebDriver driver;

    private By searchQuery = By.id("searchQuery");

    public MetricsTablePage(WebDriver driver, String url) {
        this.driver = driver;
        driver.get(createNewURL(url + BASE_URL));
    }

    @Override
    public boolean isRecordPresent(String name) {
        String xpathToEntity = String.format("//*[@id='metricsList']//a[text()='%s']", name);
        return driver.findElements(By.xpath(xpathToEntity)).size() != 0;
    }

    @Override
    public void searchRecordByName(String name) {
        WebElement searchQueryElement = driver.findElement(searchQuery);
        CommonActions.setValueOption(name, searchQueryElement);
        searchQueryElement.submit();
    }

}
