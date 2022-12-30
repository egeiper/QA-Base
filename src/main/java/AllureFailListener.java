import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import io.qameta.allure.Attachment;

public class AllureFailListener implements IInvokedMethodListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (testResult.getStatus() == ITestResult.FAILURE || testResult.getStatus() == ITestResult.SUCCESS_PERCENTAGE_FAILURE) {
            final String screenshotName = testResult.getName();
            makeScreenshot(screenshotName);
        }
    }
    @Attachment(value = "{name}", type = "image/png")
    public static void makeScreenshot(final String name) {
        ((TakesScreenshot)WebDriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
