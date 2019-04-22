package com.axibase.webtest.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CSVDataUploaderService extends Service {
    private static final String PARSER_IMPORT_PATH = "/csv/configs/import";
    private static final String CSV_IMPORT_PATH = "/data/csv/parsers/parser/";
    private static final String PARSERS_TABLE_PATH = "/csv/configs";

    private Set<String> uploadedParsers = new HashSet<>();
    private String startUrl;

    public CSVDataUploaderService(WebDriver driver, String url) {
        super(driver);
        startUrl = url;
        getUploadedParsers(driver);
    }

    public void uploadParser(String fullPathToParser, String parserName) {
        uploadParserByPath(fullPathToParser);
        uploadedParsers.add(parserName);
    }

    public void uploadWithParser(String pathToCSV, String parserName) {
        uploadParserByName(parserName);
        uploadCSVFileByPath(pathToCSV, parserName);
    }

    private void getUploadedParsers(WebDriver driver) {
        driver.get(startUrl + PARSERS_TABLE_PATH);
        List<WebElement> parsers = driver.findElements(By.xpath("//*/tbody/tr"));
        for (WebElement parser : parsers) {
            uploadedParsers.add(parser.findElement(By.xpath("td/a")).getText());
        }
    }

    private void uploadParserByName(String parserName) {
        if (!uploadedParsers.contains(parserName)) {
            String pathToParser = getPathToXMLFileByName(parserName);
            uploadParser(pathToParser, parserName);
        }
    }

    private String getPathToXMLFileByName(String parserName) {
        return CSVDataUploaderService.class.getResource(parserName + ".xml").getFile();
    }

    private void uploadParserByPath(String pathToParser) {
        driver.get(startUrl + PARSER_IMPORT_PATH);
        driver.findElement(By.name("file")).sendKeys(pathToParser);
        driver.findElement(By.xpath(".//input[@type='submit']")).click();
    }

    private void uploadCSVFileByPath(String pathToCSVFile, String parserName) {
        driver.get(startUrl + CSV_IMPORT_PATH + parserName);
        driver.findElement(By.id("file")).sendKeys(pathToCSVFile);
        driver.findElement(By.id("upload-btn")).click();
    }

}
