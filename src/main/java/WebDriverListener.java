import io.qameta.allure.Allure;
import model.BrowserType;
import model.DriverType;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class WebDriverListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        final WebDriver driver = Settings.getDriver(DriverType.REMOTE, BrowserType.CHROME);
        WebDriverManager.setDriver(driver);

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Allure.addAttachment(result.getName()+" "+DateTimeUtils.getCurrentDate(DatePatterns.DD_MM_YYYY_hh_mm),new ByteArrayInputStream(AllureUtils.makeScreenshot()));
        quitDriver();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        quitDriver();
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        quitDriver();
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
        quitDriver();
    }

    private void quitDriver() {
        WebDriverManager.getDriver().quit();
    }
}
