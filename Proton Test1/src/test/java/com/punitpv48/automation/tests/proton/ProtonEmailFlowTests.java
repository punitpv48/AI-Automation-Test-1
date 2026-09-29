package com.punitpv48.automation.tests.proton;

import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.config.Config;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.ProtonComposePage;
import com.punitpv48.automation.pages.ProtonLoginPage;
import com.punitpv48.automation.reports.ProtonEmailWorkbook;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;

@Test(groups = {"proton", "email"})
public class ProtonEmailFlowTests extends BaseWebTest {
    private ProtonEmailWorkbook.Execution execution;

    @Test
    public void signInAndSendTestEmail() {
        execution = ProtonEmailWorkbook.newExecution();
        ProtonLoginPage loginPage = new ProtonLoginPage(DriverFactory.getDriver());

        perform(0, "Proton sign-in page displayed", loginPage::open);
        perform(1, "Configured username entered", () -> loginPage.enterUsername(Config.get("protonUsername")));
        perform(2, "Configured password entered", () -> loginPage.enterPassword(Config.get("protonPassword")));
        perform(3, "Keep me signed in verified checked", loginPage::checkKeepMeSignedIn);
        perform(4, "Proton inbox opened", () -> {
            loginPage.clickSignIn();
            Assert.assertTrue(loginPage.waitForInbox(),
                    "Expected Proton inbox route; observed path: " + loginPage.currentPath());
        });

        ProtonComposePage composePage = new ProtonComposePage(DriverFactory.getDriver());
        perform(5, "New message composer opened", composePage::openNewMessage);
        perform(6, "Recipient added", () -> composePage.enterRecipient(Config.get("mailRecipient")));
        perform(7, "Subject entered", () -> composePage.enterSubject(Config.get("mailSubject")));
        perform(8, "Message body entered", () -> composePage.enterBody(Config.get("mailBody")));
        perform(9, "Message sent confirmation received", () -> Assert.assertTrue(
                composePage.sendAndWaitForConfirmation(), "Proton did not show a message-sent confirmation."));
    }

    @AfterMethod(alwaysRun = true)
    public void writeExcelExecutionReport() throws IOException {
        if (execution != null) {
            Path report = execution.write();
            System.out.println("Email test workbook: " + report);
        }
    }

    private void perform(int step, String successMessage, Runnable action) {
        try {
            action.run();
            execution.passed(step, successMessage);
        } catch (RuntimeException | AssertionError failure) {
            execution.failed(step, failure);
            throw failure;
        }
    }
}