package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Simple DriverManager for local Chrome browser.
 * Creates one WebDriver per thread (safe for parallel runs).
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final boolean HEADLESS = true;
    private DriverManager() {}

    public static void initDriver() {
        if (DRIVER.get() == null) {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            if (HEADLESS) options.addArguments("--headless=new");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            // Uncomment if running in CI/CD without display:
            // options.addArguments("--headless=new");

            WebDriver driver = new ChromeDriver(options);
            DRIVER.set(driver);
        }
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            initDriver();
        }
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
            DRIVER.remove();
        }
    }
}