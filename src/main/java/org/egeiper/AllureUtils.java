package org.egeiper;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public final class AllureUtils {

    private AllureUtils() {
    }
    public static byte[] makeScreenshot() {
       return ((TakesScreenshot) DriverHelper.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
