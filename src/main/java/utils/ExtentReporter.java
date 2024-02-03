package utils;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;


public class ExtentReporter {

    private ExtentReporter() {
        throw new IllegalStateException("Extent Reporter class instantiation is not allowed");
    }


    public static ExtentReports getExtentReporter(TestProps testProperties) throws IOException {
        ExtentSparkReporter reporter = new ExtentSparkReporter(testProperties.getProperty("extentReportPath"));
        reporter.loadXMLConfig("./src/main/resources/extent-report-config.xml");

        reporter.config().setCss("img.r-img { width: 30%; }");
        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(reporter);

        String applicationURL = "<a href=\"" + testProperties.getProperty("url")
                + "\" target=\"_blank\">Open cart Demo Application</a>";
        extentReports.setSystemInfo("Application", applicationURL);

        extentReports.setSystemInfo("OS", System.getProperties().getProperty("os.name"));
        extentReports.setSystemInfo("Browser", testProperties.getProperty("browser"));

        if (Boolean.getBoolean(testProperties.getProperty("enableRecordVideo"))) {
            String filePath = Paths.get(testProperties.getProperty("recordVideoDirectory")).toAbsolutePath()
                    .toString();
            String recordedVideoFilePath = "<a href=\"" + filePath
                    + "\" target=\"_blank\"Open cart Demo Application</a>";
            extentReports.setSystemInfo("Execution Recorded Video", recordedVideoFilePath);
        }
        return extentReports;
    }

    public static void extentLog(ExtentTest extentTest, Status status, String message) {
        extentTest.log(status, message);
        log(status, message);
    }

    public static void extentLogWithScreenshot(ExtentTest extentTest, Status status, String message,
            String base64Path) {
        String imageElement = "<br/><img class='r-img' src='data:image/png;base64," + base64Path
                + "' href='data:image/png;base64," + base64Path + "'data-featherlight='image'>";
        extentTest.log(status, message + imageElement);
        log(status, message);
    }

    private static void log(Status status, String message) {
        message = message.replaceAll("\\<.*?\\>", "");
        Logger log = LogManager.getLogger(Thread.currentThread().getStackTrace()[3].getClassName().split("\\.")[1]+ "." + Thread.currentThread().getStackTrace()[3].getMethodName());
        Marker marker = MarkerManager.getMarker("ReportLog");
        switch (status) {
            case FAIL, WARNING, SKIP:
                log.warn(marker, message);
                break;
            case INFO:
                log.info(marker, message);
                break;
            default:
                log.debug(marker, message);
                break;
        }
    }

}
