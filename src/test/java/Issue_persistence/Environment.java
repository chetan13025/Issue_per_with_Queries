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
import validators.ApiValidators;

public class Environment {
	public static int EnvironmentId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> EnvironmentIds;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String Environment_endpoint = ConfigReader.getProperty("environment_endpoint");
	private static String EnvironmentBulk_endpoint = ConfigReader.getProperty("environmentbulk_endpoint");
	private static String EnvironmentBulkUpdate_endpoint = ConfigReader.getProperty("environmentbulku_endpoint");
	private static String EnvironmentDelete_endpoint = ConfigReader.getProperty("environmentbulkd_endpoint");
	
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

	private void captureDbSnapshot(int EnvironmentId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
//				ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Environment` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`Environment` "
								+ "   ORDER BY id DESC LIMIT 5" + ") AS lastFive " + "ORDER BY id ASC;")){

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

				if (currentId == EnvironmentId) {
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

	private void captureElasticScreenshot(int EnvironmentId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_environment_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + EnvironmentId + "');");
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
				current.info("Elasticsearch Verification for EnvironmentId: " + EnvironmentId);
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
	@Given("I have a Environment")
	public void i_have_a_environment() {
		requestBody = DataCreate.generateEnvironmentJson();
		System.out.println("Environment JSON:\n" + requestBody);
	}

	@When("Create New Environment")
	public void Create_new_environment() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + Environment_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		EnvironmentId = response.jsonPath().getInt("id");
		RequestBodyForValidation = requestBody;
		System.out.println("Stored EnvironmentId: " + EnvironmentId);
		logToExtent("Create Environment Response", requestBody, response);
		captureDbSnapshot(EnvironmentId, "Create New Environment");
		// captureElasticScreenshot(EnvironmentId, "Create New Environment");
	}
	
	@When("Fetch All Environment Details")
	public void Fetch_All_environment_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + Environment_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Environment Response", requestBody, response);
	}
	
	@When("Fetch Environment Details with ID")
	public void Fetch_environment_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", EnvironmentId).when().get(baseUrl + Environment_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Environment by ID", null, response);
		ApiValidators.validatePathParamToResponse(response, "id", EnvironmentId);
	}
	
	@When("Update Environment Details")
	public void Update_environment_details() {
		if (EnvironmentId == 0)
			throw new IllegalStateException("No EnvironmentId available. Run POST first.");

		String newJson = DataCreate.generateEnvironmentJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", EnvironmentId).body(newJson).when()
				.put(baseUrl + Environment_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update Environment (PUT)", newJson, response);
		captureDbSnapshot(EnvironmentId, "Update Environment (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", EnvironmentId);
		// captureElasticScreenshot(EnvironmentId, "Update Environment (PUT)");

	}
	
	@When("Update Patch Environment")
	public void Update_patch_environment() {
		if (EnvironmentId == 0)
			throw new IllegalStateException("No EnvironmentId available. Run POST first.");

		String newJson = DataCreate.generateEnvironmentJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", EnvironmentId).body(newJson).when()
				.patch(baseUrl + Environment_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update Environment (PATCH)", newJson, response);
		captureDbSnapshot(EnvironmentId, "Update Environment (PATCH)");
		ApiValidators.validatePathParamToResponse(response, "id", EnvironmentId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(EnvironmentId, "Update Environment (PATCH)");

	}
	@When("Delete Environment with ID")
	public void delete_environment_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", EnvironmentId).when().delete(baseUrl + Environment_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Environment", null, response);
		captureDbSnapshot(EnvironmentId, "Delete Environment");
		// captureElasticScreenshot(EnvironmentId, "Delete Environment");
	}
	
	@Given("I have Bulk Environment")
	public void i_have_a_bulk_Environment() {
		requestBody = DataCreate.generateEnvironmentBulkJson();
		System.out.println("DependencyType JSON:\n" + requestBody);

	}

	@When("Create New Bulk Environment")
	public void Create_new_bulk_Environment() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + EnvironmentBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored EnvironmentIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create Environment Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk Environment");
			// captureElasticScreenshot(id, "Create Bulk Environment"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "Environment", null);
		}
	}

	private String injectEnvironmentIdsIntoBulk(String json, List<Integer> environmentIds) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != environmentIds.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " EnvironmentIds=" + environmentIds.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", environmentIds.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk Environment")
	public void Update_put_bulk_Environment() throws Exception {

		EnvironmentIds = ScenarioContext.Ids;

	    if (EnvironmentIds == null || EnvironmentIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No EnvironmentIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateEnvironmentBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectEnvironmentIdsIntoBulk(newJson, EnvironmentIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + EnvironmentBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update Environment (PUT)", newJson, response);

	    for (Integer id : EnvironmentIds) {
	        captureDbSnapshot(id, "Update Bulk Environment");
	        // captureElasticScreenshot(id, "Update Bulk Environment"+": "+id);
	        ApiDataValidator.validateDbNotNullExcept(id, "Environment", null);
	    }
	}

	
	@When("Delete Bulk Environment")
	public void Delete_bulk_Environment() throws Exception {

		EnvironmentIds = ScenarioContext.Ids;

	    if (EnvironmentIds == null || EnvironmentIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No EnvironmentIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", EnvironmentIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + EnvironmentDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent("Update Environment (Delete)", updateRequestBody, response);

	    for (Integer id : EnvironmentIds) {
	        captureDbSnapshot(id, "Delete Bulk Environment");
	        // captureElasticScreenshot(id, "Delete Bulk Environment");
	    }
	}
	
	
//	
	@Then("Validate Bulk Environment Request Response ES DB")
	public void validate_bulk_Environment_request_response_es_db() throws Exception {

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
	                    "environment",
	                    "Environment",
	                    true,
	                    new String[]{
	                            "deleted",
	                            "createdDate",
	                            "updatedDate"
	                    },
	                    new String[]{"id"},
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
	
	@Then("The Environment response status code should be {int}")
	public void the_environment_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The Environment response should contain {string}")
	public void the_environment_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	@Then("Validate API ES and DB for EnvironmentId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_EnvironmentId_dynamic(
	        String method,
	        String skipEsToken,
	        String excludeEverywhereCsv,
	        String excludeReqCsv,
	        String excludeEsCsv) throws Exception {

	    if (EnvironmentId == 0) {
	        throw new IllegalStateException("No EnvironmentId available. Run POST/PUT/PATCH first.");
	    }
	    if (response == null) {
	        throw new IllegalStateException("Response is null. Make sure the request step ran.");
	    }
	    if (RequestBodyForValidation == null) {
	        throw new IllegalStateException("RequestBodyForValidation is null. Ensure you stored the request body.");
	    }

	    boolean skipEs = Boolean.parseBoolean(skipEsToken); // "true"/"false" from feature

	    String[] excludeEverywhere = parseCsvKeys(excludeEverywhereCsv);
	    String[] excludeReqOnly    = parseCsvKeys(excludeReqCsv);
	    String[] excludeEsOnly     = parseCsvKeys(excludeEsCsv);

	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
	    if (current != null) {
	        current.info("🧪 Cross-layer validation for EnvironmentId=" + EnvironmentId
	                + " after " + method.toUpperCase()
	                + " | skipEs=" + skipEs);
	        current.info("ExcludeEverywhere=" + excludeEverywhereCsv
	                + " | RequestOnly=" + excludeReqCsv
	                + " | EsOnly=" + excludeEsCsv);
	    }

	    // 🔥 your core call, now parameterised
	    ApiDataValidator.validateKeysAcrosRequestResponseEsDb(
	            RequestBodyForValidation,
	            response,
	            EnvironmentId,
	            "environment",          // ES index suffix
	            "Environment",          // DB table name
	            skipEs,                // <== comes from feature
	            excludeEverywhere,     // <== comes from feature
	            excludeReqOnly,        // <== comes from feature
	            excludeEsOnly          // <== comes from feature
	    );
	}

	private String[] parseCsvKeys(String csv) {
	    if (csv == null || csv.trim().isEmpty()
	            || csv.equalsIgnoreCase("null")
	            || csv.equalsIgnoreCase("none")
	            || csv.equals("-")) {
	        return new String[]{};  // treat as no excludes
	    }
	    return Arrays.stream(csv.split(","))
	            .map(String::trim)
	            .filter(s -> !s.isEmpty())
	            .toArray(String[]::new);
	}
}
