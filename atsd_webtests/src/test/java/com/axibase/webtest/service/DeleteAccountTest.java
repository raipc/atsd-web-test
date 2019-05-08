package com.axibase.webtest.service;

import io.qameta.allure.Flaky;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class DeleteAccountTest extends AtsdTest {
    @Test
    @Flaky
    public void deleteUser() {
        open("/admin/users/edit.xhtml");
        assertEquals(generateAssertMessage("Should get page with title 'New User'"), "New User", title());
        String testUser = "axiuser-" + System.currentTimeMillis();
        final AccountService accountService = new AccountService();
        assertTrue(generateAssertMessage("Can't create account"), accountService.createUser(testUser, testUser));
        assertEquals(generateAssertMessage("Should get page with title 'User " + testUser + "'"), "User " + testUser, title());
        assertTrue(generateAssertMessage("Can't delete account '" + testUser + "'"), accountService.deleteUser(testUser));
        assertEquals(generateAssertMessage("Should get page with title 'Users'"), "Users", title());

    }
}
