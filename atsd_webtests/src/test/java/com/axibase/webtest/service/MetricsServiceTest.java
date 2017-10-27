package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class MetricsServiceTest extends AtsdTest {

    @Test
    public void testDefaultMetrics() {
        try {
            String uri = "/metrics?page=1&size=1000&filter=all&mask=&tag-name=&tag-value=";
            Assert.assertEquals(this.generateAssertMessage("Should get login page"), LoginService.title, this.driver.getTitle());
            LoginService ls = new LoginService(AtsdTest.driver);
            ls.login(AtsdTest.login, AtsdTest.password);
            AtsdTest.driver.navigate().to(AtsdTest.url + uri);
            Assert.assertEquals(generateAssertMessage("Title should be 'Metrics'"), MetricsService.title, AtsdTest.driver.getTitle());
            MetricsService ms = new MetricsService(AtsdTest.driver);
            Assert.assertTrue(generateAssertMessage("Row count in metricList table should be more then 2"), ms.getMetricsCount() >= 3);
            Assert.assertNotNull(generateAssertMessage("Can't find metric 'jvm_memory_used_percent'"), ms.getMetricByName("jvm_memory_used_percent"));
            Assert.assertNotNull(generateAssertMessage("Can't find metric metric_writes_per_second'"), ms.getMetricByName("metric_writes_per_second"));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }
}
