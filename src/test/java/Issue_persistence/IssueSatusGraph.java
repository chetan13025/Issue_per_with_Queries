package Issue_persistence;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import DataCreate.DataCreate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import reports.ExtentCucumberListener;
import utils.ConfigReader;
import validators.ApiValidators;

public class IssueSatusGraph {
	public static int IssueSatusGraphId;
	private Response response;
	private String requestBody;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String IssueStatusGraph_endpoint = ConfigReader.getProperty("issuestatusgraph_endpoint");
	
	private void logToExtent(String action, String reqBody, Response resp) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		current.info("Action: " + action);

		if (reqBody != null) {
			current.info("Request Payload:");
			current.info(MarkupHelper.createCodeBlock(reqBody, CodeLanguage.JSON));
		}

		current.info("Response Status Code: " + (resp == null ? "null" : resp.getStatusCode()));
		if (resp != null) {
			current.info("Response Body:");
			current.info(MarkupHelper.createCodeBlock(resp.getBody().asString(), CodeLanguage.JSON));
		}
	}

	private void captureDbSnapshot(int IssueSatusGraphId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`IssueStatusGraph` LIMIT 1000")) {

			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();

			StringBuilder html = new StringBuilder("<table border='1' style='border-collapse:collapse;'>");

			// Table header
			html.append("<tr style='background-color:#f2f2f2;'>");
			for (int i = 1; i <= colCount; i++) {
				html.append("<th>").append(meta.getColumnName(i)).append("</th>");
			}
			html.append("</tr>");

			// Table rows
			while (rs.next()) {
				int currentId;
				try {
					currentId = rs.getInt("id"); // ✅ use Id if exists
				} catch (Exception e) {
					currentId = rs.getInt("id"); // fallback
				}

				if (currentId == IssueSatusGraphId) {
					html.append("<tr style='background-color:#90EE90; font-weight:bold;'>"); // highlight
				} else {
					html.append("<tr>");
				}

				for (int i = 1; i <= colCount; i++) {
					html.append("<td>").append(rs.getString(i)).append("</td>");
				}
				html.append("</tr>");
			}

			html.append("</table>");

			// ✅ Attach HTML table into Extent report
			current.info("Database Snapshot after: " + action);
			current.info(html.toString());

		} catch (Exception e) {
			current.warning("⚠️ Failed to capture DB snapshot: " + e.getMessage());
		}
	}

	private void captureElasticScreenshot(int IssueSatusGraphId, String action) {
		WebDriver driver = null;
		try {
			// Launch Chrome
			driver = new ChromeDriver();
			driver.manage().window().maximize();

			// Open Elasticsearch _search URL
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_issue-status-graph_index/_search?pretty&size=2000";
			driver.get(url);

			// Small wait for JSON to load
			Thread.sleep(2000);

//                WebElement prettyPrint = driver.findElement(By.xpath("//div[@class='json-formatter-container']"));
//                    prettyPrint.click(); 

			// Use browser search to highlight BookId
			JavascriptExecutor js = (JavascriptExecutor) driver;
//            js.executeScript(
//                "window.find(arguments[0], false, false, true, false, true, false);", 
//                String.valueOf(bookId)
//            );
			js.executeScript("window.find('\"id\" : " + IssueSatusGraphId + "');");
			js.executeScript("var sel = window.getSelection();"
					+ "if (sel.rangeCount > 0) sel.getRangeAt(0).startContainer.parentElement.scrollIntoView({behavior: 'smooth', block: 'center'});");

			// Screenshot
//            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//            String path = "target/screenshots/es_" + bookId + "_" + action.replace(" ", "_") + ".png";
//            Files.createDirectories(Paths.get("target/screenshots/"));
//            File destFile = new File(path);
//            org.openqa.selenium.io.FileHandler.copy(srcFile, destFile);
			String Useridscreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			// Attach to Extent
//            ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//            if (current != null) {
//                current.info("Elasticsearch Verification for BookId: " + bookId);
//                current.addScreenCaptureFromPath(destFile.getAbsolutePath());
//            }
			ExtentTest current = ExtentCucumberListener.getCurrentScenario();
			if (current != null) {
				current.info("Elasticsearch Verification for IssueSatusGraphId: " + IssueSatusGraphId);
				current.addScreenCaptureFromBase64String(Useridscreenshot, "ElasticSearch_" + action.replace(" ", "_"));
//                MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot, "ElasticSearch Screenshot").build();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	@Given("I have a IssueStatusGraph")
	public void i_have_a_issuestatusgraph() {
		requestBody = DataCreate.generateIssueStatusGraphson();
		System.out.println("IssueStatusGraph JSON:\n" + requestBody);
	}

	@When("Create New IssueStatusGraph")
	public void Create_new_issuestatusgraph() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueStatusGraph_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		IssueSatusGraphId = response.jsonPath().getInt("id");
		System.out.println("Stored IssueStatusGraphId: " + IssueSatusGraphId);
		logToExtent("Create IssueStatusGraph Response", requestBody, response);
		captureDbSnapshot(IssueSatusGraphId, "Create New IssueStatusGraph");
		captureElasticScreenshot(IssueSatusGraphId, "Create New IssueStatusGraph");
	}
	
	@When("Fetch All IssueStatusGraph Details")
	public void Fetch_All_issuestatusgraph_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + IssueStatusGraph_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All IssueStatusGraph Response", requestBody, response);
	}
	
	@When("Fetch IssueStatusGraph Details with ID")
	public void Fetch_issuestatusgraph_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSatusGraphId).when().get(baseUrl + IssueStatusGraph_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueStatusGraph by ID", null, response);
	}
	
	@When("Update IssueStatusGraph Details")
	public void Update_issuestatusgarph_details() {
		if (IssueSatusGraphId == 0)
			throw new IllegalStateException("No IssueSatusGraphId available. Run POST first.");

		String newJson = DataCreate.generateIssueStatusGraphson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSatusGraphId).body(newJson).when()
				.put(baseUrl + IssueStatusGraph_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Update IssueStatusGraph (PUT)", newJson, response);
		captureDbSnapshot(IssueSatusGraphId, "Update IssueStatusGraph (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", IssueSatusGraphId);
		captureElasticScreenshot(IssueSatusGraphId, "Update IssueStatusGraph (PUT)");

	}
	
	@When("Update Patch IssueStatusGraph")
	public void Update_Patch_issuestatusgraph() {
		if (IssueSatusGraphId == 0)
			throw new IllegalStateException("No IssueSatusGraphId available. Run POST first.");

		String newJson = DataCreate.generateIssueStatusGraphson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSatusGraphId).body(newJson).when()
				.patch(baseUrl + IssueStatusGraph_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Update IssueStatusGraph (PATCH)", newJson, response);
		captureDbSnapshot(IssueSatusGraphId, "Update IssueStatusGraph (PATCH)");
//		ApiValidators.validatePathParamToResponse(response, "id", IssueRootCauseId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueRootCauseId, "comments");
		captureElasticScreenshot(IssueSatusGraphId, "Update IssueStatusGraph (PATCH)");

	}
	@When("Delete IssueStatusGraph with ID")
	public void delete_issuestatusgraph_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSatusGraphId).when().delete(baseUrl + IssueStatusGraph_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete IssueStatusGraph", null, response);
		captureDbSnapshot(IssueSatusGraphId, "Delete IssueStatusGraph");
		captureElasticScreenshot(IssueSatusGraphId, "Delete IssueStatusGraph");
	}
	
	@Then("The IssueStatusGraph response status code should be {int}")
	public void the_issuestatusgraph_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The IssueStatusGraph response should contain {string}")
	public void the_issuestatusgraph_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
}
