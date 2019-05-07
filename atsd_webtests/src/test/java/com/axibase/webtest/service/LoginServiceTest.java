package com.axibase.webtest.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sild on 02.02.15.
 */
public class LoginServiceTest extends AtsdTest {

    @Test
    public void testLogin() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(driver);
        assertTrue("Can't login on page", ls.loginAsAdmin());
    }

    @Test
    public void wrongLogin() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        final LoginService ls = new LoginService(driver);
        final String wrongLogin = "123";
        final String wrongPassword = "123";
        final String rightLogin = Config.getInstance().getLogin();
        final String rightPassword = Config.getInstance().getPassword();
        assertFalse("Should return to login page with wrong login", ls.login(wrongLogin, rightPassword));
        assertFalse("Should return to login page with wrong password", ls.login(rightLogin, wrongPassword));
    }

    @Test
    public void testLogout() {
        assertTrue(generateAssertMessage("Title should be 'Login"), driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(driver);
        assertTrue("Can't login on page", ls.loginAsAdmin());
        assertTrue("Can't logout from page", ls.logout());
    }
}
