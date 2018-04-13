package com.axibase.webtest.service;


import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class AccountServiceTest extends AtsdTest {

    private AccountService as;

    @Before
    public void setUp() {
        as = new AccountService(AtsdTest.driver);
    }

    @Test
    public void createUser() {
        try {
            if (!AtsdTest.driver.getTitle().equals("Login")) {
                System.out.println("Try to create admin...");
                Assert.assertEquals(generateAssertMessage("Should get page with title 'Create Account'"), AtsdTest.driver.getTitle(), "Create Account");
                Assert.assertTrue(generateAssertMessage("Can't create account"), as.createUser(AtsdTest.login, AtsdTest.password));
                URL url = new URL(AtsdTest.driver.getCurrentUrl());
                Assert.assertEquals(generateAssertMessage("Should get redirect on home page"), "/", url.getPath());
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

    @Test
    public void deleteUser() {
        try {
            if (!AtsdTest.driver.getTitle().equals("Login")) {
                System.out.println("Try to create admin...");
                as.createUser(AtsdTest.login, AtsdTest.password);
            } else {
                login();
            }
            AtsdTest.driver.navigate().to(AtsdTest.url + "/admin/users/edit.xhtml");
            Assert.assertEquals(generateAssertMessage("Should get page with title 'New User'"), "New User", AtsdTest.driver.getTitle());
            String testUser = "axiuser";
            Assert.assertTrue(generateAssertMessage("Can't create account"), as.createUser(testUser, testUser));
            Assert.assertEquals(generateAssertMessage("Should get page with title 'User " + testUser + "'"), "User " + testUser, AtsdTest.driver.getTitle());
            Assert.assertTrue(generateAssertMessage("Can't delete account '" + testUser + "'"), as.deleteUser(testUser));
            Assert.assertEquals(generateAssertMessage("Should get page with title 'Users'"), "Users", AtsdTest.driver.getTitle());
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }
}
