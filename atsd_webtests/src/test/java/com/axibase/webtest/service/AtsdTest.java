package com.axibase.webtest.service;

import org.junit.Before;
import org.junit.Rule;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.WebDriverRunner.url;

public abstract class AtsdTest {
    static {
        Config.getInstance(); // initialize config
    }

    @Rule
    public final ActionOnTestState action = new ActionOnTestState(this);

    @Before
    public void setUp() {
        login();
    }

    protected String generateAssertMessage(String thread) {
        return thread + "\n" +
                "url: " + url() + "\n";
//                "page source:\n" + source();
    }

    protected void login() {
        open("/");
        if (LoginService.TITLE.equals(title())) {
            LoginService ls = new LoginService();
            if (!ls.loginAsAdmin()) {
                throw new BadLoginException("Can not login as admin");
            }
        } else {
            throw new BadLoginException("Expected login page title is '" + LoginService.TITLE + "' but there is '" + title());
        }
    }
}
