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

public class ImpactArea {
	public static int ImpactAreaId,ProjectId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> ImpactAreaIds;
	public static String Label;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String ImpactArea_endpoint = ConfigReader.getProperty("impactarea_endpoint");
	private static String ImpactArea_FindBylowerLabelendpoint = ConfigReader.getProperty("impact_areafindbylowerlabel");
	private static String ImpactArea_FindByProjectIdendpoint = ConfigReader.getProperty("impact_areafindbyprojectid");
	private static String ImpactAreaBulk_endpoint = ConfigReader.getProperty("impactareabulk_endpoint");
	private static String ImpactAreaBulkUpdate_endpoint = ConfigReader.getProperty("impactareabulku_endpoint");
	private static String ImpactAreaBulkDelete_endpoint = ConfigReader.getProperty("impactareabulkd_endpoint");

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

	private void captureDbSnapshot(int ImpactAreaId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
//				ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`ImpactArea` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`ImpactArea` "
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

				if (currentId == ImpactAreaId) {
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

	private void captureElasticScreenshot(int ImpactAreaId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_impact-area_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + ImpactAreaId + "');");
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
				current.info("Elasticsearch Verification for ImpactAreaId: " + ImpactAreaId);
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

	@Given("I have a ImpactArea")
	public void i_have_a_impactarea() {
		requestBody = DataCreate.generateImpactAreaJson();
		System.out.println("ImpactArea JSON:\n" + requestBody);
	}

	@When("Create New ImpactArea")
	public void Create_new_impactarea() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + ImpactArea_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		ImpactAreaId = response.jsonPath().getInt("id");
		RequestBodyForValidation = requestBody;
		System.out.println("Stored ImpactAreaId: " + ImpactAreaId);
		logToExtent("Create ImpactArea Response", requestBody, response);
		captureDbSnapshot(ImpactAreaId, "Create New ImpactArea");
		// captureElasticScreenshot(ImpactAreaId, "Create New ImpactArea");
	}

	@When("Fetch All ImpactArea Details")
	public void Fetch_All_impactarea_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + ImpactArea_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All ImpactArea Response", requestBody, response);
	}

	@When("Fetch ImpactArea Details with ID")
	public void Fetch_impactarea_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", ImpactAreaId).when()
				.get(baseUrl + ImpactArea_endpoint+ "/{id}").then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch ImpactArea by ID", null, response);
		ApiValidators.validatePathParamToResponse(response, "id", ImpactAreaId);
	}

	@When("Update ImpactArea Details")
	public void Update_impactarea_details() {
		if (ImpactAreaId == 0)
			throw new IllegalStateException("No ImpactAreaId available. Run POST first.");

		String newJson = DataCreate.generateImpactAreaJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", ImpactAreaId).body(newJson).when()
				.put(baseUrl + ImpactArea_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update ImpactArea (PUT)", newJson, response);
		captureDbSnapshot(ImpactAreaId, "Update ImpactArea (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", ImpactAreaId);
		// captureElasticScreenshot(ImpactAreaId, "Update ImpactArea (PUT)");

	}

	@When("Update Patch ImpactArea")
	public void Update_patch_impactarea() {
		if (ImpactAreaId == 0)
			throw new IllegalStateException("No ImpactAreaId available. Run POST first.");

		String newJson = DataCreate.generateImpactAreaJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", ImpactAreaId).body(newJson).when()
				.patch(baseUrl + ImpactArea_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		Label = response.jsonPath().get("label");
		ProjectId = response.jsonPath().getInt("projectId");
		RequestBodyForValidation = newJson;
		logToExtent("Update ImpactArea (PATCH)", newJson, response);
		captureDbSnapshot(ImpactAreaId, "Update ImpactArea (PATCH)");
		ApiValidators.validatePathParamToResponse(response, "id", ImpactAreaId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(ImpactAreaId, "Update ImpactArea (PATCH)");

	}
	
	@When("FindByLowerLabel for ImpactArea")
	public void findByLowerLabel() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("label", Label).when()
				.get(baseUrl + ImpactArea_FindBylowerLabelendpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch ImpactArea by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("label", Label);   
		ApiValidators.validateQueryParamsInList(
		        response,
		        "_embedded.impactAreas",
		        params
		);
	}
	
	@When("FindByProjectIdOrderByLabelAsc for ImpactArea")
	public void findByProjectIdOrderByLabelAsc_for_impactarea() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("projectId", ProjectId).when()
				.get(baseUrl + ImpactArea_FindByProjectIdendpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch ImpactArea by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("projectId", ProjectId);   
		ApiValidators.validateQueryParamsInList(
		        response,
		        "_embedded.impactAreas",
		        params
		);
	}

	@When("Delete ImpactArea with ID")
	public void delete_impactarea_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", ImpactAreaId).when()
				.delete(baseUrl + ImpactArea_endpoint + "/{id}").then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Impactarea", null, response);
		captureDbSnapshot(ImpactAreaId, "Delete ImpactArea");
		// captureElasticScreenshot(ImpactAreaId, "Delete ImpactArea");
	}
	
	
	@Given("I have Bulk ImpactArea")
	public void i_have_a_bulk_ImpactArea() {
		requestBody = DataCreate.generateImpactAreaBulkJson();
		System.out.println("ImpactArea JSON:\n" + requestBody);

	}

	@When("Create New Bulk ImpactArea")
	public void Create_new_bulk_ImpactArea() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + ImpactAreaBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored ImpactAreaIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create ImpactArea Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk ImpactArea");
			// captureElasticScreenshot(id, "Create Bulk ImpactArea"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "ImpactArea", null);
		}
	}

	private String injectImpactAreaIdsIntoBulk(String json, List<Integer> impactareaIds) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != impactareaIds.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " ImpactAreaIds=" + impactareaIds.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", impactareaIds.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk ImpactArea")
	public void Update_put_bulk_ImpactArea() throws Exception {

		ImpactAreaIds = ScenarioContext.Ids;

	    if (ImpactAreaIds == null || ImpactAreaIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No ImpactAreaIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateImpactAreaBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectImpactAreaIdsIntoBulk(newJson, ImpactAreaIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + ImpactAreaBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update ImpactArea (PUT)", newJson, response);

	    for (Integer id : ImpactAreaIds) {
	        captureDbSnapshot(id, "Update Bulk ImpactArea");
	        // captureElasticScreenshot(id, "Update Bulk ImpactArea"+": "+id);
	        ApiDataValidator.validateDbNotNullExcept(id, "ImpactArea", null);
	    }
	}

	
	@When("Delete Bulk ImpactArea")
	public void Delete_bulk_ImpactArea() throws Exception {

		ImpactAreaIds = ScenarioContext.Ids;

	    if (ImpactAreaIds == null || ImpactAreaIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No ImpactAreaIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", ImpactAreaIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + ImpactAreaBulkDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent(" ImpactArea (Delete)", updateRequestBody, response);

	    for (Integer id : ImpactAreaIds) {
	        captureDbSnapshot(id, "Delete Bulk ImpactArea");
	        // captureElasticScreenshot(id, "Delete Bulk ImpactArea");
	    }
	}
	
	
//	
	@Then("Validate Bulk ImpactArea Request Response ES DB")
	public void validate_bulk_ImpactArea_request_response_es_db() throws Exception {

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
	                    "impact-area",
	                    "ImpactArea",
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


	@Then("The ImpactArea response status code should be {int}")
	public void the_impactarea_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The ImpactArea response should contain {string}")
	public void the_impactarea_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}

	@Then("Validate API ES and DB for ImpactAreaId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_ImpactAreaId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (ImpactAreaId == 0) {
			throw new IllegalStateException("No ImpactAreaId available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for ImpactAreaId=" + ImpactAreaId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, ImpactAreaId,
				"impact-area", // ES index suffix
				"ImpactArea", // DB table name
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
