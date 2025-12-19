package Issue_persistence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;

import io.cucumber.java.en.When;
import reports.ExtentCucumberListener;
import utils.ConfigReader;
import utils.DriverManager;

	public class VersionStepsP {

		@When("I open the Fusion_Persistence_Builder URL and capture version")
	    public void captureAndAttachScreenshotDirectly() {
	        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
	        WebDriver driver = DriverManager.getDriver();

	        String versionText = "";

	        try {
	            String url = ConfigReader.getProperty("baseurl");
	            if (t != null) t.info("🌐 Navigating to: " + url);

	            driver.get(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	            WebElement versionEl = wait.until(
	                    ExpectedConditions.visibilityOfElementLocated(By.xpath("(//pre[@class='version'])[1]"))
	            );

	            // extract version text
	            versionText = versionEl.getText() == null ? "" : versionEl.getText().trim();
	            if (t != null) t.info("Version: " + (versionText.isEmpty() ? "<unknown>" : versionText));

	            // store for later (runner will pick this up)
	            if (versionText != null && !versionText.isBlank()) {
	                System.setProperty("detected.version", versionText);
	            }

	            // optional: rename extent test name (best-effort)
	            try {
	                if (t != null && !versionText.isEmpty()) {
	                    t.getModel().setName("Version: " + abbreviate(versionText, 120));
	                }
	            } catch (Throwable ignored) {}

	            // scroll element into view
	            ((JavascriptExecutor) driver).executeScript(
	                    "arguments[0].scrollIntoView({behavior:'instant', block:'center'});",
	                    versionEl
	            );
	            Thread.sleep(350);

	            // take screenshot as base64
	            TakesScreenshot ts = (TakesScreenshot) driver;
	            String base64 = ts.getScreenshotAs(OutputType.BASE64);

	            // decode -> BufferedImage
	            byte[] decoded = Base64.getDecoder().decode(base64);
	            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decoded));
	            if (img == null) {
	                if (t != null) t.warning("⚠️ Screenshot decode returned null image.");
	                return;
	            }

	            // get element rect (preferred)
	            int ex = 0, ey = 0, ew = 1, eh = 1;
	            try {
	                org.openqa.selenium.Rectangle rect = versionEl.getRect();
	                ex = rect.getX();
	                ey = rect.getY();
	                ew = rect.getWidth();
	                eh = rect.getHeight();
	            } catch (Throwable rectEx) {
	                // fallback to boundingClientRect via JS
	                try {
	                    @SuppressWarnings("unchecked")
	                    java.util.Map<String, Object> m = (java.util.Map<String, Object>)
	                            ((JavascriptExecutor) driver).executeScript(
	                                    "var r = arguments[0].getBoundingClientRect();"
	                                            + "return {x: Math.round(r.left), y: Math.round(r.top), w: Math.round(r.width), h: Math.round(r.height)};",
	                                    versionEl
	                            );
	                    if (m != null) {
	                        ex = ((Number) m.getOrDefault("x", 0)).intValue();
	                        ey = ((Number) m.getOrDefault("y", 0)).intValue();
	                        ew = ((Number) m.getOrDefault("w", 1)).intValue();
	                        eh = ((Number) m.getOrDefault("h", 1)).intValue();
	                    }
	                } catch (Throwable ignored) {}
	            }

	            // clamp
	            ex = Math.max(0, Math.min(ex, img.getWidth() - 1));
	            ey = Math.max(0, Math.min(ey, img.getHeight() - 1));
	            ew = Math.max(1, Math.min(ew, img.getWidth() - ex));
	            eh = Math.max(1, Math.min(eh, img.getHeight() - ey));

	            // draw rectangle
	            Graphics2D g = img.createGraphics();
	            try {
	                g.setColor(new Color(255, 0, 0, 200));
	                g.setStroke(new BasicStroke(4));
	                g.drawRect(ex, ey, ew - 1, eh - 1);
	            } finally {
	                g.dispose();
	            }

	            // encode and attach
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(img, "png", baos);
	            String highlightedBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

	            if (t != null) {
	                t.addScreenCaptureFromBase64String(highlightedBase64, "Highlighted Version Screenshot");
	                t.pass("✅ Version highlighted & captured successfully.");
	            }

	        } catch (TimeoutException te) {
	            if (t != null) t.fail("⏱ Timeout waiting for version element: " + te.getMessage());
	            throw te;
	        } catch (Exception e) {
	            if (t != null) t.fail("❌ Failed capturing version: " + e.getMessage());
	            throw new RuntimeException(e);
	        }
	    }

	    private String abbreviate(String s, int max) {
	        if (s == null) return "";
	        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
	    }
	}