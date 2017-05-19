package com.axibase.webtest.service;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 30.01.15.
 */
public class EntitiesService extends Service {
    public static final String title = "Entities";

    public EntitiesService(WebDriver driver) {
        super(driver);
    }

    public int getEntitiesCount() {
        return driver.findElements(By.xpath("//*[@id='entitiesList']/tbody/tr")).size();
    }

    public String getEntityByName(String name) {
            return driver.findElement(By.xpath("//*[@id='entitiesList']/descendant::tr[td//text()='" + name + "']")).getText();//fix xpath, td should be first in tr
    }
}
