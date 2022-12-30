import org.openqa.selenium.WebDriver;

public class WebDriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private WebDriverManager() {
    }
    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(final WebDriver driver) {
        WebDriverManager.driver.set(driver);
    }
}
