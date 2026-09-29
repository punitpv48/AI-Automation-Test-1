package com.punitpv48.automation.tests.smoke;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.CartPage;
import com.punitpv48.automation.pages.InventoryPage;
import com.punitpv48.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"smoke", "web"})
public class SmokeTests extends BaseWebTest {
    public void userCanNavigateAndAddAnItemToCart() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver()).open();
        loginPage.login("standard_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(DriverFactory.getDriver());
        Assert.assertEquals(inventoryPage.title(), "Products", "Inventory page should open after login.");
        Assert.assertTrue(inventoryPage.productNames().size() > 0, "Inventory should contain products.");
        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.cartItemCount(), "1", "Cart badge should reflect the added item.");

        CartPage cartPage = inventoryPage.openCart();
        Assert.assertEquals(cartPage.title(), "Your Cart");
        Assert.assertTrue(cartPage.itemNames().contains("Sauce Labs Backpack"));
    }
}
