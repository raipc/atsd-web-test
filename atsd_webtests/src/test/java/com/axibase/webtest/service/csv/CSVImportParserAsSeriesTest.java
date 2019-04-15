package com.axibase.webtest.service.csv;

import com.axibase.webtest.service.AtsdTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;

public class CSVImportParserAsSeriesTest extends AtsdTest {
    private static final String PARSER_NAME = "test-atsd-import-series-parser";
    private static final String PATH_TO_PARSER = CSVImportParserAsSeriesTest.class.getResource("test-atsd-import-series-parser.xml").getFile();


    @Before
    public void setUp() {
        login();
        goToCSVParsersImportPage();
    }

    @Test
    public void testImportCSVParserPage() {
        try {
            setReplaceExisting(false);
            sendParserIntoTableWithoutReplacement(PATH_TO_PARSER);

            goToCSVParsersPage();
            assertTrue("Parser is not added into table",
                    driver.findElement(By.cssSelector("#configurationList > tbody")).getText().contains(PARSER_NAME));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @Test
    public void testImportCSVParserWithReplace() {
        try {
            setReplaceExisting(false);
            sendParserIntoTableWithoutReplacement(PATH_TO_PARSER);
            setReplaceExisting(true);
            sendParserIntoTableWithReplacement(PATH_TO_PARSER);

            goToCSVParsersPage();
            assertTrue("Parser is not added into table",
                    driver.findElement(By.cssSelector("#configurationList > tbody")).getText().contains(PARSER_NAME));
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            throw err;
        }
    }

    @After
    public void cleanUp() {
        deleteParsers();
        assertEquals("Parsers are not removed", 0,
                driver.findElements(By.cssSelector("#configurationList > tbody > tr")).size());
    }

    private void deleteParsers() {
        setCheckbox(driver.findElement(By.xpath("//*/input[@title='Select all']")), true);
        driver.findElement(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        driver.findElement(By.xpath("//*/input[@type='submit' and @value='Delete']")).click();

        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirm-modal']//button[contains(text(), 'Yes')]")));

        driver.findElement(By.xpath("//*[@id='confirm-modal']//button[contains(text(), 'Yes')]")).click();
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
        assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs");
    }

    private void goToCSVParsersImportPage() {
        goToCSVParsersPage();
        driver.findElement(By.xpath("//*/button[@data-toggle='dropdown']")).click();
        driver.findElement(By.xpath("//*/a[text()='Import']")).click();
        assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs/import");
    }

    private void setCheckbox(WebElement webElement, boolean on) {
        if (on != webElement.isSelected()) {
            webElement.click();
        }
    }
}
