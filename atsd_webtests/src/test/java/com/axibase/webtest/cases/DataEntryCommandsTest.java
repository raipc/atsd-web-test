package com.axibase.webtest.cases;

import com.axibase.webtest.pageobjects.*;
import com.axibase.webtest.service.AtsdTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static com.axibase.webtest.CommonActions.clickCheckboxByValueAttribute;
import static com.axibase.webtest.CommonActions.dropCheckedRecords;
import static com.axibase.webtest.CommonAssertions.assertStringContainsValues;
import static com.axibase.webtest.CommonAssertions.assertValueAttributeOfElement;
import static org.junit.Assert.*;

public class DataEntryCommandsTest extends AtsdTest {
    private final String ENTITY_NAME = "entity_name_data_entry_commands_test";
    private final String METRIC_NAME = "metric_name_data_entry_commands_test";
    private DataEntryPage dataEntryPage;

    @Before
    public void setUp() {
        this.login();
        dataEntryPage = new DataEntryPage(driver, url);
    }

    @Test
    public void testMessageAdd() {
        String messageText = "\"Message text\"";
        String[] tagNames = {"type", "source", "message_tag_name"};
        String[] tagValues = {"message_tag_type_value", "message_tag_source_value", "message_tag_tag_value"};
        String insertMessage = String.format("message e:%s t:%s=%s t:%s=%s t:%s=%s m:%s", ENTITY_NAME,
                tagNames[0], tagValues[0], tagNames[1], tagValues[1], tagNames[2], tagValues[2], messageText);

        dataEntryPage.typeCommands(insertMessage).sendCommands();

        assertEntityAdd();
        assertStringContainsValues("Message tag key is not added into Message Tag Key IDs: ",
                tagNames, new MessageTagKeyIDsPage(driver, url).getValuesInTable());
        assertStringContainsValues("Message tag value is not added into Message Tag Value IDs: ",
                tagValues, new MessageTagValueIDsPage(driver, url).getValuesInTable());
        assertMessageAddByEntityName();
    }

    @Test
    public void testPropertyAdd() {
        String propType = "property_type";
        String[] key_names = {"property_key_name1", "property_key_name2"};
        String[] key_values = {"property_key_value1", "property_key_value2"};
        String[] tag_names = {"property_tag_name1", "property_tag_name2"};
        String[] tag_values = {"property_tag_value1", "property_tag_value2"};
        String insertMessage = String.format("property e:%s t:%s k:%s=%s k:%s=%s v:%s=%s v:%s=%s",
                ENTITY_NAME, propType, key_names[0], key_values[0], key_names[1], key_values[1],
                tag_names[0], tag_values[0], tag_names[1], tag_values[1]);

        dataEntryPage.typeCommands(insertMessage).sendCommands();

        assertEntityAdd();
        assertPropertiesAdd(propType);
        assertPropertiesKeysAndTags(propType, key_names, key_values, tag_names, tag_values);
    }

    @Test
    public void testSeriesAdd() {
        String metricText1 = "metric_text1";
        String metricText2 = "metric_text2";
        String textAppend = "true";
        String metricValue = "10";
        String[] tagNames = {"series_tag_key"};
        String[] tagValues = {"series_tag_value"};
        String time = "1425482080";
        String insertMessage1 = String.format("series e:%s m:%s=%s0 x:%s=%s a:%s t:%s=%s s:%s", ENTITY_NAME,
                METRIC_NAME, metricValue, METRIC_NAME, metricText1, textAppend, tagNames[0], tagValues[0], time);
        String insertMessage2 = String.format("series e:%s m:%s=%s0 x:%s=%s a:%s t:%s=%s s:%S", ENTITY_NAME,
                METRIC_NAME, metricValue, METRIC_NAME, metricText2, textAppend, tagNames[0], tagValues[0], time);

        dataEntryPage.typeCommands(insertMessage1 + "\n" + insertMessage2).sendCommands();

        assertStringContainsValues("Series tag keys is not added into Series Tag Key IDs: ",
                tagNames, new SeriesTagKeyIDsPage(driver, url).getValuesInTable());
        assertStringContainsValues("Series tag values is not added into Series Tag Values IDs: ",
                tagValues, new SeriesTagValueIDsPage(driver, url).getValuesInTable());
        assertSeriesAdd();
        assertEntityAdd();
        assertSeriesParams(metricText1 + "; " + metricText2, tagNames, tagValues);
    }

    @Test
    public void testMetricAdd() {
        String status = "false";
        String label = "label_metric";
        String description = "descr_metric";
        String dataType = "Long";
        String interpolationMode = "Previous";
        String units = "Celsius";
        String filterExpression = "value>0";
        String timeZone = "CET";
        String versioning = "true";
        String invalidAction = "DISCARD";
        String persistent = "true";
        String retentionIntervalDays = "20";
        String minVal = "10";
        String maxVal = "100";
        String[] tagNames = {"metric_tag_name1"};
        String[] tagValues = {"metric_tag_value1"};
        String insertMessage = String.format("metric m:%s b:%s l:%s d:%s p:%s i:%s u:%s f:%s z:%s v:%s a:%s pe:%s " +
                        "rd:%s min:%s max:%s t:%s=%s", METRIC_NAME, status, label, description, dataType,
                interpolationMode, units, filterExpression, timeZone, versioning, invalidAction, persistent,
                retentionIntervalDays, minVal, maxVal, tagNames[0], tagValues[0]);

        dataEntryPage.typeCommands(insertMessage).sendCommands();

        assertMetricAdd();
        checkMetricParams(status, label, description, dataType, interpolationMode, units,
                filterExpression, timeZone, versioning, invalidAction, persistent,
                retentionIntervalDays, minVal, maxVal, tagNames, tagValues);
    }

    @Test
    public void testEntityAdd() {
        String status = "true";
        String label = "label_entity";
        String timeZone = "CET";
        String interpolationMode = "Previous";
        String[] tagNames = {"entity_tag_name1"};
        String[] tagValues = {"entity_tag_value1"};
        String insertMessage = String.format("entity e:%s b:%s l:%s i:%s z:%s t:%s=%s",
                ENTITY_NAME, status, label, interpolationMode, timeZone, tagNames[0], tagValues[0]);

        dataEntryPage.typeCommands(insertMessage).sendCommands();

        assertEntityAdd();
        assertEntityParams(status, label, interpolationMode, timeZone, tagNames, tagValues);
    }

    @Override
    public void cleanup() {
        dropRecord(new EntitiesTablePage(driver, url), ENTITY_NAME);
        dropRecord(new MetricsTablePage(driver, url), METRIC_NAME);
    }

    private <T extends Table> void dropRecord(T page, String recordName) {
        page.searchRecordByName(recordName);
        if (page.isRecordPresent(recordName)) {
            removeRecordByCheckbox(recordName);
        }
        assertFalse(recordName + " is not deleted", page.isRecordPresent(recordName));
    }

    private void removeRecordByCheckbox(String value) {
        clickCheckboxByValueAttribute(driver, value);
        dropCheckedRecords(driver);
    }

    private void assertEntityAdd() {
        EntitiesTablePage entitiesTablePage = new EntitiesTablePage(driver, url);
        assertTrue("Entity is not added", entitiesTablePage.isRecordPresent(ENTITY_NAME));
    }

    private void assertMessageAddByEntityName() {
        MessagesPage messagesPage = new MessagesPage(driver, url);
        messagesPage.setEntity(ENTITY_NAME);
        messagesPage.search();
        assertTrue("Message is not added into table", messagesPage.getCountOfMessages() > 0);
    }

    private void assertPropertiesAdd(String propType) {
        PropertiesTablePage propertiesTablePage = new PropertiesTablePage(driver, url, ENTITY_NAME);
        assertTrue("Property is not added", propertiesTablePage.isPropertyPresent(propType));
    }

    private void assertPropertiesKeysAndTags(String propType, String[] key_names, String[] key_values,
                                             String[] tag_names, String[] tag_values) {
        PropertiesPage propertiesPage = new PropertiesPage(driver, url, ENTITY_NAME, propType);
        String[] keys = ArrayUtils.addAll(key_names, key_values);
        String[] tags = ArrayUtils.addAll(tag_names, tag_values);
        String allTagsAdnKeys = propertiesPage.getTagsAndKeys();
        assertStringContainsValues("There is no such key in property:", keys, allTagsAdnKeys);
        assertStringContainsValues("There is no such tag in property:", tags, allTagsAdnKeys);
    }

    private void assertSeriesAdd() {
        MetricsSeriesTablePage metricsSeriesTablePage = new MetricsSeriesTablePage(driver, url, METRIC_NAME);
        assertTrue("Series is not added", metricsSeriesTablePage.isSeriesPresent());
    }

    private void assertSeriesParams(String metricText, String[] tagNames, String[] tagValues) {
        StatisticsPage statisticsPage = new StatisticsPage(driver, url, ENTITY_NAME, METRIC_NAME, tagNames, tagValues);
        String allTags = statisticsPage.getSeriesTags();
        assertStringContainsValues("There is no such tag name:", tagNames, allTags);
        assertStringContainsValues("There is no such tag value:", tagValues, allTags);

        statisticsPage.getSampleDataTab();
        assertTrue("Metric text is not added", statisticsPage.getSampleDataTableText().contains(metricText));
    }

    private void assertMetricAdd() {
        MetricIDsPage metricIDsPage = new MetricIDsPage(driver, url);
        assertTrue("Metric is not added into Metric IDs table", metricIDsPage.getValuesInTable().contains(METRIC_NAME));

        MetricsTablePage metricsTablePage = new MetricsTablePage(driver, url);
        metricsTablePage.searchRecordByName(METRIC_NAME);
        assertTrue("Metric is not added into table on Metric Page", metricsTablePage.isRecordPresent(METRIC_NAME));
    }

    private void checkMetricParams(String status, String label, String description, String dataType,
                                   String interpolationMode, String units, String filter, String timeZone,
                                   String versioning, String invalidAction, String persistent, String retentionIntervalDays,
                                   String minVal, String maxVal, String[] tagNames, String[] tagValues) {
        MetricPage metricPage = new MetricPage(driver, url, METRIC_NAME);

        assertSwitchOnOffButton("Wrong persistent", persistent, metricPage.getPersistentSwitch());
        assertSwitchOnOffButton("Wrong status", status, metricPage.getEnabledSwitch());
        assertSwitchOnOffButton("Wrong versioning", versioning, metricPage.getVersioningSwitch());
        assertValueAttributeOfElement("Wrong label", label, metricPage.getLabel());
        assertValueAttributeOfElement("Wrong interpolation", interpolationMode, metricPage.getInterpolation());
        assertStringContainsValues("There is no such tag name: ", tagNames, metricPage.getTagNames());
        assertStringContainsValues("There is no such tag value: ", tagValues, metricPage.getTagValues());
        assertValueAttributeOfElement("Wrong description", description, metricPage.getDescription());
        assertValueAttributeOfElement("Wrong data type", dataType, metricPage.getDataType());
        assertValueAttributeOfElement("Wrong units", units, metricPage.getUnits());
        assertValueAttributeOfElement("Wrong filter", filter, metricPage.getPersistentFilter());
        assertValueAttributeOfElement("Wrong min value", minVal, metricPage.getMinValue());
        assertValueAttributeOfElement("Wrong max value", maxVal, metricPage.getMaxValue());
        assertValueAttributeOfElement("Wrong retention days", retentionIntervalDays, metricPage.getRetentionIntervalDays());
        assertValueAttributeOfElement("Wrong invalid action", invalidAction, metricPage.getInvalidAction());
        assertValueAttributeOfElement("Wrong time zone", timeZone, metricPage.getTimeZone());
    }

    private void assertSwitchOnOffButton(String errorMessage, String valueOfButton, WebElement switchButton) {
        assertEquals(errorMessage, valueOfButton.toLowerCase().equals("true") ? "Yes" : "No",
                switchButton.getAttribute("class").contains("btn-warning") ? "Yes" : "No");
    }

    private void assertEntityParams(String status, String label, String interpolationMode,
                                    String timeZone, String[] tagNames, String[] tagValues) {
        EntityPage entityPage = new EntityPage(driver, url, ENTITY_NAME);

        assertSwitchOnOffButton("Wrong status", status, entityPage.getEnabledSwitch());
        assertValueAttributeOfElement("Wrong label", label, entityPage.getLabel());
        assertValueAttributeOfElement("Wrong interpolation", interpolationMode, entityPage.getInterpolation());
        assertStringContainsValues("There is no such tag name: ", tagNames, entityPage.getTagNames());
        assertStringContainsValues("There is no such tag value: ", tagValues, entityPage.getTagValues());
        assertValueAttributeOfElement("Wrong time zone", timeZone, entityPage.getTimeZone());
    }

}
