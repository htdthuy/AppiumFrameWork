package com.axonvibe.Base;

import io.appium.java_client.AppiumDriver;

public class DriverFactory {

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    /**
     * Get the AppiumDriver instance for the current thread.
     */
    public static AppiumDriver getDriver() {
        return driver.get();
    }

//    /**
//     * Set the AppiumDriver instance for the current thread.
//     */
//    public static void setDriver(AppiumDriver driverInstance) {
//        driver.set(driverInstance);
//    }

    /**
     * Remove the driver instance from the ThreadLocal (cleanup after test).
     * Always call this in @AfterTest or @AfterClass.
     */
    public static void unload() {
        AppiumDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
        }
    }
}
