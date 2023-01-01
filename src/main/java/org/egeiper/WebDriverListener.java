package org.egeiper;

import io.qameta.allure.Allure;
import org.egeiper.util.model.BrowserType;
import org.egeiper.util.model.DriverType;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class WebDriverListener implements ITestListener {
    @Override
    public void onTestStart(final ITestResult result) {
        final WebDriver driver = DriverUtils.getDriver(DriverType.REMOTE, BrowserType.CHROME);
        DriverHelper.setDriver(driver);
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        Allure.addAttachment(result.getName() + " "
                + DateTimeUtils.getCurrentDate(DatePatterns.DD_MM_YYYY_hh_mm),
                new ByteArrayInputStream(AllureUtils.makeScreenshot()));
        quitDriver();
    }

    @Override
    public void onTestSkipped(final ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailedWithTimeout(final ITestResult result) {
        quitDriver();
    }

    @Override
    public void onFinish(final ITestContext context) {
        quitDriver();
    }

    private void quitDriver() {
        DriverHelper.getDriver().quit();
    }
}
