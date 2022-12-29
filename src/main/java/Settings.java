import io.restassured.RestAssured;
import model.BrowserType;
import model.DriverType;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import util.ConfigReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class Settings {

    private static final String CHROME_PROPERTY = "webdriver.chrome.driver";
    private static final String FIREFOX_PROPERTY = "webdriver.gecko.driver";
    private static final String CONFIG_PROPERTY = "config.properties";
    private static final String FILE_DOWNLOAD_PATH = "//src//test//resources//fileDownload";
    private static final ConfigReader CONFIG_READER = ConfigFactory.create(ConfigReader.class, System.getProperties());
    private static final String hubURL = String.format("http://%s:%s/wd/hub", CONFIG_READER.gridHost(), CONFIG_READER.gridPort());
    private static final String hubEndpoint = String.format("http://%s:%s/status", CONFIG_READER.gridHost(), CONFIG_READER.gridPort());


    private Settings() {
    }

    public static WebDriver getDriver(final DriverType driverType, final BrowserType browserType) {
        switch (driverType) {
            case LOCAL:
                return getLocalDriver(browserType);
            case REMOTE:
                return getRemoteDriver(browserType);
            default:
                throw new UnknownBrowserException("Cannot create driver for unknown browser type");

        }
    }

    private static WebDriver getLocalDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                System.setProperty(CHROME_PROPERTY, PropertyFinder.getProperty(CONFIG_PROPERTY, "chromeDriverURL"));
                return new ChromeDriver(getChromeOptions());
            case FIREFOX:
                System.setProperty(FIREFOX_PROPERTY, PropertyFinder.getProperty(CONFIG_PROPERTY, "firefoxDriverURL"));
                return new FirefoxDriver(getFirefoxOptions());
            default:
                throw new UnknownBrowserException("Cannot create driver for unknown browser type");
        }
    }

    private static ChromeOptions getChromeOptions() {
        final ChromeOptions options = new ChromeOptions();
        final HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.default_directory", System.getProperty("user.dir")
                + FILE_DOWNLOAD_PATH);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static FirefoxOptions getFirefoxOptions() {
        final FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("download.default_directory", System.getProperty("user.dir")
                + FILE_DOWNLOAD_PATH);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.setProfile(profile);
        options.setCapability(FirefoxDriver.SystemProperty.BROWSER_PROFILE, profile);
        return options;
    }

    private static RemoteWebDriver getRemoteDriver(final BrowserType browserType) {
        startHubAndNode();
        switch (browserType) {
            case CHROME:
                return new RemoteWebDriver(getRemoteURL(), getChromeOptions());
            case FIREFOX:
                return new RemoteWebDriver(getRemoteURL(), getFirefoxOptions());
            default:
                throw new UnknownBrowserException("Cannot create driver for unknown browser type");
        }
    }

    private static URL getRemoteURL() {
        try {
            return new URL(hubURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startHubAndNode() {
        String[] hubArgs = new String[]{"/bin/bash", "-c", startHubCommand(), "with", "args"};
        String[] nodeArgs = new String[]{"/bin/bash", "-c", startNodeCommand(), "with", "args"};
        try {
            if (!isHubUp()) {
                new ProcessBuilder(hubArgs).start();
            }
            if (!isHubReady()) {
                new ProcessBuilder(nodeArgs).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not start hub or node",e);
        }
        waitUntilHubIsLoaded();
    }
    private static String getSeleniumServerVersion() {
        return PropertyFinder.getProperty("config.properties", "seleniumServerVersion");
    }

    private static String getBrowsersDirectory() {
        return PropertyFinder.getProperty("config.properties", "browsersDirectory");
    }

    private static String startHubCommand() {
        return String.format("cd %s && java -jar selenium-server-%s.jar hub", getBrowsersDirectory(), getSeleniumServerVersion());
    }

    private static String startNodeCommand() {
        return String.format("cd %s && java -jar selenium-server-%s.jar node", getBrowsersDirectory(), getSeleniumServerVersion());
    }

    public static void waitUntilHubIsLoaded() {
        await().atMost(60, TimeUnit.SECONDS).until(Settings::isHubReady);
    }

    public static boolean isHubUp() {
        try {
            return RestAssured.given().baseUri(hubEndpoint).get().getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isHubReady() {
        try {
            return RestAssured.given().baseUri(hubEndpoint).get().jsonPath().getBoolean("value.ready");
        }catch (Exception e) {
            return false;
        }
    }
}

