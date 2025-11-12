package com.axonvibe.Base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.axonvibe.Utils.ExtentManager;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static ExtentReports extent;
    String platform = "Unknown";

    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.getInstance();
        System.out.println("=== Test Suite Started ===");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        System.out.println("=== Test Suite Finished ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        Test annotation = method.getAnnotation(Test.class);
        String testName;
        if (annotation != null && !annotation.description().isEmpty()) {
            testName = annotation.description();  // dùng description nếu có
        } else {
            testName = method.getName();
            if (testName.startsWith("test")) {
                testName = testName.substring(4);
            }
            // 2️⃣ Convert camelCase to easy reading: loginValidUser -> Login Valid User
            testName = testName.replaceAll("([A-Z])", " $1").trim();
            // 3️⃣ Viết chữ đầu tiên hoa
            if (!testName.isEmpty()) {
                testName = testName.substring(0, 1).toUpperCase() + testName.substring(1);
            }
        }
        testName = testName + "[" + platform(result).toUpperCase() + "]";
        ExtentTest extentTest = extent.createTest(testName);
        extentTest.assignCategory(platform(result));
        test.set(extentTest);

    }

    @Override
    public void onTestSuccess(ITestResult result) {

        test.get().log(Status.PASS, "✅ Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable().getMessage());
        String screenshotPath = takeScreenshot(result);
        test.get().addScreenCaptureFromPath(screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "⚠️ Test Skipped");
    }

    private String takeScreenshot(ITestResult result) {
        AppiumDriver driver = DriverFactory.getDriver();
        if (driver == null) return null;

        File srcFile = driver.getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = "screenshots/" + result.getMethod().getMethodName() + "_" + timestamp + ".png";

        try {
            FileUtils.copyFile(srcFile, new File(screenshotPath));
            System.out.println("📸 Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return screenshotPath;
    }

    private String platform(ITestResult result) {
        Object testInstance = result.getInstance(); // lấy instance của class test đang chạy
        if (testInstance instanceof BaseTest) {
            return ((BaseTest) testInstance).getPlatform();
        }
        return "Unknown";
    }


}
