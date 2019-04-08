package com.axibase.webtest.service;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;

public class ReplacementTablesImportTest extends AtsdTest {

    private static String fileXml = ReplacementTablesImportTest.class.getResource("replacement-table" + File.separator + "xml-file.xml").getFile();

    private String[][] expectedResult = {
            {"data-availability-json", "JSON", "Tommy Crow"},
            {"graphql-queries", "GRAPHQL", "Tommy Crow"},
            {"stickers", "LIST", ""},
            {"test-after-new-editor-release-1", "SQL", "Tory Eagle"},
            {"test-text", "TEXT", "Tony Bluejay"}};

    @Before
    public void setUp() {
        login();
        goToReplacementTablesImportPage();
    }

    @Test
    public void testImportDataImportPage() {
        setReplaceExisting(false);
        sendFilesFromReplacementTable(fileXml);

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable"))));
    }

    @Test
    public void testImportDataWithReplaceImportPage() {
        setReplaceExisting(false);
        sendFilesFromReplacementTable(fileXml);
        setReplaceExisting(true);
        sendFilesFromReplacementTable(fileXml);

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable"))));
    }

    @After
    public void cleanUp() {
        deleteReplacementTables();
    }

    private void sendFilesFromReplacementTable(String file) {
        WebElement putTable = driver.findElement(By.id("putTable"));
        WebElement inputFile = putTable.findElement(By.xpath(".//input[@type='file']"));
        WebElement submitButton = driver.findElement(By.xpath(".//input[@type='submit']"));

        inputFile.sendKeys(file);
        submitButton.click();

        Assert.assertTrue("No success message",
                driver.findElements(By.xpath("//*/span[@class='successMessage']")).size() != 0);
    }

    private void setReplaceExisting(boolean on) {
        setCheckbox(driver.findElement(By.xpath("*//input[@name='replace']")), on);
    }

    private void goToReplacementTablesPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/a[contains(text(),'Replacement Tables')]")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/replacement-tables/");
    }

    private void goToReplacementTablesImportPage() {
        goToReplacementTablesPage();

        driver.findElement(By.xpath("//*/button/span[@class='caret']")).click();
        driver.findElement(By.xpath("//*/a[text()='Import']")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/replacement-tables/import");
    }

    private void deleteReplacementTables() {
        setCheckbox(driver.findElement(By.xpath("//*/input[@title='Select All']")), true);
        driver.findElement(By.xpath("//*/button/span[@class='caret']")).click();
        driver.findElement(By.xpath("//*/input[@type='submit' and @value='Delete']")).click();

        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"confirm-modal\"]/div/button[contains(text(), 'Yes')]")));

        driver.findElement(By.xpath("//*[@id='confirm-modal']/div/button[contains(text(), 'Yes')]")).click();
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
