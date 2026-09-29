package com.punitpv48.automation.tests.proton;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.ProtonLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"regression", "proton"})
public class ProtonRegressionTests extends BaseWebTest {
    @Test
    public void signInFormShowsExpectedControls() {
        ProtonLoginPage loginPage = new ProtonLoginPage(DriverFactory.getDriver()).open();

        Assert.assertTrue(loginPage.signInFormIsVisible(),
                "Proton sign-in page should show username, password, and submit controls.");
    }
    }
