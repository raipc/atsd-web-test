package com.axibase.webtest.service.csv;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.service.AtsdTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static com.axibase.webtest.CommonConditions.clickable;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CSVImportParserAsSeriesTest extends AtsdTest {
    private static final String PARSER_NAME = "test-atsd-import-series-parser";
    private static final String PATH_TO_PARSER = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-parser.xml").getFile();

    @Before
    public void setUp() {
        super.setUp();
        goToCSVParsersImportPage();
    }

    @After
    public void cleanup() {
        goToCSVParsersPage();
        $(By.xpath("//*/input[@title='Select all']")).setSelected(true);
        $(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        $(By.xpath("//*/input[@type='submit' and @value='Delete']")).click();
        $(By.xpath("//*[@id='confirm-modal']//button[contains(text(), 'Yes')]")).waitUntil(clickable, 1000).click();
    }

    @Test
    public void testImportCSVParserPage() {
        setReplaceExisting(false);
        sendParserIntoTableWithoutReplacement(PATH_TO_PARSER);

        goToCSVParsersPage();


        final boolean isParserPresented = Optional.of($("#configurationList > tbody"))
                .map(WebElement::getText)
                .map(text -> text.contains(PARSER_NAME))
                .orElse(false);
        assertTrue("Parser is not added into table", isParserPresented);
    }

    @Test
    public void testImportCSVParserWithReplace() {
        setReplaceExisting(false);
        sendParserIntoTableWithoutReplacement(PATH_TO_PARSER);
        setReplaceExisting(true);
        sendParserIntoTableWithReplacement(PATH_TO_PARSER);

        goToCSVParsersPage();
        assertTrue("Parser is not added into table",
                $(By.cssSelector("#configurationList > tbody")).getText().contains(PARSER_NAME));
    }

    private void sendParserIntoTableWithoutReplacement(String file) {
        $(By.cssSelector("#putTable input[type=\"file\"]")).sendKeys(file);
        $(By.name("send")).click();
        assertFalse("No success message", $$(By.className("successMessage")).isEmpty());
    }

    private void sendParserIntoTableWithReplacement(String file) {
        sendParserIntoTableWithoutReplacement(file);
        assertTrue("There was no replacement",
                $(By.className("successMessage")).getText().matches(".*replaced:\\s[1-9]\\d*"));
    }

    private void setReplaceExisting(boolean on) {
        $(By.name("overwrite")).setSelected(on);
    }

    private void goToCSVParsersPage() {
        $(By.linkText("Data")).click();
        $(By.linkText("CSV Parsers")).click();
        CommonAssertions.assertPageUrlPathEquals("/csv/configs");
    }

    private void goToCSVParsersImportPage() {
        goToCSVParsersPage();
        $(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        $(By.xpath("//*/a[text()='Import']")).click();
        CommonAssertions.assertPageUrlPathEquals("/csv/configs/import");
    }

}
