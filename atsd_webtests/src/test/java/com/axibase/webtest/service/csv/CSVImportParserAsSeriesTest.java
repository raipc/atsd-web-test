package com.axibase.webtest.service.csv;

import com.axibase.webtest.CommonAssertions;
import com.axibase.webtest.service.AtsdTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CSVImportParserAsSeriesTest extends AtsdTest {
    private static final String PARSER_NAME = "test-atsd-import-series-parser";
    private static final String PATH_TO_PARSER = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-parser.xml").getFile();

    @Before
    public void setUp() {
        login();
        goToCSVParsersImportPage();
    }

    @After
    public void cleanup() {
        goToCSVParsersPage();
        setCheckbox(driver.findElement(By.xpath("//*/input[@title='Select all']")), true);
        driver.findElement(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        driver.findElement(By.xpath("//*/input[@type='submit' and @value='Delete']")).click();
        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirm-modal']//button[contains(text(), 'Yes')]")));
        driver.findElement(By.xpath("//*[@id='confirm-modal']//button[contains(text(), 'Yes')]")).click();
    }

    @Test
    public void testImportCSVParserPage() {
        setReplaceExisting(false);
        sendParserIntoTableWithoutReplacement(PATH_TO_PARSER);

        goToCSVParsersPage();

        final boolean isParserPresented = Optional.of(driver)
                .map(d -> d.findElement(By.cssSelector("#configurationList > tbody")))
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
                driver.findElement(By.cssSelector("#configurationList > tbody")).getText().contains(PARSER_NAME));
    }

    private void sendParserIntoTableWithoutReplacement(String file) {
        driver.findElement(By.cssSelector("#putTable input[type=\"file\"]")).sendKeys(file);
        driver.findElement(By.name("send")).click();
        assertFalse("No success message", driver.findElements(By.className("successMessage")).isEmpty());
    }

    private void sendParserIntoTableWithReplacement(String file) {
        sendParserIntoTableWithoutReplacement(file);
        assertTrue("There was no replacement",
                driver.findElement(By.className("successMessage")).getText().matches(".*replaced:\\s[1-9]\\d*"));
    }

    private void setReplaceExisting(boolean on) {
        setCheckbox(driver.findElement(By.name("overwrite")), on);
    }

    private void goToCSVParsersPage() {
        driver.findElement(By.linkText("Data")).click();
        driver.findElement(By.linkText("CSV Parsers")).click();
        CommonAssertions.assertPageUrl(url + "/csv/configs", driver.getCurrentUrl());
    }

    private void goToCSVParsersImportPage() {
        goToCSVParsersPage();
        driver.findElement(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        driver.findElement(By.xpath("//*/a[text()='Import']")).click();
        CommonAssertions.assertPageUrl(url + "/csv/configs/import", driver.getCurrentUrl());
    }

    private void setCheckbox(WebElement webElement, boolean on) {
        if (on != webElement.isSelected()) {
            webElement.click();
        }
    }

}
