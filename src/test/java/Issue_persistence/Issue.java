package Issue_persistence;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;

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
import validators.ApiDataValidator;
import validators.ApiValidator;
import validators.ApiValidators;

public class Issue {
	public static int IssueId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String Issue_endpoint = ConfigReader.getProperty("issue_endpoint");

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

//	private void captureDbSnapshot(int IssueId, String action) {
//		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//		if (current == null)
//			return;
//
//		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//				Statement stmt = conn.createStatement();
//				ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Issue` LIMIT 1000")) {
//
//			ResultSetMetaData meta = rs.getMetaData();
//			int colCount = meta.getColumnCount();
//
//			StringBuilder html = new StringBuilder("<table border='1' style='border-collapse:collapse;'>");
//
//			// Table header
//			html.append("<tr style='background-color:#f2f2f2;'>");
//			for (int i = 1; i <= colCount; i++) {
//				html.append("<th>").append(meta.getColumnName(i)).append("</th>");
//			}
//			html.append("</tr>");
//
//			// Table rows
//			while (rs.next()) {
//				int currentId;
//				try {
//					currentId = rs.getInt("id"); // ✅ use Id if exists
//				} catch (Exception e) {
//					currentId = rs.getInt("id"); // fallback
//				}
//
//				if (currentId == IssueId) {
//					html.append("<tr style='background-color:#90EE90; font-weight:bold;'>"); // highlight
//				} else {
//					html.append("<tr>");
//				}
//
//				for (int i = 1; i <= colCount; i++) {
//					html.append("<td>").append(rs.getString(i)).append("</td>");
//				}
//				html.append("</tr>");
//			}
//
//			html.append("</table>");
//
//			// ✅ Attach HTML table into Extent report
//			current.info("Database Snapshot after: " + action);
//			current.info(html.toString());
//
//		} catch (Exception e) {
//			current.warning("⚠️ Failed to capture DB snapshot: " + e.getMessage());
//		}
//	}

//	private void captureElasticScreenshot(int IssueId, String action) {
//		WebDriver driver = null;
//		try {
//			// Launch Chrome
//			driver = new ChromeDriver();
//			driver.manage().window().maximize();
//
//			// Open Elasticsearch _search URL
//			String url = "http://10.10.2.45:9200/piqa_issue-persistence_issue_index/_search?pretty&size=2000";
//			driver.get(url);
//
//			// Small wait for JSON to load
//			Thread.sleep(2000);
//
////                WebElement prettyPrint = driver.findElement(By.xpath("//div[@class='json-formatter-container']"));
////                    prettyPrint.click(); 
//
//			// Use browser search to highlight BookId
//			JavascriptExecutor js = (JavascriptExecutor) driver;
////            js.executeScript(
////                "window.find(arguments[0], false, false, true, false, true, false);", 
////                String.valueOf(bookId)
////            );
//			js.executeScript("window.find('\"id\" : " + IssueId + "');");
//			js.executeScript("var sel = window.getSelection();"
//					+ "if (sel.rangeCount > 0) sel.getRangeAt(0).startContainer.parentElement.scrollIntoView({behavior: 'smooth', block: 'center'});");
//
//			// Screenshot
////            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
////            String path = "target/screenshots/es_" + bookId + "_" + action.replace(" ", "_") + ".png";
////            Files.createDirectories(Paths.get("target/screenshots/"));
////            File destFile = new File(path);
////            org.openqa.selenium.io.FileHandler.copy(srcFile, destFile);
//			String Useridscreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
//			// Attach to Extent
////            ExtentTest current = ExtentCucumberListener.getCurrentScenario();
////            if (current != null) {
////                current.info("Elasticsearch Verification for BookId: " + bookId);
////                current.addScreenCaptureFromPath(destFile.getAbsolutePath());
////            }
//			ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//			if (current != null) {
//				current.info("Elasticsearch Verification for IssueId: " + IssueId);
//				current.addScreenCaptureFromBase64String(Useridscreenshot, "ElasticSearch_" + action.replace(" ", "_"));
////                MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot, "ElasticSearch Screenshot").build();
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (driver != null) {
//				driver.quit();
//			}
//		}
//	}

	@Given("I have a Issue")
	public void i_have_a_issue() {
		requestBody = DataCreate.generateIssueJson();
		System.out.println("Resource JSON:\n" + requestBody);
	}

	@When("Create New Issue")
	public void Create_new_issue() throws Exception {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + Issue_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		IssueId = response.jsonPath().getInt("id");
		RequestBodyForValidation = requestBody;
		System.out.println("Stored IssueId: " + IssueId);
		logToExtent("Create Issue Response", requestBody, response);
//		captureElasticScreenshot(IssueId, "Create New Issue");
		ApiValidators.captureDbSnapshot(IssueId, "Issue", "Create New Issue");
		ApiValidators.captureElasticScreenshot(IssueId, "issue", "Create New Issue");
//		ApiValidators.validateKeysAcrossResponseEsDb(response, IssueId, "issue","Issue");
	}

	@When("Fetch All Issues Details")
	public void Fetch_All_issues_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + Issue_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Issues Response", requestBody, response);
	}

	@When("Fetch Issue Details with ID")
	public void Fetch_issue_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueId).when().get(baseUrl + Issue_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Issue by ID", null, response);
	}

	@When("Update Issue Details")
	public void Update_issue_details() {
		if (IssueId == 0)
			throw new IllegalStateException("No IssueId available. Run POST first.");

		String newJson = DataCreate.generateIssueJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueId).body(newJson).when()
				.put(baseUrl + Issue_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update Issue (PUT)", newJson, response);
		ApiValidators.captureDbSnapshot(IssueId, "Issue", "Update Issue (PUT)");
//		ApiValidators.captureElasticScreenshot(IssueId,"issue", "Update Issue (PUT)");
//		ApiValidators.validateKeysAcrossResponseEsDb(response, IssueId, "issue","Issue");

	}

	@When("Update Patch Issue")
	public void Update_Patch_issue() {
		if (IssueId == 0)
			throw new IllegalStateException("No IssueId available. Run POST first.");

		String newJson = DataCreate.generateIssueJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueId).body(newJson).when()
				.patch(baseUrl + Issue_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update Issue (PATCH)", newJson, response);
		ApiValidators.captureDbSnapshot(IssueId, "Issue", "Update Issue (PATCH)");
		ApiValidators.captureElasticScreenshot(IssueId, "issue", "Update Issue (PATCH)");

	}

	@When("Delete Issue with ID")
	public void delete_issue_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueId).when().delete(baseUrl + Issue_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Issue", null, response);
		ApiValidators.captureDbSnapshot(IssueId, "Issue", "Delete Issue");
		ApiValidators.captureElasticScreenshot(IssueId, "issue", "Delete Issue");
	}

	@Then("The Issue response status code should be {int}")
	public void the_issue_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The Issue response should contain {string}")
	public void the_issue_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	
	@Then("Validate API ES and DB for IssueId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_IssueId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (IssueId == 0) {
			throw new IllegalStateException("No IssueId available. Run POST/PUT/PATCH first.");
		}
		if (response == null) {
			throw new IllegalStateException("Response is null. Make sure the request step ran.");
		}
		if (RequestBodyForValidation == null) {
			throw new IllegalStateException("RequestBodyForValidation is null. Ensure you stored the request body.");
		}

		boolean skipEs = Boolean.parseBoolean(skipEsToken); // "true"/"false" from feature

		String[] excludeEverywhere = parseCsvKeys(excludeEverywhereCsv);
		String[] excludeReqOnly = parseCsvKeys(excludeReqCsv);
		String[] excludeEsOnly = parseCsvKeys(excludeEsCsv);

		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current != null) {
			current.info("🧪 Cross-layer validation for IssueId=" + IssueId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, IssueId,
				"issue", // ES index suffix
				"Issue", // DB table name
				skipEs, // <== comes from feature
				excludeEverywhere, // <== comes from feature
				excludeReqOnly, // <== comes from feature
				excludeEsOnly // <== comes from feature
		);
	}

	private String[] parseCsvKeys(String csv) {
		if (csv == null || csv.trim().isEmpty() || csv.equalsIgnoreCase("null") || csv.equalsIgnoreCase("none")
				|| csv.equals("-")) {
			return new String[] {}; // treat as no excludes
		}
		return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
	}
}
