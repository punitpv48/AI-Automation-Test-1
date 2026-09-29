package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.List;

public class ProtonComposePage extends BasePage {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final By NEW_MESSAGE = By.xpath(
            "//button[contains(translate(normalize-space(.), '" + UPPER + "', '" + LOWER + "'), 'new message')"
                    + " or contains(translate(@aria-label, '" + UPPER + "', '" + LOWER + "'), 'new message')"
                    + " or contains(translate(@title, '" + UPPER + "', '" + LOWER + "'), 'new message')]"
    );
    private static final List<By> RECIPIENT_FIELDS = List.of(
            By.cssSelector("input[placeholder='To']"),
            By.cssSelector("input[aria-label='To']"),
            By.cssSelector("input[placeholder*='recipient']"),
            By.cssSelector("input[aria-label*='recipient']"),
            By.cssSelector("input[placeholder*='email']"),
            By.cssSelector("input[aria-label*='email']")
    );
    private static final List<By> SUBJECT_FIELDS = List.of(
            By.cssSelector("input[placeholder='Subject']"),
            By.cssSelector("input[aria-label='Subject']")
    );
    private static final List<By> BODY_FIELDS = List.of(
            By.cssSelector("[contenteditable='true'][role='textbox']"),
            By.cssSelector("[contenteditable='true']"),
            By.cssSelector("textarea[aria-label*='message']")
    );
    private static final By SEND_BUTTON = By.xpath(
            "//button[normalize-space()='Send' or contains(translate(@aria-label, '" + UPPER + "', '" + LOWER + "'), 'send')]"
    );
    private static final By SENT_CONFIRMATION = By.xpath(
            "//*[contains(translate(normalize-space(.), '" + UPPER + "', '" + LOWER + "'), 'message sent')]"
    );

    public ProtonComposePage(WebDriver driver) {
        super(driver);
    }

    public void openNewMessage() {
        click(NEW_MESSAGE);
    }

    public void enterRecipient(String recipient) {
        WebElement field = visibleAny(RECIPIENT_FIELDS);
        field.sendKeys(recipient);
        field.sendKeys(Keys.ENTER);
    }

    public void enterSubject(String subject) {
        WebElement field = visibleAny(SUBJECT_FIELDS);
        field.clear();
        field.sendKeys(subject);
    }

    public void enterBody(String body) {
        WebElement field = visibleAny(BODY_FIELDS);
        field.click();
        field.sendKeys(body);
    }

    public boolean sendAndWaitForConfirmation() {
        click(SEND_BUTTON);
        try {
            wait.withTimeout(Duration.ofSeconds(Config.getInt("protonComposeTimeoutSeconds")))
                    .until(driver -> driver.findElements(SENT_CONFIRMATION).stream()
                            .anyMatch(WebElement::isDisplayed));
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    private WebElement visibleAny(List<By> locators) {
        return wait.until(driver -> {
            for (By locator : locators) {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                }
            }
            return null;
        });
    }
}