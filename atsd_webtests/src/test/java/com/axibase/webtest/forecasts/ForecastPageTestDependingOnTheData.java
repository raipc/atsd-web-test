package com.axibase.webtest.forecasts;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.pages.ForecastSettingsPage;
import com.axibase.webtest.pages.ForecastViewerPage;
import com.axibase.webtest.pages.PortalPage;
import com.axibase.webtest.service.AtsdTest;
import com.axibase.webtest.service.CSVDataUploaderService;
import com.axibase.webtest.CommonSelects;
import com.axibase.webtest.service.csv.CSVImportParserAsSeriesTest;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import org.openqa.selenium.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ForecastPageTestDependingOnTheData extends AtsdTest {
    private static final String PAGE_URL = url + "/series/forecast";
    private static final String URL_FOR_GROUPING_WITHOUT_TAGS = PAGE_URL + "?entity=entity-forecast-viewer-test&" +
            "metric=metric-forecast-viewer-test&_g&" +
            "startDate=2019-04-12T14:17:23.000Z";
    private static final String URL_FOR_GROUPING_WITH_TAGS = URL_FOR_GROUPING_WITHOUT_TAGS + "&host=A4AF797F3737&name=entityById&";
    private static final String START_URL = PAGE_URL + "?entity=nurswgvml007&" +
            "metric=forecastpagetest&tag_name1=forecastPageTest&tag_name2=forecastPageTest&" +
            "startDate=2019-03-16T08:11:22.000Z&horizonInterval=12-HOUR&period=5-MINUTE";
    private static final String DATA_CSV = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-data.csv").getFile();
    private CSVDataUploaderService csvDataUploaderService;
    private long timeZoneHours = 0;
    private ForecastViewerPage forecastViewerPage;

    @Before
    public void setUp() {
        this.login();
        csvDataUploaderService = new CSVDataUploaderService(AtsdTest.driver, AtsdTest.url);
        csvDataUploaderService.uploadWithParser(DATA_CSV, "test-atsd-import-series-parser");
        setTimeZone();
        driver.get(START_URL);
        forecastViewerPage = new ForecastViewerPage(driver);
    }

    @Test
    public void testEigenvaluesWithZeroThresholdOnSmallSample() {
        forecastViewerPage.setStartDate("2019-03-16")
                .setStartTime("16:11:00")
                .setEndDate("2019-03-16")
                .setEndTime("17:11:00")
                .setThreshold("0")
                .setPeriodCount("15")
                .setPeriodUnit("minute")
                .setForecastHorizonCount("1")
                .setForecastHorizonUnit("hour")
                .submitFormAndWait(15);

        assertEquals("Should be only one silver component", 1,
                forecastViewerPage.getCountOfPassiveComponentsInComponentContainer());
    }

    @Test
    public void testGroupURLParameterWithTagsOnViewerPage() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
        driver.get(URL_FOR_GROUPING_WITH_TAGS);
        String tags = forecastViewerPage.getGroupedByURLTags();
        assertTrue("There is a missing tag in the grouping", tags.contains("name = entityById"));
        assertTrue("There is a missing tag in the grouping", tags.contains("host = A4AF797F3737"));
    }

    @Test
    public void testGroupURLParameterWithoutTagsOnViewerPage() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
        driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
        assertEquals("There is no sign of presence of grouping", "Grouped by all tags",
                forecastViewerPage.getGroupedByURLText());
    }

    @Test
    public void testGroupURLParameterWithTagsOnSettingsPage() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");

        driver.get(URL_FOR_GROUPING_WITH_TAGS);
        forecastViewerPage.scheduleForecast();
        switchToNewWindowTab();
        ForecastSettingsPage forecastSettings = new ForecastSettingsPage(driver);
        assertEquals("Wrong type of grouping in forecast settings", "Metric - Entity - Defined Tags",
                forecastSettings.getGroupingType());
        assertEquals("Wrong tags in grouping in forecast settings", "host name",
                forecastSettings.getGroupingTags());
    }

    @Test
    public void testGroupURLParameterWithoutTagsOnSettingsPage() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
        driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
        forecastViewerPage.scheduleForecast();
        switchToNewWindowTab();
        ForecastSettingsPage forecastSettings = new ForecastSettingsPage(driver);
        assertEquals("Wrong type of grouping in forecast settings", "Metric - Entity",
                forecastSettings.getGroupingType());
    }

    @Test
    public void testGroupURLParameterWithTagsInPortal() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
        driver.get(URL_FOR_GROUPING_WITH_TAGS);
        forecastViewerPage.savePortal();
        switchToNewWindowTab();
        PortalPage portalPage = new PortalPage(driver);
        assertTrue("There is no tags section", portalPage.getContentWrapperText().contains("[tags]"));
        assertTrue("There is no host tag", portalPage.getContentWrapperText().contains("host = A4AF797F3737"));
        assertTrue("There is no name tag", portalPage.getContentWrapperText().contains("name = entityById"));
    }

    @Test
    public void testGroupURLParameterWithoutTagsInPortal() {
        loadDataAndParserByNames("test-atsd-_g-forecast-viewer-parser", "test-atsd-_g-forecast-viewer-data");
        driver.get(URL_FOR_GROUPING_WITHOUT_TAGS);
        forecastViewerPage.savePortal();
        switchToNewWindowTab();
        PortalPage portalPage = new PortalPage(driver);
        assertFalse("There is tag section without tags", portalPage.getContentWrapperText().contains("[tags]"));
    }

    @Test
    public void testStrictTagsAndDataMapping() {
        driver.get(removeURLParameter(driver.getCurrentUrl(), "tag_name1"));
        assertFalse("There is some data but with not full set of tags there shouldn't be any data",
                forecastViewerPage.isWidgetContainerLoading());
        assertEquals("There are some charts but it shouldn't be", 0,
                forecastViewerPage.getCountOfForecastsInWidgetContainer());
    }

    @Test
    public void testCorrectnessOfLinkClicks() {
        forecastViewerPage.getBreadcrumbElement(2).click();
        assertEquals("Wrong page while click on entity link in breadcrumb",
                "Entity: nurswgvml007", driver.getTitle());
        driver.navigate().back();
        forecastViewerPage.getBreadcrumbElement(1).click();
        assertEquals("Wrong page while click on metric link in breadcrumb",
                "Metric: forecastpagetest", driver.getTitle());
        driver.navigate().back();

        assertEquals("Wrong tag parameters in breadcrumb",
                "tag_name1=\"forecastPageTest\", tag_name2=\"forecastPageTest\"",
                forecastViewerPage.getBreadcrumbElement(3).getText());
    }

    @Test
    public void testForecastURLParams() {
        Map<String, String> params = prepareURLParams();
        String newURL = createNewURL(AtsdTest.url + "/series/forecast", params);
        driver.navigate().to(newURL);

        assertRegularizeOptionValues(params.get("aggregation"), params.get("interpolation"), "25",
                "minute", "URL params test");
        assertDecomposeOptionValues(params.get("componentThreshold"), params.get("componentCount"),
                params.get("windowLength"), "URL params test");
        assertForecastOptions("AVG", "1", "minute", "URL params test");
        assertStartDate("URLParams: Wrong Start Date", params.get("startDate"));
        assertEndDate("URLParams: Wrong End Date", params.get("endDate"));
        assertIntervalEquals("URLParams: Wrong horizon interval", params.get("horizonInterval"),
                CommonSelects.getFormattedInterval(forecastViewerPage.getForecastHorizonCount(),
                        forecastViewerPage.getForecastHorizonUnit()));
    }

    @Test
    public void testPresenceOfForecastsUntilSubmit() {
        forecastViewerPage.waitUntilSummaryTableIsLoaded(20);
        int forecastCountInChart = forecastViewerPage.getCountOfForecastsInWidgetContainer();
        int forecastCountInSummary = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks().size();
        assertEquals("Different count of forecasts in the chart and in the summary",
                forecastCountInChart, forecastCountInSummary);
    }

    @Test
    public void testChangeActiveSummary() {
        forecastViewerPage.setGroupAuto()
                .setGroupCount("11")
                .setComponentCount("19")
                .submitFormAndWait(20);

        List<WebElement> forecastSingularValues = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks();
        WebElement componentContainer = forecastViewerPage.getComponentContainer();

        for (WebElement forecastSingularValue : forecastSingularValues) {
            forecastSingularValue.click();
            assertNameOfForecastInComponentsWindowAndNameInSummary(forecastSingularValue, componentContainer);
            assertCountOfGreenComponents(forecastSingularValues.indexOf(forecastSingularValue));
        }
    }

    @Test
    public void testActiveRegularizeOptionsCloning() {
        forecastViewerPage.setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute")
                .addForecastTab();
        assertRegularizeOptionValues("PERCENTILE_99", "PREVIOUS", "20", "minute", "cloning");
    }

    @Test
    public void testActiveDecomposeOptionsCloning() {
        forecastViewerPage.setDecomposeOptions("10", "12", "44")
                .addForecastTab();
        assertDecomposeOptionValues("10", "12", "44", "cloning");
    }

    @Test
    public void testActiveForecastOptionsCloning() {
        forecastViewerPage.setForecastOptions("MEDIAN", "10", "year")
                .addForecastTab();
        assertForecastOptions("MEDIAN", "10", "year", "cloning");
    }

    @Test
    public void testSwitchTabsRegularizeOptions() {
        forecastViewerPage.setRegularizeOptions("PERCENTILE_99", "PREVIOUS", "20", "minute")
                .addForecastTab();

        String[] names = forecastViewerPage.getForecastTabNames();
        assertNotEquals("Forecast names in tabs are equals but they shouldn't be", names[0], names[1]);

        forecastViewerPage.setRegularizeOptions("SUM", "LINEAR", "10", "hour")
                .switchForecastTab("Forecast 1");
        assertRegularizeOptionValues("PERCENTILE_99", "PREVIOUS", "20", "minute", "switching");
        forecastViewerPage.switchForecastTab("Forecast 2");
        assertRegularizeOptionValues("SUM", "LINEAR", "10", "hour", "switching");
    }

    @Test
    public void testSwitchTabsDecomposeOptions() {
        forecastViewerPage.setDecomposeOptions("10", "12", "44")
                .addForecastTab()
                .setDecomposeOptions("20", "15", "2")
                .switchForecastTab("Forecast 1");
        assertDecomposeOptionValues("10", "12", "44", "switching");
        forecastViewerPage.switchForecastTab("Forecast 2");
        assertDecomposeOptionValues("20", "15", "2", "switching");
    }

    @Test
    public void testSwitchTabsForecastOptions() {
        forecastViewerPage.setForecastOptions("MEDIAN", "10", "year")
                .addForecastTab()
                .setForecastOptions("AVG", "11", "minute")
                .switchForecastTab("Forecast 1");
        assertForecastOptions("MEDIAN", "10", "year", "switching");
        forecastViewerPage.switchForecastTab("Forecast 2");
        assertForecastOptions("AVG", "11", "minute", "switching");
    }

    @Test
    public void testCountOfGroups() {
        String countOfGroups = "11";
        forecastViewerPage.setGroupAuto()
                .setGroupCount(countOfGroups)
                .setComponentCount("19")
                .submitFormAndWait(20);
        int countInPic = forecastViewerPage.getCountOfForecastsInWidgetContainer();
        assertEquals("Wrong count of groups on the chart", countOfGroups, Integer.toString(countInPic));
    }

    @Test
    public void testPresenceOfForecastsInSummary() {
        forecastViewerPage.addForecastTab()
                .submitFormAndWait(20);
        int forecastCountInSummary = forecastViewerPage.getSummaryContainerForecastsSingularValueLinks().size();
        assertEquals("Wrong count of history charts in summary", 2, forecastCountInSummary);
    }

    @Test
    public void testPresenceOfHistoryChartsInPicWithDifferentPeriods() {
        forecastViewerPage.addForecastTab()
                .setPeriodCount("20")
                .submitFormAndWait(20);
        int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
        assertEquals("Wrong count of history charts in chart", 2, forecastCountInChart);
    }

    @Test
    public void testPresenceOfHistoryChartsInPicWithDifferentAggregation() {
        forecastViewerPage.addForecastTab()
                .setAggregation("SUM")
                .submitFormAndWait(20);
        int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
        assertEquals("Wrong count of history charts in chart", 2, forecastCountInChart);
    }

    @Test
    public void testPresenceOfHistoryChartsInPicWithDifferentInterpolation() {
        forecastViewerPage.addForecastTab()
                .setInterpolation("PREVIOUS")
                .submitFormAndWait(20);
        int forecastCountInChart = forecastViewerPage.getCountOfHistoryChartsInWidgetContainer();
        assertEquals("Wrong count of history charts in chart", 2, forecastCountInChart);
    }

    @Test
    public void testNamesInSummary() {
        forecastViewerPage.addForecastTab()
                .addForecastTab()
                .switchForecastTab("Forecast 2")
                .removeForecastTab()
                .submitFormAndWait(25);

        String[] names = forecastViewerPage.getForecastTabNames();
        List<String> forecastNames = forecastViewerPage.getSummaryContainerForecastNames();
        forecastNames.remove(0);
        for (int i = 0; i < forecastNames.size(); i++) {
            assertTrue("Wrong name of forecast in summary table", forecastNames.get(i).contains(names[i]));
        }
    }

    private void assertCountOfGreenComponents(int forecastNumber) {
        int countOfActiveComponents = forecastViewerPage.getCountOfActiveComponentsInSingularValueContainer(forecastNumber);
        int countOfGreenComponents = forecastViewerPage.getCountOfActiveComponentsInComponentContainer();
        assertEquals("Wrong count of green components", countOfActiveComponents, countOfGreenComponents);
    }

    private void assertNameOfForecastInComponentsWindowAndNameInSummary(WebElement forecast, WebElement componentContainer) {
        String name = forecastViewerPage.getNameOfForecastInSummaryTable(forecast);
        assertTrue("Wrong forecast name in components window", componentContainer.getText().contains(name));
    }

    private void assertIntervalEquals(String errorMessage, String expectedInterval, String elementInterval) {
        assertEquals(errorMessage, expectedInterval, elementInterval);
    }

    private void assertEndDate(String errorMessage, String sendedDate) {
        String newDate = forecastViewerPage.getEndDate().getAttribute("value") + "T" +
                forecastViewerPage.getEndTime().getAttribute("value");
        assertEquals(errorMessage, getTranslatedDate(sendedDate).toString(), newDate);
    }

    private LocalDateTime getTranslatedDate(String oldDate) {
        LocalDateTime sended = LocalDateTime.parse(oldDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        sended = sended.plusHours(timeZoneHours);
        return sended;
    }

    private void assertStartDate(String errorMessage, String sendedDate) {
        String newDate = forecastViewerPage.getStartDate().getAttribute("value") + "T" +
                forecastViewerPage.getStartTime().getAttribute("value");
        assertEquals(errorMessage, getTranslatedDate(sendedDate).toString(), newDate);
    }

    private String createNewURL(String URLPrefix, Map<String, String> params) {
        List<NameValuePair> paramsForEncoding = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsForEncoding.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            return new URIBuilder(URLPrefix).addParameters(paramsForEncoding).build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Wrong URI", e);
        }
    }

    private String removeURLParameter(String url, String parameterName) {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url);
            List<NameValuePair> queryParameters = uriBuilder.getQueryParams();
            for (NameValuePair queryPair : queryParameters) {
                if (queryPair.getName().equals(parameterName)) {
                    queryParameters.remove(queryPair);
                    break;
                }
            }
            uriBuilder.setParameters(queryParameters);
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Wrong current URL", e);
        }
    }

    private void assertRegularizeOptionValues(String aggregationType, String interpolationType,
                                              String periodCount, String periodUnit, String testType) {
        CommonAssertions.assertValueAttributeOfElement("Wrong aggregation after " + testType, aggregationType,
                forecastViewerPage.getAggregation());
        CommonAssertions.assertValueAttributeOfElement("Wrong interpolation after " + testType, interpolationType,
                forecastViewerPage.getInterpolation());
        assertIntervalEquals("Wrong period in the regularize section",
                periodCount + "-" + periodUnit,
                CommonSelects.getFormattedInterval(forecastViewerPage.getPeriodCount(),
                        forecastViewerPage.getPeriodUnit()));
    }

    private void assertDecomposeOptionValues(String threshold, String componentCount, String windowLen, String testType) {
        CommonAssertions.assertValueAttributeOfElement("Wrong threshold after " + testType,
                threshold, forecastViewerPage.getThreshold());
        CommonAssertions.assertValueAttributeOfElement("Wrong component count after " + testType,
                componentCount, forecastViewerPage.getComponentCount());
        CommonAssertions.assertValueAttributeOfElement("Wrong window length after " + testType,
                windowLen, forecastViewerPage.getWindowLength());
    }

    private void assertForecastOptions(String average, String intervalCount, String intervalUnit, String testType) {
        CommonAssertions.assertValueAttributeOfElement("Wrong average name after " + testType,
                average, forecastViewerPage.getAveragingFunction());
        assertIntervalEquals("Wrong score interval in the Forecast section",
                intervalCount + "-" + intervalUnit,
                CommonSelects.getFormattedInterval(forecastViewerPage.getScoreIntervalCount(),
                        forecastViewerPage.getScoreIntervalUnit()));
    }

    private void setTimeZone() {
        String url = AtsdTest.url + "/api/v1/version";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        String authorizationParams = AtsdTest.login + ":" + AtsdTest.password;
        try {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encodeBase64(
                    authorizationParams.getBytes(StandardCharsets.ISO_8859_1))));
            HttpResponse response = client.execute(request);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            String resultJSON = result.toString();
            Matcher matcher = Pattern.compile("\"offsetMinutes\":(\\d+)").matcher(resultJSON);
            if (matcher.find()) {
                timeZoneHours = Long.parseLong(matcher.group(1)) / 60;
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get time zone", e);
        }
    }

    private void switchToNewWindowTab() {
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.get(1));
    }

    private void loadDataAndParserByNames(String parserName, String dataName) {
        String pathToCSVFile = CSVImportParserAsSeriesTest.class.getResource(dataName + ".csv").getFile();
        csvDataUploaderService.uploadWithParser(pathToCSVFile, parserName);
    }

    private Map<String, String> prepareURLParams() {
        Map<String, String> params = new HashMap<>();
        params.put("entity", "nurswgvml007");
        params.put("metric", "forecastpagetest");
        params.put("startDate", "2019-03-17T08:11:22.000Z");
        params.put("endDate", "2019-03-18T08:11:22.000Z");
        params.put("horizonInterval", "1-day");
        params.put("period", "25-minute");
        params.put("scoreInterval", "1-MINUTE");
        params.put("componentThreshold", "10");
        params.put("windowLength", "45");
        params.put("componentCount", "100");
        params.put("aggregation", "PERCENTILE_75");
        params.put("interpolation", "PREVIOUS");
        params.put("tag_name1", "forecastPageTest");
        return params;
    }

}
