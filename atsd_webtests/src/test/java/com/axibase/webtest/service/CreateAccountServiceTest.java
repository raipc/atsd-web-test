package com.axibase.webtest.service;


import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class CreateAccountServiceTest extends AtstTest {

    @Test
    public void createUser() {
        try {
            if(!AtstTest.driver.getTitle().equals("Login")) {
                System.out.println("try to create new user...");
                Assert.assertEquals(generateAssertMessage("Should get page with title 'Create Account'"), AtstTest.driver.getTitle(), "Create Account");
                CreateAccountService cas = new CreateAccountService(AtstTest.driver);
                Assert.assertTrue("Can't create account", cas.createUser(AtstTest.login, AtstTest.password));
                URL url = new URL(AtstTest.driver.getCurrentUrl());
                Assert.assertTrue(generateAssertMessage("Should get redirect on page with title 'Login'"), url.getPath().equals("/"));
            } else {
                System.out.println("User already created");
            }
        } catch (AssertionError err) {
            String filepath = AtstTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            System.out.println(err.toString());
            throw err;
        } catch (MalformedURLException err) {
            throw new RuntimeException("Bad URL returned from Webdriver", err);
        }
    }
}
