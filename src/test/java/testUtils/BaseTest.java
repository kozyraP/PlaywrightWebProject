package testUtils;

import static base.PlaywrightFactory.takeScreenshot;
import static utils.ExtentReporter.extentLogWithScreenshot;

import java.io.File;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;

import base.PlaywrightFactory;
import pages.HomePage;
import pages.LoginPage;
import utils.ExtentReporter;
import utils.TestProps;

public class BaseTest {

    protected Page page;
    protected SoftAssert softAssert = new SoftAssert();
    protected ExtentTest extentTest, testNode;
    protected HomePage homePage;
    protected LoginPage loginPage;
    protected static final String HOME_PAGE_TITLE = "Your Store";
    protected static final String LOGIN_PAGE_TITLE = "Account Login";
    protected static ExtentReports reporter;
    protected static TestProps testProperties;
    private static Logger log;

    @BeforeSuite
    public void setupBeforeTestSuite() throws Exception {
        File file = new File("test-results");
        log = LogManager.getLogger();
        testProperties = new TestProps();
        testProperties.updateTestProperties();
        reporter = ExtentReporter.getExtentReporter(testProperties);
    }

    @AfterSuite
    public void teardownAfterTestSuite() {
        try {
            softAssert.assertAll();
            reporter.flush();
        } catch (Exception e) {
            log.error("Error in AfterSuite Method ", e);
        }
    }

    @BeforeMethod
    public void startPlaywrightServer() {
        PlaywrightFactory pf = new PlaywrightFactory(testProperties);
        page = pf.createPage();
        page.navigate(testProperties.getProperty("url"));
    }

    @AfterMethod
    public void closePage(ITestResult result) {
        String testName = testNode.getModel().getName().replaceAll("[^A-Za-z0-9_\\-\\.\\s]", "");
        if (!result.isSuccess())
            extentLogWithScreenshot(testNode, Status.WARNING, "The test is not Passed. Please refer the previous step.",
                    takeScreenshot(page));
        page.context().browser().close();
        reporter.flush();
    }
}
