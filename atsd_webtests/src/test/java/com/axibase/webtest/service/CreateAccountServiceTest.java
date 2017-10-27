package com.axibase.webtest.service;


import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class CreateAccountServiceTest extends AtsdTest {

    @Test
    public void createUser() {
        try {
            if(!AtsdTest.driver.getTitle().equals("Login")) {
                System.out.println("try to create new user...");
                Assert.assertEquals(generateAssertMessage("Should get page with title 'Create Account'"), AtsdTest.driver.getTitle(), "Create Account");
                CreateAccountService cas = new CreateAccountService(AtsdTest.driver);
                Assert.assertTrue(generateAssertMessage("Can't create account"), cas.createUser(AtsdTest.login, AtsdTest.password));
                URL url = new URL(AtsdTest.driver.getCurrentUrl());
                Assert.assertTrue(generateAssertMessage("Should get redirect on home page"), url.getPath().equals("/"));
            } else {
                System.out.println("User already created");
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        } catch (MalformedURLException err) {
            throw new AssertionError("Bad URL returned from Webdriver", err);
        }
    }
}
