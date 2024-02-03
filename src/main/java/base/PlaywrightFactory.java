package base;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import utils.TestProps;

public class PlaywrightFactory {

    private static final Logger log = LogManager.getLogger();
    private final Playwright playwright;
    private final TestProps testProperties;

    public PlaywrightFactory(TestProps testProperties) {
        this.testProperties = testProperties;
        playwright = Playwright.create();
    }

    private Browser getBrowser() throws IllegalArgumentException {
        String browserName = testProperties.getProperty("browser");
        boolean headless = Boolean.parseBoolean(testProperties.getProperty("headless"));
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
        BrowserType browserType;
        switch (browserName.toLowerCase()) {
            case "chromium" -> browserType = playwright.chromium();
            case "firefox" -> browserType = playwright.firefox();
            case "safari" -> browserType = playwright.webkit();
            case "chrome" -> {
                browserType = playwright.chromium();
                launchOptions.setChannel("chrome");
            }
            case "edge" -> {
                browserType = playwright.chromium();
                launchOptions.setChannel("msedge");
            }
            default -> {
                String message = "Browser Name '" + browserName + "' specified in Invalid.";
                message += " Please specify one of the supported browsers [chromium, firefox, safari, chrome, edge].";
                log.debug(message);
                throw new IllegalArgumentException(message);
            }
        }
        log.info("Browser Selected for Test Execution '{}' with headless mode as '{}'", browserName, headless);
        return browserType.launch(launchOptions);
    }

    private BrowserContext getBrowserContext() {
        BrowserContext browserContext;
        Browser browser = getBrowser();
        NewContextOptions newContextOptions = new Browser.NewContextOptions();

        if (Boolean.parseBoolean(testProperties.getProperty("enableRecordVideo"))) {
            Path path = Paths.get(testProperties.getProperty("recordVideoDirectory"));
            newContextOptions.setRecordVideoDir(path);
            log.info("Browser Context - Video Recording is enabled at location '{}'", path.toAbsolutePath());
        }

        int viewPortHeight = Integer.parseInt(testProperties.getProperty("viewPortHeight"));
        int viewPortWidth = Integer.parseInt(testProperties.getProperty("viewPortWidth"));
        newContextOptions.setViewportSize(viewPortWidth, viewPortHeight);
        log.info("Browser Context - Viewport Width '{}' and Height '{}'", viewPortWidth, viewPortHeight);

        if (Boolean.parseBoolean(testProperties.getProperty("useSessionState"))) {
            Path path = Paths.get(testProperties.getProperty("sessionState"));
            newContextOptions.setStorageStatePath(path);
            log.info("Browser Context - Used the Session Storage State at location '{}'", path.toAbsolutePath());
        }

        browserContext = (browser.newContext(newContextOptions));

        if (Boolean.parseBoolean(testProperties.getProperty("enableTracing"))) {
            browserContext.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
            log.info("Browser Context - Tracing is enabled with Screenshots and Snapshots");
        }
        return browserContext;
    }

    public Page createPage() {
        Page page = null;
        try {
            page = (getBrowserContext().newPage());
        } catch (Exception e) {
            log.error("Unable to create Page : ", e);
        }
        return page;
    }

    public static String takeScreenshot(Page page) {
        String path = System.getProperty("user.dir") + "/test-results/screenshots/" + System.currentTimeMillis()
                + ".png";

        byte[] buffer = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
        String base64Path = Base64.getEncoder().encodeToString(buffer);

        log.debug("Screenshot is taken and saved at the location  {}", path);
        return base64Path;
    }
}
