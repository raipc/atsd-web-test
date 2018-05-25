package com.axibase.webtest.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by sild on 02.02.15.
 */
public class MetricsServiceTest extends AtsdTest {

    @Test
    public void testDefaultMetrics() {
        String uri = "/metrics?page=1&size=1000&filter=all&mask=&tag-name=&tag-value=";
        assertEquals(generateAssertMessage("Should get login page"), LoginService.title, driver.getTitle());
        LoginService ls = new LoginService(driver);
        ls.login(AtsdTest.login, AtsdTest.password);
        driver.navigate().to(AtsdTest.url + uri);
        assertEquals(generateAssertMessage("Title should be 'Metrics'"), MetricsService.title, AtsdTest.driver.getTitle());
        MetricsService ms = new MetricsService(AtsdTest.driver);
        assertTrue(generateAssertMessage("Row count in metricList table should be more then 2"), ms.getMetricsCount() >= 3);
        assertNotNull(generateAssertMessage("Can't find metric 'jvm_memory_used_percent'"), ms.getMetricByName("jvm_memory_used_percent"));
        assertNotNull(generateAssertMessage("Can't find metric metric_writes_per_second'"), ms.getMetricByName("metric_writes_per_second"));

    }
}
