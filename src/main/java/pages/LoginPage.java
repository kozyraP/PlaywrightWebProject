package pages;

import static base.PlaywrightFactory.takeScreenshot;
import static utils.ExtentReporter.extentLogWithScreenshot;

import java.util.Base64;

import static utils.ExtentReporter.extentLog;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;
    private final ExtentTest extentTest;

    private static final String ALERT_ERROR_SELECTOR = "div.alert";
    private static final String EMAIL_ID = "//input[@id='input-email']";
    private static final String LOGIN_BTN = "//input[@value='Login']";
    private static final String LOGOUT_LINK = "//a[@class='list-group-item'][normalize-space()='Logout']";
    private static final String PASSWORD = "//input[@id='input-password']";

    public LoginPage(Page page, ExtentTest extentTest) {
        this.page = page;
        this.extentTest = extentTest;
    }

    public boolean doLogin(String appUserName, String appPassword) {
        extentLog(extentTest, Status.INFO, "Login to Application using username " + appUserName);
        page.fill(EMAIL_ID, appUserName);
        page.fill(PASSWORD, new String(Base64.getDecoder().decode(appPassword)));
        page.click(LOGIN_BTN);
        if (page.locator(LOGOUT_LINK).isVisible()) {
            extentLog(extentTest, Status.PASS, "User login to the Application successful.");
            return true;
        }
        boolean isErrorDisplayed = page.textContent(ALERT_ERROR_SELECTOR)
                .contains("Warning: No match for E-Mail Address and/or Password.");
        extentLogWithScreenshot(extentTest, Status.FAIL, "User login to the Application is unsuccessful.",
                takeScreenshot(page));
        return !isErrorDisplayed;
    }
    public String getLoginPageTitle() {
        page.waitForLoadState();
        return page.title();
    }
}
