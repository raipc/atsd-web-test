package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class LoginServiceTest extends AtsdTest {
    private final static String wrongLogin = "123";
    private final static String wrongPassword = "123";
    @Test
    public void testLogin() {
        Assert.assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        Assert.assertTrue("Can't login on page", ls.login(AtsdTest.login, AtsdTest.password));
    }

    @Test
    public void wrongLogin() {
        Assert.assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        Assert.assertFalse("Should return to login page with wrong login", ls.login(wrongLogin, AtsdTest.password));
        Assert.assertFalse("Should return to login page with wrong password", ls.login(AtsdTest.login, wrongPassword));
    }

    @Test
    public void testLogout() {
        Assert.assertTrue(generateAssertMessage("Title should be 'Login"), AtsdTest.driver.getTitle().equals("Login"));
        LoginService ls = new LoginService(AtsdTest.driver);
        Assert.assertTrue("Can't login on page", ls.login(AtsdTest.login, AtsdTest.password));
        Assert.assertTrue("Can't logout from page", ls.logout());
    }
}
