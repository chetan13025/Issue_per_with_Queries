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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import reports.ExtentCucumberListener;

public class ApiValidators {

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

		String sql = "SELECT * FROM `social_comments_model_dhin`.`" + tableName + "` LIMIT 1000";

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
	
	//Examples 
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
	
	//Examples
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate");
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry", "createdDate", "updatedDate");
//	ApiValidators.validateDbNotNullExcept(ResourceRegistryId, "ResourceRegistry");

// print only which are null values
	public static void validateDbOnlyNullExcept(int id, String tableName, String... skipColumns) {
	    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
	    if (current == null) return;

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
	                if (skipSet.contains(colName)) continue;

	                Object val = rs.getObject(colName);
	                String s = (val == null) ? null : String.valueOf(val).trim();

	                if (s == null || s.isEmpty() ||
	                    "0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) {

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
	
	//validates all columns and print which are non null also
	
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
	private static final String DEFAULT_SCHEMA = "social_comments_model_dhin";

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
	    if (t != null) t.info("🔍 validateNoExtraKeys — allowed exceptions: " + Arrays.toString(allowedExtraKeys));

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
	                if (a == null) continue;
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
	                if (t != null) t.pass("✅ No unexpected keys found in response (after allowed exceptions).");
	            } else {
	                Set<String> formatted = formatArrayKeys(extraKeys);
	                if (t != null) t.fail("❌ Unexpected keys found in response (not in request): " + formatted);
	                throw new AssertionError("Unexpected keys in response: " + formatted);
	            }
	            return;
	        }

	        // CASE B: no request body -> treat allowedExtraKeys AS the whitelist (expected keys)
	        // If caller didn't pass allowedExtraKeys, we cannot sensibly validate/restrict; fail fast.
	        if ((allowedExact.isEmpty() && allowedPrefixes.isEmpty())) {
	            if (t != null) t.fail("❌ No request body and no allowedExtraKeys provided — cannot determine expected response keys.");
	            throw new IllegalArgumentException("Request body missing; pass expected/allowed keys in allowedExtraKeys when requestJson is empty.");
	        }

	        // Build "allowed set" from allowedExact + allowedPrefixes (prefix matches allowed)
	        // Unexpected keys = responseKeys - allowedMatches
	        Set<String> unexpected = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	        unexpected.addAll(responseKeys);

	        // Remove keys that match allowedExact or allowedPrefixes
	        Iterator<String> it = unexpected.iterator();
	        while (it.hasNext()) {
	            String key = it.next();
	            String kl = key.toLowerCase();

	            if (allowedExact.contains(kl)) {
	                if (t != null) t.info("↷ Allowed key (exact): " + key);
	                it.remove();
	                continue;
	            }
	            boolean matched = false;
	            for (String pref : allowedPrefixes) {
	                if (kl.startsWith(pref)) {
	                    if (t != null) t.info("↷ Allowed key (prefix " + pref + ".*): " + key);
	                    it.remove();
	                    matched = true;
	                    break;
	                }
	            }
	            if (matched) continue;
	        }

	        if (unexpected.isEmpty()) {
	            if (t != null) t.pass("✅ Response contains only allowed keys (whitelist).");
	        } else {
	            Set<String> formatted = formatArrayKeys(unexpected);
	            if (t != null) t.fail("❌ Response contains unexpected keys (not in whitelist): " + formatted);
	            throw new AssertionError("Response contains unexpected keys: " + formatted);
	        }

	    } catch (AssertionError ae) {
	        throw ae;
	    } catch (Exception e) {
	        if (t != null) t.warning("⚠️ validateNoExtraKeys error: " + e.getMessage());
	        throw new AssertionError("Failed to validate extra keys: " + e.getMessage(), e);
	    }
	}

	// helper: remove allowed items in-place from 'candidates' using exact & prefix lists
	private static void filterAllowed(Set<String> candidates, Set<String> allowedExact, List<String> allowedPrefixes, ExtentTest t) {
	    Iterator<String> it = candidates.iterator();
	    while (it.hasNext()) {
	        String key = it.next();
	        String kl = key.toLowerCase();
	        if (allowedExact.contains(kl)) {
	            if (t != null) t.info("↷ Allowed extra key (exact): " + key);
	            it.remove();
	            continue;
	        }
	        boolean matchedPref = false;
	        for (String pref : allowedPrefixes) {
	            if (kl.startsWith(pref)) {
	                if (t != null) t.info("↷ Allowed extra key (prefix " + pref + ".*): " + key);
	                it.remove();
	                matchedPref = true;
	                break;
	            }
	        }
	        if (matchedPref) continue;
	    }
	}

	// helper: format any index-style array keys into canonical name[] for clearer logs
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
	    if (map == null) return result;
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
	            flatMap.put(prefix + "[]", "[]");  // ✅ represent empty arrays
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
	            java.util.regex.Matcher m = java.util.regex.Pattern
	                    .compile("\"([^\"]+)\"\\s*:\\s*\\[")
	                    .matcher(jsonString);
	            while (m.find()) {
	                String key = m.group(1);
	                keys.add(key + "[]");
	            }
	        }
	    } catch (Exception ignored) {
	    }
	}
    
	//ApiValidators.validateNoExtraKeys(requestJson, response);
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
		if (actualId == expectedId ) {
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
	 * Validate: response contains all keys that request contains (i.e. no missing keys).
	 * allowedMissingKeys may contain exact names or prefix.* entries.
	 */
	public static void validateNoMissingKeys(String requestJson, Response response, String... allowedMissingKeys) {
	    validateKeySetDiff(requestJson, response, false, null, allowedMissingKeys);
	}

	/**
	 * Combined convenience: run both extra-keys and missing-keys checks in a single call.
	 * allowedExtraKeys and allowedMissingKeys work like in the individual methods.
	 */
	public static void validateKeyParity(String requestJson,
	                                     Response response,
	                                     String[] allowedExtraKeys,
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
	        if (t != null) t.fail("One or more parity validators failed: \n - " + String.join("\n - ", failures));
	        throw new AssertionError("One or more validators failed: " + String.join(" | ", failures));
	    } else {
	        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
	        if (t != null) t.pass("✅ Key parity checks passed (no unexpected extras, no missing keys)");
	    }
	}
	// ----------------- Internal helpers -----------------

	/** Core worker: when checkExtras==true -> validate response - request (extra keys)
	 *  when checkExtras==false -> validate request - response (missing keys)
	 *  allowedExtra/allowedMissing behave as exact or prefix.*.
	 */
	private static void validateKeySetDiff(String requestJson,
	                                       Response response,
	                                       boolean checkExtras,
	                                       String[] allowedExtra,
	                                       String[] allowedMissing) {
	    ExtentTest t = ExtentCucumberListener.getCurrentScenario();
	    String verb = checkExtras ? "Response:Except" : "Request:Missing";

	    if (t != null) t.info("🔍 Running validator: " + (checkExtras ? "Response → Extra keys" : "Request → Missing keys"));

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
	            for (String a : allowed) if (a != null) {
	                String s = a.trim();
	                if (s.endsWith(".*")) allowedPrefixes.add(normalizeKey(s.substring(0, s.length()-2)).toLowerCase());
	                else allowedExact.add(normalizeKey(s).toLowerCase());
	            }
	        }

	        // remove allowed items
	        Iterator<String> it = diff.iterator();
	        while (it.hasNext()) {
	            String k = it.next();
	            String lk = k.toLowerCase();
	            if (allowedExact.contains(lk)) { if (t != null) t.info("↷ Allowed (exact): " + k); it.remove(); continue; }
	            boolean prefMatch = false;
	            for (String p : allowedPrefixes) {
	                if (lk.startsWith(p)) { prefMatch = true; if (t != null) t.info("↷ Allowed (prefix " + p + ".*): " + k); it.remove(); break; }
	            }
	            if (prefMatch) continue;
	        }

	        if (diff.isEmpty()) {
	            if (t != null) t.pass("✅ " + (checkExtras ? "No unexpected keys in response." : "No missing keys in response."));
	            return;
	        }

	        // make a readable message: show normalized keys, but for arrays append [] (already normalized)
	        if (t != null) t.fail("❌ " + verb + " " + (allowed == null ? "[]" : "Except " + Arrays.toString(allowed)) +
	                " -> Unexpected keys in " + subjectName + ": " + diff);
	        throw new AssertionError(subjectName + " has unexpected keys: " + diff);
	    } catch (AssertionError ae) {
	        throw ae;
	    } catch (Exception e) {
	        if (t != null) t.warning("⚠️ Validator error: " + e.getMessage());
	        throw new AssertionError("Validator failed: " + e.getMessage(), e);
	    }
	}

	/* Helpers used by the validators (flattening + normalization) */

	private static Set<String> normalizeKeySet(Collection<String> keys) {
	    Set<String> out = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	    for (String k : keys) if (k != null) out.add(normalizeKey(k));
	    return out;
	}

	/** Convert index forms to canonical [] form:
	 *  e.g. foo[0] -> foo[] ; foo[0].id -> foo[].id
	 */
	private static String normalizeKey(String key) {
	    // replace numeric indices with []
	    String replaced = key.replaceAll("\\[\\d+\\]", "[]");
	    // if someone passed bare array name (like resourceRegistries) we *do not* forcibly add []
	    // includeArrayKeysExplicitly adds name[] for empty arrays. For non-empty arrays flatten() will produce indexed keys and they normalize above.
	    return replaced;
	}

	

	

	/** Quick scan of raw JSON to add explicit key[] entries for array properties (including empty arrays)
	 *  so e.g. "resourceRegistries": [] will produce resourceRegistries[] in the flattened map.
	 */
	private static void includeArrayKeysExplicitly(String jsonString, Map<String, Object> mapToAddTo) {
	    if (jsonString == null || !jsonString.contains("[")) return;
	    try {
	        Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[").matcher(jsonString);
	        while (m.find()) {
	            String key = m.group(1);
	            mapToAddTo.putIfAbsent(key + "[]", "[]");
	        }
	    } catch (Exception ignored) { }
	}
	// Only check extra keys (allow "id" and "messageId" to appear)
//	ApiValidators.validateNoExtraKeys(requestJson, response, "id", "messageId");

	// Only check missing keys (allow "optionalField" to be absent)
//	ApiValidators.validateNoMissingKeys(requestJson, response, "optionalField");

	// Run both checks in one shot (allow response to have additional "meta.*" keys, allow request to have optional "notes")
//	ApiValidators.validateKeyParity(requestJson, response, new String[]{"meta.*"}, new String[]{"notes"});

}
