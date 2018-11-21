package com.axibase.webtest.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ReplacementTablesTest extends AtsdTest {

    private String[][] expectedResult() {
        return new String[][]{
                {"data-availability-json", "JSON", "Tommy Crow"},
                {"graphql-queries", "GRAPHQL", "Tommy Crow"},
                {"stickers", "LIST", ""},
                {"test-after-new-editor-release-1", "SQL", "Tory Eagle"},
                {"test-text", "TEXT", "Tony Bluejay"}
        };
    }

    @Before
    public void setUp() {
        login();
    }

    @Test
    public void testImportDataImportPage() {
        goToReplacementTablesPage();

        goToReplacementTablesImportPage();

        driver.findElement(By.xpath("//*[@id=\"putTable\"]//*/input[@type='file' and @name='file']")).sendKeys(replacementXml);
        WebElement checkBoxUpdate = driver.findElement(By.xpath("//*[@id=\"putTable\"]//*/input[@type='checkbox' and @name='replace']"));
        if (checkBoxUpdate.isSelected())
            checkBoxUpdate.click();
        driver.findElement(By.xpath("//*/input[@type='submit' and @name='send']")).click();
        Assert.assertTrue("No success message",
                driver.findElements(By.xpath("//*/span[@class='successMessage']")).size() != 0);

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable")), expectedResult()));

        deleteReplacementTables();
    }

    @Test
    public void testImportDataImportBackupPage() {
        goToAdminImportBackupPage();

        ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('multiple')",
                driver.findElement(By.xpath("//*/table[@id=\"putTable\"]//*/input[@type='file']")));

        driver.findElement(By.xpath("//*/table[@id=\"putTable\"]//*/input[@type='file']")).sendKeys(replacementXml);
        driver.findElement(By.xpath("//*/input[@type='submit' and @name='send']")).click();

        goToReplacementTablesPage();
        Assert.assertTrue("Wrong table content",
                checkTable(driver.findElement(By.id("overviewTable")), expectedResult()));

        deleteReplacementTables();
    }

    private void goToReplacementTablesPage() {
        driver.findElement(By.xpath("//*/a/span[contains(text(),'Data')]")).click();
        driver.findElement(By.xpath("//*/ul/li/a[contains(text(),'Replacement Tables')]")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/replacement-tables/");
    }

    private void goToAdminImportBackupPage() {
        Actions action = new Actions(driver);
        action.moveToElement(
                driver.findElement(By.xpath("//*/a/span[contains(text(),'Settings')]"))).click().
                moveToElement(driver.findElement(By.xpath("//*/ul/li/a[contains(text(),'Diagnostics')]"))).
                moveToElement(driver.findElement(By.xpath("//*/ul/li/a[contains(text(),'Backup Import')]"))).
                click().build().perform();

        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/admin/import-backup");
    }

    private void goToReplacementTablesImportPage() {
        driver.findElement(By.xpath("//*/form//*/button/span")).click();
        driver.findElement(By.xpath("//*/form//*/ul/li/a[contains(text(),'Import')]")).click();
        Assert.assertEquals("Wrong page", driver.getCurrentUrl(), url + "/replacement-tables/import");
    }

    private void deleteReplacementTables() {
        setCheckbox(driver.findElement(By.xpath("//*[@id=\"overviewTable\"]//*/input[@type='checkbox' and @title='Select All']")), true);
        driver.findElement(By.xpath("//*/form//*/button/span")).click();
        driver.findElement(By.xpath("//*/form//*/ul/li/input[@type='submit' and @value='Delete']")).click();

        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"confirm-modal\"]/div/button[contains(text(), 'Yes')]")));

        driver.findElement(By.xpath("//*[@id=\"confirm-modal\"]/div/button[contains(text(), 'Yes')]")).click();
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
}
