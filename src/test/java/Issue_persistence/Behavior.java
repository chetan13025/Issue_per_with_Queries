package Issue_persistence;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import validators.ApiValidator;
import validators.ApiValidators;

public class Behavior {
	public static int BehaviorId;
	public static Integer IssueId;           
	public static Integer Post_IssueId;      
	public static String Post_BehaviorType;
	private Response response;
	private String requestBody;
	public static String BehaviorType;
	private String RequestBodyForValidation;
	List<Integer> BehaviorIds = new ArrayList<>();
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String Behavior_endpoint = ConfigReader.getProperty("behavior_endpoint");
	private static String Behavior_FindByIssueIdendpoint = ConfigReader.getProperty("Beha_findbyIssueId");
	private static String Behavior_FindByIssueIdandBehaviourendpoint = ConfigReader.getProperty("Beha_findbyIssueIDandBehatype");
	private static String BehaviorBulk_endpoint = ConfigReader.getProperty("behaviorbulk_endpoint");
	private static String BehaviorBulkUpdate_endpoint = ConfigReader.getProperty("behaviorbulku_endpoint");
	private static String BehaviorBulkDelete_endpoint = ConfigReader.getProperty("behaviorbulkd_endpoint");

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

	private void captureDbSnapshot(int BehaviorId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Behavior` LIMIT 1000"))
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Behavior` LIMIT 1000"))

				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`Behavior` "
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
					currentId = rs.getInt("id"); //
				} catch (Exception e) {
					currentId = rs.getInt("id"); // fallback
				}

				if (currentId == BehaviorId) {
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

	private void captureElasticScreenshot(int BehaviorId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_behavior_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + BehaviorId + "');");
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
				current.info("Elasticsearch Verification for BehaviorId: " + BehaviorId);
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
	private void DBConnection(int behaviorId, String action) {

	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();

	    try (Connection conn = DriverManager.getConnection(
	            "jdbc:mysql://10.10.2.45:3306/issue_persistence_piqa", "root", "dhi123")) {

	        // 🔥 If PATCH failed → use POST values
	        if (action.toLowerCase().contains("patch") && response.getStatusCode() != 200) {

	            System.out.println("⚠️ PATCH failed → using POST DB values");

	            IssueId = Post_IssueId;
	            BehaviorType = Post_BehaviorType;

	            if (current != null) {
	                current.warning("Using POST DB values due to PATCH failure");
	            }

	            return;
	        }

	        int retries = 5;

	        IssueId = null;
	        BehaviorType = null;

	        while (retries-- > 0) {

	            String query = "SELECT issueId, behaviorType " +
	                           "FROM Behavior " +
	                           "WHERE id = ? AND deleted = 0 AND issueId IS NOT NULL";

	            PreparedStatement ps = conn.prepareStatement(query);
	            ps.setInt(1, behaviorId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {

	                Number issueIdTemp = (Number) rs.getObject("issueId");
	                String behaviorTypeTemp = rs.getString("behaviorType");

	                if (issueIdTemp != null && behaviorTypeTemp != null) {
	                    IssueId = issueIdTemp.intValue();
	                    BehaviorType = behaviorTypeTemp;

	                    // ✅ Save POST values only
	                    if (action.toLowerCase().contains("post")) {
	                        Post_IssueId = IssueId;
	                        Post_BehaviorType = BehaviorType;
	                    }

	                    break;
	                }
	            }

	            Thread.sleep(1000);
	        }

	        if (IssueId == null || BehaviorType == null) {
	            throw new RuntimeException("❌ DB values not updated for BehaviorId: " + behaviorId);
	        }

	        // ✅ Logging
	        System.out.println("DB issueId: " + IssueId);
	        System.out.println("DB behaviorType: " + BehaviorType);

	        if (current != null) {
	            current.info("DB Data after: " + action);
	            current.info("issueId: " + IssueId);
	            current.info("behaviorType: " + BehaviorType);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        if (current != null) {
	            current.fail("DB error: " + e.getMessage());
	        }
	    }
	}

	@Given("I have a Behavior")
	public void i_have_a_behavior() {
		requestBody = DataCreate.generateBehaviorJson();
		System.out.println("Behavior JSON:\n" + requestBody);
	}

	@When("Create New Behavior")
	public void Create_new_behavior() throws Exception {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + Behavior_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		BehaviorId = response.jsonPath().getInt("id");
		RequestBodyForValidation = requestBody;
		System.out.println("Stored BehaviorId: " + BehaviorId);
//		System.out.println(IssueId);
		logToExtent("Create Behavior Response", requestBody, response);
		DBConnection(BehaviorId, "After POST");
//		captureDbSnapshot(BehaviorId, "Create New Behavior");
		if (response.getStatusCode()==201) {
			captureDbSnapshot(BehaviorId, "Create New Behavior");
		}
		// captureElasticScreenshot(BehaviorId, "Create New Behavior");
//		ApiValidator.validateRequestKeysWithSeparateScreenshotColumns(
//		         response,
//		         requestBody,
//		         BehaviorId,
//		         "behavior",
//		         "Behavior",
//		         true,
//		         5000L,
//		         false    // capture only failing keys (set true to capture thumbnails for all keys)
//		     );
//		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(requestBody, response, BehaviorId, "behavior", "Behavior", false,  null, new String[] {"id","deleted"},new String[] {"deleted"});
	}

	@When("Fetch All Behavior Details")
	public void Fetch_All_behavior_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + Behavior_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Behavior Response", requestBody, response);
	}

	@When("Fetch Behavior Details with ID")
	public void Fetch_behavior_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BehaviorId).when().get(baseUrl + Behavior_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Behavior by ID", null, response);
		ApiValidators.validatePathParamToResponse(response, "id", BehaviorId);
	}

	@When("Update Behavior Details")
	public void Update_behavior_details() {
		if (BehaviorId == 0)
			throw new IllegalStateException("No BehaviorId available. Run POST first.");

		String newJson = DataCreate.generateBehaviorJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BehaviorId).body(newJson).when()
				.put(baseUrl + Behavior_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation = newJson;
		logToExtent("Update Behavior (PUT)", newJson, response);
//		captureDbSnapshot(BehaviorId, "Update Behavior (PUT)");
		if (response.getStatusCode()==200) {
			captureDbSnapshot(BehaviorId, "Update Behavior (PUT)");
		}
		ApiValidators.validatePathParamToResponse(response, "id", BehaviorId);
		// captureElasticScreenshot(BehaviorId, "Update Behavior (PUT)");
//		ApiValidator.validateRequestKeysWithSeparateScreenshotColumns(
//		         response,
//		         newJson,
//		         BehaviorId,
//		         "behavior",
//		         "Behavior",
//		         true,
//		         5000L,
//		         false    // capture only failing keys (set true to capture thumbnails for all keys)
//		     );

	}

	@When("Update Patch Behavior")
	public void Update_patch_behavior() {
		if (BehaviorId == 0)
			throw new IllegalStateException("No BehaviorId available. Run POST first.");

		String newJson = DataCreate.generateBehaviorJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BehaviorId).body(newJson).when()
				.patch(baseUrl + Behavior_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
//		IssueId = response.jsonPath().getInt("issueId");
//		BehaviorType=response.jsonPath().get("behaviorType");
//		System.out.println(BehaviorType);
		RequestBodyForValidation = newJson;
		logToExtent("Update Behavior (PATCH)", newJson, response);
		DBConnection(BehaviorId, "After PATCH");
//		captureDbSnapshot(BehaviorId, "Update Behavior (PATCH)");
		if (response.getStatusCode()==200) {
			captureDbSnapshot(BehaviorId, "Update Behavior (PATCH)");
		}
		ApiValidators.validatePathParamToResponse(response, "id", BehaviorId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(BehaviorId, "Update Behavior (PATCH)");
//		ApiValidator.validateRequestKeysWithSeparateScreenshotColumns(
//		         response,
//		         newJson,
//		         BehaviorId,
//		         "behavior",
//		         "Behavior",
//		         true,
//		         5000L,
//		         false    // capture only failing keys (set true to capture thumbnails for all keys)
//		     );

	}

	@When("FindByIssueIdOrderByIdAsc")
	public void FindByIssueIdOrderByIdAsc() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("issueId", IssueId).when().get(baseUrl + Behavior_FindByIssueIdendpoint)
				.then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("FindByIssueIdOrderByIdAsc", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("issueId", IssueId);   
		ApiValidators.validateQueryParamsInList(response,"_embedded.behaviors",params);
	}
	
	@When("findByIssueIdAndBehaviorTypeOrderByIdAsc")
	public void findByIssueIdAndBehaviorTypeOrderByIdAsc() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("issueId", IssueId).queryParam("behaviorType", BehaviorType).when().get(baseUrl + Behavior_FindByIssueIdandBehaviourendpoint)
				.then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("findByIssueIdAndBehaviorTypeOrderByIdAsc", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("issueId", IssueId);   // ✅ no parseInt
		params.put("behaviorType", BehaviorType);
		ApiValidators.validateQueryParamsInList(response,"_embedded.behaviors",params);

	}
	

	@When("Delete Behavior with ID")
	public void delete_behavior_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", BehaviorId).when()
				.delete(baseUrl + Behavior_endpoint + "/{id}").then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Behavior", null, response);
//		captureDbSnapshot(BehaviorId, "Delete Behavior");
		if (response.getStatusCode()==204) {
			captureDbSnapshot(BehaviorId, "Delete Behavior");
		}
		// captureElasticScreenshot(BehaviorId, "Delete Behavior");
	}

	@Given("i have Bulk Behavior")
	public void i_have_a_bulk_behavior() {
		requestBody = DataCreate.generateBehaviorBulkJson();
		System.out.println("Behavior JSON:\n" + requestBody);

	}

	@When("Create New BulkBehavior")
	public void Create_new_bulkbehavior() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + BehaviorBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored BehaviorIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create Behavior Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk Behavior");

			// captureElasticScreenshot(id, "Create Bulk Behavior"+": "+id);

			// captureElasticScreenshot(id, "Create Bulk Behavior" + ": " + id);

			captureElasticScreenshot(id, "Create Bulk Behavior" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Behavior", null);
		}
	}

	private String injectBehaviorIdsIntoBulk(String json, List<Integer> behaviorIds) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != behaviorIds.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " BehaviorIds=" + behaviorIds.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", behaviorIds.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk Behavior")
	public void Update_put_bulk_behavior() throws Exception {

		List<Integer> behaviorIds = ScenarioContext.Ids;

		if (behaviorIds == null || behaviorIds.isEmpty()) {
			throw new IllegalStateException("No BehaviorIds found. Make sure CREATE scenario ran before UPDATE.");
		}

		// 1️⃣ Generate bulk JSON
		String newJson = DataCreate.generateBehaviorBulkJson();

		// 2️⃣ Inject stored IDs
		newJson = injectBehaviorIdsIntoBulk(newJson, behaviorIds);

		// 3️⃣ Send PUT request
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(newJson).when().put(baseUrl + BehaviorBulkUpdate_endpoint).then()
				.extract().response();

		System.out.println("PUT Request Body:\n" + newJson);
		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = newJson;

		logToExtent("Update Behavior (PUT)", newJson, response);

		for (Integer id : behaviorIds) {
			captureDbSnapshot(id, "Update Bulk Behavior");
			// captureElasticScreenshot(id, "Update Bulk Behavior" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Behavior", null);
		}
	}

	@When("Delete Bulk Behavior")
	public void Delete_bulk_behavior() throws Exception {

		List<Integer> behaviorIds = ScenarioContext.Ids;

		if (behaviorIds == null || behaviorIds.isEmpty()) {
			throw new IllegalStateException("No BehaviorIds found. Run CREATE scenario first.");
		}

		// ✅ Build request JSON directly
		String updateRequestBody = new ObjectMapper().writeValueAsString(Map.of("trace", true, "skipErrors", true,
				"updateExisting", true, "bulk", behaviorIds, "skipSofDeleteValidation", false));

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(updateRequestBody).when().delete(baseUrl + BehaviorBulkDelete_endpoint)
				.then().extract().response();

		System.out.println("Delete Request Body:\n" + updateRequestBody);
		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = updateRequestBody;

		logToExtent("Update Behavior (Delete)", updateRequestBody, response);


	    for (Integer id : behaviorIds) {
	        captureDbSnapshot(id, "Delete Bulk Behavior");
	        // captureElasticScreenshot(id, "Delete Bulk Behavior");
	        
	    }

		for (Integer id : behaviorIds) {
			captureDbSnapshot(id, "Delete Bulk Behavior");
			// captureElasticScreenshot(id, "Delete Bulk Behavior");

		}

	}

	@Then("The Behavior response status code should be {int}")
	public void the_behavior_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The Behavior response should contain {string}")
	public void the_behavior_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}

	@Then("Validate API ES and DB for BehaviorId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_BehaviorId(String method, String skipEsToken, String excludeEverywhereCsv,
			String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (BehaviorId == 0) {
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
		String[] excludeReqOnly = parseCsvKeys(excludeReqCsv);
		String[] excludeEsOnly = parseCsvKeys(excludeEsCsv);

		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current != null) {
			current.info("🧪 Cross-layer validation for BehaviorId=" + BehaviorId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, BehaviorId,
				"behavior", // ES index suffix
				"Behavior", // DB table name
				skipEs, // <== comes from feature
				excludeEverywhere, // <== comes from feature
				excludeReqOnly, // <== comes from feature
				excludeEsOnly // <== comes from feature
		);
	}

	@Then("Validate Bulk Behavior Request Response ES DB")
	public void validate_bulk_behavior_request_response_es_db() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		int totalIds = 0;
		int failedIds = 0;

		ExtentTest parent = ExtentCucumberListener.getCurrentScenario();

		// ---------------- Parse request bulk ----------------
		Map<String, Object> reqRoot = mapper.readValue(ScenarioContext.RequestBodyForValidation,
				new TypeReference<Map<String, Object>>() {
				});

		List<Map<String, Object>> requestBulk = (List<Map<String, Object>>) reqRoot.get("bulk");

		// ---------------- Parse response DATA ----------------
		List<Map<String, Object>> responseData = response.jsonPath().getList("DATA");

		if (requestBulk == null || responseData == null) {
			parent.fail("Request bulk or response DATA is null");
			return;
		}

		parent.info("🔍 Bulk validation started | Request size=" + requestBulk.size() + " | Response size="
				+ responseData.size());

		// ---------------- Loop per response object ----------------
		for (int i = 0; i < responseData.size(); i++) {

			totalIds++;

			try {
				Map<String, Object> reqObj = requestBulk.get(i);
				Map<String, Object> respObj = responseData.get(i);

				int id = Integer.parseInt(respObj.get("id").toString());

				String requestJson = mapper.writeValueAsString(reqObj);
				String responseJson = mapper.writeValueAsString(respObj);

				parent.info("🔎 Validating BugType ID=" + id);
				ApiDataValidator.validateKeysAcrosRequestResponseEsDb(requestJson, responseJson, id, "behavior",
						"Behavior", true, new String[] { "deleted", "createdDate", "updatedDate" },
						new String[] { "id" }, new String[] { "_class" });

			} catch (Exception e) {
				failedIds++;
				parent.fail("❌ Validation exception: " + e.getMessage());
			}
		}

		// ---------------- BULK SUMMARY ----------------
		String bannerColor = (failedIds == 0) ? "#1e8e3e" : "#d93025";
		String statusWord = (failedIds == 0) ? "PASSED ✓" : "FAILED ✗";

		String summaryHtml = "<div style='padding:10px;border-left:6px solid " + bannerColor + ";'>"
				+ "<b>Bulk Behavior Validation " + statusWord + "</b><br>" + "Total IDs: " + totalIds + " | Passed: "
				+ (totalIds - failedIds) + " | Failed: " + failedIds + "</div>";

		if (failedIds > 0) {
			parent.fail(summaryHtml);
		} else {
			parent.pass(summaryHtml);
		}
		;
	}

	private String[] parseCsvKeys(String csv) {
		if (csv == null || csv.trim().isEmpty() || csv.equalsIgnoreCase("null") || csv.equalsIgnoreCase("none")
				|| csv.equals("-")) {
			return new String[] {}; // treat as no excludes
		}
		return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
	}

}
