package com.axibase.webtest.service;


import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.util.Random;

/**
 * Created by sild on 02.02.15.
 */
public class ExportServiceTest extends AtsdTest {

    @Test
    public void testHttpCsv() {
        String uri = "/export?settings=%7B%22m%22%3A%22jvm_memory_used_percent%22%2C%22si%22%3A%225-MINUTE%22%2C%22t%22%3A%22HISTORY%22%2C%22f%22%3A%22CSV%22%2C%22np%22%3A-1%2C%22v%22%3Afalse%2C%22tf%22%3A%22LOCAL%22%2C%22ms%22%3Afalse%2C%22ro%22%3Afalse%2C%22te%22%3A%5B%5D%2C%22am%22%3Afalse%7D";
        AtsdTest.driver.quit();//will use htmlwebdriver instead of phantomjs for this test
        AtsdTest.driver = new HtmlUnitDriver();
        try {
            AtsdTest.driver.navigate().to(AtsdTest.url);
            Assert.assertEquals(this.generateAssertMessage("Should get login page"), driver.getTitle(), LoginService.title);
            LoginService ls = new LoginService(AtsdTest.driver);
            ls.login(AtsdTest.login, AtsdTest.password);
            AtsdTest.driver.navigate().to(AtsdTest.url + uri);
            String[] src = AtsdTest.driver.getPageSource().split("\n");
            String head = src[0];
            String body = src[1];
            Assert.assertEquals("Csv file (head) is incorrect", "Timestamp,Value,Metric,Entity,host", head.trim());
            Assert.assertTrue("Csv file (body) is incorrect", body.contains("jvm_memory_used_percent"));
        } finally {
            AtsdTest.driver.quit();
            AtsdTest.driver = null;
        }
    }

    @Test
    public void testCurlCsv() {
        try {
            Random rnd = new Random();
            String uri = "/export?settings=%7B%22m%22%3A%22jvm_memory_used_percent%22%2C%22si%22%3A%225-MINUTE%22%2C%22t%22%3A%22HISTORY%22%2C%22f%22%3A%22CSV%22%2C%22np%22%3A-1%2C%22v%22%3Afalse%2C%22tf%22%3A%22LOCAL%22%2C%22ms%22%3Afalse%2C%22ro%22%3Afalse%2C%22te%22%3A%5B%5D%2C%22am%22%3Afalse%7D";
            AtsdTest.driver.quit();
            AtsdTest.driver = null;
            try {
                String fpath = "/tmp/output" + rnd.nextInt(100) + ".csv";
                String command = "curl -u " + login + ":" + password + " -o " + fpath + " " + AtsdTest.url + uri;
                Process curlproc = Runtime.getRuntime().exec(command);
                curlproc.waitFor();
                File file = new File(fpath);
                String head = FileUtils.readLines(file).get(0);
                String body = FileUtils.readLines(file).get(1);
                Assert.assertTrue(generateAssertMessage("Title should be equal of 'Timestamp,Value,Metric,Entity,host'"), head.equals("Timestamp,Value,Metric,Entity,host"));
                Assert.assertTrue(generateAssertMessage("Body should contain 'jvm_memory_used_percent'"), body.contains("jvm_memory_used_percent"));
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.assertTrue(generateAssertMessage("Catch exception"), false);
            }
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            System.out.println(err.toString());
            Assert.fail();
        }

    }
}
