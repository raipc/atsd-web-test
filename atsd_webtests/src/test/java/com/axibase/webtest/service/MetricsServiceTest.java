package com.axibase.webtest.service;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sild on 02.02.15.
 */
public class MetricsServiceTest extends AtstTest {

    @Test
    public void testDefaultMetrics() {
        try {
            String uri = "/metrics?page=1&size=1000&filter=all&mask=&tag-name=&tag-value=";
            Assert.assertEquals(this.generateAssertMessage("Should get login page"), this.driver.getTitle(), LoginService.title);
            LoginService ls = new LoginService(AtstTest.driver);
            ls.login(AtstTest.login, AtstTest.password);
            AtstTest.driver.navigate().to(AtstTest.url + uri);
            Assert.assertEquals(generateAssertMessage("Title should be 'Metrics'"), AtstTest.driver.getTitle(), MetricsService.title);
            MetricsService ms = new MetricsService(AtstTest.driver);
            Assert.assertTrue(generateAssertMessage("Row count in metricList table should be more then 2"), ms.getMetricsCount() >= 3);
            Assert.assertNotNull(generateAssertMessage("Can't find metric 'jvm_memory_used_percent'"), ms.getMetricByName("jvm_memory_used_percent"));
            Assert.assertNotNull(generateAssertMessage("Can't find metric metric_writes_per_second'"), ms.getMetricByName("metric_writes_per_second"));
        } catch (AssertionError err) {
            String filepath = AtstTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            System.out.println(err.toString());
            Assert.fail();
        }
    }
}
