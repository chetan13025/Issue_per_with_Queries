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

public class Nature {
	public static int NatureId;
	private Response response;
	private String requestBody;
	private String RequestBodyForValidation;
	public List<Integer> NatureIds;
	public static Integer ProjectId;
	public static Boolean IsDefault;
	public static Integer Post_ProjectId;
	public static Boolean Post_IsDefault;
	private static String baseUrl = ConfigReader.getProperty("baseUrl");
	private static String tenantId = ConfigReader.getProperty("tenantId");
	private static String author = ConfigReader.getProperty("author");
	private static String Nature_endpoint = ConfigReader.getProperty("nature_endpoint");
	private static String Nature_FindByProjectIdendpoint = ConfigReader.getProperty("nature_findbyprojectid");
	private static String Nature_FindByProjectIdandIsDefaultendpoint = ConfigReader.getProperty("nature_findbyprojectidandisdefault");
	private static String NatureBulk_endpoint = ConfigReader.getProperty("naturebulk_endpoint");
	private static String NatureBulkUpdate_endpoint = ConfigReader.getProperty("naturebulku_endpoint");
	private static String NatureBulkDelete_endpoint = ConfigReader.getProperty("naturebulkd_endpoint");
	
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

	private void captureDbSnapshot(int NatureId, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				// ResultSet rs = stmt.executeQuery("SELECT * FROM `issue_persistence_piqa`.`Nature` LIMIT 1000"))
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM (" + "   SELECT * FROM `issue_persistence_piqa`.`Nature` "
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

				if (currentId == NatureId) {
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

	private void captureElasticScreenshot(int NatureId, String action) {
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
			String url = "http://10.10.2.45:9200/piqa_issue-persistence_nature_index/_search?pretty&size=2000";
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
			js.executeScript("window.find('\"id\" : " + NatureId + "');");
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
				current.info("Elasticsearch Verification for NatureId: " + NatureId);
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
	
	private void DBConnection(int NatureId, String action) {

	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();

	    try (Connection conn = DriverManager.getConnection(
	            "jdbc:mysql://10.10.2.45:3306/issue_persistence_piqa", "root", "dhi123")) {

	        // 🔥 If PATCH failed → use POST values
	        if (action.toLowerCase().contains("patch") && response.getStatusCode() != 200) {

	            System.out.println("⚠️ PATCH failed → using POST DB values");

	            ProjectId = Post_ProjectId;
	            IsDefault = Post_IsDefault;

	            if (current != null) {
	                current.warning("Using POST DB values due to PATCH failure");
	            }

	            return;
	        }

	        int retries = 5;

	        ProjectId = null;
	        IsDefault = null;

	        while (retries-- > 0) {

	            String query = "SELECT projectId, isDefault " +
	                           "FROM Nature " +
	                           "WHERE id = ? AND deleted = 0 AND projectId IS NOT NULL";

	            PreparedStatement ps = conn.prepareStatement(query);
	            ps.setInt(1, NatureId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {

	                Number issueIdTemp = (Number) rs.getObject("projectId");
	                Boolean behaviorTypeTemp = rs.getBoolean("isDefault");

	                if (issueIdTemp != null && behaviorTypeTemp != null) {
	                	ProjectId = issueIdTemp.intValue();
	                	IsDefault = behaviorTypeTemp;

	                    // ✅ Save POST values only
	                    if (action.toLowerCase().contains("post")) {
	                    	Post_ProjectId = ProjectId;
	                    	Post_IsDefault = IsDefault;
	                    }

	                    break;
	                }
	            }

	            Thread.sleep(1000);
	        }

	        if (ProjectId == null || IsDefault == null) {
	            throw new RuntimeException("❌ DB values not updated for NatureId: " + NatureId);
	        }

	        // ✅ Logging
	        System.out.println("DB ProjectId: " + ProjectId);
	        System.out.println("DB IsDefault: " + IsDefault);

	        if (current != null) {
	            current.info("DB Data after: " + action);
	            current.info("ProjectId: " + ProjectId);
	            current.info("IsDefault: " + IsDefault);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        if (current != null) {
	            current.fail("DB error: " + e.getMessage());
	        }
	    }
	}
	@Given("I have a Nature")
	public void i_have_a_nature() {
		requestBody = DataCreate.generateNatureJson();
		System.out.println("Nature JSON:\n" + requestBody);
	}

	@When("Create New Nature")
	public void Create_new_nature() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + Nature_endpoint).then().extract()
				.response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		NatureId = response.jsonPath().getInt("id");
		RequestBodyForValidation=requestBody;
		System.out.println("Stored NatureId: " + NatureId);
		logToExtent("Create Nature Response", requestBody, response);
		captureDbSnapshot(NatureId, "Create New Nature");
		DBConnection(NatureId, "After POST");
		// captureElasticScreenshot(NatureId, "Create New Nature");
	}
	
	@When("Fetch All Nature Details")
	public void Fetch_All_nature_Details() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).when().get(baseUrl + Nature_endpoint).then().extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		logToExtent("Fetch All Nature Response", requestBody, response);
	}
	
	@When("Fetch Nature Details with ID")
	public void Fetch_nature_Details_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", NatureId).when().get(baseUrl + Nature_endpoint + "/{id}").then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Nature by ID", null, response);
	}
	
	@When("Update Nature Details")
	public void Update_nature_details() {
		if (NatureId == 0)
			throw new IllegalStateException("No NatureId available. Run POST first.");

		String newJson = DataCreate.generateNatureJson();

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", NatureId).body(newJson).when()
				.put(baseUrl + Nature_endpoint + "/{id}").then().extract().response();

		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		RequestBodyForValidation=newJson;
		logToExtent("Update Nature (PUT)", newJson, response);
		captureDbSnapshot(NatureId, "Update Nature (PUT)");
		ApiValidators.validatePathParamToResponse(response, "id", NatureId);
		// captureElasticScreenshot(NatureId, "Update Nature (PUT)");
		

	}
	
	@When("Update Patch Nature")
	public void Update_Patch_nature() {
		if (NatureId == 0)
			throw new IllegalStateException("No NatureId available. Run POST first.");

		String newJson = DataCreate.generateNatureJson();
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", NatureId).body(newJson).when()
				.patch(baseUrl + Nature_endpoint + "/{id}").then().extract().response();

		System.out.println("PATCH Request Body:\n" + newJson);
		System.out.println("PATCH Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
//		ProjectId = response.jsonPath().getInt("projectId");
//		IsDefault= response.jsonPath().getBoolean("isDefault");
		RequestBodyForValidation=newJson;
		DBConnection(NatureId, "After PATCH");
		logToExtent("Update Nature (PATCH)", newJson, response);
		captureDbSnapshot(NatureId, "Update Nature (PATCH)");
		ApiValidators.validatePathParamToResponse(response, "id", NatureId);
//		ApiValidators.validateRequestToResponse(newJson, response, IssueTypeId, "comments");
		// captureElasticScreenshot(NatureId, "Update Nature (PATCH)");

	}
	
	@When("FindByProjectId For Nature")
	public void findByProjectId_for_Nature() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("projectId", ProjectId).when().get(baseUrl + Nature_FindByProjectIdendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Nature by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("projectId", ProjectId);   
		ApiValidators.validateQueryParamsInList(response,"_embedded.natures",params);
	}
	
	@When("FindByProjectIdAndIsDefault For Nature")
	public void findByProjectIdAndIsDefault_for_nature() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).queryParam("projectId", ProjectId).queryParam("isDefault", IsDefault).when().get(baseUrl + Nature_FindByProjectIdandIsDefaultendpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());
		logToExtent("Fetch Nature by ID", null, response);
		Map<String, Object> params = new HashMap<>();
		params.put("projectId", ProjectId);
		params.put("isDefault", IsDefault);
		ApiValidators.validateQueryParamsInList(response,"_embedded.natures",params);
	}
	
	@When("Delete Nature with ID")
	public void delete_nature_with_ID() {
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).pathParam("id", NatureId).when().delete(baseUrl + Nature_endpoint + "/{id}")
				.then().extract().response();

		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Delete Status Code: " + response.getStatusCode());

		logToExtent("Delete Nature", null, response);
		captureDbSnapshot(NatureId, "Delete Nature");
		// captureElasticScreenshot(NatureId, "Delete Nature");
	}
	
	@Given("I have Bulk Nature")
	public void i_have_a_bulk_Nature() {
		requestBody = DataCreate.generateNatureBulkJson();
		System.out.println("Nature JSON:\n" + requestBody);

	}

	@When("Create New Bulk Nature")
	public void Create_new_bulk_Nature() throws Exception {

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(requestBody).when().post(baseUrl + NatureBulk_endpoint).then()
				.extract().response();

		System.out.println("Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		// ✅ STORE IDs FROM DATA[]
		List<Integer> idsFromResponse = response.jsonPath().getList("DATA.id");

		ScenarioContext.Ids.clear();
		ScenarioContext.Ids.addAll(idsFromResponse);

		System.out.println("Stored NatureIds: " + ScenarioContext.Ids);

		ScenarioContext.RequestBodyForValidation = requestBody;

		logToExtent("Create Nature Response", requestBody, response);

		for (Integer id : ScenarioContext.Ids) {
			captureDbSnapshot(id, "Create Bulk Nature" + ": " + id);
			// captureElasticScreenshot(id, "Create Bulk Nature" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Nature", null);
		}
	}

	private String injectIdsIntoBulk(String json, List<Integer> Ids) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<Map<String, Object>> bulk = (List<Map<String, Object>>) root.get("bulk");

		if (bulk.size() != Ids.size()) {
			throw new IllegalStateException(
					"Bulk size mismatch. bulk=" + bulk.size() + " NatureIds=" + Ids.size());
		}

		for (int i = 0; i < bulk.size(); i++) {
			bulk.get(i).put("id", Ids.get(i));
		}

		return mapper.writeValueAsString(root);
	}

	@When("Update PUT Bulk Nature")
	public void Update_put_bulk_Nature() throws Exception {

		NatureIds = ScenarioContext.Ids;

		if (NatureIds == null || NatureIds.isEmpty()) {
			throw new IllegalStateException("No NatureIds found. Make sure CREATE scenario ran before UPDATE.");
		}

		// 1️⃣ Generate bulk JSON
		String newJson = DataCreate.generateNatureBulkJson();

		// 2️⃣ Inject stored IDs
		newJson = injectIdsIntoBulk(newJson, NatureIds);

		// 3️⃣ Send PUT request
		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(newJson).when().put(baseUrl + NatureBulkUpdate_endpoint).then()
				.extract().response();

		System.out.println("PUT Request Body:\n" + newJson);
		System.out.println("PUT Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = newJson;

		logToExtent("Update Nature (PUT)", newJson, response);

		for (Integer id : NatureIds) {
			captureDbSnapshot(id, "Update Nature (PUT)" + ": " + id);
			// captureElasticScreenshot(id, "Update Nature (PUT)" + ": " + id);
			ApiDataValidator.validateDbNotNullExcept(id, "Nature", null);
		}
	}

	@When("Delete Bulk Nature")
	public void Delete_bulk_Nature() throws Exception {

		NatureIds = ScenarioContext.Ids;

		if (NatureIds == null || NatureIds.isEmpty()) {
			throw new IllegalStateException("No NatureIds found. Run CREATE scenario first.");
		}

		// ✅ Build request JSON directly
		String updateRequestBody = new ObjectMapper().writeValueAsString(Map.of("trace", true, "skipErrors", true,
				"updateExisting", true, "bulk", NatureIds, "skipSofDeleteValidation", false));

		response = given().header("Content-Type", "application/json").header("X-TENANT-ID", tenantId)
				.header("Author", author).body(updateRequestBody).when().delete(baseUrl + NatureBulkDelete_endpoint)
				.then().extract().response();

		System.out.println("Delete Request Body:\n" + updateRequestBody);
		System.out.println("Delete Response Body:\n" + response.getBody().asString());
		System.out.println("Status Code: " + response.getStatusCode());

		ScenarioContext.RequestBodyForValidation = updateRequestBody;

		logToExtent(" Nature (Delete)", updateRequestBody, response);

		for (Integer id : NatureIds) {
			captureDbSnapshot(id, "Delete Nature" + ": " + id);
			// captureElasticScreenshot(id, "Delete Nature" + ": " + id);
		}
	}

	@Then("Validate Bulk Nature Request Response ES DB")
	public void validate_bulk_Nature_request_response_es_db() throws Exception {

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
				ApiDataValidator.validateKeysAcrosRequestResponseEsDb(requestJson, responseJson, id, "nature",
						"Nature", true, new String[] { "deleted" },
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
	
	@Then("The Nature response status code should be {int}")
	public void the_nature_response_status_code_should_be(Integer statusCode) {
		assertEquals(statusCode.intValue(), response.getStatusCode());

	}

	@Then("The Nature response should contain {string}")
	public void the_nature_response_should_contain(String key) {
		assertTrue("Response does not contain key: " + key, response.getBody().asString().contains(key));

	}
	
	@Then("Validate API ES and DB for NatureId after {word} skipping ES {word} excludingEverywhere {string} excludeFromRequest {string} excludeFromEs {string}")
	public void validate_api_es_db_for_NatureId_dynamic(String method, String skipEsToken,
			String excludeEverywhereCsv, String excludeReqCsv, String excludeEsCsv) throws Exception {

		if (NatureId == 0) {
			throw new IllegalStateException("No NatureId available. Run POST/PUT/PATCH first.");
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
			current.info("🧪 Cross-layer validation for NatureId=" + NatureId + " after " + method.toUpperCase()
					+ " | skipEs=" + skipEs);
			current.info("ExcludeEverywhere=" + excludeEverywhereCsv + " | RequestOnly=" + excludeReqCsv + " | EsOnly="
					+ excludeEsCsv);
		}

		// 🔥 your core call, now parameterised
		ApiDataValidator.validateKeysAcrosRequestResponseEsDb(RequestBodyForValidation, response, NatureId,
				"nature", // ES index suffix
				"Nature", // DB table name
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
