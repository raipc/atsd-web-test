package com.axibase.webtest.service.csv;

import com.axibase.webtest.service.AtsdTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CSVImportParserAsSeriesTest extends AtsdTest {

    private static String fileXml = CSVImportParserAsSeriesTest.class.getResource("csv-parsers.xml").getFile();

    private String[][] expectedResult = {{"csv2-parser-test", "Yes", "Series"}};

    @Before
    public void setUp() {
        login();
        goToCSVParsersImportPage();
    }

    @Test
    public void testImportCSPParserPage() {
        setReplaceExisting(false);
        sendParserToTable(fileXml);

        goToCSVParsersPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("configurationList"))));
    }

    @Test
    public void testImportCSPParserWithReplace() {
        setReplaceExisting(false);
        sendParserToTable(fileXml);
        setReplaceExisting(true);
        sendParserToTable(fileXml);

        goToCSVParsersPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("configurationList"))));
    }

    @After
    public void cleanUp() {
        deleteParsers();
        Assert.assertEquals("Parsers are not removed", 0,
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

        Assert.assertTrue("No success message",
                driver.findElements(By.xpath("//*/span[@class='successMessage']")).size() != 0);
    }

    private void setReplaceExisting(boolean on) {
        setCheckbox(driver.findElement(By.xpath("*//input[@name='overwrite']")), on);
    }

    private void goToCSVParsersPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/a[contains(text(),'CSV Parsers')]")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs");
    }

    private void goToCSVParsersImportPage() {
        goToCSVParsersPage();

        driver.findElement(By.xpath("//*/button/span[@class='caret']")).click();
        driver.findElement(By.xpath("//*/a[text()='Import']")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/csv/configs/import");
    }

    private void setCheckbox(WebElement webElement, boolean on) {
        if (on != webElement.isSelected()) {
            webElement.click();
        }
    }

    private boolean checkTable(WebElement table) {
        List<WebElement> findElements = table.findElements(By.xpath("./tbody/tr"));
        if (findElements.size() != expectedResult.length) {
            return false;
        }

        for (int i = 0; i < findElements.size(); i++) {
            List<WebElement> tdList = findElements.get(i).findElements(By.xpath("./td"));
            if (!tdList.get(1).getText().equals(expectedResult[i][0]) &&
                    !tdList.get(2).getText().equals(expectedResult[i][1]) &&
                    !tdList.get(3).getText().equals(expectedResult[i][2])) {
                return false;
            }
        }

        return true;
    }

}
