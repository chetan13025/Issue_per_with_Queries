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

public class IssueLinked {
	
	public static int IssueLinkedId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> IssueLinkedIds;
	public static Integer RelatedIssueId,IssueId;
	public static String Relationship;
	public static Integer Post_RelatedIssueId,Post_IssueId;
	public static String Post_Relationship;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String IssueLinked_endpoint = ConfigReader.getProperty("issuelinked_endpoint");
	private static String IssueLinked_FindByRelationshipandRelatedIssueIdendpoint = ConfigReader.getProperty("issuelinked_findbyrelationandrela");
	private static String IssueLinked_FindByIssueIdandRelatedIssueIdRelationshipendpoint = ConfigReader.getProperty("issuelinked_findByIssueIdAndRelatedIssueIdAndRela");
	private static String IssueLinkedBulk_endpoint = ConfigReader.getProperty("issuelinkedbulk_endpoint");
	private static String IssueLinkedBulkUpdate_endpoint = ConfigReader.getProperty("issuelinkedbulku_endpoint");
	private static String IssueLinkedBulkDelete_endpoint = ConfigReader.getProperty("issuelinkedbulkd_endpoint");
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

	private void captureDbSnapshot(int IssueLinkedId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();

				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`IssueLinked` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`IssueLinked` "
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

				if (currentId == IssueLinkedId) {
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

	private void captureElasticScreenshot(int IssueLinkedId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_issue-linked_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + IssueLinkedId + "');");
			js.executeScript("var sel = window.getSelection();"
					+ "if (sel.rangeCount > 0) sel.getRangeAt(0).startContainer.parentElement.scrollIntoView({behavior: 'smooth', block: 'center'});");

			// Screenshot
//            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//            String path = "target/screenshots/es_" + bookId + "_" + action.replace(" ", "_") + ".png";
//            Files.createDirectories(Paths.get("target/screenshots/"));
//            File destFile = new File(path);
//            org.openqa.selenium.io.FileHandler.copy(srcFile, destFile);
			String IssueLinkedIdscreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			// Attach to Extent
//            ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//            if (current != null) {
//                current.info("Elasticsearch Verification for BookId: " + bookId);
//                current.addScreenCaptureFromPath(destFile.getAbsolutePath());
//            }
			ExtentTest current = ExtentCucumberListener.getCurrentScenario();
			if (current != null) {
				current.info("Elasticsearch Verification for IssueLinkedId: " + IssueLinkedId);
				current.addScreenCaptureFromBase64String(IssueLinkedIdscreenshot, "ElasticSearch_" + action.replace(" ", "_"));
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
	private void DBConnection(int IssueLinkedId, String action) {

	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();

	    try (Connection conn = DriverManager.getConnection(
	            "jdbc:mysql://10.10.2.45:3306/issue_persistence_piqa", "root", "dhi123")) {

	        // 🔥 If PATCH failed → use POST values
	        if (action.toLowerCase().contains("patch") && response.getStatusCode() != 200) {

	            System.out.println("⚠️ PATCH failed → using POST DB values");

	            IssueId = Post_IssueId;
	            Relationship = Post_Relationship;
	            RelatedIssueId = Post_RelatedIssueId;

	            if (current != null) {
	                current.warning("Using POST DB values due to PATCH failure");
	            }

	            return;
	        }

	        int retries = 5;

	        IssueId = null;
	        Relationship = null;
	        RelatedIssueId = null;

	        while (retries-- > 0) {

	            String query = "SELECT issueId, relatedIssueId, relationship " +
	                           "FROM IssueLinked " +
	                           "WHERE id = ? AND deleted = 0 AND issueId IS NOT NULL";

	            PreparedStatement ps = conn.prepareStatement(query);
	            ps.setInt(1, IssueLinkedId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {

	                Number issueIdTemp = (Number) rs.getObject("issueId");
	                Number relatedissueIdTemp = (Number) rs.getObject("relatedIssueId");
	                String relationshipTemp = rs.getString("relationship");

	                if (issueIdTemp != null && relatedissueIdTemp != null && relationshipTemp != null) {
	                    IssueId = issueIdTemp.intValue();
	                    Relationship = relationshipTemp;
	                    RelatedIssueId = relatedissueIdTemp.intValue();
	                    // ✅ Save POST values only
	                    if (action.toLowerCase().contains("post")) {
	                        Post_IssueId = IssueId;
	                        Post_Relationship = Relationship;
	                        Post_RelatedIssueId = RelatedIssueId;
	                    }

	                    break;
	                }
	            }

	            Thread.sleep(1000);
	        }

	        if (IssueId == null || Relationship == null || RelatedIssueId == null) {
	            throw new RuntimeException("❌ DB values not updated for IssueLinkedId: " + IssueLinkedId);
	        }

	        // ✅ Logging
	        System.out.println("DB issueId: " + IssueId);
	        System.out.println("DB Relationship: " + Relationship);
	        System.out.println("DB RelatedIssueId: " + RelatedIssueId);

	        if (current != null) {
	            current.info("DB Data after: " + action);
	            current.info("issueId: " + IssueId);
	            current.info("Relationship: " + Relationship);
	            current.info("RelatedIssueId: " + RelatedIssueId);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        if (current != null) {
	            current.fail("DB error: " + e.getMessage());
	        }
	    }
	}

	@Given("I have a IssueLinked")
	public void i_have_a_issuelinked() {
		requestBody = DataCreate.generateIssueLinkedJson();
		System.out.println("IssueLinked JSON:\n" + requestBody);
	}

	@When("Create New IssueLinked")
	public void Create_new_issuelinked() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueLinked_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		IssueLinkedId = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored IssueLinkedId: " + IssueLinkedId);
		logToExtent("Create IssueLinked Response", requestBody, response);
		captureDbSnapshot(IssueLinkedId, "Create New IssueLinked");
		DBConnection(IssueLinkedId, "After POST");
		// captureElasticScreenshot(IssueLinkedId, "Create IssueLinked");

//		captureElasticScreenshot(IssueLinkedId, "Create IssueLinked");

	}

	@When("Fetch All IssueLinked Details")
	public void Fetch_All_issuelinked_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + IssueLinked_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All IssueLinked Response", requestBody, response);
	}

	@When("Fetch IssueLinked Details with ID")
	public void Fetch_issuelinked_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueLinkedId).when().get(baseUrl + IssueLinked_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueLinked by ID", null, response);
	}

	@When("Update IssueLinked Details")
	public void Update_issueimpactarea_details() {
		if (IssueLinkedId == 0)
			throw new IllegalStateException("No IssueLinkedId available. Run POST first.");

		String newJson = DataCreate.generateIssueLinkedJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueLinkedId).body(newJson).when()
				.put(baseUrl + IssueLinked_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueLinked (PUT)", newJson, response);
		captureDbSnapshot(IssueLinkedId, "Update IssueLinked (PUT)");

		// captureElasticScreenshot(IssueLinkedId, "Update IssueLinked (PUT)");

//		captureElasticScreenshot(IssueLinkedId, "Update IssueLinked (PUT)");


	}

	@When("Update Patch IssueLinked")
	public void Update_Patch_issueimpactarea() {
		if (IssueLinkedId == 0)
			throw new IllegalStateException("No IssueLinkedId available. Run POST first.");

		String newJson = DataCreate.generateIssueLinkedJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueLinkedId).body(newJson).when()
				.patch(baseUrl + IssueLinked_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
//		Relationship = response.jsonPath().get("relationship");
//		RelatedIssueId = response.jsonPath().getInt("relatedIssueId");
//		IssueId = response.jsonPath().getInt("issueId");
		DBConnection(IssueLinkedId, "After PATCH");
		RequestBodyForValidation=newJson;
		logToExtent("Update IssueLinked (PATCH)", newJson, response);
		captureDbSnapshot(IssueLinkedId, "Update IssueLinked (PATCH)");

		// captureElasticScreenshot(IssueLinkedId, "Update IssueLinked (PATCH)");

//		captureElasticScreenshot(IssueLinkedId, "Update IssueLinked (PATCH)");


	}
	
	@When("FindByRelationshipAndRelatedIssueIdInOrderByPositionDesc")
	public void findByRelationshipAndRelatedIssueIdInOrderByPositionDesc() {
		response = given().header("Content-Type", "application/json").header("Accept", "application/hal+json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("relationship", Relationship).queryParam("relatedIssueId", RelatedIssueId).when().get(baseUrl + IssueLinked_FindByRelationshipandRelatedIssueIdendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueLinked by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("relationship", Relationship);
		params.put("relatedIssueId", RelatedIssueId);
		ApiValidators.validateQueryParamsInList(response,"_embedded.issueLinkeds",params);
	}
	
	@When("FindByIssueIdAndRelatedIssueIdAndRelationship")
	public void findByIssueIdAndRelatedIssueIdAndRelationship() {
		response = given().header("Content-Type", "application/json").header("Accept", "application/hal+json").header("X-TENANT-ID", tenantId)
				.header("Author", author)
				.queryParam("issueId", IssueId).queryParam("relationship", Relationship)
				.queryParam("relatedIssueId", RelatedIssueId).when().get(baseUrl + IssueLinked_FindByIssueIdandRelatedIssueIdRelationshipendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch IssueLinked by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("issueId", IssueId);
		params.put("relatedIssueId", RelatedIssueId);
		params.put("relationship", Relationship);
		ApiValidators.validateQueryParamsInList(response,"_embedded.issueLinkeds",params);
	}

	@When("Delete IssueLinked with ID")
	public void delete_issueimpactarea_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", IssueLinkedId).when().delete(baseUrl + IssueLinked_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete IssueLinked", null, response);
		captureDbSnapshot(IssueLinkedId, "Delete IssueLinked");

		// captureElasticScreenshot(IssueLinkedId, "Delete IssueLinked");

//		captureElasticScreenshot(IssueLinkedId, "Delete IssueLinked");

	}
	
	@Given("I have Bulk IssueLinked")
	public void i_have_a_bulk_IssueLinked() {
		requestBody = DataCreate.generateIssueLinkedBulkJson();
		System.out.println("IssueLinked JSON:\n" + requestBody);

	}

	@When("Create New Bulk IssueLinked")
	public void Create_new_bulk_IssueLinked() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + IssueLinkedBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored IssueLinkedIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create IssueLinked Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id,  "Create Bulk IssueLinked"+": "+id);

			// captureElasticScreenshot(id,  "Create Bulk IssueLinked"+": "+id);

//			captureElasticScreenshot(id,  "Create Bulk IssueLinked"+": "+id);

			ApiDataValidator.validateDbNotNullExcept(id, "IssueLinked", null);
		}
	}

	private String injectIdsIntoBulk(String json, List<Integer> Ids) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != Ids.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " IssueLinkedIds=" + Ids.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", Ids.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk IssueLinked")
	public void Update_put_bulk_IssueLinked() throws Exception {

		IssueLinkedIds = ScenarioContext.Ids;

	    if (IssueLinkedIds == null || IssueLinkedIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueLinkedIds found. Make sure CREATE scenario ran before UPDATE."
	        );
	    }

	    // 1️⃣ Generate bulk JSON
	    String newJson = DataCreate.generateIssueLinkedBulkJson();

	    // 2️⃣ Inject stored IDs
	    newJson = injectIdsIntoBulk(newJson, IssueLinkedIds);

	    // 3️⃣ Send PUT request
	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(newJson)
	            .when()
	            .put(baseUrl + IssueLinkedBulkUpdate_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("PUT Request Body:\n" + newJson);
	    System.out.println("PUT Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = newJson;

	    logToExtent("Update IssueLinked (PUT)", newJson, response);

	    for (Integer id : IssueLinkedIds) {
	    	captureDbSnapshot(id, "Update IssueLinked (PUT)"+": "+id);

			// captureElasticScreenshot(id, "Update IssueLinked (PUT)"+": "+id);

//			captureElasticScreenshot(id, "Update IssueLinked (PUT)"+": "+id);

			ApiDataValidator.validateDbNotNullExcept(id, "IssueLinked", null);
	    }
	}

	
	@When("Delete Bulk IssueLinked")
	public void Delete_bulk_IssueLinked() throws Exception {

		IssueLinkedIds = ScenarioContext.Ids;

	    if (IssueLinkedIds == null || IssueLinkedIds.isEmpty()) {
	        throw new IllegalStateException(
	                "No IssueLinkedIds found. Run CREATE scenario first."
	        );
	    }

	    // ✅ Build request JSON directly
	    String updateRequestBody = new ObjectMapper()
	            .writeValueAsString(Map.of(
	                    "trace", true,
	                    "skipErrors", true,
	                    "updateExisting", true,
	                    "bulk", IssueLinkedIds,
	                    "skipSofDeleteValidation", false
	            ));

	    response = given()
	            .header("Content-Type", "application/json")
	            .header("X-TENANT-ID", tenantId)
	            .header("Author", author)
	            .body(updateRequestBody)
	            .when()
	            .delete(baseUrl + IssueLinkedBulkDelete_endpoint)
	            .then()
	            .extract()
	            .response();

	    System.out.println("Delete Request Body:\n" + updateRequestBody);
	    System.out.println("Delete Response Body:\n" + response.getBody().asString());
	    System.out.println("Status Code: " + response.getStatusCode());

	    ScenarioContext.RequestBodyForValidation = updateRequestBody;

	    logToExtent(" IssueLinked (Delete)", updateRequestBody, response);

	    for (Integer id : IssueLinkedIds) {
	    	captureDbSnapshot(id, "Delete IssueLinked"+": "+id);

			// captureElasticScreenshot(id, "Delete IssueLinked"+": "+id);

//			captureElasticScreenshot(id, "Delete IssueLinked"+": "+id);

	    }
	}
		
	@Then("Validate Bulk IssueLinked Request Response ES DB")
	public void validate_bulk_IssueLinked_request_response_es_db() throws Exception {

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
	                    "issue-linked",
	                    "IssueLinked",
	                    true,
	                    new String[]{
	                            "deleted"
	                    },
	                    new String[]{"id","updatedate","createdate","linkeddate"},
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

	@Then("The IssueLinked response status code should be {int}")
	public void the_issueimpactarea_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The IssueLinked response should contain {string}")
	public void the_issueimpactarea_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	@Then("Validate API ES and DB for IssueLinkedId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_IssueLinkedId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (IssueLinkedId == 0) {
			throw new IllegalStateException("No IssueLinkedId available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for IssueLinkedId=" + IssueLinkedId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, IssueLinkedId,
				"issue-linked", // ES index suffix
				"IssueLinked", // DB table name
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
