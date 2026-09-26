package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.Set;

public class ProtonLoginPage extends BasePage {
    private static final By SIGN_IN_HEADING = By.xpath("//h1[normalize-space()='Sign in']");
    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By SIGN_IN_BUTTON = By.cssSelector("button[type='submit']");
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

    public void signIn(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(SIGN_IN_BUTTON);
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
}
