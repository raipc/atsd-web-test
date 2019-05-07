package com.axibase.webtest.service;


import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sild on 02.02.15.
 */
@Ignore("Enable when selenide is used")
public class ExportServiceTest extends AtsdTest {
    @Test
    public void testHttpCsv() {
        String uri = "/export?settings=%7B%22m%22%3A%22jvm_memory_used_percent%22%2C%22si%22%3A%225-MINUTE%22%2C%22t%22%3A%22HISTORY%22%2C%22f%22%3A%22CSV%22%2C%22np%22%3A-1%2C%22v%22%3Afalse%2C%22tf%22%3A%22LOCAL%22%2C%22ms%22%3Afalse%2C%22ro%22%3Afalse%2C%22te%22%3A%5B%5D%2C%22am%22%3Afalse%7D";
        driver.quit();//will use htmlwebdriver instead of phantomjs for this test
        driver = new HtmlUnitDriver();
        driver.navigate().to(url);
        assertEquals(generateAssertMessage("Should get login page"), LoginService.title, driver.getTitle());
        LoginService ls = new LoginService(driver);
        ls.loginAsAdmin();
        driver.navigate().to(url + uri);
        String[] src = driver.getPageSource().split("\n");
        String head = src[0];
        String body = src[1];
        assertTrue("Csv file is incorrect", head.trim().equals("Timestamp,Value,Metric,Entity,host") && body.contains("jvm_memory_used_percent"));
        driver.quit();
        driver = null;
    }

    @Test
    public void testCurlCsv() {
        Random rnd = new Random();
        String uri = "/export?settings=%7B%22m%22%3A%22jvm_memory_used_percent%22%2C%22si%22%3A%225-MINUTE%22%2C%22t%22%3A%22HISTORY%22%2C%22f%22%3A%22CSV%22%2C%22np%22%3A-1%2C%22v%22%3Afalse%2C%22tf%22%3A%22LOCAL%22%2C%22ms%22%3Afalse%2C%22ro%22%3Afalse%2C%22te%22%3A%5B%5D%2C%22am%22%3Afalse%7D";
        driver.quit();
        driver = null;
        final String fpath = "/tmp/output" + rnd.nextInt(100) + ".csv";
        final File file = new File(fpath);
        try {
            final Config config = Config.getInstance();
            final String command = "curl -u " + config.getLogin() + ":" + config.getPassword() + " -o " + fpath + " " + config.getUrl() + uri;
            final Process curlproc = Runtime.getRuntime().exec(command);
            curlproc.waitFor();
            final List<String> lines = FileUtils.readLines(file);
            final String head = lines.get(0);
            final String body = lines.get(1);
            assertEquals(generateAssertMessage("Title should be equal to 'Timestamp,Value,Metric,Entity,host'"), "Timestamp,Value,Metric,Entity,host", head);
            assertTrue(generateAssertMessage("Body should contain 'jvm_memory_used_percent'"), body.contains("jvm_memory_used_percent"));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new AssertionError("Can't check export due to " + e, e);
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }
}
