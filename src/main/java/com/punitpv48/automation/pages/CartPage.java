package com.punitpv48.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By REMOVE_BACKPACK = By.cssSelector("[data-test='remove-sauce-labs-backpack']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return text(TITLE);
    }

    public List<String> itemNames() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
        return driver.findElements(ITEM_NAMES).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public void removeBackpack() {
        click(REMOVE_BACKPACK);
        wait.until(ExpectedConditions.numberOfElementsToBe(ITEM_NAMES, 0));
    }
}
