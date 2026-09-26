package com.punitpv48.automation.tests.regression;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.data.TestDataProvider;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.InventoryPage;
import com.punitpv48.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"regression", "web"})
public class LoginRegressionTests extends BaseWebTest {
    @Test(dataProvider = "loginScenarios", dataProviderClass = TestDataProvider.class)
    public void loginBehaviorMatchesCredentials(String username, String password, boolean expectedSuccess,
                                                String expectedError) {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver()).open();
        loginPage.login(username, password);

        if (expectedSuccess) {
            Assert.assertEquals(new InventoryPage(DriverFactory.getDriver()).title(), "Products");
        } else {
            Assert.assertTrue(loginPage.hasError(), "An error should be displayed for rejected credentials.");
            Assert.assertTrue(loginPage.errorMessage().toLowerCase().contains(expectedError.toLowerCase()),
                    "The error should explain why login was rejected.");
        }
    }
}
