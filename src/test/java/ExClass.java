import model.BrowserType;
import model.DriverType;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class ExClass {

    WebDriver driver;
    @Test
    public void test() throws InterruptedException {
        driver = Settings.getDriver(DriverType.REMOTE, BrowserType.CHROME);
        driver.get("https://google.com");
        Thread.sleep(5000);
    }
    @AfterTest
    public void afterTest() {
        driver.quit();
    }
}
