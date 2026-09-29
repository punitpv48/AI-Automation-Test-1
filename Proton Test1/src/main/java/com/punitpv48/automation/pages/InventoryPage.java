package com.punitpv48.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class InventoryPage extends BasePage {
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By PRODUCT_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRODUCT_PRICES = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By ADD_BACKPACK = By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return text(TITLE);
    }

    public List<String> productNames() {
        return texts(PRODUCT_NAMES);
    }

    public List<String> productPrices() {
        return texts(PRODUCT_PRICES);
    }

    public void sortBy(String optionValue) {
        selectByValue(SORT, optionValue);
    }

    public void addBackpackToCart() {
        click(ADD_BACKPACK);
    }

    public String cartItemCount() {
        return text(By.cssSelector(".shopping_cart_badge"));
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }
}
