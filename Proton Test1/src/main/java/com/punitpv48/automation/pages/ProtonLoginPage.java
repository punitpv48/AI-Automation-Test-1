package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ProtonLoginPage extends BasePage {
    private static final By SIGN_IN_HEADING = By.xpath("//h1[normalize-space()='Sign in']");
    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By SIGN_IN_BUTTON = By.cssSelector("button[type='submit']");
        private static final By KEEP_SIGNED_IN_LABEL = By.xpath(
            "//label[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'keep me signed in')]");
        private static final By LABELED_KEEP_CONTROL = By.xpath(
            "//*[@role='checkbox' and contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'keep me signed in')]");
    private static final Set<String> ALLOWED_HOSTS = Set.of("mail.proton.me", "account.proton.me");

    public ProtonLoginPage(WebDriver driver) {
        super(driver);
    }

    public ProtonLoginPage open() {
        String baseUrl = Config.get("protonBaseUrl");
        URI uri = URI.create(baseUrl);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || !ALLOWED_HOSTS.contains(uri.getHost())) {
            throw new IllegalArgumentException("Proton base URL must use HTTPS on an official Proton sign-in host.");
        }
        driver.get(baseUrl);
        visible(SIGN_IN_HEADING);
        visible(USERNAME);
        visible(PASSWORD);
        visible(SIGN_IN_BUTTON);
        return this;
    }

    public boolean signInFormIsVisible() {
        return isVisible(SIGN_IN_HEADING)
                && isVisible(USERNAME)
                && isVisible(PASSWORD)
                && isVisible(SIGN_IN_BUTTON);
    }

    public void enterUsername(String username) {
        type(USERNAME, username);
    }

    public void enterPassword(String password) {
        type(PASSWORD, password);
    }

    public void checkKeepMeSignedIn() {
        WebElement control;
        WebElement label = null;
        try {
            label = wait.until(driver -> {
                List<WebElement> labels = driver.findElements(KEEP_SIGNED_IN_LABEL);
                return labels.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
            });
        } catch (TimeoutException ignored) {
            control = wait.until(driver -> {
                List<WebElement> controls = driver.findElements(LABELED_KEEP_CONTROL);
                return controls.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
            });
            clickIfNotSelected(control);
            if (!isSelected(control)) {
                throw new IllegalStateException("Keep me signed in could not be selected.");
            }
            return;
        }

        List<WebElement> nestedControls = label.findElements(By.cssSelector("input[type='checkbox'], [role='checkbox']"));
        if (!nestedControls.isEmpty()) {
            control = nestedControls.get(0);
            clickIfNotSelected(control);
            if (!isSelected(control)) {
                label.click();
            }
            if (!isSelected(control)) {
                throw new IllegalStateException("Keep me signed in could not be selected.");
            }
            return;
        }

        String controlId = label.getAttribute("for");
        if (controlId != null && !controlId.isBlank()) {
            List<WebElement> associatedControls = driver.findElements(By.id(controlId));
            if (!associatedControls.isEmpty()) {
                control = associatedControls.get(0);
                clickIfNotSelected(control);
                if (!isSelected(control)) {
                    throw new IllegalStateException("Keep me signed in could not be selected.");
                }
                return;
            }
        }

        label.click();
        if (!"true".equalsIgnoreCase(label.getAttribute("aria-checked"))) {
            throw new IllegalStateException("Keep me signed in control has no verifiable selected state.");
        }
    }

    public void clickSignIn() {
        click(SIGN_IN_BUTTON);
    }

    public void signIn(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
    }

    public boolean waitForInbox() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(Config.getInt("protonLoginTimeoutSeconds")))
                    .until(currentDriver -> {
                        URI currentUri = URI.create(currentDriver.getCurrentUrl());
                        return "https".equalsIgnoreCase(currentUri.getScheme())
                            && "mail.proton.me".equalsIgnoreCase(currentUri.getHost())
                            && currentUri.getPath().matches("/u/\\d+/inbox/?");
                    });
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public String currentPath() {
        return URI.create(driver.getCurrentUrl()).getPath();
    }

    private void clickIfNotSelected(WebElement control) {
        if (!isSelected(control)) {
            control.click();
        }
    }

    private boolean isSelected(WebElement control) {
        String ariaChecked = control.getAttribute("aria-checked");
        return control.isSelected() || "true".equalsIgnoreCase(ariaChecked);
    }
}
