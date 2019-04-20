package com.axibase.webtest.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sild on 02.02.15.
 */
public class LoginServiceTest extends AtsdTest {

    private final static String wrongLogin = "123";
    private final static String wrongPassword = "123";

    @Test
    public void testLogin() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(driver);
        assertTrue("Can't login on page", ls.login(login, password));
    }

    @Test
    public void wrongLogin() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(driver);
        assertFalse("Should return to login page with wrong login", ls.login(wrongLogin, password));
        assertFalse("Should return to login page with wrong password", ls.login(login, wrongPassword));
    }

    @Test
    public void testLogout() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(driver);
        assertTrue("Can't login on page", ls.login(login, password));
        assertTrue("Can't logout from page", ls.logout());
    }
}
