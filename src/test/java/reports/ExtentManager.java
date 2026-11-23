package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;

    // Keep the generated report at the existing path.
    private static final String REPORT_PATH = "target/Issue_persistence.html";

    // Create one ExtentReports instance for the test execution.
    public synchronized static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

            // Use Extent's built-in dark dashboard theme.
            spark.config().setTheme(Theme.DARK);
            spark.config().setReportName("Automation Test Report");
            spark.config().setDocumentTitle("API Automation Project");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Tester", "Chetan Patil");
            extent.setSystemInfo("Project", "API Automation");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }

    // Flush the report after the test run.
    public synchronized static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
