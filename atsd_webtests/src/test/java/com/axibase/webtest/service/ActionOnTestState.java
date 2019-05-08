package com.axibase.webtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

import static com.axibase.webtest.service.AccountService.CREATE_ACCOUNT_TITLE;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

@Slf4j
@RequiredArgsConstructor
public class ActionOnTestState extends TestWatcher {
    private final AtsdTest tests;

    @Override
    protected void failed(Throwable e, Description description) {
        String clazz = description.getTestClass().getSimpleName();
        String method = description.getMethodName();
        String classAndMethod = clazz + "_" + method;
        takeScreenshot(classAndMethod);
    }

    private void takeScreenshot(String classAndMethod) {
        String filepath = Config.getInstance().getScreenshotDir() + "/"
                + classAndMethod + "_"
                + System.currentTimeMillis()
                + ".png";
        File scrFile = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(filepath), true);
            log.info("Screenshot saved to '{};", filepath);
        } catch (IOException ex) {
            log.error("Can't save screenshot to '{}'", filepath, ex);
        }
    }

    @Override
    protected void finished(Description description) {
        new LoginService().logout();
    }

    @Override
    protected void starting(Description description) {
        if (!hasWebDriverStarted()) {
            open("/");
        }
        if (!(tests instanceof CreateAdminAccountTest) && CREATE_ACCOUNT_TITLE.equals(title())) {
            final AccountService accountService = new AccountService();
            if (!accountService.createAdmin()) {
                throw new IllegalStateException("Admin account failed to be created");
            }
        }
    }
}