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
import java.util.Objects;

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

public class BugType {
	public static int BugTypeId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String BugType_endpoint = ConfigReader.getProperty("bugtype_endpoint");
	private static String BugTypeBulk_endpoint = ConfigReader.getProperty("bugtypebulk_enpoint");
	private static String BugTypeBulkUpdate_endpoint = ConfigReader.getProperty("bugtypeu_endpoint");
	private static String BugTypeBulkDelete_endpoint = ConfigReader.getProperty("bugtyped_endpoint");
	
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

	private void captureDbSnapshot(int BugTypeId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
<<<<<<< HEAD
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`BugType` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`BugType` "
								+ "   ORDER BY id DESC LIMIT 5" + ") AS lastFive " + "ORDER BY id ASC;")) {
=======
//				ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`BugType` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`BugType` "
								+ "   ORDER BY id DESC LIMIT 5" + ") AS lastFive " + "ORDER BY id ASC;")){
>>>>>>> 4f4da5e (Removed_ES)

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

				if (currentId == BugTypeId) {
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

	private void captureElasticScreenshot(int BugTypeId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_bug-type_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + BugTypeId + "');");
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
				current.info("Elasticsearch Verification for BugTypeId: " + BugTypeId);
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
	@Given("I have a BugType")
	public void i_have_a_bugtype() {
		requestBody = DataCreate.generateBugTypeJson();
		System.out.println("BugType JSON:\n" + requestBody);
	}

	@When("Create New BugType")
	public void Create_new_bugtype() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + BugType_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		BugTypeId = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored BugTypeId: " + BugTypeId);
		logToExtent("Create BugType Response", requestBody, response);
		captureDbSnapshot(BugTypeId, "Create New BugType");
		// captureElasticScreenshot(BugTypeId, "Create New BugType");
	}
	
	@When("Fetch All BugType Details")
	public void Fetch_All_bugtype_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + BugType_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All BugType Response", requestBody, response);
	}
	
	@When("Fetch BugType Details with ID")
	public void Fetch_bugtype_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BugTypeId).when().get(baseUrl + BugType_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch BugType by ID", null, response);
		ApiValidators.validatePathParamToResponse(response, "id", BugTypeId);
	}
	
	@When("Update BugType Details")
	public void Update_bugtype_details() {
		if (BugTypeId == 0)
			throw new IllegalStateException("No BugTypeId available. Run POST first.");

		String newJson = DataCreate.generateBugTypeJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BugTypeId).body(newJson).when()
				.put(baseUrl + BugType_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update BugType (PUT)", newJson, response);
		captureDbSnapshot(BugTypeId, "Update BugType (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", BugTypeId);
		// captureElasticScreenshot(BugTypeId, "Update BugType (PUT)");

	}
	
	@When("Update Patch BugType")
	public void Update_patch_bugtype() {
		if (BugTypeId == 0)
			throw new IllegalStateException("No BugTypeId available. Run POST first.");

		String newJson = DataCreate.generateBugTypeJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BugTypeId).body(newJson).when()
				.patch(baseUrl + BugType_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update BugType (PATCH)", newJson, response);
		captureDbSnapshot(BugTypeId, "Update BugType (PATCH)");
		ApiValidators.validatePathParamToResponse(response, "id", BugTypeId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(BugTypeId, "Update BugType (PATCH)");

	}
	@When("Delete BugType with ID")
	public void delete_bugtype_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BugTypeId).when().delete(baseUrl + BugType_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete BugType", null, response);
		captureDbSnapshot(BugTypeId, "Delete BugType");
		// captureElasticScreenshot(BugTypeId, "Delete BugType");
	}
	
	@Given("I have Bulk BugType")
	public void i_have_a_bulk_bugtype() {
		requestBody = DataCreate.generateBugTypeBulkJson();
		System.out.println("BugType JSON:\n" + requestBody);

	}

	@When("Create New Bulk BugType")
	public void Create_new_bulk_bugtype() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + BugTypeBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored BugTypeIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create BugType Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk BugType");
			// captureElasticScreenshot(id, "Create Bulk BugType"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "BugType", null);
		}
	}

	private String injectBugTypeIdsIntoBulk(String json, List<Integer> bugtypeIds) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != bugtypeIds.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " BugTypeIds=" + bugtypeIds.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", bugtypeIds.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk BugType")
	public void Update_put_bulk_bugtype() throws Exception {

	    List<Integer> bugtypeIds = ScenarioContext.Ids;

	    if (bugtypeIds == null || bugtypeIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No BehaviorIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateBugTypeBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectBugTypeIdsIntoBulk(newJson, bugtypeIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + BugTypeBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update BugType (PUT)", newJson, response);

	    for (Integer id : bugtypeIds) {
	        captureDbSnapshot(id, "Update Bulk BugType");
	        // captureElasticScreenshot(id, "Update Bulk BugType"+": "+id);
	        ApiDataValidator.validateDbNotNullExcept(id, "BugType", null);
	    }
	}

	
	@When("Delete Bulk BugType")
	public void Delete_bulk_bugtype() throws Exception {

	    List<Integer> bugtypeIds = ScenarioContext.Ids;

	    if (bugtypeIds == null || bugtypeIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No BehaviorIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", bugtypeIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + BugTypeBulkDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent("Update BugType (Delete)", updateRequestBody, response);

	    for (Integer id : bugtypeIds) {
	        captureDbSnapshot(id, "Delete Bulk BugType");
	        // captureElasticScreenshot(id, "Delete Bulk BugType");
	        
	    }
	}
	
	
	@Then("Validate Bulk BugType Request Response ES DB")
	public void validate_bulk_bugtype_request_response_es_db() throws Exception {

	    ObjectMapper mapper = new ObjectMapper();

	    int totalIds = 0;
	    int failedIds = 0;

	    ExtentTest parent = ExtentCucumberListener.getCurrentScenario();

	    // ---------------- Parse request bulk ----------------
	    Map<String, Object> reqRoot =
	            mapper.readValue(
	                    ScenarioContext.RequestBodyForValidation,
	                    new TypeReference<Map<String, Object>>() {}
	            );

	    List<Map<String, Object>> requestBulk =
	            (List<Map<String, Object>>) reqRoot.get("bulk");

	    // ---------------- Parse response DATA ----------------
	    List<Map<String, Object>> responseData =
	            response.jsonPath().getList("DATA");

	    if (requestBulk == null || responseData == null) {
	        parent.fail("Request bulk or response DATA is null");
	        return;
	    }

	    parent.info("🔍 Bulk BugType validation started | Request size="
	            + requestBulk.size() + " | Response size=" + responseData.size());

	   

	    // ---------------- INDEX-BASED LOOP (✅ CORRECT) ----------------
	    for (int i = 0; i < responseData.size(); i++) {

	        totalIds++;

	        try {
	            Map<String, Object> reqObj  = requestBulk.get(i);
	            Map<String, Object> respObj = responseData.get(i);

	            int id = Integer.parseInt(respObj.get("id").toString());

	            String requestJson  = mapper.writeValueAsString(reqObj);
	            String responseJson = mapper.writeValueAsString(respObj);

	            parent.info("🔎 Validating BugType ID=" + id);

	            ApiDataValidator.validateKeysAcrosRequestResponseEsDb(
	                    requestJson,
	                    responseJson,
	                    id,
	                    "bug-type",      // ES index
	                    "BugType",       // DB table
	                    false,
	                    new String[]{
	                            "deleted",
	                            "createdDate",
	                            "updatedDate"
	                    },
	                    new String[]{"id"},     // exclude request-only
	                    new String[]{"_class"}  // exclude ES-only
	            );

	        } catch (Exception e) {
	            failedIds++;
	            parent.fail("❌ Validation exception for one ID: " + e.getMessage());
	        }
	    }

	    // ---------------- BULK SUMMARY ----------------
	    String bannerColor = (failedIds == 0) ? "#1e8e3e" : "#d93025";
	    String statusWord  = (failedIds == 0) ? "PASSED ✓" : "FAILED ✗";

	    String summaryHtml =
	            "<div style='padding:10px;border-left:6px solid " + bannerColor + ";'>" +
	            "<b>Bulk BugType Validation " + statusWord + "</b><br>" +
	            "Total IDs: " + totalIds +
	            " | Passed: " + (totalIds - failedIds) +
	            " | Failed: " + failedIds +
	            "</div>";

	    if (failedIds > 0) {
	        parent.fail(summaryHtml);
	    } else {
	        parent.pass(summaryHtml);
	    }
	}


	
	@Then("The BugType response status code should be {int}")
	public void the_bugtype_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The BugType response should contain {string}")
	public void the_bugtype_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	@Then("Validate API ES and DB for BugTypeId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_BugTypeId(
	        String method,
	        String skipEsToken,
	        String excludeEverywhereCsv,
	        String excludeReqCsv,
	        String excludeEsCsv) throws Exception {

	    if (BugTypeId == 0) {
	        throw new IllegalStateException("No ComponentId available. Run POST/PUT/PATCH first.");
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
	        current.info("🧪 Cross-layer validation for BugTypeId=" + BugTypeId
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
	            BugTypeId,
	            "bug-type",          // ES index suffix
	            "BugType",          // DB table name
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
