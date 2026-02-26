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
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import DataCreate.DataCreate;
import context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import reports.ExtentCucumberListener;
import utils.ConfigReader;
import validators.ApiDataValidator;

public class IssueFeatureStatistics {
	public static int IssueFeatureStatisticsID;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> IssueFeatureStatisticsIds;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String IssueFeatureStatistics_endpoint = ConfigReader.getProperty("issuefeaturestatistics_endpoint");
	private static String IssueFeatureStatisticsBulk_endpoint = ConfigReader.getProperty("issuefeaturestatisticsbulk_endpoint");
	private static String IssueFeatureStatisticsBulkUpdate_endpoint = ConfigReader.getProperty("issuefeaturestatisticsbulku_endpoint");
	private static String IssueFeatureStatisticsBulkDelete_endpoint = ConfigReader.getProperty("issuefeaturestatisticsbulkd_endpoint");

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

	private void captureDbSnapshot(int IssueFeatureStatisticsID, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`IssueFeatureStatistics` LIMIT 1000"))ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`IssueFeatureStatistics` "
								+ "   ORDER BY id DESC LIMIT 5" + ") AS lastFive " + "ORDER BY id ASC;")) {

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

				if (currentId == IssueFeatureStatisticsID) {
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

	private void captureElasticScreenshot(int IssueFeatureStatisticsID, String action) {
		WebDriver driver = null;
		try {
			// Launch Chrome
//			driver = new ChromeDriver();
//			driver.manage().window().maximize();
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("--headless=new");   // modern headless
	        options.addArguments("--window-size=1920,1080");
	        options.addArguments("--disable-gpu");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");

	        // ✅ USE options here
	        driver = new ChromeDriver(options);


			// Open Elasticsearch _search URL
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_issue-feature-statistics_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + IssueFeatureStatisticsID + "');");
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
				current.info("Elasticsearch Verification for IssueFeatureStatisticsID: " + IssueFeatureStatisticsID);
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

	@Given("I have a IssueFeatureStatistics")
	public void i_have_a_issuefeaturestatistics() {
		requestBody = DataCreate.generateIssueFeatureStatisticsJson();
		System.out.println("Resource JSON:\n" + requestBody);
	}

	@When("Create New IssueFeatureStatistics")
	public void Create_new_issuefeaturestatistics() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueFeatureStatistics_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		IssueFeatureStatisticsID = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored UserId: " + IssueFeatureStatisticsID);
		logToExtent("Create IssueFeatureStatistics Response", requestBody, response);
		captureDbSnapshot(IssueFeatureStatisticsID, "Create New IssueFeatureStatistics");
		// captureElasticScreenshot(IssueFeatureStatisticsID, "Create IssueFeatureStatistics");
	}

	@When("Fetch All IssueFeatureStatistics Details")
	public void Fetch_All_issuefeaturestatistics_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + IssueFeatureStatistics_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Users Response", requestBody, response);
	}

	@When("Fetch IssueFeatureStatistics Details with ID")
	public void Fetch_issuefeaturestatistics_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueFeatureStatisticsID).when().get(baseUrl + IssueFeatureStatistics_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch User by ID", null, response);
	}

	@When("Update IssueFeatureStatistics Details")
	public void Update_issuefeaturestatistics_details() {
		if (IssueFeatureStatisticsID == 0)
			throw new IllegalStateException("No IssueFeatureStatisticsID available. Run POST first.");

		String newJson = DataCreate.generateIssueFeatureStatisticsJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueFeatureStatisticsID).body(newJson).when()
				.put(baseUrl + IssueFeatureStatistics_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueFeatureStatistics (PUT)", newJson, response);
		captureDbSnapshot(IssueFeatureStatisticsID, "Update IssueFeatureStatistics (PUT)");
		// captureElasticScreenshot(IssueFeatureStatisticsID, "Update IssueFeatureStatistics (PUT)");

	}

	@When("Update Patch IssueFeatureStatistics")
	public void issuefeaturestatistics() {
		if (IssueFeatureStatisticsID == 0)
			throw new IllegalStateException("No IssueFeatureStatisticsID available. Run POST first.");

		String newJson = DataCreate.generateIssueFeatureStatisticsJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueFeatureStatisticsID).body(newJson).when()
				.patch(baseUrl + IssueFeatureStatistics_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueFeatureStatistics (PATCH)", newJson, response);
		captureDbSnapshot(IssueFeatureStatisticsID, "Update IssueFeatureStatistics (PATCH)");
		// captureElasticScreenshot(IssueFeatureStatisticsID, "Update IssueFeatureStatistics (PATCH)");

	}

	@When("Delete IssueFeatureStatistics with ID")
	public void delete_user_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueFeatureStatisticsID).when().delete(baseUrl + IssueFeatureStatistics_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete IssueFeatureStatistics", null, response);
		captureDbSnapshot(IssueFeatureStatisticsID, "Delete IssueFeatureStatistics");
		// captureElasticScreenshot(IssueFeatureStatisticsID, "Delete IssueFeatureStatistics");
	}
	
	
	@Given("I have Bulk IssueFeatureStatistics")
	public void i_have_a_bulk_IssueFeatureStatistics() {
		requestBody = DataCreate.generateIssueFeatureStatisticsBulkJson();
		System.out.println("IssueFeatureStatistics JSON:\n" + requestBody);

	}

	@When("Create New Bulk IssueFeatureStatistics")
	public void Create_new_bulk_IssueFeatureStatistics() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueFeatureStatisticsBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored IssueFeatureStatisticsIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create IssueFeatureStatistics Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id,  "Create Bulk IssueFeatureStatistics"+": "+id);
			// captureElasticScreenshot(id,  "Create Bulk IssueFeatureStatistics"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "IssueFeatureStatistics", null);
		}
	}

	private String injectIssueFeatureStatisticsIdsIntoBulk(String json, List<Integer> issuefeaturestatisticsIds) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != issuefeaturestatisticsIds.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " IssueFeatureStatisticsIds=" + issuefeaturestatisticsIds.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", issuefeaturestatisticsIds.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk IssueFeatureStatistics")
	public void Update_put_bulk_IssueFeatureStatistics() throws Exception {

		IssueFeatureStatisticsIds = ScenarioContext.Ids;

	    if (IssueFeatureStatisticsIds == null || IssueFeatureStatisticsIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueFeatureStatisticsIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateIssueFeatureStatisticsBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectIssueFeatureStatisticsIdsIntoBulk(newJson, IssueFeatureStatisticsIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + IssueFeatureStatisticsBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update IssueFeatureStatistics (PUT)", newJson, response);

	    for (Integer id : IssueFeatureStatisticsIds) {
	    	captureDbSnapshot(id, "Update IssueFeatureStatistics (PUT)"+": "+id);
			// captureElasticScreenshot(id, "Update IssueFeatureStatistics (PUT)"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "IssueFeatureStatistics", null);
	    }
	}

	
	@When("Delete Bulk IssueFeatureStatistics")
	public void Delete_bulk_IssueFeatureStatistics() throws Exception {

		IssueFeatureStatisticsIds = ScenarioContext.Ids;

	    if (IssueFeatureStatisticsIds == null || IssueFeatureStatisticsIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueFeatureStatisticsIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", IssueFeatureStatisticsIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + IssueFeatureStatisticsBulkDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent(" IssueFeatureStatistics (Delete)", updateRequestBody, response);

	    for (Integer id : IssueFeatureStatisticsIds) {
	    	captureDbSnapshot(id, "Delete IssueFeatureStatistics"+": "+id);
			// captureElasticScreenshot(id, "Delete IssueFeatureStatistics"+": "+id);
	    }
	}
		
	@Then("Validate Bulk IssueFeatureStatistics Request Response ES DB")
	public void validate_bulk_IssueFeatureStatistics_request_response_es_db() throws Exception {

	    ObjectMapper mapper = new ObjectMapper();

	    int totalIds = 0;
	    int failedIds = 0;

	    ExtentTest parent = ExtentCucumberListener.getCurrentScenario();

	    // -------- Parse request bulk --------
	    Map<String, Object> reqRoot =
	            mapper.readValue(
	                    ScenarioContext.RequestBodyForValidation,
	                    new TypeReference<Map<String, Object>>() {}
	            );

	    List<Map<String, Object>> requestBulk =
	            (List<Map<String, Object>>) reqRoot.get("bulk");

	    // -------- Parse response DATA --------
	    List<Map<String, Object>> responseData =
	            response.jsonPath().getList("DATA");

	    if (requestBulk == null || responseData == null) {
	        parent.fail("❌ Request bulk or response DATA is null");
	        throw new AssertionError("Invalid bulk data");
	    }

	    parent.info("🔍 Bulk validation started | Request size="
	            + requestBulk.size() + " | Response size=" + responseData.size());

	    // -------- LOOP OVER ALL IDS (DO NOT STOP) --------
	    for (int i = 0; i < responseData.size(); i++) {

	        totalIds++;

	        try {
	            Map<String, Object> respObj = responseData.get(i);
	            Map<String, Object> matchingReq = requestBulk.get(i);

	            int id = Integer.parseInt(respObj.get("id").toString());

//	            parent.info("🔎 Validation Started (ID: " + id + ")");

	            String requestJson  = mapper.writeValueAsString(matchingReq);
	            String responseJson = mapper.writeValueAsString(respObj);

	            // ✅ CALL VOID METHOD (NO BOOLEAN)
	            ApiDataValidator.validateKeysAcrosRequestResponseEsDb(
	                    requestJson,
	                    responseJson,
	                    id,
	                    "issue-feature-statistics",
	                    "IssueFeatureStatistics",
	                    true,
	                    new String[]{
	                            "deleted"
	                    },
	                    new String[]{"id","updatedate","createdate"},
	                    new String[]{""}
	            );

	        } catch (Exception e) {
	            failedIds++;
	            parent.info("⚠️ Validation exception for one ID: " + e.getMessage());
	        }
	    }

	    // -------- BULK SUMMARY --------
	    String bannerColor = (failedIds == 0) ? "#1e8e3e" : "#d93025";
	    String statusWord  = (failedIds == 0) ? "PASSED ✓" : "FAILED ✗";

	    String summaryHtml =
	            "<div style='padding:12px;border-left:6px solid " + bannerColor + ";'>" +
	            "<b>Bulk Component Validation " + statusWord + "</b><br>" +
	            "Total IDs: " + totalIds +
	            " | Passed: " + (totalIds - failedIds) +
	            " | Failed: " + failedIds +
	            "</div>";

	    if (failedIds > 0) {
	        parent.fail(summaryHtml);
	        throw new AssertionError("Bulk validation failed. See Extent report.");
	    } else {
	        parent.pass(summaryHtml);
	    }
	}

	@Then("The IssueFeatureStatistics response status code should be {int}")
	public void the_issuefeaturestatistics_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The IssueFeatureStatistics response should contain {string}")
	public void the_issuefeaturestatistics_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	@Then("Validate API ES and DB for IssueFeatureStatisticsID after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_IssueFeatureStatisticsID_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (IssueFeatureStatisticsID == 0) {
			throw new IllegalStateException("No IssueFeatureStatisticsID available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for IssueFeatureStatisticsID=" + IssueFeatureStatisticsID + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, IssueFeatureStatisticsID,
				"issue-feature-statistics", // ES index suffix
				"IssueFeatureStatistics", // DB table name
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
