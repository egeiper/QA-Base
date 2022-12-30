import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ExClass {

    @Test
    public void test() {
        WebDriverManager.getDriver().get("https://google.com");
    }

}
