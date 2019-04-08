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
    public void testImportCSPParserPage() {
        try {
            setReplaceExisting(false);
            sendParserToTable(PATH_TO_PARSER);

            goToCSVParsersPage();
            assertTrue("Parser is not added",
                    driver.findElement(By.xpath("//*[@id='configurationList']/tbody")).getText().contains(PARSER_NAME));
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
            sendParserToTable(PATH_TO_PARSER);
            setReplaceExisting(true);
            sendParserToTable(PATH_TO_PARSER);

            goToCSVParsersPage();
            assertTrue("Parser is not added",
                    driver.findElement(By.xpath("//*[@id='configurationList']/tbody")).getText().contains(PARSER_NAME));
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
                driver.findElement(By.id("configurationList")).findElements(By.xpath("./tbody/tr")).size());
    }


    private void deleteParsers() {
        setCheckbox(driver.findElement(By.xpath("//*/input[@title='Select all']")), true);
        driver.findElement(By.xpath("//*/button/span[@class='caret']")).click();
        driver.findElement(By.xpath("//*/input[@type='submit' and @value='Delete']")).click();

        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='confirm-modal']/div/button[contains(text(), 'Yes')]")));

        driver.findElement(By.xpath("//*[@id='confirm-modal']/div/button[contains(text(), 'Yes')]")).click();
    }


    private void sendParserToTable(String file) {
        WebElement putTable = driver.findElement(By.id("putTable"));
        WebElement inputFile = putTable.findElement(By.xpath(".//input[@type='file']"));
        WebElement submitButton = driver.findElement(By.xpath(".//input[@type='submit']"));

        inputFile.sendKeys(file);
        submitButton.click();

        assertFalse("No success message",
                driver.findElements(By.xpath("//*/span[@class='successMessage']")).isEmpty());
    }

    private void setReplaceExisting(boolean on) {
        setCheckbox(driver.findElement(By.xpath("*//input[@name='overwrite']")), on);
    }

    private void goToCSVParsersPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/a[contains(text(),'CSV Parsers')]")).click();
        assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs");
    }

    private void goToCSVParsersImportPage() {
        goToCSVParsersPage();

        driver.findElement(By.xpath("//*/button/span[@class='caret']")).click();
        driver.findElement(By.xpath("//*/a[text()='Import']")).click();
        assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs/import");
    }

    private void setCheckbox(WebElement webElement, boolean on) {
        if (on != webElement.isSelected()) {
            webElement.click();
        }
    }
}
