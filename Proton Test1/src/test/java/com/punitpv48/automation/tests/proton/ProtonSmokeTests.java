package com.punitpv48.automation.tests.proton;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.config.Config;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.ProtonLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"smoke", "proton"})
public class ProtonSmokeTests extends BaseWebTest {
    @Test
    public void validCredentialsOpenMailbox() {
        String username = Config.get("protonUsername");
        String password = Config.get("protonPassword");
        ProtonLoginPage loginPage = new ProtonLoginPage(DriverFactory.getDriver()).open();

        loginPage.signIn(username, password);

        Assert.assertTrue(loginPage.waitForInbox(),
                "Expected Proton Mail inbox route; observed path: " + loginPage.currentPath()
                        + ". MFA, CAPTCHA, or another security prompt is not automated or bypassed.");
    }
    }
