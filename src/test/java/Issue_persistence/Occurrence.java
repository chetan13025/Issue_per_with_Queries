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
import java.util.HashMap;
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
import validators.ApiValidators;

public class Occurrence {
	public static int OccurrenceId,ProjectId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> OccurrenceIds;
	public static Boolean IsDefault;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String Occurrence_endpoint = ConfigReader.getProperty("occurrence_endpoint");
	private static String Occurrence_FindByProjectIdendpoint = ConfigReader.getProperty("occurence_findbyprojectid");
	private static String Occurrence_FindByProjectIdandIsDefaultendpoint = ConfigReader.getProperty("occurence_findbyprojectidandisdefult");
	private static String OccurrenceBulk_endpoint = ConfigReader.getProperty("occurrencebulk_endpoint");
	private static String OccurrenceBulkUpdate_endpoint = ConfigReader.getProperty("occurrencebulku_endpoint");
	private static String OccurrenceBulkDelete_endpoint = ConfigReader.getProperty("occurrencebulkd_endpoint");
	
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

	private void captureDbSnapshot(int OccurrenceId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Occurrence` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`Occurrence` "
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

				if (currentId == OccurrenceId) {
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

	private void captureElasticScreenshot(int OccurrenceId, String action) {
		WebDriver driver = null;
		try {
			// Launch Chrome
//			driver = new ChromeDriver();
//			driver.manage().window().maximize();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless=new"); // modern headless
			options.addArguments("--window-size=1920,1080");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");

			// ✅ USE options here
			driver = new ChromeDriver(options);

			// Open Elasticsearch _search URL
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_occurrence_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + OccurrenceId + "');");
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
				current.info("Elasticsearch Verification for OccurrenceId: " + OccurrenceId);
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
	@Given("I have a Occurrence")
	public void i_have_a_occurence() {
		requestBody = DataCreate.generateOccurrenceJson();
		System.out.println("Occurrence JSON:\n" + requestBody);
	}

	@When("Create New Occurrence")
	public void Create_new_occurrence() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + Occurrence_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		OccurrenceId = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored OccurrenceId: " + OccurrenceId);
		logToExtent("Create Occurrence Response", requestBody, response);
		captureDbSnapshot(OccurrenceId, "Create New Occurrence");
		// captureElasticScreenshot(OccurrenceId, "Create New Occurrence");
	}
	
	@When("Fetch All Occurrence Details")
	public void Fetch_All_occurrence_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + Occurrence_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Occurrence Response", requestBody, response);
	}
	
	@When("Fetch Occurrence Details with ID")
	public void Fetch_occurrence_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", OccurrenceId).when().get(baseUrl + Occurrence_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Occurrence by ID", null, response);
		ApiValidators.validatePathParamToResponse(response, "id", OccurrenceId);
	}
	
	@When("Update Occurrence Details")
	public void Update_occurrence_details() {
		if (OccurrenceId == 0)
			throw new IllegalStateException("No OccurrenceId available. Run POST first.");

		String newJson = DataCreate.generateOccurrenceJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", OccurrenceId).body(newJson).when()
				.put(baseUrl + Occurrence_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update Occurrence (PUT)", newJson, response);
		captureDbSnapshot(OccurrenceId, "Update Occurrence (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", OccurrenceId);
		// captureElasticScreenshot(OccurrenceId, "Update Occurrence (PUT)");

	}
	
	@When("Update Patch Occurrence")
	public void Update_Patch_occurrence() {
		if (OccurrenceId == 0)
			throw new IllegalStateException("No OccurrenceId available. Run POST first.");

		String newJson = DataCreate.generateOccurrenceJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", OccurrenceId).body(newJson).when()
				.patch(baseUrl + Occurrence_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		ProjectId = response.jsonPath().getInt("projectId");
		IsDefault = response.jsonPath().getBoolean("isDefault");
		RequestBodyForValidation=newJson;
		logToExtent("Update Occurrence (PATCH)", newJson, response);
		captureDbSnapshot(OccurrenceId, "Update Occurrence (PATCH)");
		ApiValidators.validatePathParamToResponse(response, "id", OccurrenceId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(OccurrenceId, "Update Occurrence (PATCH)");

	}
	
	@When("FindByProjectId For Occurrence")
	public void findbyprojectid_for_occurrence() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("projectId", ProjectId).when().get(baseUrl + Occurrence_FindByProjectIdendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Occurrence by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("projectId", ProjectId);   
		ApiValidators.validateQueryParamsInList(response,"_embedded.occurrences",params);
	}
	
	@When("FindByProjectIdandIsDefault For Occurence")
	public void findbyprojectidandisdefault_for_occurence() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("projectId", ProjectId).queryParam("isDefault", IsDefault).when().get(baseUrl + Occurrence_FindByProjectIdandIsDefaultendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Occurrence by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("projectId", ProjectId);
		params.put("isDefault", IsDefault);
		ApiValidators.validateQueryParamsInList(response,"_embedded.occurrences",params);
	}
	@When("Delete Occurrence with ID")
	public void delete_occurrence_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", OccurrenceId).when().delete(baseUrl + Occurrence_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Occurrence", null, response);
		captureDbSnapshot(OccurrenceId, "Delete Occurrence");
		// captureElasticScreenshot(OccurrenceId, "Delete Occurrence");
	}
	
	@Given("I have Bulk Occurrence")
	public void i_have_a_bulk_Occurrence() {
		requestBody = DataCreate.generateOccurrenceBulkJson();
		System.out.println("Occurrence JSON:\n" + requestBody);

	}

	@When("Create New Bulk Occurrence")
	public void Create_new_bulk_Occurrence() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + OccurrenceBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored OccurrenceIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create Occurrence Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk Occurrence" + ": " + id);
			// captureElasticScreenshot(id, "Create Bulk Occurrence" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Occurrence", null);
		}
	}

	private String injectIdsIntoBulk(String json, List<Integer> Ids) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != Ids.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " OccurrenceIds=" + Ids.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", Ids.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk Occurrence")
	public void Update_put_bulk_Occurrence() throws Exception {

		OccurrenceIds = ScenarioContext.Ids;

		if (OccurrenceIds == null || OccurrenceIds.isEmpty()) {
			throw new IllegalStateException("No OccurrenceIds found. Make sure CREATE scenario ran before UPDATE.");
		}

		// 1️⃣ Generate bulk JSON
		String newJson = DataCreate.generateOccurrenceBulkJson();

		// 2️⃣ Inject stored IDs
		newJson = injectIdsIntoBulk(newJson, OccurrenceIds);

		// 3️⃣ Send PUT request
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(newJson).when().put(baseUrl + OccurrenceBulkUpdate_endpoint).then()
				.extract().response();

		System.out.println("PUT Request Body:\n" + newJson);
		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = newJson;

		logToExtent("Update Occurrence (PUT)", newJson, response);

		for (Integer id : OccurrenceIds) {
			captureDbSnapshot(id, "Update Occurrence (PUT)" + ": " + id);
			// captureElasticScreenshot(id, "Update Occurrence (PUT)" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Occurrence", null);
		}
	}

	@When("Delete Bulk Occurrence")
	public void Delete_bulk_Occurrence() throws Exception {

		OccurrenceIds = ScenarioContext.Ids;

		if (OccurrenceIds == null || OccurrenceIds.isEmpty()) {
			throw new IllegalStateException("No OccurrenceIds found. Run CREATE scenario first.");
		}

		// ✅ Build request JSON directly
		String updateRequestBody = new ObjectMapper().writeValueAsString(Map.of("trace", true, "skipErrors", true,
				"updateExisting", true, "bulk", OccurrenceIds, "skipSofDeleteValidation", false));

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(updateRequestBody).when().delete(baseUrl + OccurrenceBulkDelete_endpoint)
				.then().extract().response();

		System.out.println("Delete Request Body:\n" + updateRequestBody);
		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = updateRequestBody;

		logToExtent(" Occurrence (Delete)", updateRequestBody, response);

		for (Integer id : OccurrenceIds) {
			captureDbSnapshot(id, "Delete Occurrence" + ": " + id);
			// captureElasticScreenshot(id, "Delete Occurrence" + ": " + id);
		}
	}

	@Then("Validate Bulk Occurrence Request Response ES DB")
	public void validate_bulk_Occurrence_request_response_es_db() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		int totalIds = 0;
		int failedIds = 0;

		ExtentTest parent = ExtentCucumberListener.getCurrentScenario();

		// -------- Parse request bulk --------
		Map<String, Object> reqRoot = mapper.readValue(ScenarioContext.RequestBodyForValidation,
				new TypeReference<Map<String, Object>>() {
				});

		List<Map<String, Object>> requestBulk = (List<Map<String, Object>>) reqRoot.get("bulk");

		// -------- Parse response DATA --------
		List<Map<String, Object>> responseData = response.jsonPath().getList("DATA");

		if (requestBulk == null || responseData == null) {
			parent.fail("❌ Request bulk or response DATA is null");
			throw new AssertionError("Invalid bulk data");
		}

		parent.info("🔍 Bulk validation started | Request size=" + requestBulk.size() + " | Response size="
				+ responseData.size());

		// -------- LOOP OVER ALL IDS (DO NOT STOP) --------
		for (int i = 0; i < responseData.size(); i++) {

			totalIds++;

			try {
				Map<String, Object> respObj = responseData.get(i);
				Map<String, Object> matchingReq = requestBulk.get(i);

				int id = Integer.parseInt(respObj.get("id").toString());

//	            parent.info("🔎 Validation Started (ID: " + id + ")");

				String requestJson = mapper.writeValueAsString(matchingReq);
				String responseJson = mapper.writeValueAsString(respObj);

				// ✅ CALL VOID METHOD (NO BOOLEAN)
				ApiDataValidator.validateKeysAcrosRequestResponseEsDb(requestJson, responseJson, id, "occurrence",
						"Occurrence", true, new String[] { "deleted" },
						new String[] { "id", "updatedate", "createdate", "eventDate" }, new String[] { "" });

			} catch (Exception e) {
				failedIds++;
				parent.info("⚠️ Validation exception for one ID: " + e.getMessage());
			}
		}

		// -------- BULK SUMMARY --------
		String bannerColor = (failedIds == 0) ? "#1e8e3e" : "#d93025";
		String statusWord = (failedIds == 0) ? "PASSED ✓" : "FAILED ✗";

		String summaryHtml = "<div style='padding:12px;border-left:6px solid " + bannerColor + ";'>"
				+ "<b>Bulk Component Validation " + statusWord + "</b><br>" + "Total IDs: " + totalIds + " | Passed: "
				+ (totalIds - failedIds) + " | Failed: " + failedIds + "</div>";

		if (failedIds > 0) {
			parent.fail(summaryHtml);
			throw new AssertionError("Bulk validation failed. See Extent report.");
		} else {
			parent.pass(summaryHtml);
		}
	}
	
	@Then("The Occurrence response status code should be {int}")
	public void the_occurrence_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The Occurrence response should contain {string}")
	public void the_occurrence_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	
	@Then("Validate API ES and DB for OccurrenceId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_OccurrenceId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (OccurrenceId == 0) {
			throw new IllegalStateException("No OccurrenceId available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for OccurrenceId=" + OccurrenceId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, OccurrenceId,
				"occurrence", // ES index suffix
				"Occurrence", // DB table name
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
