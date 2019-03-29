package com.axibase.webtest.cases;

import com.axibase.webtest.service.AtsdTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataEntryCommandsTest extends AtsdTest {

    @Before
    public void setUp() {
        this.login();
        goToDataEntryPage();
    }

    private void goToDataEntryPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/li/a[text()='Data Entry']")).click();
        assertEquals("Wrong Page: this is not the Data Page", url + "/metrics/entry", driver.getCurrentUrl());
    }

    @Test
    public void testMessageAdd() {
        String entityName = "entity_message";
        try {
            String messageText = "\"Message text\"";
            String[] tagsNames = {"type", "source", "message_tag_name"};
            String[] tagValues = {"message_tag_type_value", "message_tag_source_value", "message_tag_tag_value"};
            String insertMessage = String.format("message e:%s t:%s=%s t:%s=%s t:%s=%s m:%s", entityName,
                    tagsNames[0], tagValues[0], tagsNames[1], tagValues[1], tagsNames[2], tagValues[2], messageText);

            sendCommand(insertMessage);

            checkEntityAdd(entityName);
            checkTagsAdd("Message tag Key is not added into Message Tag Key IDs: ",
                    "Message Tag Key IDs", tagsNames);
            checkTagsAdd("Message tag Value is not added into Message Tag Value IDs: ",
                    "Message Tag Value IDs", tagValues);
            checkMessageAddByEntityName(entityName);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        } finally {
            dropEntity(entityName);
        }
    }

    @Test
    public void testPropertyAdd() {
        String entityName = "entity_property";
        try {
            String propType = "property_type";
            String[] key_names = {"property_key_name1", "property_key_name2"};
            String[] key_values = {"property_key_value1", "property_key_value2"};
            String[] tag_names = {"property_tag_name1", "property_tag_name2"};
            String[] tag_values = {"property_tag_value1", "property_tag_value2"};

            String insertMessage = String.format("property e:%s t:%s k:%s=%s k:%s=%s v:%s=%s v:%s=%s",
                    entityName, propType, key_names[0], key_values[0], key_names[1], key_values[1],
                    tag_names[0], tag_values[0], tag_names[1], tag_values[1]);
            sendCommand(insertMessage);

            checkEntityAdd(entityName);
            checkPropsAdd(entityName, propType, key_names, key_values, tag_names, tag_values);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        } finally {
            dropEntity(entityName);
        }
    }

    @Test
    public void testSeriesAdd() {
        String entityName = "entity_series";
        String metricName = "metric_series";
        try {
            String metricText1 = "metric_text1";
            String metricText2 = "metric_text2";
            String textAppend = "true";
            String metricValue = "10";
            String[] tagNames = {"series_tag_name"};
            String[] tagValues = {"series_tag_value"};

            String insertMessage1 = String.format("series e:%s m:%s=%s0 x:%s=%s a:%s t:%s=%s",
                    entityName, metricName, metricValue, metricName, metricText1, textAppend, tagNames[0], tagValues[0]);
            String insertMessage2 = String.format("series e:%s m:%s=%s0 x:%s=%s a:%s t:%s=%s",
                    entityName, metricName, metricValue, metricName, metricText2, textAppend, tagNames[0], tagValues[0]);
            sendCommand(insertMessage1 + "\n" + insertMessage2);

            checkTagsAdd("Series tag Values is not added into Series Tag Value IDs: ",
                    "Series Tag Value IDs", tagValues);
            checkTagsAdd("Series tag Keys is not added into Series Tag Key IDs: ",
                    "Series Tag Key IDs", tagNames);
            checkSeriesAddByMetricName(metricName);
            checkEntityAdd(entityName);
            checkSeriesParams(metricName, metricText1 + "; " + metricText2, tagNames, tagValues);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        } finally {
            dropEntity(entityName);
            dropMetric(metricName);
        }
    }

    @Test
    public void testMetricAdd() {
        String metricName = "metric_metric";
        try {
            String status = "false";
            String label = "label_metric";
            String description = "descr_metric";
            String dataType = "Long";
            String interpolationMode = "Previous";
            String units = "Celsius";
            String filterExpression = "value>0";
            String timeZone = "CET";
            String timeZoneTimeInterval = "GMT+02:00";
            String versioning = "true";
            String invalidAction = "DISCARD";
            String persistent = "true";
            String retentionIntervalDays = "20";
            String minVal = "10";
            String maxVal = "100";
            String[] tagNames = {"metric_tag_name1"};
            String[] tagValues = {"metric_tag_value1"};

            String insertMessage = String.format("metric m:%s b:%s l:%s d:%s p:%s i:%s u:%s f:%s z:%s v:%s a:%s pe:%s " +
                            "rd:%s min:%s max:%s t:%s=%s", metricName, status, label, description, dataType,
                    interpolationMode, units, filterExpression, timeZone, versioning, invalidAction, persistent,
                    retentionIntervalDays, minVal, maxVal, tagNames[0], tagValues[0]);
            sendCommand(insertMessage);

            checkMetricAdd(metricName);
            checkMetricParams(metricName, status, label, description, dataType, interpolationMode, units,
                    filterExpression, timeZoneTimeInterval, versioning, invalidAction, persistent,
                    retentionIntervalDays, minVal, maxVal, tagNames, tagValues);

        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        } finally {
            dropMetric(metricName);
        }
    }

    @Test
    public void testEntityAdd() {
        String entityName = "entity_entity";
        try {
            String status = "false";
            String label = "label_entity";
            String timeZone = "CET";
            String timeZoneTimeInterval = "GMT+02:00";
            String interpolationMode = "Previous";
            String[] tagNames = {"entity_tag_name1"};
            String[] tagValues = {"entity_tag_value1"};

            String insertMessage = String.format("entity e:%s b:%s l:%s i:%s z:%s t:%s=%s"
                    , entityName, status, label, interpolationMode, timeZone, tagNames[0], tagValues[0]);
            sendCommand(insertMessage);

            checkEntityAdd(entityName);
            checkEntityParams(entityName, status, label, interpolationMode, timeZoneTimeInterval, tagNames, tagValues);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        } finally {
            dropEntity(entityName);
        }
    }

    private void checkSeriesParams(String metricName, String metricText, String[] tagNames, String[] tagValues) {
        searchMetricInMetricPage(metricName);
        driver.findElement(By.xpath("//*[@id='metricsList']//a[@data-original-title='Series']")).click();
        driver.findElement(By.xpath("//*[@id='metricList']//a[@data-original-title='Statistics']")).click();

        String allTags = driver.
                findElement(By.xpath("//*/caption[contains(text(),'Series')]")).findElement(By.xpath("./..")).getText();
        for (String name : tagNames) {
            assertTrue("There is no such tag name:" + name, allTags.contains(name));
        }
        for (String value : tagValues) {
            assertTrue("There is no such tag value:" + value, allTags.contains(value));
        }

        driver.findElement(By.xpath("//*/a[text()='Sample Data']")).click();
        assertTrue("Metric text is not added",
                driver.findElement(By.xpath("//*[@id='sample-data']/table")).getText().contains(metricText));
    }

    private void checkSeriesAddByMetricName(String metricName) {
        searchMetricInMetricPage(metricName);
        driver.findElement(By.xpath("//*[@id='metricsList']//a[@data-original-title='Series']")).click();
        assertEquals("Series is not added", 1,
                driver.findElements(By.xpath("//*[@id='metricList']/tbody/tr")).size());
    }

    private void checkMetricAdd(String metricName) {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Settings')]")).click();
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.xpath("//*/li/a[text()='Schema']"))).build().perform();
        driver.findElement(By.xpath("//*/li/a[text()='Metric IDs']")).click();
        assertTrue("Metric is not added into Metric IDs table",
                driver.findElement(By.id("buildInfo")).getText().contains(metricName));

        searchMetricInMetricPage(metricName);
        String xpathToMetric = String.format("//*[@id='metricsList']//a[text()='%s']", metricName);
        assertTrue("Metric is not added into table on Metric Page", driver.findElements(By.xpath(xpathToMetric)).size() != 0);
    }

    private void searchMetricInMetricPage(String metricName) {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Metrics')]")).click();
        WebElement searchQuery = driver.findElement(By.id("searchQuery"));
        searchQuery.clear();
        searchQuery.sendKeys(metricName);
        searchQuery.submit();
    }

    private void checkMetricParams(String metricName, String status, String label, String description, String dataType,
                                   String interpolationMode, String units, String filter, String timeZoneTimeInterval,
                                   String versioning, String invalidAction, String persistent, String retentionIntervalDays,
                                   String minVal, String maxVal, String[] tagNames, String[] tagValues) {

        String xpathToMetric = String.format("//*[@id='metricsList']//a[text()='%s']", metricName);
        searchMetricInMetricPage(metricName);
        driver.findElement(By.xpath(xpathToMetric)).click();

        assertSwitchOnOffButton("Wrong persistent", persistent, "Persistent");
        assertSwitchOnOffButton("Wrong status", status, "Enabled");
        assertSwitchOnOffButton("Wrong versioning", versioning, "Versioning");
        assertEquals("Wrong label", label, driver.findElement(By.id("metric.label")).getAttribute("value"));
        assertEquals("Wrong interpolation", interpolationMode,
                driver.findElement(By.xpath("//*[@id='metric.interpolate']/option[@selected]")).getText());

        for (int i = 0; i < tagNames.length; i++) {
            assertEquals("There is no such tag name: " + tagNames[i], tagNames[i],
                    driver.findElement(By.id("tags" + i + ".key")).getAttribute("value"));
            assertEquals("There is no such tag value: " + tagValues[i], tagValues[i],
                    driver.findElement(By.id("tags" + i + ".value")).getAttribute("value"));
        }
        assertEquals("Wrong description", description, driver.findElement(By.id("metric.description")).getText());
        assertEquals("Wrong data type", dataType,
                driver.findElement(By.xpath("//*[@id='metric.dataType']/option[@selected]")).getText());
        assertEquals("Wrong units", units, driver.findElement(By.id("metric.units")).getAttribute("value"));
        assertEquals("Wrong filter", filter, driver.findElement(By.id("metric.filter")).getAttribute("value"));
        assertEquals("Wrong min value", minVal, driver.findElement(By.id("metric.minValue")).getAttribute("value"));
        assertEquals("Wrong max value", maxVal, driver.findElement(By.id("metric.maxValue")).getAttribute("value"));
        assertEquals("Wrong retention days", retentionIntervalDays,
                driver.findElement(By.id("metric.retentionIntervalDays")).getAttribute("value"));
        assertEquals("Wrong invalid action", invalidAction,
                driver.findElement(By.xpath("//*[@id='metric.invalidValueAction']/option[@selected]")).getText());
        assertTrue("Wrong time zone",
                driver.findElements(By.xpath("//*[@id='metric.timeZone']/option[@selected]")).size() != 0 && driver.
                        findElement(By.xpath("//*[@id='metric.timeZone']/option[@selected]")).getText().contains(timeZoneTimeInterval));
    }

    private void assertSwitchOnOffButton(String errorMessage, String valueOfButton, String nameOfButton) {
        assertEquals(errorMessage, valueOfButton.toLowerCase().equals("true") ? "Yes" : "No",
                driver.findElement(By.xpath("//*[@id='settingsPanel']//label[text()='" + nameOfButton + "']")).
                        findElement(By.xpath("./..")).
                        findElement(By.xpath("./div/div")).getAttribute("class").contains("btn-warning") ? "Yes" : "No");
    }

    private void checkEntityParams(String entityName, String status, String label, String interpolationMode,
                                   String timeZoneTimeInterval, String[] tagNames, String[] tagValues) {
        String xpathToEntity = String.format("//*[@id='entitiesList']//a[text()='%s']", entityName);
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Entities')]")).click();
        driver.findElement(By.xpath(xpathToEntity)).click();

        assertSwitchOnOffButton("Wrong status", status, "Enabled");
        assertEquals("Wrong label", label, driver.findElement(By.id("label")).getAttribute("value"));
        assertEquals("Wrong interpolation", interpolationMode,
                driver.findElement(By.xpath("//*[@id='interpolate']/option[@selected]")).getText());
        for (int i = 0; i < tagNames.length; i++) {
            assertEquals("There is no such tag name: " + tagNames[i], tagNames[i],
                    driver.findElement(By.id("tags" + i + ".key")).getAttribute("value"));
            assertEquals("There is no such tag value: " + tagValues[i], tagValues[i],
                    driver.findElement(By.id("tags" + i + ".value")).getAttribute("value"));
        }
        assertTrue("Wrong time zone",
                driver.findElements(By.xpath("//*[@id='timeZone']/option[@selected]")).size() != 0 && driver.
                        findElement(By.xpath("//*[@id='timeZone']/option[@selected]")).getText().contains(timeZoneTimeInterval));
    }

    private void checkPropsAdd(String entityName, String propType, String[] key_names, String[] key_values,
                               String[] tag_names, String[] tag_values) {
        String xpathToEntity = String.format("//*[@id='entitiesList']//a[text()='%s']", entityName);
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Entities')]")).click();
        driver.findElement(By.xpath(xpathToEntity)).click();

        driver.findElement(By.xpath("//*[text()='Properties']")).click();

        assertTrue("Property is not added",
                driver.findElement(By.id("property-types-table")).getText().contains(propType));

        clickAndSwitch(driver.findElement(By.xpath("//*[contains(@data-original-title,'properties')]")));

        String[] keys = ArrayUtils.addAll(key_names, key_values);
        String[] tags = ArrayUtils.addAll(tag_names, tag_values);
        String allTagsAdnKeys = driver.findElement(By.id("property-widget")).getText();

        for (String key : keys) {
            assertTrue("There is no such key in property:" + key, allTagsAdnKeys.contains(key));
        }
        for (String tag : tags) {
            assertTrue("There is no such tag in property:" + tag, allTagsAdnKeys.contains(tag));
        }
    }


    private void checkEntityAdd(String entityName) {
        String xpathToEntity = String.format("//*[@id='entitiesList']//a[text()='%s']", entityName);

        driver.findElement(By.xpath("//*/a/span[contains(text(),'Entities')]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 3);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathToEntity)));
        } catch (TimeoutException exception) {
            driver.navigate().refresh();
        }

        assertTrue("Entity is not added", driver.findElements(By.xpath(xpathToEntity)).size() != 0);
    }

    private void checkMessageAddByEntityName(String entityName) {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Messages')]")).click();
        driver.findElement(By.id("entity")).clear();
        driver.findElement(By.id("entity")).sendKeys(entityName);
        driver.findElement(By.xpath("//*/button[text()='Search']")).click();
        assertTrue("Message is not added into table",
                driver.findElements(By.xpath("//*[@id='messagesList']/tbody/tr")).size() > 0);

    }

    private void checkTagsAdd(String errorMessage, String pageName, String[] tags) {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Settings')]")).click();

        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(By.xpath("//*/li/a[text()='Schema']"))).build().perform();
        driver.findElement(By.xpath(String.format("//*/li/a[text()='%s']", pageName))).click();

        String allTags = driver.findElement(By.id("buildInfo")).getText();
        for (String tag : tags) {
            assertTrue(errorMessage + tag, allTags.contains(tag));
        }
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Metrics')]")).click();
    }

    private void sendCommand(String insertMessage) {
        driver.findElement(By.id("commandsType")).click();
        driver.findElement(By.xpath("//*/form[1]/div[1]/div/div[2]/div[6]")).click();
        driver.findElement(By.xpath("//*/form[1]/div[1]/div/div[2]/div[1]/textarea")).sendKeys(insertMessage);
        driver.findElement(By.xpath("//*/button[text()='Send']")).click();
    }

    private void clickAndSwitch(WebElement link) {
        link.click();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.get(1));
    }

    private void dropEntity(String entityName) {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Entities')]")).click();
        findCheckboxAndDelete(entityName);
    }

    private void dropMetric(String metricName) {
        searchMetricInMetricPage(metricName);
        findCheckboxAndDelete(metricName);
    }

    private void findCheckboxAndDelete(String elementName) {
        while (driver.findElements(By.xpath(String.format("//*/a[text()='%s']", elementName))).size() > 0) {
            driver.findElement(By.xpath(String.format("//*/a[text()='%s']", elementName))).
                    findElement(By.xpath("./../..")).findElement(By.xpath(".//*[@type='checkbox']")).click();
            driver.findElement(By.xpath("//*/div[@class='table-controls']//*[@data-toggle='dropdown']")).click();
            driver.findElement(By.xpath("//*/a[text()='Delete']")).click();
            WebDriverWait wait = new WebDriverWait(driver, 9);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("confirm-modal")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirm-modal']//button[text()='Yes']")));
            driver.findElement(By.xpath("//*[@id='confirm-modal']//button[text()='Yes']")).click();
            driver.navigate().refresh();
        }
        assertEquals(elementName + " is not deleted", 0,
                driver.findElements(By.xpath(String.format("//*/a[text()='%s']", elementName))).size());
    }
}
