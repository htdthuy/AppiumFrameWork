package com.axonvibe.Base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class BaseTest {

    private static final ThreadLocal<String> platformThread = new ThreadLocal<>();
    protected AppiumDriver driver;
    protected Properties config;
    protected String platform;


    @Parameters({"platform"})
    @BeforeTest(alwaysRun = true)
    public void setup(@Optional("") String platform) throws IOException {

        // Load config.properties
        config = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            config.load(fis);
        }

        // Platform priority: ENV > TestNG param > default Android
        String envPlatform = System.getenv("PLATFORM");
        if (envPlatform != null && !envPlatform.trim().isEmpty()) {
            platform = envPlatform;
            System.out.println("🌍 Using platform from ENV variable: " + platform);
        } else if (platform == null || platform.trim().isEmpty()) {
            platform = "android";
            System.out.println("⚠️ No platform specified. Defaulting to ANDROID...");
        } else {
            System.out.println("📦 Using platform from TestNG: " + platform);
        }

        System.out.println("🚀 Starting tests on platform: " + platform);

        DesiredCapabilities caps = new DesiredCapabilities();

        switch (platform.toLowerCase()) {
            case "android" -> {
                caps.setCapability("platformName", "Android");
                caps.setCapability("deviceName", config.getProperty("android.deviceName"));
                caps.setCapability("automationName", config.getProperty("android.automationName"));
                caps.setCapability("appPackage", config.getProperty("android.appPackage"));
                caps.setCapability("appActivity", config.getProperty("android.appActivity"));
                //caps.setCapability("noReset", Boolean.parseBoolean(config.getProperty("appium:noReset", "true")));
                //caps.setCapability("newCommandTimeout", Integer.parseInt(config.getProperty("appium:newCommandTimeout", "300")));
                driver = new AndroidDriver(new URL(config.getProperty("android.serverUrl")), caps);
            }

            case "ios" -> {
                caps.setCapability("platformName", config.getProperty("ios.platformName"));
                caps.setCapability("deviceName", config.getProperty("ios.deviceName"));
                caps.setCapability("platformVersion", config.getProperty("ios.platformVersion"));
                caps.setCapability("automationName", config.getProperty("ios.automationName"));
                caps.setCapability("bundleId", config.getProperty("bundleId"));
                //caps.setCapability("noReset", Boolean.parseBoolean(config.getProperty("appium:noReset", "true")));
                //caps.setCapability("newCommandTimeout", Integer.parseInt(config.getProperty("appium:newCommandTimeout", "300")));
                driver = new IOSDriver(new URL(config.getProperty("ios.serverUrl")), caps);
            }

            default -> throw new IllegalArgumentException("❌ Invalid platform: " + platform);
        }
        System.out.println("✅ Driver initialized successfully!");
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            System.out.println("🧹 Closing driver...");
            DriverFactory.unload();
            // driver.quit();
        }
    }
}
