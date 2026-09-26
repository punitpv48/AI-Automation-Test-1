package com.punitpv48.automation.tests.regression;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.CartPage;
import com.punitpv48.automation.pages.InventoryPage;
import com.punitpv48.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"regression", "web"})
public class InventoryRegressionTests extends BaseWebTest {
    @Test
    public void priceSortOrdersProductsFromLowToHigh() {
        InventoryPage inventoryPage = openInventory();
        inventoryPage.sortBy("lohi");

        List<String> prices = inventoryPage.productPrices();
        Assert.assertFalse(prices.isEmpty(), "Sorted inventory should contain prices.");
        Assert.assertEquals(prices.get(0), "$7.99", "The cheapest product should appear first.");
    }

    @Test
    public void cartItemCanBeRemoved() {
        InventoryPage inventoryPage = openInventory();
        inventoryPage.addBackpackToCart();
        CartPage cartPage = inventoryPage.openCart();

        Assert.assertTrue(cartPage.itemNames().contains("Sauce Labs Backpack"));
        cartPage.removeBackpack();
        Assert.assertTrue(cartPage.itemNames().isEmpty(), "Cart should be empty after removing its only item.");
    }

    private InventoryPage openInventory() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver()).open();
        loginPage.login("standard_user", "secret_sauce");
        return new InventoryPage(DriverFactory.getDriver());
    }
}
