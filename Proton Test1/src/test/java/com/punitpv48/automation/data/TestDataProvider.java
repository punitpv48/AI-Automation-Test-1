package com.punitpv48.automation.data;

import org.testng.annotations.DataProvider;

public final class TestDataProvider {
    private TestDataProvider() {
    }

    @DataProvider(name = "loginScenarios", parallel = true)
    public static Object[][] loginScenarios() {
        return new Object[][]{
                {"standard_user", "secret_sauce", true, ""},
                {"standard_user", "invalid_password", false, "Username and password do not match"},
                {"locked_out_user", "secret_sauce", false, "locked out"}
        };
    }
}
