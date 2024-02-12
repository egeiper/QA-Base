package org.egeiper;

import io.restassured.RestAssured;
import org.egeiper.util.model.BrowserType;
import org.egeiper.util.model.DriverType;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.egeiper.util.ConfigReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidCatchingGenericException",
        "checkstyle:ClassDataAbstractionCoupling", "checkstyle:ReturnCount"})
public final class WebDriverUtils {

    private static final String CONFIG_PROPERTY = "config.properties";
    private static final String FILE_DOWNLOAD_PATH = "//src//test//resources//fileDownload";
    private static final ConfigReader CONFIG_READER = ConfigFactory.create(ConfigReader.class, System.getProperties());
    private static final String DRIVER_EXCEPTION = "Cannot create driver for unknown browser type";
    private static String hubUrl;



    private WebDriverUtils() {
    }

    public static WebDriver getRemoteDriver(final DriverType driverType, final BrowserType browserType,
                                            final String host, final String port) {
        setHubUrl(host, port);
        if (driverType.equals(DriverType.REMOTE)) {
            switch (browserType) {
                case CHROME:
                    return new RemoteWebDriver(getRemoteURL(), getChromeOptions());
                case FIREFOX:
                    return new RemoteWebDriver(getRemoteURL(), getFirefoxOptions());
                default:
                    throw new UnknownBrowserException(DRIVER_EXCEPTION);
            }
        } else {
            return getRemoteDriverWithHub(browserType);
        }
    }

    public static WebDriver getLocalDriver(final BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                return new ChromeDriver(getChromeOptions());
            case FIREFOX:
                return new FirefoxDriver(getFirefoxOptions());
            default:
                throw new UnknownBrowserException(DRIVER_EXCEPTION);
        }
    }

    private static ChromeOptions getChromeOptions() {
        final Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.cookie_controls_mode", 0);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.default_directory", System.getProperty("user.dir")
                + FILE_DOWNLOAD_PATH);
        final ChromeOptions chromeOpts = new ChromeOptions();
        chromeOpts.addArguments("incognito");
        chromeOpts.addArguments("disable-features=DownloadBubble,DownloadBubbleV2");
        chromeOpts.addArguments("--remote-allow-origins=*");
        chromeOpts.addArguments("--disable-popup-blocking");
        chromeOpts.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        chromeOpts.setExperimentalOption("prefs", prefs);
        return chromeOpts;
    }

    private static FirefoxOptions getFirefoxOptions() {
        final FirefoxOptions options = new FirefoxOptions();
        final FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("download.default_directory", System.getProperty("user.dir")
                + FILE_DOWNLOAD_PATH);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.setProfile(profile);
        options.setCapability(FirefoxDriver.SystemProperty.BROWSER_PROFILE, profile);
        return options;
    }

    private static RemoteWebDriver getRemoteDriverWithHub(final BrowserType browserType) {
        startHubAndNode();
        switch (browserType) {
            case CHROME:
                return new RemoteWebDriver(getRemoteURL(), getChromeOptions());
            case FIREFOX:
                return new RemoteWebDriver(getRemoteURL(), getFirefoxOptions());
            default:
                throw new UnknownBrowserException(DRIVER_EXCEPTION);
        }
    }

    private static URL getRemoteURL() {
        try {
            return new URL(hubUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startHubAndNode() {
        try {
            if (!isHubUp()) {
                new ProcessBuilder("/bin/bash", "-c", startHubCommand(), "with", "args").start();
            }
            if (!isHubReady()) {
                new ProcessBuilder("/bin/bash", "-c", startNodeCommand(), "with", "args").start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not start hub or node", e);
        }
        waitUntilHubIsLoaded();
    }
    private static String getSeleniumServerVersion() {
        return PropertyUtils.getProperty(CONFIG_PROPERTY, "seleniumServerVersion");
    }

    private static String getBrowsersDirectory() {
        return PropertyUtils.getProperty(CONFIG_PROPERTY, "browsersDirectory");
    }

    private static String startHubCommand() {
        return String.format("cd %s && java -jar selenium-server-%s.jar hub --port %s",
                getBrowsersDirectory(), getSeleniumServerVersion(), CONFIG_READER.gridPort());
    }

    private static String startNodeCommand() {
        return String.format("cd %s && java -jar selenium-server-%s.jar node --port %s",
                getBrowsersDirectory(), getSeleniumServerVersion(), CONFIG_READER.nodePort());
    }

    private static void waitUntilHubIsLoaded() {
        await().atMost(60, TimeUnit.SECONDS).until(WebDriverUtils::isHubReady);
    }

    private static boolean isHubUp() {
        try {
            return RestAssured.given().baseUri(hubUrl + "/wd/hub/status").get().getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isHubReady() {
        try {
            return RestAssured.given().baseUri(hubUrl + "/wd/hub/status").get().jsonPath().getBoolean("value.ready");
        } catch (Exception e) {
            return false;
        }
    }

    private static void setHubUrl(final String host, final String port) {
        hubUrl = "http://" + host + ":" + port;
    }
}

