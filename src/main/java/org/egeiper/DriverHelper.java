package org.egeiper;

import org.openqa.selenium.WebDriver;

public final class DriverHelper {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverHelper() {
    }
    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(final WebDriver driver) {
        DriverHelper.driver.set(driver);
    }
}
