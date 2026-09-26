package com.punitpv48.automation.driver;

import com.punitpv48.automation.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver startDriver() {
        quitDriver();
        String browser = Config.get("browser").toLowerCase(Locale.ROOT);
        boolean headless = Config.getBoolean("headless");
        WebDriver driver = switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().window().maximize();
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No WebDriver is active on this thread.");
        }
        return driver;
    }

    public static WebDriver getDriverOrNull() {
        return DRIVER.get();
    }

    public static String captureScreenshot(String testName) throws IOException {
        WebDriver driver = getDriver();
        Path directory = Paths.get(Config.get("screenshotDir"));
        Files.createDirectories(directory);
        String safeName = testName.replaceAll("[^A-Za-z0-9._-]", "_");
        Path screenshot = directory.resolve(safeName + "-" + System.currentTimeMillis() + ".png");
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(), screenshot,
                StandardCopyOption.REPLACE_EXISTING);
        return screenshot.toAbsolutePath().toString();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000", "--disable-dev-shm-usage", "--no-sandbox");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000");
        return new EdgeDriver(options);
    }
}
