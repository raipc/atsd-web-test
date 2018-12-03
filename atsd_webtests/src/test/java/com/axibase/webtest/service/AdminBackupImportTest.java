package com.axibase.webtest.service;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(value = Parameterized.class)
public class AdminBackupImportTest extends AtsdTest {

    private String testFile;
    private String[][] expectedResult = {
            {"data-availability-json", "JSON", "Tommy Crow"},
            {"graphql-queries", "GRAPHQL", "Tommy Crow"},
            {"stickers", "LIST", ""},
            {"test-after-new-editor-release-1", "SQL", "Tory Eagle"},
            {"test-text", "TEXT", "Tony Bluejay"}};

    public AdminBackupImportTest(String file) {
        testFile = file;
    }

    @Parameterized.Parameters(name = "{index} {0}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {AdminBackupImportTest.class.getResource("replacement-table" + File.separator + "xml-file.xml").getFile()},
                {AdminBackupImportTest.class.getResource("replacement-table" + File.separator + "zip-archive.zip").getFile()},
                {AdminBackupImportTest.class.getResource("replacement-table" + File.separator + "tar-archive.tar").getFile()},
                {AdminBackupImportTest.class.getResource("replacement-table" + File.separator + "bz2-archive.tar.bz2").getFile()},
                {AdminBackupImportTest.class.getResource("replacement-table" + File.separator + "gz-archive.tar.gz").getFile()}};

        return Arrays.asList(data);
    }

    @Before
    public void setUp() {
        login();
        goToAdminImportBackupPage();
    }

    @Test
    public void testImportDataImportBackupPage() {
        setReplaceExisting(false);
        setAutoEnable(false);
        sendFilesOnAdminImportBackup(testFile);

        goToReplacementTablesPage();

        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable"))));
    }

    @Test
    public void testImportDataImportBackupPageWithReplace() {
        setReplaceExisting(false);
        setAutoEnable(false);
        sendFilesOnAdminImportBackup(testFile);

        setReplaceExisting(true);
        setAutoEnable(false);
        sendFilesOnAdminImportBackup(testFile);

        goToReplacementTablesPage();

        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable"))));
    }

    @Test
    public void testImportDataImportBackupPageWithAutoEnable() {
        setReplaceExisting(false);
        setAutoEnable(true);
        sendFilesOnAdminImportBackup(testFile);

        goToReplacementTablesPage();

        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable"))));
    }

    @After
    public void cleanUp() {
        deleteReplacementTables();
    }

    private void sendFilesOnAdminImportBackup(String file) {
        WebElement putTable = driver.findElement(By.id("putTable"));
        WebElement inputFile = putTable.findElement(By.xpath(".//input[@type='file']"));
        WebElement submitButton = driver.findElement(By.xpath(".//input[@type='submit']"));

        removeMultipleTag(inputFile);
        inputFile.sendKeys(file);
        submitButton.click();
    }

    // This function was created in need to avoid PhantomJS  multiple input bug
    // Function perform javascript on page to remove 'multiple' attribute from input element
    private void removeMultipleTag(WebElement inputFile) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('multiple')", inputFile);
    }

    private void setReplaceExisting(boolean on) {
        setCheckbox(driver.findElement(By.id("replaceExisting")), on);
    }

    private void setAutoEnable(boolean on) {
        setCheckbox(driver.findElement(By.id("autoEnable")), on);
    }

    private void goToReplacementTablesPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/a[contains(text(),'Replacement Tables')]")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/replacement-tables/");
    }

    private void goToAdminImportBackupPage() {
        Actions action = new Actions(driver);

        action.moveToElement(driver.findElement(By.xpath("//*/a/span[contains(text(),'Settings')]"))).click();
        action.moveToElement(driver.findElement(By.xpath("//*/a[contains(text(),'Diagnostics')]")));
        action.moveToElement(driver.findElement(By.xpath("//*/a[contains(text(),'Backup Import')]"))).click();
        action.build().perform();

        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/admin/import-backup");
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
                    !tdList.get(3).getText().equals(expectedResult[i][2]))
                return false;
        }

        return true;
    }
}
