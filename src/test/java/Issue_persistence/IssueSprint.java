package Issue_persistence;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

public class IssueSprint {
	public static int IssueSprintId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> IssueSprintIds;
	public static Integer IssueId,SprintId;
	public static Integer Post_IssueId,Post_SprintId;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String IssueSprint_endpoint = ConfigReader.getProperty("issuesprint_endpoint");
	private static String IssueSprint_FindByIssueIdandSprintIdendpoint = ConfigReader.getProperty("issuesprint_endpoint");
	private static String IssueSprintBulk_endpoint = ConfigReader.getProperty("issuesprintbulk_endpoint");
	private static String IssueSprintBulkUpdate_endpoint = ConfigReader.getProperty("issuesprintbulku_endpoint");
	private static String IssueSprintBulkDelete_endpoint = ConfigReader.getProperty("issuesprintbulkd_endpoint");

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

	private void captureDbSnapshot(int IssueSprintId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`IssueSprint` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`IssueSprint` "
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

				if (currentId == IssueSprintId) {
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

	private void captureElasticScreenshot(int IssueSprintId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_issue-sprint_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + IssueSprintId + "');");
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
				current.info("Elasticsearch Verification for IssueSprintId: " + IssueSprintId);
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
	
	private void DBConnection(int IssueSprintId, String action) {

	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();

	    try (Connection conn = DriverManager.getConnection(
	            "jdbc:mysql://10.10.2.45:3306/issue_persistence_piqa", "root", "dhi123")) {

	        // 🔥 If PATCH failed → use POST values
	        if (action.toLowerCase().contains("patch") && response.getStatusCode() != 200) {

	            System.out.println("⚠️ PATCH failed → using POST DB values");

	            IssueId = Post_IssueId;
	            SprintId = Post_SprintId;

	            if (current != null) {
	                current.warning("Using POST DB values due to PATCH failure");
	            }

	            return;
	        }

	        int retries = 5;

	        IssueId = null;
	        SprintId = null;

	        while (retries-- > 0) {

	            String query = "SELECT issueId, sprintId " +
	                           "FROM IssueSprint " +
	                           "WHERE id = ? AND deleted = 0 AND issueId IS NOT NULL";

	            PreparedStatement ps = conn.prepareStatement(query);
	            ps.setInt(1, IssueSprintId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {

	                Number issueIdTemp = (Number) rs.getObject("issueId");
	                Number sprintIdTemp = (Number) rs.getObject("sprintId");

	                if (issueIdTemp != null && sprintIdTemp != null) {
	                    IssueId = issueIdTemp.intValue();
	                    SprintId = sprintIdTemp.intValue();

	                    // ✅ Save POST values only
	                    if (action.toLowerCase().contains("post")) {
	                        Post_IssueId = IssueId;
	                        Post_SprintId = SprintId;
	                    }

	                    break;
	                }
	            }

	            Thread.sleep(1000);
	        }

	        if (IssueId == null || SprintId == null) {
	            throw new RuntimeException("❌ DB values not updated for IssueSprintId: " + IssueSprintId);
	        }

	        // ✅ Logging
	        System.out.println("DB issueId: " + IssueId);
	        System.out.println("DB sprintId: " + SprintId);

	        if (current != null) {
	            current.info("DB Data after: " + action);
	            current.info("issueId: " + IssueId);
	            current.info("sprintId: " + SprintId);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        if (current != null) {
	            current.fail("DB error: " + e.getMessage());
	        }
	    }
	}
	@Given("I have a IssueSprint")
	public void i_have_a_issuesprint() {
		requestBody = DataCreate.generateIssueSprintJson();
		System.out.println("IssueSprint JSON:\n" + requestBody);
	}

	@When("Create New IssueSprint")
	public void Create_new_issuesprint() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueSprint_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		IssueSprintId = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored IssueSprintId: " + IssueSprintId);
		logToExtent("Create IssueSprint Response", requestBody, response);
		captureDbSnapshot(IssueSprintId, "Create New IssueSprint");
		DBConnection(IssueSprintId, "After POST");
		// captureElasticScreenshot(IssueSprintId, "Create New IssueSprint");
	}
	
	@When("Fetch All IssueSprint Details")
	public void Fetch_All_issuesprint_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + IssueSprint_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All IssueSprint Response", requestBody, response);
	}
	
	@When("Fetch IssueSprint Details with ID")
	public void Fetch_issuesprint_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSprintId).when().get(baseUrl + IssueSprint_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueSprint by ID", null, response);
	}
	
	@When("Update IssueSprint Details")
	public void Update_issuesprint_details() {
		if (IssueSprintId == 0)
			throw new IllegalStateException("No IssueSprintId available. Run POST first.");

		String newJson = DataCreate.generateIssueSprintJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSprintId).body(newJson).when()
				.put(baseUrl + IssueSprint_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueSprint (PUT)", newJson, response);
		captureDbSnapshot(IssueSprintId, "Update IssueSprint (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", IssueSprintId);
		// captureElasticScreenshot(IssueSprintId, "Update IssueSprint (PUT)");

	}
	
	@When("Update Patch IssueSprint")
	public void Update_Patch_issuesprint() {
		if (IssueSprintId == 0)
			throw new IllegalStateException("No IssueSprintId available. Run POST first.");

		String newJson = DataCreate.generateIssueSprintJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSprintId).body(newJson).when()
				.patch(baseUrl + IssueSprint_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
//		IssueId = response.jsonPath().getInt("issueId");
//		SprintId = response.jsonPath().getInt("sprintId");
		DBConnection(IssueSprintId, "After PATCH");
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueSprint (PATCH)", newJson, response);
		captureDbSnapshot(IssueSprintId, "Update IssueSprint (PATCH)");
//		ApiValidators.validatePathParamToResponse(response, "id", IssueRootCauseId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueRootCauseId, "comments");
		// captureElasticScreenshot(IssueSprintId, "Update IssueSprint (PATCH)");

	}
	
	@When("FindByIssueIdAndSprintId")
	public void findByIssueIdAndSprintId() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("issueId", IssueId).queryParam("sprintId", SprintId).when().get(baseUrl + IssueSprint_FindByIssueIdandSprintIdendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueSprint by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("issueId", IssueId);
		params.put("sprintId", SprintId);
		ApiValidators.validateQueryParamsInList(response,params,null);
	}
	@When("Delete IssueSprint with ID")
	public void delete_issuesprint_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueSprintId).when().delete(baseUrl + IssueSprint_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete IssueSprint", null, response);
		captureDbSnapshot(IssueSprintId, "Delete IssueSprint");
		// captureElasticScreenshot(IssueSprintId, "Delete IssueSprint");
	}
	@Given("I have Bulk IssueSprint")
	public void i_have_a_bulk_IssueSprint() {
		requestBody = DataCreate.generateIssueSprintBulkJson();
		System.out.println("IssueSprint JSON:\n" + requestBody);

	}

	@When("Create New Bulk IssueSprint")
	public void Create_new_bulk_IssueSprint() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueSprintBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored IssueSprintIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create IssueSprint Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id,  "Create Bulk IssueSprint"+": "+id);
			// captureElasticScreenshot(id,  "Create Bulk IssueSprint"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "IssueSprint", null);
		}
	}

	private String injectIdsIntoBulk(String json, List<Integer> Ids) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != Ids.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " IssueSprintIds=" + Ids.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", Ids.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk IssueSprint")
	public void Update_put_bulk_IssueSprint() throws Exception {

		IssueSprintIds = ScenarioContext.Ids;

	    if (IssueSprintIds == null || IssueSprintIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueSprintIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateIssueSprintBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectIdsIntoBulk(newJson, IssueSprintIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + IssueSprintBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update IssueSprint (PUT)", newJson, response);

	    for (Integer id : IssueSprintIds) {
	    	captureDbSnapshot(id, "Update IssueSprint (PUT)"+": "+id);
			// captureElasticScreenshot(id, "Update IssueSprint (PUT)"+": "+id);
			ApiDataValidator.validateDbNotNullExcept(id, "IssueSprint", null);
	    }
	}

	
	@When("Delete Bulk IssueSprint")
	public void Delete_bulk_IssueSprint() throws Exception {

		IssueSprintIds = ScenarioContext.Ids;

	    if (IssueSprintIds == null || IssueSprintIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueSprintIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", IssueSprintIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + IssueSprintBulkDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent(" IssueSprint (Delete)", updateRequestBody, response);

	    for (Integer id : IssueSprintIds) {
	    	captureDbSnapshot(id, "Delete IssueSprint"+": "+id);
			// captureElasticScreenshot(id, "Delete IssueSprint"+": "+id);
	    }
	}
		
	@Then("Validate Bulk IssueSprint Request Response ES DB")
	public void validate_bulk_IssueSprint_request_response_es_db() throws Exception {

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
	                    "issue-sprint",
	                    "IssueSprint",
	                    true,
	                    new String[]{
	                            "deleted"
	                    },
	                    new String[]{"id","updatedate","createdate","eventDate"},
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
	
	@Then("The IssueSprint response status code should be {int}")
	public void the_issuesprint_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The IssueSprint response should contain {string}")
	public void the_issuesprint_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	
	@Then("Validate API ES and DB for IssueSprintId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_IssueSprintId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (IssueSprintId == 0) {
			throw new IllegalStateException("No IssueSprintId available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for IssueSprintId=" + IssueSprintId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, IssueSprintId,
				"issue-sprint", // ES index suffix
				"IssueSprint", // DB table name
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
