package com.axibase.webtest.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ReplacementTablesImportTest extends AtsdTest {

    private static String fileXml;

    @BeforeClass
    public static void initProperties() throws IOException {
        Properties testProperties = new Properties();
        testProperties.load(new FileInputStream(new File(AdminBackupImportTest.class.getClassLoader().getResource("test.properties").getFile())));

        fileXml = testProperties.getProperty("file_xml");
    }

    @Before
    public void setUp() {
        login();
    }

    @Test
    public void testImportDataImportPage() {
        goToReplacementTablesImportPage();

        sendFilesFromReplacementTable(false, fileXml);

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable")), expectedResult()));

        deleteReplacementTables();
    }

    @Test
    public void testImportDataWithReplaceImportPage() {
        goToReplacementTablesImportPage();

        sendFilesFromReplacementTable(false, fileXml);
        sendFilesFromReplacementTable(true, fileXml);

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable")), expectedResult()));

        deleteReplacementTables();
    }


    private void sendFilesFromReplacementTable(boolean replaceExisting, String file) {
        WebElement putTable = driver.findElement(By.id("putTable"));
        WebElement inputFile = putTable.findElement(By.xpath(".//input[@type='file']"));
        WebElement submitButton = driver.findElement(By.xpath(".//input[@type='submit']"));

        setCheckbox(putTable.findElement(By.xpath("*//input[@name='replace']")), replaceExisting);
        inputFile.sendKeys(file);
        submitButton.click();

        Assert.assertTrue("No success message",
                driver.findElements(By.xpath("//*/span[@class='successMessage']")).size() != 0);
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
        if (on) {
            if (!webElement.isSelected())
                webElement.click();
        } else if (webElement.isSelected())
            webElement.click();
    }

    private boolean checkTable(WebElement table, String[][] expectedResult) {
        List<WebElement> findElements = table.findElements(By.xpath("./tbody/tr"));
        if (findElements.size() != expectedResult.length)
            return false;

        for (int i = 0; i < findElements.size(); i++) {
            List<WebElement> tdList = findElements.get(i).findElements(By.xpath("./td"));
            if (!tdList.get(0).getAttribute("class").equals("select-field") &&
                    !tdList.get(1).getText().equals(expectedResult[i][0]) &&
                    !tdList.get(2).getText().equals(expectedResult[i][1]) &&
                    !tdList.get(3).getText().equals(expectedResult[i][2]))
                return false;
        }

        return true;
    }

    private String[][] expectedResult() {
        return new String[][]{
                {"data-availability-json", "JSON", "Tommy Crow"},
                {"graphql-queries", "GRAPHQL", "Tommy Crow"},
                {"stickers", "LIST", ""},
                {"test-after-new-editor-release-1", "SQL", "Tory Eagle"},
                {"test-text", "TEXT", "Tony Bluejay"}
        };
    }
}
