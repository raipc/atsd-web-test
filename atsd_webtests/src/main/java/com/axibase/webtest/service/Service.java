package com.axibase.webtest.service;


import org.openqa.selenium.WebDriver;

/**
 * Created by sild on 02.02.15.
 */
abstract public class Service {
    WebDriver driver;
    public Service(WebDriver driver) {
       this.driver = driver;
    }

    public Service() {
        System.out.println("Can't create some services without driver");
    }
}
