package org.egeiper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

@Data
@Slf4j
@SuppressWarnings("PMD.GodClass")
public class BasePage {
    private static final String ERROR_MESSAGE = "Wasn't able to wait";
    private static final String NO_MATCH_FOUND = "No match found";
    private static final String NO_SUCH_ELEMENT = "No such element";
    private static final String WEB_DRIVER_EXCEPTION = "Web driver exception";
    private static final String STALE_ELEMENT_EXCEPTION = "Element is no longer present in page";
    private static final String JS_CLICK = "arguments[0].click();";
    private static final String CLASS = "class";
    private static final int DEFAULT_TIMEOUT_DURATION = 20;
    private WebDriver driver;
    private JavascriptExecutor js;
    private WebDriverWait wait;
    private Actions actions;

    public BasePage(final WebDriver driver) {
        setDriver(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_DURATION));
        js = (JavascriptExecutor) getDriver();
        actions = new Actions(driver);
    }

    public void waitFor(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(ERROR_MESSAGE, e);
        }
    }

    public WebElement findElementWithText(final List<WebElement> elements, final String text) {
        try {
            WebElement foundElement;
            for (final WebElement element : elements) {
                if (element.getText().equalsIgnoreCase(text)) {
                    foundElement = element;
                    return foundElement;
                }
            }
        } catch (NoSuchElementException | WebDriverException e) {
            log.error(e.getClass().getCanonicalName().equals(NO_SUCH_ELEMENT)
                    ? NO_SUCH_ELEMENT : WEB_DRIVER_EXCEPTION);
        }
        return null;
    }

    public boolean isElementPresent(final WebElement element) {
        try {
            waitUntil(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (NoSuchElementException | WebDriverException e) {
            return false;
        }
    }

    public boolean isElementPresent(final WebElement element, final int duration) {
        try {
            waitUntil(ExpectedConditions.visibilityOf(element), duration);
            return true;
        } catch (NoSuchElementException | WebDriverException e) {
            return false;
        }
    }

    public boolean isElementPresent(final By locator) {
        try {
            waitUntil(ExpectedConditions.visibilityOf(getDriver().findElement(locator)));
            return true;
        } catch (NoSuchElementException | WebDriverException e) {
            return false;
        }
    }

    public boolean isElementPresent(final By locator, final int duration) {
        try {
            waitUntil(ExpectedConditions.visibilityOf(getDriver().findElement(locator)), duration);
            return true;
        } catch (NoSuchElementException | WebDriverException e) {
            return false;
        }
    }

    public boolean areElementsPresent(final List<WebElement> elements) {
        try {
            waitUntil(ExpectedConditions.visibilityOfAllElements(elements));
            return true;
        } catch (WebDriverException | NoSuchElementException e) {
            return false;
        }
    }

    public boolean areElementsPresent(final By locator) {
        try {
            waitUntil(ExpectedConditions.visibilityOfAllElements(getDriver().findElements(locator)));
            return true;
        } catch (WebDriverException | NoSuchElementException e) {
            return false;
        }
    }

    public void waitUntil(final ExpectedCondition<?> expectedCondition, final int duration) {
        waitCondition(duration).until(expectedCondition);
    }

    public void waitUntil(final ExpectedCondition<?> expectedCondition) {
        waitCondition(DEFAULT_TIMEOUT_DURATION).until(expectedCondition);
    }

    public Wait<WebDriver> waitCondition(final int duration) {
        return new FluentWait<>(getDriver())
                .pollingEvery(Duration.ofMillis(250))
                        .withTimeout(Duration.ofSeconds(duration))
                                .ignoring(NoSuchElementException.class, WebDriverException.class);
    }

    private WebElement centerElement(final WebElement element) {
        final String scrollElementIntoMiddle = "var viewPortHeight = "
                + "Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        js.executeScript(scrollElementIntoMiddle, element);
        return element;
    }

    public void click(final WebElement element) {
        try {
            centerElement(element).click();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            log.error(e.getClass().getCanonicalName()
                    .equals(NO_SUCH_ELEMENT) ? NO_SUCH_ELEMENT : STALE_ELEMENT_EXCEPTION);
        }
    }

    public void click(final By locator) {
        try {
            getDriver().findElement(locator).click();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            log.error(e.getClass().getCanonicalName().equals(NO_SUCH_ELEMENT)
                    ? NO_SUCH_ELEMENT : STALE_ELEMENT_EXCEPTION);
        }
    }

    public void clickWithJS(final WebElement element) {
        js.executeScript(JS_CLICK, element);
    }

    public void clickWithJS(final String cssSelector) {
        try {
            final WebElement element = getDriver().findElement(By.cssSelector(cssSelector));
            js.executeScript(JS_CLICK, element);
        } catch (NoSuchElementException e) {
            log.error(NO_SUCH_ELEMENT, e);
        }
    }

    public void waitAjaxRequestToBeFinished() {
        waitUntil((ExpectedCondition<Boolean>) driver -> js.executeScript("return jQuery.active == 0").equals(true));
    }

    public void scrollPage(final Integer y) {
        final String script = String.format("windows.scrollBy(0,%s)", y);
        js.executeScript(script);
    }

    public void scrollDownToPage() {
        js.executeScript("windows.scrollBy(0,1000)");
    }

    public void acceptAlert() {
        final Alert alert = getDriver().switchTo().alert();
        alert.accept();
    }

    public void scrollToElement(final WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void waitForJQueryToBeFinished() {
        waitUntil((ExpectedCondition<Boolean>) driver ->
                js.executeScript("return !!window.jQuery && window.jQuery.active == 0")
                        .equals(true));
    }

    public void selectByText(final WebElement element, final String text) {
        final Select select = new Select(element);
        select.selectByVisibleText(text);
        waitForJQueryToBeFinished();
    }

    public void selectByValue(final WebElement element, final String text) {
        new Select(element).selectByValue(text);
    }

    public void switchToTheFrame(final String frameName) {
        waitUntil(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
    }

    public void scrollToTheTop() {
        js.executeScript("window.scrollTo(0,0)");
    }

    public void scrollToTheBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public String getAttribute(final WebElement element, final String attributeName) {
        final String attribute;
        try {
            attribute = element.getAttribute(attributeName);
            return attribute;

        } catch (WebDriverException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }

    public boolean isAttributePresentForElement(final String attribute, final WebElement element) {
        try {
            return !element.getAttribute(attribute).isEmpty();
        } catch (WebDriverException e) {
            return false;
        }
    }

    public void uploadFile(final WebElement element, final String fileName, final String path) {
        element.sendKeys(new File(path + fileName).getAbsolutePath());
    }

    public void clearField(final WebElement element) {
        element.sendKeys(Keys.CONTROL, "a");
        element.sendKeys(Keys.BACK_SPACE);
    }

    public void sendKeysIntoWebElement(final WebElement element, final String text) {
        element.clear();
        element.sendKeys(text);
    }

    public String getClassAttributeOfWebElement(final WebElement element) {
        waitUntil(ExpectedConditions.visibilityOf(element));
        return element.getAttribute(CLASS);
    }

    public String getCurrentURL() {
        return getDriver().getCurrentUrl();
    }

    public void clickWithOffset(final WebElement element) {
        final Dimension webElementSize = element.getSize();
        final int xCenter = webElementSize.getWidth() / 2;
        final int yCenter = webElementSize.getHeight() / 2;
        actions.moveToElement(element, xCenter, yCenter).click().perform();
    }
}
