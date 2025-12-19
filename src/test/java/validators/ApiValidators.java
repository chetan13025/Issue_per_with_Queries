//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//
//import io.restassured.path.json.JsonPath;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//public class ApiValidators {
//
//    private static ExtentTest log() {
//        return ExtentCucumberListener.getCurrentScenario();
//    }
//
//    private static void ensureRequestJsonPresent(String requestJson) {
//        if (requestJson == null || requestJson.trim().isEmpty()) {
//            ExtentTest t = log();
//            if (t != null) t.fail("❌ Request JSON is null/empty. Capture the request body before calling validator.");
//            throw new IllegalArgumentException("Request JSON is null/empty");
//        }
//    }
//
//    private static void ensureJsonPathLooksValid(String jsonPath) {
//        if (jsonPath == null || jsonPath.trim().isEmpty()) {
//            ExtentTest t = log();
//            if (t != null) t.fail("❌ JSONPath is null/empty. Pass a key like \"resourceName\", not a value.");
//            throw new IllegalArgumentException("JSONPath is null/empty");
//        }
//        // common mistake: passing a literal value like "Enda Kilback"
//        if (jsonPath.contains(" ")) {
//            ExtentTest t = log();
//            if (t != null) t.fail("❌ You passed a VALUE as JSONPath: \"" + jsonPath +
//                    "\". Use a key/path like \"resourceName\" or \"customTemplate.id\".");
//            throw new IllegalArgumentException("Value provided instead of JSONPath key");
//        }
//    }
//
//    /** Same key in request & response */
//    public static void validateRequestToResponse(String requestJson, Response response, String jsonPath) {
//        ensureRequestJsonPresent(requestJson);
//        ensureJsonPathLooksValid(jsonPath);
//
//        String reqVal  = JsonPath.from(requestJson).getString(jsonPath);
//        String respVal = response.jsonPath().getString(jsonPath);
//
//        ExtentTest t = log();
//        if (equalsNullable(reqVal, respVal)) {
//            if (t != null) t.pass("✅ Match [" + jsonPath + "] → " + safe(reqVal));
//        } else {
//            if (t != null) t.fail("❌ Mismatch [" + jsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//            throw new AssertionError("Request vs Response mismatch for key: " + jsonPath);
//        }
//    }
//
//    /** Different keys in request & response */
//    public static void validateRequestToResponse(String requestJson, Response response,
//                                                 String requestJsonPath, String responseJsonPath) {
//        ensureRequestJsonPresent(requestJson);
//        ensureJsonPathLooksValid(requestJsonPath);
//        ensureJsonPathLooksValid(responseJsonPath);
//
//        String reqVal  = JsonPath.from(requestJson).getString(requestJsonPath);
//        String respVal = response.jsonPath().getString(responseJsonPath);
//
//        ExtentTest t = log();
//        if (equalsNullable(reqVal, respVal)) {
//            if (t != null) t.pass("✅ Match [" + requestJsonPath + " -> " + responseJsonPath + "] → " + safe(reqVal));
//        } else {
//            if (t != null) t.fail("❌ Mismatch [" + requestJsonPath + " -> " + responseJsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//            throw new AssertionError("Request vs Response mismatch for mapping: " + requestJsonPath + " -> " + responseJsonPath);
//        }
//    }
//
//    private static boolean equalsNullable(String a, String b) {
//        if (a == null && b == null) return true;
//        if (a == null || b == null) return false;
//        return a.trim().equalsIgnoreCase(b.trim());
//    }
//
//    private static String safe(String s) {
//        return s == null ? "null" : s;
//    }
//}
package validators;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import reports.ExtentCucumberListener;

public class ApiValidators {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules()
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	private static ExtentTest log() {
		return ExtentCucumberListener.getCurrentScenario();
	}

	private static void ensureRequestJsonPresent(String requestJson) {
		if (requestJson == null || requestJson.trim().isEmpty()) {
			ExtentTest t = log();
			if (t != null)
				t.fail("❌ Request JSON is null/empty. Capture the request body before calling validator.");
			throw new IllegalArgumentException("Request JSON is null/empty");
		}
	}

	private static void ensureJsonPathLooksValid(String jsonPath) {
		if (jsonPath == null || jsonPath.trim().isEmpty()) {
			ExtentTest t = log();
			if (t != null)
				t.fail("❌ JSONPath is null/empty. Pass a key like \"resourceName\", not a value.");
			throw new IllegalArgumentException("JSONPath is null/empty");
		}
		if (jsonPath.contains(" ")) {
			ExtentTest t = log();
			if (t != null)
				t.fail("❌ You passed a VALUE as JSONPath: \"" + jsonPath
						+ "\". Use a key/path like \"resourceName\" or \"customTemplate.id\".");
			throw new IllegalArgumentException("Value provided instead of JSONPath key");
		}
	}

	/** Same key in request & response */
//    public static void validateRequestToResponse(String requestJson, Response response, String jsonPath) {
//        ensureRequestJsonPresent(requestJson);
//        ensureJsonPathLooksValid(jsonPath);
//
//        Object reqObj  = JsonPath.from(requestJson).get(jsonPath);
//        Object respObj = response.jsonPath().get(jsonPath);
//
//        String reqVal  = toScalarString(reqObj);
//        String respVal = toScalarString(respObj);
//
//        ExtentTest t = log();
//        if (t != null) {
//            t.info("🔎 Compare [" + jsonPath + "]\n" +
//                   "   • Request : " + safe(reqVal) + "\n" +
//                   "   • Response: " + safe(respVal));
//        }
//
//        if (equalsNullable(reqVal, respVal)) {
//            if (t != null) t.pass("✅ Match [" + jsonPath + "] → " + safe(reqVal));
//        } else {
//            if (t != null) t.fail("❌ Mismatch [" + jsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//            throw new AssertionError("Request vs Response mismatch for key: " + jsonPath);
//        }
//    }
//    public static void validateRequestToResponse(String requestJson, Response response,String... jsonPaths) {
//        ensureRequestJsonPresent(requestJson);
//
//        if (jsonPaths == null || jsonPaths.length == 0) {
//            throw new IllegalArgumentException("At least one JSONPath is required");
//        }
//
//        for (String jsonPath : jsonPaths) {
//            ensureJsonPathLooksValid(jsonPath);
//
//            Object reqObj  = JsonPath.from(requestJson).get(jsonPath);
//            Object respObj = response.jsonPath().get(jsonPath);
//
//            String reqVal  = toScalarString(reqObj);
//            String respVal = toScalarString(respObj);
//
//            ExtentTest t = log();
//            if (t != null) {
//                t.info("🔎 Compare [" + jsonPath + "]\n" +
//                       "   • Request : " + safe(reqVal) + "\n" +
//                       "   • Response: " + safe(respVal));
//            }
//
//            if (equalsNullable(reqVal, respVal)) {
//                if (t != null) t.pass("✅ Match [" + jsonPath + "] → " + safe(reqVal));
//            } else {
//                if (t != null) t.fail("❌ Mismatch [" + jsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//                throw new AssertionError("Request vs Response mismatch for key: " + jsonPath);
//            }
//        }
//    }

	// -----------------------
	// Existing validateRequestToResponse for same-names (varargs) — unchanged:
	public static void validateRequestToResponse(String requestJson, Response response, String... jsonPaths) {
		ensureRequestJsonPresent(requestJson);

		if (jsonPaths == null || jsonPaths.length == 0) {
			throw new IllegalArgumentException("At least one JSONPath is required");
		}

		boolean anyFailure = false;
		ExtentTest t = log();

		for (String jsonPath : jsonPaths) {
			ensureJsonPathLooksValid(jsonPath);

			Object reqObj = null;
			Object respObj = null;
			try {
				reqObj = JsonPath.from(requestJson).get(jsonPath);
			} catch (Exception e) {
				if (t != null)
					t.warning("⚠️ Could not read request JSONPath '" + jsonPath + "': " + e.getMessage());
			}
			try {
				respObj = response.jsonPath().get(jsonPath);
			} catch (Exception e) {
				if (t != null)
					t.warning("⚠️ Could not read response JSONPath '" + jsonPath + "': " + e.getMessage());
			}

			String reqVal = toScalarString(reqObj);
			String respVal = toScalarString(respObj);

			if (t != null) {
				t.info("🔎 Compare [" + jsonPath + "]\n" + "   • Request : " + safe(reqVal) + "\n" + "   • Response: "
						+ safe(respVal));
			}

			if (equalsNullable(reqVal, respVal)) {
				if (t != null)
					t.pass("✅ Match [" + jsonPath + "] → " + safe(reqVal));
			} else {
				if (t != null)
					t.fail("❌ Mismatch [" + jsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
				anyFailure = true;
			}
		}

		if (anyFailure) {
			throw new AssertionError("One or more Request vs Response validations failed.");
		}
	}

//	public static void validateRequestToResponse(String requestJson, Response response, int pathParamId,
//			String... jsonPaths) {
//		validateRequestToResponse(requestJson, response, "id", pathParamId, jsonPaths);
//	}

//	public static void validateRequestToResponse(String requestJson, Response response, String responseIdJsonPath,
//			int expectedPathParamId, String... jsonPaths) {
//		ensureRequestJsonPresent(requestJson);
//		ExtentTest t = log();
//		boolean anyFailure = false;
//
//		// 1) Validate path param id vs response id
//		try {
//			ensureJsonPathLooksValid(responseIdJsonPath);
//			Integer actualId = null;
//			try {
//				// try as int first
//				actualId = response.jsonPath().getInt(responseIdJsonPath);
//			} catch (Exception e) {
//				// maybe it's a string that contains an int
//				try {
//					String s = response.jsonPath().getString(responseIdJsonPath);
//					if (s != null && !s.trim().isEmpty())
//						actualId = Integer.parseInt(s.trim());
//				} catch (Exception ex) {
//					// leave as null
//				}
//			}
//
//			if (t != null) {
//				t.info("🔎 Compare PathParam vs Response id\n" + "   • Expected (path param) : " + expectedPathParamId
//						+ "\n" + "   • Actual   (response " + responseIdJsonPath + ") : "
//						+ (actualId == null ? "null" : actualId));
//			}
//
//			if (actualId == null || actualId != expectedPathParamId) {
//				if (t != null)
//					t.fail("❌ PathParam id mismatch. Expected " + expectedPathParamId + " but response has "
//							+ (actualId == null ? "null" : actualId));
//				anyFailure = true;
//			} else {
//				if (t != null)
//					t.pass("✅ PathParam id matches response (" + expectedPathParamId + ")");
//			}
//		} catch (Exception e) {
//			if (t != null)
//				t.warning("⚠️ Could not validate PathParam id: " + e.getMessage());
//			anyFailure = true;
//		}
//
//		// 2) Validate the provided request->response jsonPaths (reuse existing logic,
//		// but don't throw early)
//		if (jsonPaths != null && jsonPaths.length > 0) {
//			for (String jsonPath : jsonPaths) {
//				try {
//					ensureJsonPathLooksValid(jsonPath);
//				} catch (Exception ex) {
//					if (t != null)
//						t.warning("⚠️ Skipping invalid jsonPath '" + jsonPath + "': " + ex.getMessage());
//					anyFailure = true;
//					continue;
//				}
//
//				Object reqObj = null;
//				Object respObj = null;
//				try {
//					reqObj = JsonPath.from(requestJson).get(jsonPath);
//				} catch (Exception e) {
//					if (t != null)
//						t.warning("⚠️ Could not read request JSONPath '" + jsonPath + "': " + e.getMessage());
//				}
//				try {
//					respObj = response.jsonPath().get(jsonPath);
//				} catch (Exception e) {
//					if (t != null)
//						t.warning("⚠️ Could not read response JSONPath '" + jsonPath + "': " + e.getMessage());
//				}
//
//				String reqVal = toScalarString(reqObj);
//				String respVal = toScalarString(respObj);
//
//				if (t != null) {
//					t.info("🔎 Compare [" + jsonPath + "]\n" + "   • Request : " + safe(reqVal) + "\n"
//							+ "   • Response: " + safe(respVal));
//				}
//
//				if (equalsNullable(reqVal, respVal)) {
//					if (t != null)
//						t.pass("✅ Match [" + jsonPath + "] → " + safe(reqVal));
//				} else {
//					if (t != null)
//						t.fail("❌ Mismatch [" + jsonPath + "] → request=" + safe(reqVal) + " | response="
//								+ safe(respVal));
//					anyFailure = true;
//				}
//			}
//		}
//
//		if (anyFailure) {
//			throw new AssertionError("One or more validations failed (see report for details).");
//		}
//	}
//    /** Different keys in request & response */
//    public static void validateRequestToResponse(String requestJson, Response response,
//                                                 String requestJsonPath, String responseJsonPath) {
//        ensureRequestJsonPresent(requestJson);
//        ensureJsonPathLooksValid(requestJsonPath);
//        ensureJsonPathLooksValid(responseJsonPath);
//
//        Object reqObj  = JsonPath.from(requestJson).get(requestJsonPath);
//        Object respObj = response.jsonPath().get(responseJsonPath);
//
//        String reqVal  = toScalarString(reqObj);
//        String respVal = toScalarString(respObj);
//
//        ExtentTest t = log();
//        if (t != null) {
//            t.info("🔎 Compare [" + requestJsonPath + " -> " + responseJsonPath + "]\n" +
//                   "   • Request : " + safe(reqVal) + "\n" +
//                   "   • Response: " + safe(respVal));
//        }
//
//        if (equalsNullable(reqVal, respVal)) {
//            if (t != null) t.pass("✅ Match [" + requestJsonPath + " -> " + responseJsonPath + "] → " + safe(reqVal));
//        } else {
//            if (t != null) t.fail("❌ Mismatch [" + requestJsonPath + " -> " + responseJsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//            throw new AssertionError("Request vs Response mismatch for mapping: " + requestJsonPath + " -> " + responseJsonPath);
//        }
//    }
//
//    /** Optional: case-sensitive variant */
//    public static void validateRequestToResponseCaseSensitive(String requestJson, Response response, String jsonPath) {
//        ensureRequestJsonPresent(requestJson);
//        ensureJsonPathLooksValid(jsonPath);
//
//        String reqVal  = toScalarString(JsonPath.from(requestJson).get(jsonPath));
//        String respVal = toScalarString(response.jsonPath().get(jsonPath));
//
//        ExtentTest t = log();
//        if (t != null) {
//            t.info("🔎 Compare (case-sensitive) [" + jsonPath + "]\n" +
//                   "   • Request : " + safe(reqVal) + "\n" +
//                   "   • Response: " + safe(respVal));
//        }
//
//        if (equalsExact(reqVal, respVal)) {
//            if (t != null) t.pass("✅ Match (case-sensitive) [" + jsonPath + "] → " + safe(reqVal));
//        } else {
//            if (t != null) t.fail("❌ Mismatch (case-sensitive) [" + jsonPath + "] → request=" + safe(reqVal) + " | response=" + safe(respVal));
//            throw new AssertionError("Request vs Response mismatch (case-sensitive) for key: " + jsonPath);
//        }
//    }

	public static void captureDbSnapshot(int recordId, String tableName, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		String sql = "SELECT * FROM `issue_persistence_piqa`.`" + tableName + "` LIMIT 1000";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();

			StringBuilder html = new StringBuilder("<table border='1' style='border-collapse:collapse;'>");

			// 🔹 Table Header
			html.append("<tr style='background-color:#f2f2f2;'>");
			for (int i = 1; i <= colCount; i++) {
				html.append("<th>").append(meta.getColumnName(i)).append("</th>");
			}
			html.append("</tr>");

			// 🔹 Table Rows & Highlight Matching ID Row
			while (rs.next()) {
				int currentId = rs.getInt("id");

				if (currentId == recordId) {
					html.append("<tr style='background-color:#90EE90; font-weight:bold;'>");
				} else {
					html.append("<tr>");
				}

				for (int i = 1; i <= colCount; i++) {
					html.append("<td>").append(rs.getString(i)).append("</td>");
				}
				html.append("</tr>");
			}

			html.append("</table>");

			// 🔹 Attach in Extent Report
			current.info("📌 DB Snapshot after: <b>" + action + "</b> (Table: <b>" + tableName + "</b>)");
			current.info(html.toString());

		} catch (Exception e) {
			current.warning("⚠️ Failed to capture DB snapshot: " + e.getMessage());
		}
	}

//	ApiValidators.captureDbSnapshot(resourceRegistryId, "ResourceRegistry", "Create ResourceRegistry (POST)");

	public static void captureElasticScreenshot(int recordId, String indexName, String action) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		WebDriver driver = null;

		try {
			// 🔹 Initialize Chrome
			driver = new ChromeDriver();
			driver.manage().window().maximize();

			// 🔹 Build the Elasticsearch URL dynamically
			String url = String.format(
					"http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?pretty&size=2000", indexName);
			driver.get(url);

			// 🔹 Wait for JSON content to load
			Thread.sleep(2000);

			// 🔹 Use JavaScript to find and scroll to the given issueId
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.find('\"id\" : ' + arguments[0]);", recordId);
			js.executeScript("var sel = window.getSelection();" + "if (sel.rangeCount > 0) {"
					+ "  sel.getRangeAt(0).startContainer.parentElement.scrollIntoView({behavior: 'smooth', block: 'center'});"
					+ "}");

			// 🔹 Capture Screenshot as Base64
			String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

			// 🔹 Log in Extent Report
			if (current != null) {
				current.info("📸 Elasticsearch Verification for IssueId: **" + recordId + "** in index: `" + indexName
						+ "`");
				current.addScreenCaptureFromBase64String(base64Screenshot,
						"ElasticSearch_" + indexName + "_" + action.replace(" ", "_"));
			}

		} catch (Exception e) {
			if (current != null) {
				current.warning("⚠️ Failed to capture Elasticsearch screenshot: " + e.getMessage());
			} else {
				e.printStackTrace();
			}
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}

	public static void validateDbNotNull(int id, String tableName, String... columnNames) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		if (columnNames == null || columnNames.length == 0) {
			throw new IllegalArgumentException("At least one columnName is required");
		}

		// Build SELECT
		StringBuilder select = new StringBuilder("SELECT ");
		for (int i = 0; i < columnNames.length; i++) {
			if (i > 0)
				select.append(", ");
			select.append("`").append(columnNames[i]).append("`");
		}

		select.append(" FROM `social_comments_model_dhin`.`").append(tableName).append("` WHERE `id` = ? LIMIT 1");

		boolean anyFailure = false;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				PreparedStatement ps = conn.prepareStatement(select.toString())) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {

				if (!rs.next()) {
					current.fail("❌ No row found in DB for Id=" + id);
					throw new AssertionError("DB row not found for Id=" + id);
				}

				for (String col : columnNames) {
					Object val = rs.getObject(col);
					String s = (val == null) ? null : String.valueOf(val).trim();

					if (s == null || s.isEmpty() || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) {
						anyFailure = true;
						current.fail("❌ DB NotNull Failed: " + col + " is NULL/EMPTY for Id=" + id);
					} else {
						current.pass("✅ DB NotNull Passed: " + col + " = " + s);
					}
				}
			}
		} catch (Exception e) {
			current.warning("⚠️ DB Validation Error: " + e.getMessage());
		}

		if (anyFailure) {
			throw new AssertionError("One or more DB fields are NULL/EMPTY for Id=" + id);
		}
	}

	// Examples
//	ApiValidators.validateDbNotNull(ResourceRegistryId, "ResourceRegistry", "createdDate");
//	ApiValidators.validateDbNotNull(ResourceRegistryId, "ResourceRegistry", "createdDate", "isRemoved", "resourceName");

//	public static void validateDbNotNullExcept(int id, String tableName, String... skipColumns) {
//	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//	    if (current == null)
//	        return;
//
//	    // Convert skipColumns to a Set for quick lookup
//	    java.util.Set<String> skipSet = new java.util.HashSet<>();
//	    if (skipColumns != null) {
//	        for (String col : skipColumns) {
//	            if (col != null && !col.trim().isEmpty()) {
//	                skipSet.add(col.trim());
//	            }
//	        }
//	    }
//
//	    String sql = "SELECT * FROM `social_comments_model_dhin`.`" + tableName + "` WHERE id = ? LIMIT 1";
//	    boolean anyFailure = false;
//
//	    try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//	         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//	        ps.setInt(1, id);
//	        try (ResultSet rs = ps.executeQuery()) {
//
//	            if (!rs.next()) {
//	                current.fail("❌ No row found in DB for Id=" + id + " in table=" + tableName);
//	                throw new AssertionError("DB row not found for Id=" + id);
//	            }
//
//	            ResultSetMetaData meta = rs.getMetaData();
//	            int colCount = meta.getColumnCount();
//
//	            for (int i = 1; i <= colCount; i++) {
//	                String colName = meta.getColumnName(i);
//	                Object val = rs.getObject(colName);
//	                String s = (val == null) ? null : String.valueOf(val).trim();
//
//	                boolean isEmptyOrZeroDate = (s == null || s.isEmpty()
//	                        || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s));
//
//	                if (skipSet.contains(colName)) {
//	                    // Skip this column
//	                    if (isEmptyOrZeroDate) {
//	                        current.warning("⚠️ Skipped column (allowed null): " + colName);
//	                    } else {
//	                        current.info("ℹ️ Skipped column has value: " + colName + " = " + s);
//	                    }
//	                    continue;
//	                }
//
//	                // Validate all other columns
//	                if (isEmptyOrZeroDate) {
//	                    current.fail("❌ DB Validation Failed: " + colName + " is NULL/EMPTY for Id=" + id);
//	                    anyFailure = true;
//	                } else {
//	                    current.pass("✅ DB Validation Passed: " + colName + " = " + s);
//	                }
//	            }
//	        }
//	    } catch (Exception e) {
//	        current.warning("⚠️ DB Validation Error: " + e.getMessage());
//	    }
//
//	    if (anyFailure) {
//	        throw new AssertionError("One or more non-exempt DB fields are NULL/EMPTY for Id=" + id);
//	    }
//	}

	// Examples
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate");
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate", "updatedDate");
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry");

// print only which are null values
	public static void validateDbOnlyNullExcept(int id, String tableName, String... skipColumns) {
		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
		if (current == null)
			return;

		// Columns to skip (like createdDate)
		java.util.Set<String> skipSet = new java.util.HashSet<>();
		if (skipColumns != null) {
			for (String col : skipColumns) {
				if (col != null && !col.trim().isEmpty()) {
					skipSet.add(col.trim());
				}
			}
		}

		String sql = "SELECT * FROM `social_comments_model_dhin`.`" + tableName + "` WHERE id = ? LIMIT 1";
		boolean anyFailure = false;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {

				if (!rs.next()) {
					current.fail("❌ No row found in DB for Id=" + id + " in table=" + tableName);
					throw new AssertionError("DB row not found for Id=" + id);
				}

				ResultSetMetaData meta = rs.getMetaData();
				int colCount = meta.getColumnCount();

				for (int i = 1; i <= colCount; i++) {
					String colName = meta.getColumnName(i);

					// Skip excluded columns like createdDate
					if (skipSet.contains(colName))
						continue;

					Object val = rs.getObject(colName);
					String s = (val == null) ? null : String.valueOf(val).trim();

					if (s == null || s.isEmpty() || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) {

						current.fail("❌ DB Column is NULL/EMPTY: " + colName + " for Id=" + id);
						anyFailure = true;
					}
				}

				if (!anyFailure) {
					current.pass("✅ No NULL columns found (excluding: " + skipSet + ")");
				}

			}
		} catch (Exception e) {
			current.warning("⚠️ DB Validation Error: " + e.getMessage());
		}

		if (anyFailure) {
			throw new AssertionError("One or more non-excluded DB columns are NULL/EMPTY for Id=" + id);
		}
	}

	// Examples
//	ApiValidators.validateDbOnlyNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate");
//	ApiValidators.validateDbOnlyNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate", "updatedDate");
//	ApiValidators.validateDbOnlyNullExcept(ResourceRegistryId, "ResourceRegistry");

	// validates all columns and print which are non null also

//	public static void validateDbOnlyNullExcept(int id, String tableName, String... skipColumns) {
//	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//	    if (current == null) return;
//
//	    // Build skip set (lower-case) — if empty, we will NOT skip any column (i.e. validate all)
//	    java.util.Set<String> skipSet = new java.util.HashSet<>();
//	    if (skipColumns != null) {
//	        for (String col : skipColumns) {
//	            if (col != null && !col.trim().isEmpty()) {
//	                skipSet.add(col.trim().toLowerCase());
//	            }
//	        }
//	    }
//
//	    String sql = "SELECT * FROM `social_comments_model_dhin`.`" + tableName + "` WHERE id = ? LIMIT 1";
//	    boolean anyFailure = false;
//	    java.util.List<String> nullCols = new java.util.ArrayList<>();
//
//	    try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//	         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//	        ps.setInt(1, id);
//	        try (ResultSet rs = ps.executeQuery()) {
//
//	            if (!rs.next()) {
//	                current.fail("❌ No row found in DB for Id=" + id + " in table=" + tableName);
//	                throw new AssertionError("DB row not found for Id=" + id);
//	            }
//
//	            ResultSetMetaData meta = rs.getMetaData();
//	            int colCount = meta.getColumnCount();
//
//	            for (int i = 1; i <= colCount; i++) {
//	                String colName = meta.getColumnName(i);
//	                String colNameLower = colName.toLowerCase();
//
//	                // If skipSet is non-empty and contains this column -> skip it.
//	                // If skipSet is empty -> validate all columns.
//	                if (!skipSet.isEmpty() && skipSet.contains(colNameLower)) {
//	                    // skipping this column by caller request
//	                    if (current != null) current.info("↷ Skipping column (excluded): " + colName);
//	                    continue;
//	                }
//
//	                Object val = rs.getObject(colName);
//	                String s = (val == null) ? null : String.valueOf(val).trim();
//
//	                if (s == null || s.isEmpty()
//	                        || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) {
//	                    anyFailure = true;
//	                    nullCols.add(colName);
//	                    if (current != null) current.fail("❌ DB Column is NULL/EMPTY: " + colName + " for Id=" + id);
//	                } else {
//	                    if (current != null) current.pass("✅ DB Column OK: " + colName + " = " + s);
//	                }
//	            }
//
//	            if (!anyFailure) {
//	                current.pass("✅ No NULL/EMPTY columns found (excluding: " + (skipSet.isEmpty() ? "none" : skipSet) + ")");
//	            } else {
//	                current.warning("⚠️ Columns null/empty: " + nullCols);
//	            }
//	        }
//	    } catch (Exception e) {
//	        if (current != null) current.warning("⚠️ DB Validation Error: " + e.getMessage());
//	        throw new AssertionError("DB validation failed due to exception: " + e.getMessage(), e);
//	    }
//
//	    if (anyFailure) {
//	        throw new AssertionError("One or more non-excluded DB columns are NULL/EMPTY for Id=" + id + " -> " + nullCols);
//	    }
//	}

//	public static void validateDbNotNull(int id, String tableName, String[] optionalColumns, String... requiredColumns) {
//	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//	    if (current == null) return;
//
//	    // sanity checks
//	    if ((requiredColumns == null || requiredColumns.length == 0) && (optionalColumns == null || optionalColumns.length == 0)) {
//	        throw new IllegalArgumentException("At least one required or optional column must be supplied");
//	    }
//
//	    // build sets/lists
//	    java.util.Set<String> optionalSet = new java.util.HashSet<>();
//	    if (optionalColumns != null) {
//	        for (String o : optionalColumns) if (o != null && !o.trim().isEmpty()) optionalSet.add(o.trim());
//	    }
//
//	    java.util.List<String> allCols = new java.util.ArrayList<>();
//	    if (requiredColumns != null) {
//	        for (String r : requiredColumns) if (r != null && !r.trim().isEmpty()) allCols.add(r.trim());
//	    }
//	    for (String o : optionalSet) {
//	        if (!allCols.contains(o)) allCols.add(o); // avoid duplicates
//	    }
//
//	    // build SELECT
//	    StringBuilder select = new StringBuilder("SELECT ");
//	    for (int i = 0; i < allCols.size(); i++) {
//	        if (i > 0) select.append(", ");
//	        select.append("`").append(allCols.get(i)).append("`");
//	    }
//	    select.append(" FROM `social_comments_model_dhin`.`").append(tableName).append("` WHERE `id` = ? LIMIT 1");
//
//	    java.util.List<String> failedRequired = new java.util.ArrayList<>();
//	    java.util.List<String> optionalNulls = new java.util.ArrayList<>();
//
//	    try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//	         PreparedStatement ps = conn.prepareStatement(select.toString())) {
//
//	        ps.setInt(1, id);
//	        try (ResultSet rs = ps.executeQuery()) {
//	            if (!rs.next()) {
//	                current.fail("❌ No row found in DB for Id=" + id + " (table=" + tableName + ")");
//	                throw new AssertionError("DB row not found for Id=" + id + " (table=" + tableName + ")");
//	            }
//
//	            for (String col : allCols) {
//	                Object val = rs.getObject(col);
//	                String s = (val == null) ? null : String.valueOf(val).trim();
//
//	                boolean isOptional = optionalSet.contains(col);
//	                boolean isEmptyOrZeroDate = (s == null || s.isEmpty()
//	                        || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s));
//
//	                if (isEmptyOrZeroDate) {
//	                    if (isOptional) {
//	                        current.warning("⚠️ Optional column NULL/EMPTY (allowed): " + col + " for Id=" + id);
//	                        optionalNulls.add(col);
//	                    } else {
//	                        current.fail("❌ Required column NULL/EMPTY: " + col + " for Id=" + id);
//	                        failedRequired.add(col);
//	                    }
//	                } else {
//	                    current.pass("✅ DB value: " + col + " = " + s + " (Id=" + id + ")");
//	                }
//	            }
//	        }
//	    } catch (Exception e) {
//	        current.warning("⚠️ DB Validation Error: " + e.getMessage());
//	    }
//
//	    if (!optionalNulls.isEmpty()) {
//	        current.info("ℹ️ Optional columns were NULL/EMPTY (allowed): " + String.join(", ", optionalNulls));
//	    }
//
//	    if (!failedRequired.isEmpty()) {
//	        throw new AssertionError("Required DB columns NULL/EMPTY: " + String.join(", ", failedRequired) + " for Id=" + id);
//	    }
//	}

//	public static void validateDbIsNull(int id, String tableName, String... columnNames) {
//		ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//		if (current == null)
//			return;
//
//		if (columnNames == null || columnNames.length == 0) {
//			throw new IllegalArgumentException("At least one columnName is required");
//		}
//
//		// Build SELECT
//		StringBuilder select = new StringBuilder("SELECT ");
//		for (int i = 0; i < columnNames.length; i++) {
//			if (i > 0)
//				select.append(", ");
//			select.append("`").append(columnNames[i]).append("`");
//		}
//
//		select.append(" FROM `social_comments_model_dhin`.`").append(tableName).append("` WHERE `id` = ? LIMIT 1");
//
//		boolean anyFailure = false;
//
//		try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//				PreparedStatement ps = conn.prepareStatement(select.toString())) {
//
//			ps.setInt(1, id);
//			try (ResultSet rs = ps.executeQuery()) {
//
//				if (!rs.next()) {
//					current.fail("❌ No row found in DB for Id=" + id);
//					throw new AssertionError("DB row not found for Id=" + id);
//				}
//
//				for (String col : columnNames) {
//					Object val = rs.getObject(col);
//					String s = (val == null) ? null : String.valueOf(val).trim();
//
//					if (s == null || s.isEmpty() || "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) {
//
//						current.pass("✅ DB NULL Check Passed: " + col + " is NULL/EMPTY as expected (Id=" + id + ")");
//					} else {
//						anyFailure = true;
//						current.fail(
//								"❌ DB NULL Check Failed: " + col + " is NOT NULL → Value = " + s + " (Id=" + id + ")");
//					}
//				}
//			}
//		} catch (Exception e) {
//			current.warning("⚠️ DB Validation Error: " + e.getMessage());
//		}
//
//		if (anyFailure) {
//			throw new AssertionError("One or more DB fields were expected to be NULL but were NOT for Id=" + id);
//		}
//	}

	private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASS = "dhi123";
	private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";

//    private static ExtentTest log() { return ExtentCucumberListener.getCurrentScenario(); }
//    private static String safe(String s) { return s == null ? "null" : s; }

	private static String requireSafeIdentifier(String id) {
		if (id == null || !id.matches("[A-Za-z0-9_]+"))
			throw new IllegalArgumentException("Illegal identifier: " + id);
		return id;
	}

	private static boolean valuesEqual(String a, String b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.trim().equalsIgnoreCase(b.trim());
	}

	private static String readDbValueById(String schema, String table, int id, String column) {
		String sch = requireSafeIdentifier(schema);
		String tbl = requireSafeIdentifier(table);
		String col = requireSafeIdentifier(column);
		String sql = "SELECT `" + col + "` FROM `" + sch + "`.`" + tbl + "` WHERE `id` = ? LIMIT 1";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next())
					throw new AssertionError("No DB row for " + sch + "." + tbl + " id=" + id);
				Object o = rs.getObject(1);
				return (o == null) ? null : String.valueOf(o);
			}
		} catch (SQLException e) {
			ExtentTest t = log();
			if (t != null)
				t.fail("❌ DB read error for " + table + "." + column + ": " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Compare response[jsonPath] to DB[schema.table.column] for the row with id.
	 */
	public static void validateResponseToDbEquals(Response response, int id, String schema, String table,
			String jsonPath, String dbColumn) {
		ExtentTest t = log();

		Object respObj;
		try {
			respObj = response.jsonPath().get(jsonPath);
		} catch (Exception e) {
			if (t != null)
				t.fail("❌ Unable to read response jsonPath '" + jsonPath + "': " + e.getMessage());
			throw new AssertionError("Response jsonPath '" + jsonPath + "' not readable");
		}
		String respVal = respObj == null ? null : String.valueOf(respObj).trim();
		String dbVal = readDbValueById(schema, table, id, dbColumn);

		if (t != null) {
			t.info("🔎 Compare Response→DB [" + jsonPath + " -> " + table + "." + dbColumn + "]\n" + "   • Response: "
					+ safe(respVal) + "\n" + "   • DB      : " + safe(dbVal));
		}

		if (valuesEqual(respVal, dbVal)) {
			if (t != null)
				t.pass("✅ Match [" + jsonPath + " -> " + dbColumn + "] = " + safe(respVal));
		} else {
			if (t != null)
				t.fail("❌ Mismatch [" + jsonPath + " -> " + dbColumn + "] → response=" + safe(respVal) + " | db="
						+ safe(dbVal));
			throw new AssertionError("Response vs DB mismatch for: " + jsonPath + " -> " + dbColumn);
		}
	}

	// No Excepted columns

//	public static void validateNoExtraKeys(String requestJson, Response response) {
//        ExtentTest t = log();
//        if (t != null) t.info("🔍 Validating no unexpected keys between Request and Response");
//
//        try {
//            // Parse both JSONs into flat key maps
//            Map<String, Object> reqMap = flattenJson(JsonPath.from(requestJson).getMap(""));
//            Map<String, Object> respMap = flattenJson(response.jsonPath().getMap(""));
//
//            Set<String> requestKeys = reqMap.keySet();
//            Set<String> responseKeys = respMap.keySet();
//
//            // Find extra keys in response
//            Set<String> extraKeys = new TreeSet<>(responseKeys);
//            extraKeys.removeAll(requestKeys);
//
//            if (extraKeys.isEmpty()) {
//                if (t != null) t.pass("✅ No unexpected keys found in response.");
//            } else {
//                if (t != null)
//                    t.fail("❌ Unexpected keys found in response (not in request): " + extraKeys);
//                throw new AssertionError("Unexpected keys in response: " + extraKeys);
//            }
//
//        } catch (Exception e) {
//            if (t != null) t.warning("⚠️ validateNoExtraKeys error: " + e.getMessage());
//            throw new AssertionError("Failed to validate extra keys: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Recursively flattens a nested JSON object (Map/List) into key paths.
//     * e.g. { "a": { "b": 1 } } → {"a.b": 1}
//     */
//    @SuppressWarnings("unchecked")
//    private static Map<String, Object> flattenJson(Map<String, Object> jsonMap) {
//        Map<String, Object> flatMap = new LinkedHashMap<>();
//        flatten("", jsonMap, flatMap);
//        return flatMap;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(String prefix, Object value, Map<String, Object> flatMap) {
//        if (value instanceof Map) {
//            Map<String, Object> map = (Map<String, Object>) value;
//            for (Map.Entry<String, Object> e : map.entrySet()) {
//                String key = prefix.isEmpty() ? e.getKey() : prefix + "." + e.getKey();
//                flatten(key, e.getValue(), flatMap);
//            }
//        } else if (value instanceof List) {
//            List<Object> list = (List<Object>) value;
//            for (int i = 0; i < list.size(); i++) {
//                flatten(prefix + "[" + i + "]", list.get(i), flatMap);
//            }
//        } else {
//            flatMap.put(prefix, value);
//        }
//    }

// we add except columns are extra 
	public static void validateNoExtraKeys(String requestJson, Response response, String... allowedExtraKeys) {
		ExtentTest t = ExtentCucumberListener.getCurrentScenario();
		if (t != null)
			t.info("🔍 validateNoExtraKeys — allowed exceptions: " + Arrays.toString(allowedExtraKeys));

		try {
			// flatten response
			Map<String, Object> respMap = flattenSafe(response.jsonPath().getMap(""));
			Set<String> responseKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			responseKeys.addAll(respMap.keySet());
			includeArrayKeysExplicitly(response.getBody().asString(), responseKeys);

			// Prepare allowed exact and prefix lists (used in both modes)
			Set<String> allowedExact = new HashSet<>();
			List<String> allowedPrefixes = new ArrayList<>();
			if (allowedExtraKeys != null) {
				for (String a : allowedExtraKeys) {
					if (a == null)
						continue;
					String trimmed = a.trim();
					if (trimmed.endsWith(".*")) {
						allowedPrefixes.add(trimmed.substring(0, trimmed.length() - 2).toLowerCase());
					} else {
						allowedExact.add(trimmed.toLowerCase());
					}
				}
			}

			// CASE A: requestJson provided -> baseline is request keys (default behaviour)
			if (requestJson != null && !requestJson.trim().isEmpty()) {
				Map<String, Object> reqMap = flattenSafe(JsonPath.from(requestJson).getMap(""));
				Set<String> requestKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
				requestKeys.addAll(reqMap.keySet());
				includeArrayKeysExplicitly(requestJson, requestKeys);

				// extra = responseKeys - requestKeys
				Set<String> extraKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
				extraKeys.addAll(responseKeys);
				extraKeys.removeAll(requestKeys);

				// filter allowedExtraKeys
				filterAllowed(extraKeys, allowedExact, allowedPrefixes, t);

				if (extraKeys.isEmpty()) {
					if (t != null)
						t.pass("✅ No unexpected keys found in response (after allowed exceptions).");
				} else {
					Set<String> formatted = formatArrayKeys(extraKeys);
					if (t != null)
						t.fail("❌ Unexpected keys found in response (not in request): " + formatted);
					throw new AssertionError("Unexpected keys in response: " + formatted);
				}
				return;
			}

			// CASE B: no request body -> treat allowedExtraKeys AS the whitelist (expected
			// keys)
			// If caller didn't pass allowedExtraKeys, we cannot sensibly validate/restrict;
			// fail fast.
			if ((allowedExact.isEmpty() && allowedPrefixes.isEmpty())) {
				if (t != null)
					t.fail("❌ No request body and no allowedExtraKeys provided — cannot determine expected response keys.");
				throw new IllegalArgumentException(
						"Request body missing; pass expected/allowed keys in allowedExtraKeys when requestJson is empty.");
			}

			// Build "allowed set" from allowedExact + allowedPrefixes (prefix matches
			// allowed)
			// Unexpected keys = responseKeys - allowedMatches
			Set<String> unexpected = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			unexpected.addAll(responseKeys);

			// Remove keys that match allowedExact or allowedPrefixes
			Iterator<String> it = unexpected.iterator();
			while (it.hasNext()) {
				String key = it.next();
				String kl = key.toLowerCase();

				if (allowedExact.contains(kl)) {
					if (t != null)
						t.info("↷ Allowed key (exact): " + key);
					it.remove();
					continue;
				}
				boolean matched = false;
				for (String pref : allowedPrefixes) {
					if (kl.startsWith(pref)) {
						if (t != null)
							t.info("↷ Allowed key (prefix " + pref + ".*): " + key);
						it.remove();
						matched = true;
						break;
					}
				}
				if (matched)
					continue;
			}

			if (unexpected.isEmpty()) {
				if (t != null)
					t.pass("✅ Response contains only allowed keys (whitelist).");
			} else {
				Set<String> formatted = formatArrayKeys(unexpected);
				if (t != null)
					t.fail("❌ Response contains unexpected keys (not in whitelist): " + formatted);
				throw new AssertionError("Response contains unexpected keys: " + formatted);
			}

		} catch (AssertionError ae) {
			throw ae;
		} catch (Exception e) {
			if (t != null)
				t.warning("⚠️ validateNoExtraKeys error: " + e.getMessage());
			throw new AssertionError("Failed to validate extra keys: " + e.getMessage(), e);
		}
	}

	// helper: remove allowed items in-place from 'candidates' using exact & prefix
	// lists
	private static void filterAllowed(Set<String> candidates, Set<String> allowedExact, List<String> allowedPrefixes,
			ExtentTest t) {
		Iterator<String> it = candidates.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String kl = key.toLowerCase();
			if (allowedExact.contains(kl)) {
				if (t != null)
					t.info("↷ Allowed extra key (exact): " + key);
				it.remove();
				continue;
			}
			boolean matchedPref = false;
			for (String pref : allowedPrefixes) {
				if (kl.startsWith(pref)) {
					if (t != null)
						t.info("↷ Allowed extra key (prefix " + pref + ".*): " + key);
					it.remove();
					matchedPref = true;
					break;
				}
			}
			if (matchedPref)
				continue;
		}
	}

	// helper: format any index-style array keys into canonical name[] for clearer
	// logs
	private static Set<String> formatArrayKeys(Collection<String> keys) {
		Set<String> out = new LinkedHashSet<>();
		for (String key : keys) {
			if (key.matches(".*\\[\\d+\\].*")) {
				out.add(key.replaceAll("\\[\\d+\\]", "[]"));
			} else {
				out.add(key);
			}
		}
		return out;
	}

	/* ---------------- Helper flatten methods ---------------- */

	@SuppressWarnings("unchecked")
	private static Map<String, Object> flattenSafe(Map<String, Object> map) {
		Map<String, Object> result = new LinkedHashMap<>();
		if (map == null)
			return result;
		flatten("", map, result);
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void flatten(String prefix, Object value, Map<String, Object> flatMap) {
		if (value instanceof Map) {
			Map<String, Object> m = (Map<String, Object>) value;
			for (Map.Entry<String, Object> e : m.entrySet()) {
				String key = prefix.isEmpty() ? e.getKey() : prefix + "." + e.getKey();
				flatten(key, e.getValue(), flatMap);
			}
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			if (list.isEmpty()) {
				flatMap.put(prefix + "[]", "[]"); // ✅ represent empty arrays
			} else {
				for (int i = 0; i < list.size(); i++) {
					flatten(prefix + "[" + i + "]", list.get(i), flatMap);
				}
			}
		} else {
			flatMap.put(prefix, value);
		}
	}

	private static void includeArrayKeysExplicitly(String jsonString, Set<String> keys) {
		try {
			if (jsonString.contains("[")) {
				java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[")
						.matcher(jsonString);
				while (m.find()) {
					String key = m.group(1);
					keys.add(key + "[]");
				}
			}
		} catch (Exception ignored) {
		}
	}

	// ApiValidators.validateNoExtraKeys(requestJson, response);
//ApiValidators.validateNoExtraKeys(requestJson, response, "id", "createdDate");
	// this will allow extra keys like meta.created, meta.source, meta.whatever
//	ApiValidators.validateNoExtraKeys(requestJson, response, "meta.*", "_links.*");
//ApiValidators.validateNoExtraKeys(requestJson, response, "id", "createdDate", "meta.*");

	/** Validate multiple fields with SAME names in response and DB. */
	public static void validateResponseToDbEqualsSameNames(Response response, int id, String table, String... fields) {
		if (fields == null || fields.length == 0)
			throw new IllegalArgumentException("At least one field is required");
		for (String f : fields) {
			validateResponseToDbEquals(response, id, DEFAULT_SCHEMA, table, f, f);
		}
	}

	/** Validate multiple fields with a mapping: response jsonPath -> DB column. */
	public static void validateResponseToDbEquals(Response response, int id, String schema, String table,
			Map<String, String> jsonPathToDbColumn) {
		if (jsonPathToDbColumn == null || jsonPathToDbColumn.isEmpty())
			throw new IllegalArgumentException("Mapping cannot be empty");
		for (Map.Entry<String, String> e : jsonPathToDbColumn.entrySet()) {
			validateResponseToDbEquals(response, id, DEFAULT_SCHEMA, table, e.getKey(), e.getValue());
		}
	}

	public static void validatePathParamToResponse(Response response, String jsonPath, int expectedId) {
		ExtentTest t = log();
		if (response == null) {
			if (t != null)
				t.fail("❌ Response object is null.");
			throw new IllegalArgumentException("Response cannot be null");
		}

		// Extract actual value from response
		int actualId;
		try {
			actualId = response.jsonPath().getInt(jsonPath);
		} catch (Exception e) {
			if (t != null)
				t.fail("⚠️ Failed to extract '" + jsonPath + "' from response: " + e.getMessage());
			throw new AssertionError("Unable to read value from response for key: " + jsonPath);
		}

		// Log comparison
		if (t != null) {
			t.info("🔎 Comparing PathParam to Response:\n" + "   • Expected (" + jsonPath + ") = " + expectedId + "\n"
					+ "   • Actual (" + jsonPath + ")   = " + actualId);
		}

		// Validation
		if (actualId == expectedId) {
			if (t != null)
				t.pass("✅ Validation Passed: " + jsonPath + " in response matches PathParam (" + expectedId + ")");
		} else {
			if (t != null)
				t.fail("❌ Validation Failed: Expected " + expectedId + " but got " + actualId);
			throw new AssertionError(jsonPath + " mismatch. Expected " + expectedId + " but got " + actualId);
		}
	}

	// ---------- helpers ----------
	private static boolean equalsNullable(String a, String b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.trim().equalsIgnoreCase(b.trim());
	}

	private static boolean equalsExact(String a, String b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}

	private static String safe(String s) {
		return s == null ? "null" : s;
	}

	/**
	 * Convert numbers/booleans/strings/arrays/objects to a simple comparable string
	 */
	private static String toScalarString(Object o) {
		if (o == null)
			return null;
		if (o instanceof String)
			return (String) o;
		if (o instanceof Number || o instanceof Boolean)
			return String.valueOf(o);
		// for lists/maps, stringify compactly so you can still see values in logs
		return String.valueOf(o);
	}
	// Extra

	// ----------------- Public validators -----------------

	/**
	 * Validate: response contains no extra keys not present in request.
	 * allowedExtraKeys may contain exact names or prefix.* entries.
	 */
	public static void validateNoExtraKey(String requestJson, Response response, String... allowedExtraKeys) {
		validateKeySetDiff(requestJson, response, true, allowedExtraKeys, null);
	}

	/**
	 * Validate: response contains all keys that request contains (i.e. no missing
	 * keys). allowedMissingKeys may contain exact names or prefix.* entries.
	 */
	public static void validateNoMissingKeys(String requestJson, Response response, String... allowedMissingKeys) {
		validateKeySetDiff(requestJson, response, false, null, allowedMissingKeys);
	}

	/**
	 * Combined convenience: run both extra-keys and missing-keys checks in a single
	 * call. allowedExtraKeys and allowedMissingKeys work like in the individual
	 * methods.
	 */
	public static void validateKeyParity(String requestJson, Response response, String[] allowedExtraKeys,
			String[] allowedMissingKeys) {
		List<String> failures = new ArrayList<>();
		try {
			validateNoExtraKeys(requestJson, response, allowedExtraKeys);
		} catch (AssertionError ae) {
			failures.add("ExtraKeys -> " + ae.getMessage());
		}
		try {
			validateNoMissingKeys(requestJson, response, allowedMissingKeys);
		} catch (AssertionError ae) {
			failures.add("MissingKeys -> " + ae.getMessage());
		}

		if (!failures.isEmpty()) {
			ExtentTest t = ExtentCucumberListener.getCurrentScenario();
			if (t != null)
				t.fail("One or more parity validators failed: \n - " + String.join("\n - ", failures));
			throw new AssertionError("One or more validators failed: " + String.join(" | ", failures));
		} else {
			ExtentTest t = ExtentCucumberListener.getCurrentScenario();
			if (t != null)
				t.pass("✅ Key parity checks passed (no unexpected extras, no missing keys)");
		}
	}
	// ----------------- Internal helpers -----------------

	/**
	 * Core worker: when checkExtras==true -> validate response - request (extra
	 * keys) when checkExtras==false -> validate request - response (missing keys)
	 * allowedExtra/allowedMissing behave as exact or prefix.*.
	 */
	private static void validateKeySetDiff(String requestJson, Response response, boolean checkExtras,
			String[] allowedExtra, String[] allowedMissing) {
		ExtentTest t = ExtentCucumberListener.getCurrentScenario();
		String verb = checkExtras ? "Response:Except" : "Request:Missing";

		if (t != null)
			t.info("🔍 Running validator: " + (checkExtras ? "Response → Extra keys" : "Request → Missing keys"));

		try {
			Map<String, Object> reqMap = flattenSafe(JsonPath.from(requestJson).getMap(""));
			Map<String, Object> respMap = flattenSafe(response.jsonPath().getMap(""));

			// ensure empty arrays get represented as key[]
			includeArrayKeysExplicitly(requestJson, reqMap);
			includeArrayKeysExplicitly(response.getBody().asString(), respMap);

			Set<String> reqKeys = normalizeKeySet(reqMap.keySet());
			Set<String> respKeys = normalizeKeySet(respMap.keySet());

			Set<String> subject; // the left side of diff
			Set<String> comparator; // the right side of diff
			String subjectName, comparatorName;
			if (checkExtras) {
				subject = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
				subject.addAll(respKeys);
				comparator = reqKeys;
				subjectName = "response";
				comparatorName = "request";
			} else {
				subject = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
				subject.addAll(reqKeys);
				comparator = respKeys;
				subjectName = "request";
				comparatorName = "response";
			}

			// diff = subject - comparator
			Set<String> diff = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			diff.addAll(subject);
			diff.removeAll(comparator);

			// build allowed matchers depending on which check
			String[] allowed = checkExtras ? allowedExtra : allowedMissing;
			Set<String> allowedExact = new HashSet<>();
			List<String> allowedPrefixes = new ArrayList<>();
			if (allowed != null) {
				for (String a : allowed)
					if (a != null) {
						String s = a.trim();
						if (s.endsWith(".*"))
							allowedPrefixes.add(normalizeKey(s.substring(0, s.length() - 2)).toLowerCase());
						else
							allowedExact.add(normalizeKey(s).toLowerCase());
					}
			}

			// remove allowed items
			Iterator<String> it = diff.iterator();
			while (it.hasNext()) {
				String k = it.next();
				String lk = k.toLowerCase();
				if (allowedExact.contains(lk)) {
					if (t != null)
						t.info("↷ Allowed (exact): " + k);
					it.remove();
					continue;
				}
				boolean prefMatch = false;
				for (String p : allowedPrefixes) {
					if (lk.startsWith(p)) {
						prefMatch = true;
						if (t != null)
							t.info("↷ Allowed (prefix " + p + ".*): " + k);
						it.remove();
						break;
					}
				}
				if (prefMatch)
					continue;
			}

			if (diff.isEmpty()) {
				if (t != null)
					t.pass("✅ " + (checkExtras ? "No unexpected keys in response." : "No missing keys in response."));
				return;
			}

			// make a readable message: show normalized keys, but for arrays append []
			// (already normalized)
			if (t != null)
				t.fail("❌ " + verb + " " + (allowed == null ? "[]" : "Except " + Arrays.toString(allowed))
						+ " -> Unexpected keys in " + subjectName + ": " + diff);
			throw new AssertionError(subjectName + " has unexpected keys: " + diff);
		} catch (AssertionError ae) {
			throw ae;
		} catch (Exception e) {
			if (t != null)
				t.warning("⚠️ Validator error: " + e.getMessage());
			throw new AssertionError("Validator failed: " + e.getMessage(), e);
		}
	}

	/* Helpers used by the validators (flattening + normalization) */

	private static Set<String> normalizeKeySet(Collection<String> keys) {
		Set<String> out = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (String k : keys)
			if (k != null)
				out.add(normalizeKey(k));
		return out;
	}

	/**
	 * Convert index forms to canonical [] form: e.g. foo[0] -> foo[] ; foo[0].id ->
	 * foo[].id
	 */
	private static String normalizeKey(String key) {
		// replace numeric indices with []
		String replaced = key.replaceAll("\\[\\d+\\]", "[]");
		// if someone passed bare array name (like resourceRegistries) we *do not*
		// forcibly add []
		// includeArrayKeysExplicitly adds name[] for empty arrays. For non-empty arrays
		// flatten() will produce indexed keys and they normalize above.
		return replaced;
	}

	/**
	 * Quick scan of raw JSON to add explicit key[] entries for array properties
	 * (including empty arrays) so e.g. "resourceRegistries": [] will produce
	 * resourceRegistries[] in the flattened map.
	 */
	private static void includeArrayKeysExplicitly(String jsonString, Map<String, Object> mapToAddTo) {
		if (jsonString == null || !jsonString.contains("["))
			return;
		try {
			Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[").matcher(jsonString);
			while (m.find()) {
				String key = m.group(1);
				mapToAddTo.putIfAbsent(key + "[]", "[]");
			}
		} catch (Exception ignored) {
		}
	}
	// Only check extra keys (allow "id" and "messageId" to appear)
//	ApiValidators.validateNoExtraKeys(requestJson, response, "id", "messageId");

	// Only check missing keys (allow "optionalField" to be absent)
//	ApiValidators.validateNoMissingKeys(requestJson, response, "optionalField");

	// Run both checks in one shot (allow response to have additional "meta.*" keys,
	// allow request to have optional "notes")
//	ApiValidators.validateKeyParity(requestJson, response, new String[]{"meta.*"}, new String[]{"notes"});

//	public static void validateKeysAcrossResponseEsDb(
//            Response apiResponse,
//            int recordId,
//            String esIndexName,     // e.g. "issue-steps"
//            String dbTableName,     // e.g. "Issue"
//            String[] includeKeys,   // if non-null => validate only these keys
//            String[] excludeKeys,   // keys to skip (case-insensitive)
//            boolean compareValues   // if true, also compare values across layers
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//        if (t != null) t.info("🔎 Cross-Layer validation started for id=" + recordId
//                + " (ES index=" + esIndexName + ", DB table=" + dbTableName + ")");
//
//        // --- parse response JSON (flattened keys) ---
//        Map<String, Object> respMap = safeJsonToMap(apiResponse.getBody().asString());
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(respMap, "", respFlat);
//
//        // --- fetch ES _source ---
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map) esSource = (Map<String, Object>) src;
//            }
//        } catch (Exception e) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + e.getMessage());
//            // We'll still proceed — missing ES will be considered a failure later if keys expected
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // --- fetch DB row (columns -> values) ---
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.10.2.45:3306", "root", "dhi123");
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `issue_persistence_piqa`.`" + dbTableName + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        // store both column name and a camelCase mapping so matching against JSON keys is easier
//                        dbFlat.put(col, val);
//                        dbFlat.put(underscoreToCamel(col), val);
//                    }
//                } else {
//                    if (t != null) t.warning("⚠️ No DB row found for id=" + recordId + " in table=" + dbTableName);
//                }
//            }
//        } catch (Exception e) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + e.getMessage());
//        }
//
//        // --- Build key sets to check ---
//        // If includeKeys provided => use only those. Otherwise use union of all keys from all layers.
//        Set<String> respKeys = respFlat.keySet().stream().map(String::toLowerCase).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<String> esKeys = esFlat.keySet().stream().map(String::toLowerCase).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<String> dbKeys = dbFlat.keySet().stream().map(String::toLowerCase).collect(Collectors.toCollection(LinkedHashSet::new));
//
//        Set<String> keysToCheck;
//        if (includeKeys != null && includeKeys.length > 0) {
//            keysToCheck = Arrays.stream(includeKeys).map(String::toLowerCase).collect(Collectors.toCollection(LinkedHashSet::new));
//        } else {
//            // union
//            keysToCheck = new LinkedHashSet<>();
//            keysToCheck.addAll(respKeys);
//            keysToCheck.addAll(esKeys);
//            keysToCheck.addAll(dbKeys);
//        }
//
//        // apply excludes
//        if (excludeKeys != null) {
//            Set<String> excludeSet = Arrays.stream(excludeKeys).map(String::toLowerCase).collect(Collectors.toSet());
//            keysToCheck.removeIf(excludeSet::contains);
//        }
//
//        // --- compare keys presence & extras/missing ---
//        Set<String> missingInES = new TreeSet<>();
//        Set<String> missingInDB = new TreeSet<>();
//        Set<String> extraInES = new TreeSet<>();
//        Set<String> extraInDB = new TreeSet<>();
//
//        // missing = keysToCheck - layerKeys ; extra = layerKeys - keysToCheck (only if includeKeys provided? we do both)
//        for (String k : keysToCheck) {
//            if (!esKeys.contains(k)) missingInES.add(k);
//            if (!dbKeys.contains(k)) missingInDB.add(k);
//        }
//        // extras: keys present in ES/DB but not in request-based keysToCheck (this helps detect unexpected keys)
//        for (String k : esKeys) if (!keysToCheck.contains(k)) extraInES.add(k);
//        for (String k : dbKeys) if (!keysToCheck.contains(k)) extraInDB.add(k);
//
//        // Log and mark failures
//        List<String> failures = new ArrayList<>();
//
//        if (!missingInES.isEmpty()) {
//            String msg = "Missing in ES: " + prettyFormatKeys(missingInES);
//            if (t != null) t.fail(msg);
//            failures.add(msg);
//        } else if (t != null) t.pass("✅ No keys missing in ES (checked keys).");
//
//        if (!missingInDB.isEmpty()) {
//            String msg = "Missing in DB: " + prettyFormatKeys(missingInDB);
//            if (t != null) t.fail(msg);
//            failures.add(msg);
//        } else if (t != null) t.pass("✅ No keys missing in DB (checked keys).");
//
//        if (!extraInES.isEmpty()) {
//            String msg = "Extra keys in ES (not in response/set): " + prettyFormatKeys(extraInES);
//            if (t != null) t.fail(msg);
//            failures.add(msg);
//        } else if (t != null) t.pass("✅ No unexpected keys in ES.");
//
//        if (!extraInDB.isEmpty()) {
//            String msg = "Extra keys in DB (not in response/set): " + prettyFormatKeys(extraInDB);
//            if (t != null) t.fail(msg);
//            failures.add(msg);
//        } else if (t != null) t.pass("✅ No unexpected keys in DB.");
//
//        // --- compare values if requested (only for keys present in response) ---
//        if (compareValues) {
//            for (String rawKey : keysToCheck) {
//                String key = rawKey.toLowerCase();
//
//                Object r = findValueCaseInsensitive(respFlat, key);
//                Object e = findValueCaseInsensitive(esFlat, key);
//                Object d = findValueCaseInsensitive(dbFlat, key);
//
//                // For arrays marked as name[] - compare only presence/size for now
//                if (key.endsWith("[]")) {
//                    List<?> rl = toList(r);
//                    List<?> el = toList(e);
//                    List<?> dl = toList(d);
//
//                    int rs = rl == null ? 0 : rl.size();
//                    int esz = el == null ? 0 : el.size();
//                    int ds = dl == null ? 0 : dl.size();
//
//                    if (rs != esz || rs != ds) {
//                        String msg = String.format("Array mismatch for %s -> resp:%d es:%d db:%d", rawKey, rs, esz, ds);
//                        if (t != null) t.fail(msg);
//                        failures.add(msg);
//                    } else {
//                        if (t != null) t.pass(String.format("✅ Array matched for %s (size=%d)", rawKey, rs));
//                    }
//                    continue;
//                }
//
//                String rs = toScalarString(r);
//                String es = toScalarString(e);
//                String ds = toScalarString(d);
//
//                // if any layer missing a value -> we've already recorded missing; but also mark here
//                if (rs == null || es == null || ds == null) {
//                    String msg = String.format("Value missing for %s -> resp=%s es=%s db=%s", rawKey, rs, es, ds);
//                    if (t != null) t.fail(msg);
//                    failures.add(msg);
//                    continue;
//                }
//
//                // timestamp tolerant compare if all look like ISO instants
//                Instant ir = parseInstantOrNull(rs);
//                Instant ie = parseInstantOrNull(es);
//                Instant idb = parseInstantOrNull(ds);
//                if (ir != null && ie != null && idb != null) {
//                    if (!instantEqualWithin(ir, ie, 5000) || !instantEqualWithin(ir, idb, 5000)) {
//                        String msg = String.format("Timestamp mismatch for %s -> resp=%s es=%s db=%s", rawKey, rs, es, ds);
//                        if (t != null) t.fail(msg);
//                        failures.add(msg);
//                    } else {
//                        if (t != null) t.pass("✅ Timestamp matched for " + rawKey);
//                    }
//                } else {
//                    // string compare (case-insensitive)
//                    if (rs.trim().equalsIgnoreCase(es.trim()) && rs.trim().equalsIgnoreCase(ds.trim())) {
//                        if (t != null) t.pass("✅ Match for " + rawKey + " -> " + rs);
//                    } else {
//                        String msg = String.format("Value mismatch for %s -> resp=%s es=%s db=%s", rawKey, rs, es, ds);
//                        if (t != null) t.fail(msg);
//                        failures.add(msg);
//                    }
//                }
//            }
//        }
//
//        // final: fail if any failures
//        if (!failures.isEmpty()) {
//            String summary = "One or more cross-layer validations failed:\n" + String.join("\n", failures);
//            if (t != null) t.fail("❌ Cross-layer validation FAILED. See details above.");
//            throw new AssertionError(summary);
//        } else {
//            if (t != null) t.pass("✅ Cross-layer validation passed for id=" + recordId);
//        }
//    }
//
//    // ---------------- Helper utilities ----------------
//
//    private static Map<String, Object> safeJsonToMap(String body) {
//        if (body == null || body.trim().isEmpty()) return Collections.emptyMap();
//        try {
//            Object o = JsonPath.from(body).get("");
//            if (o instanceof Map) return (Map<String, Object>) o;
//        } catch (Exception ignored) {}
//        return Collections.emptyMap();
//    }
//
//    /**
//     * Flatten map to dot notation keys. Arrays will produce a key "name[]" (not each index).
//     * e.g. { resourceRegistries: [ {id:1}, {id:2} ] } -> key "resourceRegistries[]" present
//     */
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        if (src == null) return;
//        String pref = (prefix == null || prefix.isEmpty()) ? "" : prefix + ".";
//        for (Map.Entry<String, Object> e : src.entrySet()) {
//            String k = pref + e.getKey();
//            Object v = e.getValue();
//            if (v instanceof Map) {
//                flattenJson((Map<String, Object>) v, k, out);
//            } else if (v instanceof List) {
//                // mark array presence
//                out.put((k + "[]").toLowerCase(), v);
//                // attempt to flatten first element (if it's object) with index-agnostic key (k[]).field
//                List<?> list = (List<?>) v;
//                if (!list.isEmpty() && list.get(0) instanceof Map) {
//                    // flatten the first element's keys under k[] (we won't index)
//                    flattenJson((Map<String, Object>) list.get(0), k + "[]", out);
//                }
//            } else {
//                out.put(k.toLowerCase(), v);
//            }
//        }
//    }
//
//    private static String prettyFormatKeys(Collection<String> keys) {
//        // convert key[] style and return sorted string
//        return keys.stream()
//                .map(k -> k.replaceAll("\\[\\]", "[]"))
//                .collect(Collectors.joining(", "));
//    }
//
//    private static Object findValueCaseInsensitive(Map<String, Object> flatMap, String keyLower) {
//        if (flatMap == null) return null;
//        // direct match
//        for (Map.Entry<String, Object> e : flatMap.entrySet()) {
//            if (e.getKey().equalsIgnoreCase(keyLower)) return e.getValue();
//        }
//        // try replacing [] variants
//        String alt = keyLower.endsWith("[]") ? keyLower.replace("[]", "") : keyLower + "[]";
//        for (Map.Entry<String, Object> e : flatMap.entrySet()) {
//            if (e.getKey().equalsIgnoreCase(alt)) return e.getValue();
//        }
//        return null;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static List<?> toList(Object o) {
//        if (o == null) return null;
//        if (o instanceof List) return (List<?>) o;
//        if (o instanceof String) {
//            String s = ((String) o).trim();
//            if (s.startsWith("[") && s.endsWith("]")) {
//                try {
//                    return JsonPath.from(s).getList("");
//                } catch (Exception ignored) {}
//            }
//        }
//        return null;
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try {
//            return Instant.parse(s.replace(" ", "T").replace("+00:00", "Z"));
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        return Math.abs(a.toEpochMilli() - b.toEpochMilli()) <= millisTolerance;
//    }
//
//    private static String underscoreToCamel(String underscore) {
//        if (underscore == null) return null;
//        String[] parts = underscore.split("_");
//        StringBuilder sb = new StringBuilder(parts[0]);
//        for (int i = 1; i < parts.length; i++) {
//            if (parts[i].length() > 0) {
//                sb.append(parts[i].substring(0, 1).toUpperCase());
//                sb.append(parts[i].substring(1));
//            }
//        }
//        return sb.toString();
//    }
////    Validate all keys (union) and compare values:
////    ApiValidators.validateAcrossLayersWithExtraMissing(response, issueId, "issue-steps", "Issue", null, null, true);
//
////    Validate only specific keys:
////    ApiValidators.validateAcrossLayersWithExtraMissing(response, issueId, "issue-steps", "Issue",
////    new String[]{"resourceName","label"}, null, true);
//    
////    Validate all except createdDate:
////    ApiValidators.validateAcrossLayersWithExtraMissing(response, issueId, "issue-steps", "Issue",
////    	     null, new String[]{"createdDate"}, true);

	public static void validateKeysAcrossResponseEsDb(Response apiResponse, String requestJson, int recordId,
			String esIndexName, String dbTableName, String[] includeKeys, String[] excludeFromRequestKeys, // keys
																											// excluded
																											// from
																											// request
																											// but still
																											// validated
																											// across
																											// layers
			boolean compareValues, boolean requireEsPresence) {
		ExtentTest t = ExtentCucumberListener.getCurrentScenario();
		if (t != null)
			t.info("🔎 Cross-Layer validation started for ID=" + recordId + " (ES index=" + esIndexName + ", DB table="
					+ dbTableName + ")");

		// --- Flatten Request & Response JSON ---
		Map<String, Object> reqMap = safeJsonToMap(requestJson);
		Map<String, Object> reqFlat = new LinkedHashMap<>();
		flattenJson(reqMap, "", reqFlat);

		Map<String, Object> respMap = safeJsonToMap(apiResponse == null ? null : apiResponse.getBody().asString());
		Map<String, Object> respFlat = new LinkedHashMap<>();
		flattenJson(respMap, "", respFlat);

		// --- Flatten ES ---
		Map<String, Object> esSource = Collections.emptyMap();
		try {
			String esUrl = String.format(
					"http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1", esIndexName,
					recordId);
			Response esResp = RestAssured.given().get(esUrl).then().extract().response();
			List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
			if (hits != null && !hits.isEmpty()) {
				Object src = hits.get(0).get("_source");
				if (src instanceof Map)
					esSource = (Map<String, Object>) src;
			} else if (requireEsPresence && t != null) {
				t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
			}
		} catch (Exception e) {
			if (t != null)
				t.warning("⚠️ Elasticsearch fetch failed: " + e.getMessage());
		}
		Map<String, Object> esFlat = new LinkedHashMap<>();
		flattenJson(esSource, "", esFlat);

		// --- Flatten DB ---
		Map<String, Object> dbFlat = new LinkedHashMap<>();
		try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM `" + DEFAULT_SCHEMA + "`.`"
						+ requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
			ps.setInt(1, recordId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					ResultSetMetaData meta = rs.getMetaData();
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						String col = meta.getColumnName(i);
						Object val = rs.getObject(i);
						dbFlat.put(col.toLowerCase(), val);
						dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
					}
				} else if (t != null) {
					t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
				}
			}
		} catch (Exception e) {
			if (t != null)
				t.warning("⚠️ DB fetch failed: " + e.getMessage());
		}

		// include explicit array key markers for raw JSON strings
		includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
		includeArrayKeysExplicitly(apiResponse == null ? null : apiResponse.getBody().asString(), respFlat.keySet());
		includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());

		// --- Prepare key sets ---
		Set<String> reqKeysAll = reqFlat.keySet().stream().map(String::toLowerCase)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<String> respKeys = respFlat.keySet().stream().map(String::toLowerCase)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<String> esKeys = esFlat.keySet().stream().map(String::toLowerCase)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<String> dbKeys = dbFlat.keySet().stream().map(String::toLowerCase)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		Set<String> excludedFromReq = new HashSet<>();
		if (excludeFromRequestKeys != null) {
			for (String s : excludeFromRequestKeys)
				if (s != null)
					excludedFromReq.add(s.trim().toLowerCase());
		}

		Set<String> reqKeys = reqKeysAll.stream().filter(k -> !excludedFromReq.contains(k))
				.collect(Collectors.toCollection(LinkedHashSet::new));

		Set<String> keysToCheck = new LinkedHashSet<>();
		if (includeKeys != null && includeKeys.length > 0) {
			keysToCheck.addAll(Arrays.stream(includeKeys).map(String::toLowerCase).collect(Collectors.toList()));
		} else {
			keysToCheck.addAll(reqKeys);
			keysToCheck.addAll(respKeys);
			keysToCheck.addAll(esKeys);
			keysToCheck.addAll(dbKeys);
		}

		List<String> failures = new ArrayList<>();

		// --- Build HTML table with new .nullEs style ---

		StringBuilder html = new StringBuilder();
		html.append("<style>").append(
				"table.valTable{border-collapse:collapse;width:100%;font-family:Arial,Helvetica,sans-serif;margin-top:10px}")
				.append("table.valTable th, table.valTable td{border:1px solid #ddd;padding:6px;text-align:left;font-size:12px}")
				.append("table.valTable th{background:#f2f2f2;font-weight:600}")
				.append(".pass{background:#d4edda;color:#155724}") // green
				.append(".fail{background:#f8d7da;color:#721c24}") // red
				.append(".nullEs{background:#fff3cd;color:#856404}") // yellow/orange
				.append(".excl{background:#e2f0fb;color:#0b4f73}") // blue-ish
				.append(".legend{font-family:Arial,Helvetica,sans-serif;font-size:13px;margin-bottom:6px}")
				.append(".legend span{display:inline-block;margin-right:15px;padding:3px 6px;border-radius:4px;font-weight:500}")
				.append(".lg-pass{background:#d4edda;color:#155724}")
				.append(".lg-fail{background:#f8d7da;color:#721c24}")
				.append(".lg-nullEs{background:#fff3cd;color:#856404}")
				.append(".lg-excl{background:#e2f0fb;color:#0b4f73}").append("</style>");

		// --- Color legend summary ---
		html.append("<div class='legend'>").append("<b>Legend:</b> ")
				.append("<span class='lg-pass'>✅ Green = All layers match</span>")
				.append("<span class='lg-nullEs'>🟡 Yellow = Null/Missing in Elasticsearch</span>")
				.append("<span class='lg-fail'>🔴 Red = Missing or Mismatch in any layer</span>")
				.append("<span class='lg-excl'>🔵 Blue = Excluded from Request but validated</span>").append("</div>");

		// --- Validation table header ---
		html.append("<table class='valTable'>");
		html.append(
				"<tr><th>Key</th><th>Request</th><th>Response</th><th>Elasticsearch</th><th>Database</th><th>Status</th></tr>");
		for (String rawKey : keysToCheck) {
			String key = rawKey.toLowerCase();

			Object qObj = findValueCaseInsensitive(reqFlat, key);
			Object rObj = findValueCaseInsensitive(respFlat, key);
			Object eObj = findValueCaseInsensitive(esFlat, key);
			Object dObj = findValueCaseInsensitive(dbFlat, key);

			String qStr = toScalarString(qObj);
			String rStr = toScalarString(rObj);
			String eStr = toScalarString(eObj);
			String dStr = toScalarString(dObj);

			boolean isExcludedFromRequest = excludedFromReq.contains(key);

			String rowClass = "pass";
			String statusText = "PASS";

			boolean missingReq = isMissing(qStr);
			boolean missingResp = isMissing(rStr);
			boolean missingEs = isMissing(eStr);
			boolean missingDb = isMissing(dStr);

			// If any required layer missing -> fail (request may be excluded)
			if (!isExcludedFromRequest && (missingReq || missingResp || missingDb)) {
				rowClass = "fail";
				statusText = "MISSING";
				failures.add("Missing for key: " + rawKey + " -> req=" + qStr + ", resp=" + rStr + ", es=" + eStr
						+ ", db=" + dStr);
			}

			// NEW: strict rule - key present in Req/Resp/DB but NULL/missing in ES -> fail,
			// mark as nullEs
			if (!missingReq && !missingResp && !missingDb && missingEs) {
				rowClass = "nullEs";
				statusText = "NULL_IN_ES";
				failures.add("Key present in Req/Resp/DB but NULL in ES: " + rawKey + " -> req=" + qStr + ", resp="
						+ rStr + ", es=" + eStr + ", db=" + dStr);
			}

			// Value comparison only when still in "pass" or "nullEs" not already failing
			// for other reasons.
			if (!rowClass.equals("fail") && compareValues) {
				if (!missingReq) {
					// compare everything to request value
					String qTrim = qStr == null ? "" : qStr.trim();
					boolean respMatch = !isMissing(rStr) && qTrim.equalsIgnoreCase(rStr.trim());
					boolean dbMatch = !isMissing(dStr) && qTrim.equalsIgnoreCase(dStr.trim());
					boolean esMatch = !requireEsPresence || (!isMissing(eStr) && qTrim.equalsIgnoreCase(eStr.trim()));

					if (!(respMatch && dbMatch && esMatch)) {
						// If ES missing specifically and other two match, we already marked nullEs
						// above.
						if (!(missingEs && respMatch && dbMatch)) {
							rowClass = "fail";
							statusText = "VALUE_MISMATCH";
							failures.add("Value mismatch for key: " + rawKey + " -> req=" + qStr + ", resp=" + rStr
									+ ", es=" + eStr + ", db=" + dStr);
						}
					}
				} else {
					// request not present -> compare response vs DB (and ES if required)
					boolean respDbMatch = !isMissing(rStr) && !isMissing(dStr)
							&& rStr.trim().equalsIgnoreCase(dStr.trim());
					boolean respEsMatch = !requireEsPresence
							|| (!isMissing(eStr) && !isMissing(rStr) && rStr.trim().equalsIgnoreCase(eStr.trim()));
					if (!(respDbMatch && respEsMatch)) {
						rowClass = "fail";
						statusText = "VALUE_MISMATCH";
						failures.add("Value mismatch for key: " + rawKey + " -> resp=" + rStr + ", es=" + eStr + ", db="
								+ dStr);
					}
				}
			}

			// If excluded-from-request and passed, mark visually as excl; if it failed,
			// keep fail/nullEs color
			String appliedClass = isExcludedFromRequest ? (rowClass.equals("pass") ? "excl" : rowClass) : rowClass;
			String statusLabel = (isExcludedFromRequest ? "EXCL-REQ " : "") + statusText;

			html.append("<tr class='").append(appliedClass).append("'>").append("<td>").append(htmlEscape(rawKey))
					.append("</td>").append("<td class='mono'>").append(htmlEscape(safe(qStr))).append("</td>")
					.append("<td class='mono'>").append(htmlEscape(safe(rStr))).append("</td>")
					.append("<td class='mono'>").append(htmlEscape(safe(eStr))).append("</td>")
					.append("<td class='mono'>").append(htmlEscape(safe(dStr))).append("</td>").append("<td>")
					.append(statusLabel).append("</td>").append("</tr>");
		}

		html.append("</table>");
		if (t != null) {
			t.info("📊 Cross-layer validation results:");
			t.info(html.toString());
		}

		// final assertion: fail on any recorded issues
		if (!failures.isEmpty()) {
			if (t != null)
				t.fail("❌ Validation failed with issues:\n" + String.join("\n", failures));
			throw new AssertionError("Cross-layer validation failed:\n" + String.join("\n", failures));
		} else {
			if (t != null)
				t.pass("✅ Cross-layer validation passed for ID=" + recordId);
		}
	}

	private static Map<String, Object> safeJsonToMap(String json) {
		if (json == null || json.trim().isEmpty())
			return Collections.emptyMap();
		try {
			// first try JsonPath quick parse
			Object o = JsonPath.from(json).get("");
			if (o instanceof Map) {
				return (Map<String, Object>) o;
			}
			// fallback to Jackson mapping
			return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			try {
				return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
				});
			} catch (Exception ex) {
				return Collections.emptyMap();
			}
		}
	}

	/**
	 * Flatten a nested Map structure into dot-notated keys. Example: { user: {
	 * name: "x", roles: ["a"] } } => user.name -> "x" user.roles[] -> List
	 * user.roles[0] -> "a"
	 *
	 * Signature matches your validator code.
	 */
	@SuppressWarnings("unchecked")
	private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
		if (node == null)
			return;

		for (Map.Entry<String, Object> e : node.entrySet()) {
			String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
			Object value = e.getValue();

			if (value instanceof Map) {
				flattenJson((Map<String, Object>) value, key, out);
			} else if (value instanceof List) {
				List<?> list = (List<?>) value;

				// store the list as a whole (useful for table view)
				out.put(key + "[]", list);

				// flatten individual list items with indices
				for (int i = 0; i < list.size(); i++) {
					Object item = list.get(i);
					if (item instanceof Map) {
						flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
					} else {
						out.put(key + "[" + i + "]", item);
					}
				}
			} else {
				out.put(key, value);
			}
		}
	}

	// Helper: produce includeRequestKeys by reading requestJson keys and removing
	// excluded ones
	public static String[] computeIncludeRequestKeys(String requestJson, String... excludeFromRequest) {
		if (requestJson == null || requestJson.trim().isEmpty())
			return null; // null => use all request keys
		Map<String, Object> map = JsonPath.from(requestJson).getMap("");
		if (map == null || map.isEmpty())
			return null;
		Set<String> keys = new LinkedHashSet<>();
		for (String k : map.keySet())
			keys.add(k);

		if (excludeFromRequest != null) {
			Set<String> exclude = Arrays.stream(excludeFromRequest).filter(s -> s != null).map(String::trim)
					.map(String::toLowerCase).collect(Collectors.toSet());
			keys.removeIf(k -> exclude.contains(k.toLowerCase()));
		}
		return keys.toArray(new String[0]);
	}

	/**
	 * Find value in flat map case-insensitively. Also tries to match alias forms: -
	 * column names in DB might be underscore_case; we also check camelCase variant.
	 */
	private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
		if (flat == null || key == null)
			return null;
		String target = key.toLowerCase();

		// exact case-insensitive
		for (Map.Entry<String, Object> e : flat.entrySet()) {
			if (e.getKey() != null && e.getKey().toLowerCase().equals(target))
				return e.getValue();
		}

		// try suffix-insensitive or prefix-insensitive matches (e.g.,
		// resourceRegistries[] vs resourceRegistries[0])
		for (Map.Entry<String, Object> e : flat.entrySet()) {
			String ek = e.getKey();
			if (ek == null)
				continue;
			String lower = ek.toLowerCase();
			if (lower.equals(target + "[]") || lower.startsWith(target + "[")) {
				return e.getValue();
			}
		}

		// try underscore -> camel mapping
		String alt = underscoreToCamel(key);
		for (Map.Entry<String, Object> e : flat.entrySet()) {
			if (e.getKey() != null && e.getKey().equalsIgnoreCase(alt))
				return e.getValue();
		}

		return null;
	}

	/**
	 * Convert an arbitrary object to a list (if already list returns as-is, if
	 * scalar returns single-item list).
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> toList(Object o) {
		if (o == null)
			return Collections.emptyList();
		if (o instanceof List)
			return (List<Object>) o;
		return Collections.singletonList(o);
	}

	/**
	 * Convert object/collection/map into a scalar string for comparison/logging. -
	 * Lists are displayed as JSON-like list - Maps are converted to compact JSON
	 */
//	private static String toScalarString(Object o) {
//	    if (o == null) return null;
//	    try {
//	        if (o instanceof String) return (String) o;
//	        if (o instanceof Number) return String.valueOf(o);
//	        if (o instanceof Boolean) return String.valueOf(o);
//	        if (o instanceof List || o instanceof Map) {
//	            return OBJECT_MAPPER.writeValueAsString(o);
//	        }
//	        return String.valueOf(o);
//	    } catch (Exception e) {
//	        return String.valueOf(o);
//	    }
//	}

	/**
	 * Parse ISO timestamps or offset datetimes; return Instant or null.
	 */
	private static Instant parseInstantOrNull(String s) {
		if (s == null)
			return null;
		// try Instant.parse (strict)
		try {
			return Instant.parse(s);
		} catch (DateTimeParseException ignored) {
		}
		// try OffsetDateTime
		try {
			return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
		} catch (DateTimeParseException ignored) {
		}
		// try lenient parse via ObjectMapper (if JSON numeric epoch present)
		try {
			// last attempt: parse non-ISO using ObjectMapper to Date (rare)
			Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class);
			if (d != null)
				return d.toInstant();
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * Compare instants allowing 'millisTolerance' milliseconds difference.
	 */
	private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
		if (a == null || b == null)
			return false;
		long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
		return diff <= millisTolerance;
	}

	/**
	 * Convert underscore_case to camelCase (simple).
	 */
	private static String underscoreToCamel(String s) {
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder();
		boolean upper = false;
		for (char c : s.toCharArray()) {
			if (c == '_' || c == '-') {
				upper = true;
			} else {
				sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
				upper = false;
			}
		}
		return sb.toString();
	}

	/**
	 * Pretty-format set of keys for logs (comma separated).
	 */
	private static String prettyFormatKeys(Set<String> keys) {
		if (keys == null || keys.isEmpty())
			return "[]";
		return keys.stream().collect(Collectors.joining(", "));
	}

	private static String htmlEscape(String s) {
		if (s == null)
			return "&lt;null&gt;";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\r",
				"");
	}

	private static boolean isMissing(String s) {
		if (s == null)
			return true;
		String t = s.trim();
		if (t.isEmpty())
			return true;
		if ("null".equalsIgnoreCase(t))
			return true;
		if ("0000-00-00".equals(t) || "0000-00-00 00:00:00".equals(t))
			return true;
		return false;
	}

//	ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, resourceRegistryId, "issue-steps", "Issue", null, new String[]{"createdDate"}, true);

	// Validate everything (Request → Response → ES → DB)
	// ApiValidators.validateKeysAcrossResponseEsDb(
//    response,               // Response object from your API call
//    requestBody,            // JSON string you sent in request
//    issueId,                // The ID you want to validate in ES/DB
//    "issue-steps",          // Elasticsearch index name (without prefix)
//    "Issue",                // Database table name
//    null,                   // includeKeys = null → validate all keys
//    null,                   // excludeKeys = null → don't skip anything
//    true                    // compareValues = true → also check if values match
//);

	// Validate only specific keys
//	String[] onlyKeys = { "id", "updatedDate", "status" };
//	ApiValidators.validateKeysAcrossResponseEsDb(
//	    response,
//	    requestBody,
//	    issueId,
//	    "issue-steps",
//	    "Issue",
//	    onlyKeys,     // includeKeys
//	    null,         // excludeKeys
//	    true
//	);

	// Validate everything except a few keys

//	String[] excludeKeys = { "createdDate", "updatedDate" };
//	ApiValidators.validateKeysAcrossResponseEsDb(
//	    response,
//	    requestBody,
//	    issueId,
//	    "issue-steps",
//	    "Issue",
//	    null,          // includeKeys = null → all keys
//	    excludeKeys,   // skip these
//	    true
//	);
//	ApiValidators.validateKeysAcrossResponseEsDb(
//		    response,
//		    requestBody,
//		    issueId,
//		    "issue-steps",
//		    "Issue",
//		    null,
//		    null,
//		    false     // compareValues = false → only presence check
//		);

	// Example: exclude "notes" and "createdDate" only from request,
	// but ensure "updatedDate" from response is validated (and ES/DB compared)

	// String[] includeReq = ApiValidators.computeIncludeRequestKeys(requestJson);
	// // or null to use all
//	String[] includeResp = null; // use all response keys
//	String[] exclude = new String[] {"createdDate"};
//
//	ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, resourceId,
//	        "issue-steps", "Issue", includeReq, includeResp, exclude, true);

//	String[] includeReq = null; // use union
//	String[] includeResp = new String[] {"updatedDate", "status", "resourceRegistries[]"};
//	ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, resourceId,
//	        "issue-steps", "Issue", includeReq, includeResp, null, true);

//	ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, resourceId,
//	        "issue-steps", "Issue", null, null, new String[] {"someOptionalField"}, true);

	// ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, issueId,
//    "issue-steps", "Issue", null, null, true, false);

//	ApiValidators.validateKeysAcrossResponseEsDb(response, requestJson, issueId,
//		    "issue-steps", "Issue",
//		    new String[] {"resourceName","label","deleted"},   // only these keys
//		    new String[] {"meta.*"},                           // exclude these (if needed)
//		    true,   // compare values
//		    true    // require ES presence
//		);

//	ApiValidators.validateKeysAcrossResponseEsDb(response, null, issueId,
//		    "issue-steps", "Issue", null, null, true, false);


    private static Optional<Map.Entry<String,Object>> findEntryCaseInsensitive(Map<String,Object> flat, String key) {
        if (flat == null || key == null) return Optional.empty();
        String target = key.toLowerCase();
        // exact
        for (Map.Entry<String,Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(target)) return Optional.of(e);
        // array / index forms
        for (Map.Entry<String,Object> e : flat.entrySet()) {
            String ek = e.getKey(); if (ek == null) continue;
            String lower = ek.toLowerCase();
            if (lower.equals(target + "[]") || lower.startsWith(target + "[")) return Optional.of(e);
        }
        // underscore -> camel fallback
        String alt = underscoreToCamel(target);
        for (Map.Entry<String,Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(alt)) return Optional.of(e);
        return Optional.empty();
    }

    

    

    // ======================================================================
    // Public API: validate + screenshot (single call)
    // ======================================================================
    public static void validateRequestKeysNullsAndMismatchesWithScreenshots(
            Response response,
            String requestJson,
            int recordId,
            String esIndexName,
            String dbTableName,
            boolean requireEsPresence,
            long timestampToleranceMs
    ) {
        ExtentTest t = log();
        if (t != null) t.info("🔎 Cross-layer validation (screenshots) started for id=" + recordId);

        // flatten request & response
        Map<String,Object> reqMap = safeJsonToMap(requestJson);
        Map<String,Object> reqFlat = new LinkedHashMap<>();
        flattenJson(reqMap, "", reqFlat);
        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());

        Map<String,Object> respMap = safeJsonToMap(response == null ? null : response.getBody().asString());
        Map<String,Object> respFlat = new LinkedHashMap<>();
        flattenJson(respMap, "", respFlat);
        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());

        // fetch ES
        Map<String,Object> esSource = Collections.emptyMap();
        try {
            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
                    esIndexName, recordId);
            Response esResp = io.restassured.RestAssured.given().get(esUrl).then().extract().response();
            List<Map<String,Object>> hits = esResp.jsonPath().getList("hits.hits");
            if (hits != null && !hits.isEmpty()) {
                Object _src = hits.get(0).get("_source");
                if (_src instanceof Map) esSource = (Map<String,Object>) _src;
            } else if (requireEsPresence && t != null) {
                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
            }
        } catch (Exception ex) {
            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
        }
        Map<String,Object> esFlat = new LinkedHashMap<>();
        flattenJson(esSource, "", esFlat);
        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());

        // fetch DB raw row: preserve raw SQL column order & exact names
        LinkedHashMap<String,Object> dbRawRowOrdered = new LinkedHashMap<>();
        Map<String,Object> dbLogicalFlat = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String col = meta.getColumnName(i);
                        Object val = rs.getObject(i);
                        dbRawRowOrdered.put(col, val);
                        dbLogicalFlat.put(col.toLowerCase(), val);
                        dbLogicalFlat.put(underscoreToCamel(col).toLowerCase(), val);
                    }
                } else if (t != null) {
                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
                }
            }
        } catch (Exception ex) {
            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
        }

        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());

        // build union of keys: include raw DB column names as well as request/resp/es keys
        Set<String> keysToCheck = new LinkedHashSet<>();
        for (String k : reqFlat.keySet()) keysToCheck.add(k);
        for (String k : respFlat.keySet()) keysToCheck.add(k);
        for (String k : esFlat.keySet()) keysToCheck.add(k);
        for (String col : dbRawRowOrdered.keySet()) keysToCheck.add(col);

        Map<String,Object> reqFlatLower = reqFlat.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
        Map<String,Object> respFlatLower = respFlat.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
        Map<String,Object> esFlatLower = esFlat.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
        Map<String,Object> dbLogicalLower = dbLogicalFlat.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));

        class KRes { String key; String req; String resp; String es; String db; String status; String css; }
        List<KRes> results = new ArrayList<>();
        Set<String> failingKeys = new LinkedHashSet<>();
        Set<String> capturedKeys = new HashSet<>();

        for (String rawKey : keysToCheck) {
            String keyDisplay = rawKey;
            String keyNorm = rawKey.toLowerCase();

            Object reqVal = reqFlatLower.getOrDefault(keyNorm, null);
            Object respVal = respFlatLower.getOrDefault(keyNorm, null);
            Object esVal = esFlatLower.getOrDefault(keyNorm, null);
            Object dbVal = null;

            Optional<Map.Entry<String,Object>> rawDbEntry = dbRawRowOrdered.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getKey().equalsIgnoreCase(rawKey))
                    .findFirst();
            if (rawDbEntry.isPresent()) {
                dbVal = rawDbEntry.get().getValue();
                keyDisplay = rawDbEntry.get().getKey();
            } else {
                dbVal = dbLogicalLower.getOrDefault(keyNorm, null);
            }

            String qStr = toScalarString(reqVal);
            String rStr = toScalarString(respVal);
            String eStr = toScalarString(esVal);
            String dStr = toScalarString(dbVal);

            KRes kres = new KRes();
            kres.key = keyDisplay;
            kres.req = safe(qStr);
            kres.resp = safe(rStr);
            kres.es = safe(eStr);
            kres.db = safe(dStr);
            kres.status = "PASS";
            kres.css = "pass";

            boolean missing = false;
            boolean mismatch = false;

            if (rStr == null) { missing = true; kres.status = "MISSING"; kres.css = "fail"; }
            if (dStr == null) { missing = true; kres.status = "NULL_IN_DB"; kres.css = "fail"; }
            if (requireEsPresence && (eStr == null)) { missing = true; kres.status = "NULL_IN_ES"; kres.css = "warn"; }

            if (!missing && qStr != null) {
                Instant iq = parseInstantOrNull(qStr);
                Instant ir = parseInstantOrNull(rStr);
                Instant ie = parseInstantOrNull(eStr);
                Instant idb = parseInstantOrNull(dStr);
                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
                    boolean ok = instantEqualWithin(iq, ir, timestampToleranceMs) &&
                                 instantEqualWithin(iq, idb, timestampToleranceMs) &&
                                 (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
                    if (!ok) { mismatch = true; kres.status = "TIME_MISMATCH"; kres.css = "fail"; }
                } else {
                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
                            && qStr.trim().equalsIgnoreCase(dStr.trim())
                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase(eStr == null ? "" : eStr.trim()));
                    if (!allMatch) { mismatch = true; kres.status = "VALUE_MISMATCH"; kres.css = "fail"; }
                }
            } else if (!missing && qStr == null) {
                Instant ir = parseInstantOrNull(rStr);
                Instant idb = parseInstantOrNull(dStr);
                Instant ie = parseInstantOrNull(eStr);
                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
                    boolean ok = instantEqualWithin(ir, idb, timestampToleranceMs)
                            && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
                    if (!ok) { mismatch = true; kres.status = "TIME_MISMATCH"; kres.css = "fail"; }
                } else {
                    boolean respDb = rStr.trim().equalsIgnoreCase(dStr.trim());
                    boolean respEs = !requireEsPresence || rStr.trim().equalsIgnoreCase(eStr == null ? "" : eStr.trim());
                    if (!respDb || !respEs) { mismatch = true; kres.status = "VALUE_MISMATCH"; kres.css = "fail"; }
                }
            }

            if (missing || mismatch) {
                failingKeys.add(keyDisplay);
                if (!capturedKeys.contains(keyDisplay)) {
                    capturedKeys.add(keyDisplay);
                    try {
                        captureLayerScreenshotsForKeyStrictJsonMatch(keyDisplay, requestJson, response, esSource, dbRawRowOrdered, t);
                    } catch (Exception ex) {
                        if (t != null) t.warning("⚠️ capture failed for " + keyDisplay + ": " + ex.getMessage());
                    }
                }
            } else {
                if (t != null) t.pass("✅ Key matched: " + keyDisplay + " -> resp=" + safe(rStr) + " | db=" + safe(dStr));
            }

            results.add(kres);
        }

        String summaryHtml = buildCrossLayerSummaryHtml(results);
        if (t != null) {
            t.info("📊 Cross-layer summary:");
            t.info(summaryHtml);
        }

        if (!failingKeys.isEmpty()) {
            if (t != null) t.fail("❌ Cross-layer validation failed. Failing keys: " + failingKeys);
            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failingKeys);
        } else {
            if (t != null) t.pass("✅ Cross-layer validation passed (no failing keys)");
        }
    }

    // ---------------- Summary HTML ----------------
    private static String buildCrossLayerSummaryHtml(List<?> listResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><style>");
        sb.append("body{font-family:Arial;padding:12px;font-size:14px} ");
        sb.append(".legend{margin-bottom:10px} .legend span{display:inline-block;padding:8px;margin-right:8px;border-radius:4px}");
        sb.append(".pass{background:#d4edda;color:#155724;padding:8px} .fail{background:#f8d7da;color:#721c24;padding:8px} .warn{background:#fff3cd;color:#856404;padding:8px}");
        sb.append("table{border-collapse:collapse;width:100%} th,td{border:1px solid #ddd;padding:10px;text-align:left;vertical-align:top;font-size:14px} th{background:#f2f2f2;font-weight:700}");
        sb.append(".mono{font-family:monospace;font-size:13px} .keycol{width:18%} .statuscol{width:14%}");
        sb.append("</style></head><body>");
        sb.append("<div class='legend'><span class='pass'>✅ Green = All layers match</span><span class='warn'>⚠️ Yellow = Null/Missing in ES</span><span class='fail'>❌ Red = Missing or Mismatch (DB null or mismatch)</span></div>");
        sb.append("<table><tr><th class='keycol'>Key</th><th>Request</th><th>Response</th><th>Elasticsearch</th><th>Database (raw / logical)</th><th class='statuscol'>Status</th></tr>");

        for (Object o : listResults) {
            try {
                @SuppressWarnings("unchecked")
                Map<String,Object> m = OBJECT_MAPPER.convertValue(o, Map.class);
                String key = (String) m.getOrDefault("key", "<unknown>");
                String req = (String) m.getOrDefault("req", "null");
                String resp = (String) m.getOrDefault("resp", "null");
                String es = (String) m.getOrDefault("es", "null");
                String db = (String) m.getOrDefault("db", "null");
                String status = (String) m.getOrDefault("status", "PASS");
                String css = (String) m.getOrDefault("css", "pass");

                sb.append("<tr class='").append(css).append("'>");
                sb.append("<td class='mono'>").append(htmlEscape(key)).append("</td>");
                sb.append("<td class='mono'>").append(htmlEscape(req)).append("</td>");
                sb.append("<td class='mono'>").append(htmlEscape(resp)).append("</td>");
                sb.append("<td class='mono'>").append(htmlEscape(es)).append("</td>");
                sb.append("<td class='mono'>").append(htmlEscape(db)).append("</td>");
                sb.append("<td>").append(htmlEscape(status)).append("</td>");
                sb.append("</tr>");
            } catch (Exception ignored) {}
        }

        sb.append("</table></body></html>");
        return sb.toString();
    }

    // ---------------- Capture + helper panels ----------------

    private static void captureLayerScreenshotsForKeyStrictJsonMatch(
            String key,
            String requestJson,
            Response response,
            Map<String,Object> esSource,
            LinkedHashMap<String,Object> dbRawRowOrdered,
            ExtentTest t
    ) throws Exception {
        String reqPanel = highlightKeyInJsonStrict(requestJson == null ? "<no request>" : requestJson, key, "REQUEST");
        String respPanel = highlightKeyInJsonStrict(response == null ? "<no response>" : response.getBody().asString(), key, "RESPONSE");
        String esPanel = buildDbHtmlLogical(esSource == null ? Collections.emptyMap() : esSource, key, "ELASTICSEARCH (logical)");
        String dbPanelRaw = buildDbHtmlRaw(dbRawRowOrdered == null ? new LinkedHashMap<>() : dbRawRowOrdered, key, "DATABASE (raw columns)");

        String reqB = captureHtmlHeadlessBase64(reqPanel);
        String respB = captureHtmlHeadlessBase64(respPanel);
        String esB = captureHtmlHeadlessBase64(esPanel);
        String dbB = captureHtmlHeadlessBase64(dbPanelRaw);

        String combinedHtml = buildCombinedHtmlGrid(key, reqB, respB, esB, dbB);
        String combinedShot = captureHtmlHeadlessBase64(combinedHtml);

        if (t != null) {
            if (combinedShot != null) {
                t.info("📸 Comparison for key: " + key)
                        .addScreenCaptureFromBase64String(combinedShot, "Comparison_" + key.replaceAll("[^A-Za-z0-9_\\-]", "_"));
            } else {
                if (reqB != null) t.info("Request: " + key).addScreenCaptureFromBase64String(reqB);
                if (respB != null) t.info("Response: " + key).addScreenCaptureFromBase64String(respB);
                if (esB != null) t.info("ES: " + key).addScreenCaptureFromBase64String(esB);
                if (dbB != null) t.info("DB: " + key).addScreenCaptureFromBase64String(dbB);
            }
        }
    }

    private static String highlightKeyInJsonStrict(String json, String key, String title) {
        if (json == null) json = "<null>";
        String escaped = htmlEscape(json);
        try {
            Pattern p = Pattern.compile("(?i)((\"|')(" + Pattern.quote(key) + ")(\"|')\\s*:\\s*)");
            Matcher m = p.matcher(escaped);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, "<mark style='background:#fffd80;color:#000;'>" + m.group(1) + "</mark>");
            }
            m.appendTail(sb);
            escaped = sb.toString();
        } catch (Exception ignored) {}
        String html = "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:10px;font-size:16px;line-height:1.25} mark{padding:0 3px;border-radius:3px}</style></head><body>"
                + "<div style='font-weight:800;margin-bottom:10px;font-size:16px;'>" + htmlEscape(title) + "</div>"
                + "<pre>" + escaped + "</pre></body></html>";
        return html;
    }

    private static String buildDbHtmlRaw(Map<String,Object> dbRawRowOrdered, String keyToHighlight, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:10px;font-size:15px} table{border-collapse:collapse;width:100%} th,td{border:1px solid #ddd;padding:10px;text-align:left;vertical-align:top} th{background:#f2f2f2;font-weight:800} .hl{background:#fff3cd}.mono{font-family:monospace;font-size:14px}</style></head><body>");
        sb.append("<div style='font-weight:800;margin-bottom:10px;font-size:16px;'>").append(htmlEscape(title)).append("</div>");
        sb.append("<table><tr><th>Column</th><th>Value</th></tr>");
        if (dbRawRowOrdered == null || dbRawRowOrdered.isEmpty()) {
            sb.append("<tr><td colspan='2' class='mono'>&lt;no data&gt;</td></tr>");
        } else {
            for (Map.Entry<String,Object> e : dbRawRowOrdered.entrySet()) {
                String col = e.getKey();
                String val = toScalarString(e.getValue());
                boolean hl = col != null && col.equalsIgnoreCase(keyToHighlight);
                sb.append("<tr").append(hl ? " class='hl'":"").append(">");
                sb.append("<td class='mono'>").append(htmlEscape(col)).append("</td>");
                sb.append("<td class='mono'>").append(htmlEscape(safe(val))).append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private static String buildDbHtmlLogical(Map<String,Object> logicalFlat, String keyToHighlight, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:10px;font-size:15px} table{border-collapse:collapse;width:100%} th,td{border:1px solid #ddd;padding:10px;text-align:left} th{background:#f2f2f2;font-weight:800} .hl{background:#fff3cd} .mono{font-family:monospace;font-size:14px}</style></head><body>");
        sb.append("<div style='font-weight:800;margin-bottom:10px;font-size:16px;'>").append(htmlEscape(title)).append("</div>");
        sb.append("<table><tr><th>Key</th><th>Value</th></tr>");
        if (logicalFlat == null || logicalFlat.isEmpty()) {
            sb.append("<tr><td colspan='2' class='mono'>&lt;no data&gt;</td></tr>");
        } else {
            logicalFlat.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        String k = e.getKey();
                        String v = toScalarString(e.getValue());
                        boolean hl = k != null && k.equalsIgnoreCase(keyToHighlight);
                        sb.append("<tr").append(hl ? " class='hl'":"").append(">");
                        sb.append("<td class='mono'>").append(htmlEscape(k)).append("</td>");
                        sb.append("<td class='mono'>").append(htmlEscape(safe(v))).append("</td>");
                        sb.append("</tr>");
                    });
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private static String buildCombinedHtmlGrid(String key, String reqBase64, String respBase64, String esBase64, String dbBase64) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><title>Compare ").append(htmlEscape(key)).append("</title>");
        sb.append("<style>body{font-family:Arial;padding:12px} .grid{display:grid;grid-template-columns:1fr 1fr;grid-gap:12px} .card{border:1px solid #ddd;padding:12px} .title{font-weight:800;margin-bottom:8px;font-size:16px} img{max-width:100%;height:auto;display:block}</style>");
        sb.append("</head><body>");
        sb.append("<h2 style='font-size:18px;margin-bottom:10px'>Comparison for key: ").append(htmlEscape(key)).append("</h2>");
        sb.append("<div class='grid'>");
        sb.append("<div class='card'><div class='title'>Request</div>");
        sb.append(reqBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + reqBase64 + "'/>");
        sb.append("</div>");
        sb.append("<div class='card'><div class='title'>Response</div>");
        sb.append(respBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + respBase64 + "'/>");
        sb.append("</div>");
        sb.append("<div class='card'><div class='title'>Elasticsearch</div>");
        sb.append(esBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + esBase64 + "'/>");
        sb.append("</div>");
        sb.append("<div class='card'><div class='title'>Database</div>");
        sb.append(dbBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + dbBase64 + "'/>");
        sb.append("</div>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    private static String captureHtmlHeadlessBase64(String html) {
        WebDriver driver = null;
        Path tmp = null;
        try {
            tmp = java.nio.file.Files.createTempFile("av_html_", ".html");
            java.nio.file.Files.write(tmp, html.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            System.setProperty("webdriver.chrome.silentOutput", "true");
            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
            // larger window-size for high-res screenshots
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=2200,1400", "--hide-scrollbars");
            driver = new ChromeDriver(options);
            driver.get(tmp.toUri().toString());
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            return Base64.getEncoder().encodeToString(png);
        } catch (Exception e) {
            ExtentTest t = log();
            if (t != null) t.warning("⚠️ captureHtmlHeadlessBase64 failed: " + e.getMessage());
            return null;
        } finally {
            try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
            try { if (tmp != null) java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignored) {}
        }
    }
}
    
