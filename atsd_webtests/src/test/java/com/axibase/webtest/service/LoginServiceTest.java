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
        assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        assertTrue("Can't login on page", ls.login(AtsdTest.login, AtsdTest.password));
    }

    @Test
    public void wrongLogin() {
        assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        assertFalse("Should return to login page with wrong login", ls.login(wrongLogin, AtsdTest.password));
        assertFalse("Should return to login page with wrong password", ls.login(AtsdTest.login, wrongPassword));
    }

    @Test
    public void testLogout() {
        assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        assertTrue("Can't login on page", ls.login(AtsdTest.login, AtsdTest.password));
        assertTrue("Can't logout from page", ls.logout());
    }
}
