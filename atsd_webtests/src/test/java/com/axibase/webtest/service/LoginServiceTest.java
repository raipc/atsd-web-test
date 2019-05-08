package com.axibase.webtest.service;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.title;
import static org.junit.Assert.*;

/**
 * Created by sild on 02.02.15.
 */
public class LoginServiceTest extends AtsdTest {
    @Before
    @Override
    public void setUp() {
        // skip sign in
    }

    @Test
    public void testLogin() {
        assertEquals(generateAssertMessage("Title should be 'Login"), LoginService.TITLE, title());
        LoginService ls = new LoginService();
        assertTrue("Can't login on page", ls.loginAsAdmin());
    }

    @Test
    public void wrongLogin() {
        assertEquals(generateAssertMessage("Title should be 'Login"), LoginService.TITLE, title());
        final LoginService ls = new LoginService();
        final String wrongLogin = "123";
        final String wrongPassword = "123";
        final String rightLogin = Config.getInstance().getLogin();
        final String rightPassword = Config.getInstance().getPassword();
        assertFalse("Should return to login page with wrong login", ls.login(wrongLogin, rightPassword));
        assertFalse("Should return to login page with wrong password", ls.login(rightLogin, wrongPassword));
    }

    @Test
    public void testLogout() {
        assertEquals(generateAssertMessage("Title should be 'Login"), LoginService.TITLE, title());
        LoginService ls = new LoginService();
        assertTrue("Can't login on page", ls.loginAsAdmin());
        assertTrue("Can't logout from page", ls.logout());
    }
}
