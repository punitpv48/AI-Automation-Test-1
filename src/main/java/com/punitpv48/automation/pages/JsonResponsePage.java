package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JsonResponsePage extends BasePage {
    public JsonResponsePage(WebDriver driver) {
        super(driver);
    }

    public String openAndReadJson() {
        driver.get(Config.get("uiApiUrl"));
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return document.body.innerText;");
    }
}
