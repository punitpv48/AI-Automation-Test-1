package com.punitpv48.automation.pages;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.get("baseUrl"));
        visible(USERNAME);
        return this;
    }

    public void login(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    public boolean hasError() {
        return isVisible(ERROR_MESSAGE);
    }

    public String errorMessage() {
        return text(ERROR_MESSAGE);
    }
}
