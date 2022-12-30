import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class AllureUtils {

    public static byte[] makeScreenshot() {
       return ((TakesScreenshot)WebDriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
