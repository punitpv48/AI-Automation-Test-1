package com.punitpv48.automation.base;

import com.punitpv48.automation.driver.DriverFactory;
import org.testng.annotations.BeforeMethod;

public abstract class BaseWebTest extends BaseTest {
    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.startDriver();
    }
}
