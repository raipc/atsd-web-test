package com.axibase.webtest.service;


import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sild on 02.02.15.
 */
public class AccountServiceTest extends AtsdTest {

    private AccountService as;

    @Before
    public void setUp() {
        as = new AccountService(driver);
    }

    @Test
    public void createUser() {
        try {
            if (!driver.getTitle().equals("Login")) {
                System.out.println("Trying to create admin...");
                assertEquals(generateAssertMessage("Should get page with title 'Create Account'"), AtsdTest.driver.getTitle(), "Create Account");
                assertTrue(generateAssertMessage("Can't create account"), as.createUser(AtsdTest.login, AtsdTest.password));
                URL url = new URL(AtsdTest.driver.getCurrentUrl());
                assertEquals(generateAssertMessage("Should get redirect on home page"), "/", url.getPath());
            } else {
                System.out.println("User already created");
            }
        } catch (MalformedURLException err) {
            throw new AssertionError("Bad URL returned from Webdriver", err);
        }
    }

    @Test
    public void deleteUser() {

        if (!driver.getTitle().equals("Login")) {
            System.out.println("Trying to create admin...");
            as.createUser(AtsdTest.login, AtsdTest.password);
        } else {
            login();
        }
        driver.navigate().to(AtsdTest.url + "/admin/users/edit.xhtml");
        assertEquals(generateAssertMessage("Should get page with title 'New User'"), "New User", AtsdTest.driver.getTitle());
        String testUser = "axiuser";
        assertTrue(generateAssertMessage("Can't create account"), as.createUser(testUser, testUser));
        assertEquals(generateAssertMessage("Should get page with title 'User " + testUser + "'"), "User " + testUser, AtsdTest.driver.getTitle());
        assertTrue(generateAssertMessage("Can't delete account '" + testUser + "'"), as.deleteUser(testUser));
        assertEquals(generateAssertMessage("Should get page with title 'Users'"), "Users", AtsdTest.driver.getTitle());

    }
}
