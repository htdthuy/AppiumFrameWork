package com.axonvibe.Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent;
    private static String reportFilePath;
    private static boolean systemInfoSet = false;

    public static ExtentReports getInstance() {
        if (extent == null) {
            // Tạo file report với timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            reportFilePath = "reports/ExtentReport_" + timestamp + ".html";
            createInstance(reportFilePath);
        }
        return extent;
    }

    public static String getReportPath() {
        return reportFilePath;
    }

    public static void createInstance(String fileName) {

        try {
            File reportFile = new File(fileName);
            File parentDir = reportFile.getParentFile();

            // Tạo thư mục nếu chưa tồn tại
            if (parentDir != null) {
                if (!parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (created) {
                        System.out.println("✅ Created report directory: " + parentDir.getAbsolutePath());
                    } else {
                        System.err.println("⚠️ Could not create report directory: " + parentDir.getAbsolutePath());
                        return;
                    }
                } else {
                    System.out.println("ℹ️ Report directory already exists: " + parentDir.getAbsolutePath());
                }
            }
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportFile);
            htmlReporter.config().setTheme(Theme.STANDARD);
            htmlReporter.config().setDocumentTitle("Automation Test Report");
            htmlReporter.config().setReportName("Appium Parallel Execution");

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            setSystemInfoOnce(extent);
            System.out.println("✅ ExtentReport will be generated at: " + reportFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize ExtentReport: " + e.getMessage());
            //e.printStackTrace();
        }
    }

    private static synchronized void setSystemInfoOnce(ExtentReports extent) {
        if (!systemInfoSet) {
            extent.setSystemInfo("Project", "AutomationAppiumAxonVibe");
            extent.setSystemInfo("Environment", "CIXO Staging");
            systemInfoSet = true;
        }
    }

}
