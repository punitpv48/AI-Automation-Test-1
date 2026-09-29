package com.punitpv48.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtentReportListener implements ITestListener, ISuiteListener {
    private static final ExtentReports REPORT = createReport();
    private static final ConcurrentMap<ITestResult, ExtentTest> TESTS = new ConcurrentHashMap<>();
    private static final AtomicInteger PASSED = new AtomicInteger();
    private static final AtomicInteger FAILED = new AtomicInteger();
    private static final AtomicInteger SKIPPED = new AtomicInteger();

    private static ExtentReports createReport() {
        try {
            Files.createDirectories(Paths.get("target"));
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report.html");
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("UI and API Test Results");
            spark.config().setTheme(Theme.STANDARD);
            ExtentReports reports = new ExtentReports();
            reports.attachReporter(spark);
            reports.setSystemInfo("Framework", "Selenium, TestNG, Rest Assured");
            return reports;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public void onStart(ISuite suite) {
        REPORT.setSystemInfo("Suite", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        int total = PASSED.get() + FAILED.get() + SKIPPED.get();
        double passPercentage = total == 0 ? 0.0 : (PASSED.get() * 100.0) / total;
        REPORT.setSystemInfo("Passed", Integer.toString(PASSED.get()));
        REPORT.setSystemInfo("Failed", Integer.toString(FAILED.get()));
        REPORT.setSystemInfo("Skipped", Integer.toString(SKIPPED.get()));
        REPORT.setSystemInfo("Pass percentage", String.format(Locale.ROOT, "%.2f%%", passPercentage));
        REPORT.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        if (result.getParameters().length > 0) {
            testName += " " + Arrays.deepToString(result.getParameters());
        }
        ExtentTest test = REPORT.createTest(testName);
        test.assignCategory(result.getTestContext().getSuite().getName());
        TESTS.put(result, test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        PASSED.incrementAndGet();
        testFor(result).pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        FAILED.incrementAndGet();
        ExtentTest test = testFor(result);
        Throwable failure = result.getThrowable();
        if (failure == null) {
            test.fail("Test failed without an exception message.");
        } else {
            test.fail(failure);
        }
        Object screenshotPath = result.getAttribute("screenshotPath");
        if (screenshotPath instanceof String path && Files.exists(Path.of(path))) {
            test.addScreenCaptureFromPath(path);
        }
        Object screenshotError = result.getAttribute("screenshotError");
        if (screenshotError != null) {
            test.warning("Screenshot capture failed: " + screenshotError);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        SKIPPED.incrementAndGet();
        Throwable failure = result.getThrowable();
        ExtentTest test = testFor(result);
        if (failure == null) {
            test.skip("Test skipped");
        } else {
            test.skip(failure);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        REPORT.setSystemInfo("Test", context.getName());
    }

    private ExtentTest testFor(ITestResult result) {
        return TESTS.computeIfAbsent(result, ignored -> REPORT.createTest(result.getMethod().getMethodName()));
    }
}
