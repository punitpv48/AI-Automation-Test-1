package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.getInt("explicitWaitSeconds")));
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String value) {
        WebElement input = visible(locator);
        input.clear();
        input.sendKeys(value);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected List<String> texts(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator))
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    protected boolean isVisible(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    protected void selectByValue(By locator, String value) {
        new Select(visible(locator)).selectByValue(value);
    }
}
