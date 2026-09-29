package com.punitpv48.automation.base;

import com.punitpv48.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import java.io.IOException;

public abstract class BaseTest {
    @AfterMethod(alwaysRun = true)
    public void closeBrowserAndCaptureFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriverOrNull();
        if (driver == null) {
            return;
        }
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                try {
                    result.setAttribute("screenshotPath",
                            DriverFactory.captureScreenshot(result.getMethod().getMethodName()));
                } catch (IOException | RuntimeException screenshotException) {
                    result.setAttribute("screenshotError", screenshotException.getMessage());
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
