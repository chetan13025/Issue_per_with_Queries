////package validators;
////
////import com.aventstack.extentreports.ExtentTest;
////import reports.ExtentCucumberListener;
////import io.restassured.response.Response;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.core.type.TypeReference;
////
////import org.openqa.selenium.*;
////import org.openqa.selenium.chrome.ChromeDriver;
////import org.openqa.selenium.chrome.ChromeOptions;
////import io.github.bonigarcia.wdm.WebDriverManager;
////
////import java.sql.*;
////import java.util.*;
////import java.util.Base64;
////import java.util.regex.Matcher;
////import java.util.regex.Pattern;
////
////public class ApiDataValidator {
////
////    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
////    private static final String JDBC_USER = "root";
////    private static final String JDBC_PASS = "dhi123";
////    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
////    private static final ObjectMapper MAPPER = new ObjectMapper();
////
////    // Overloaded - accepts RestAssured Response directly
////    public static void validateKeysAcrosRequestResponseEsDb(
////            String requestJson,
////            Response response,
////            int id,
////            String esIndexName,
////            String dbTableName,
////            boolean excludeElasticCompletely,
////            String[] excludeAllKeys,
////            String[] excludeReqOnly
////    ) throws Exception {
////        String responseJson = response != null ? response.getBody().asString() : null;
////        validateKeysAcrosRequestResponseEsDb(
////                requestJson, responseJson, id,
////                esIndexName, dbTableName,
////                excludeElasticCompletely,
////                excludeAllKeys, excludeReqOnly
////        );
////    }
////
////    // Main Validator — Full screenshots always
////    public static void validateKeysAcrosRequestResponseEsDb(
////            String requestJson,
////            String responseJson,
////            int id,
////            String esIndexName,
////            String dbTableName,
////            boolean excludeElasticCompletely,
////            String[] excludeAllKeys,
////            String[] excludeReqOnly
////    ) throws Exception {
////
////        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
////
////        if (test != null)
////            test.info("Cross-validation started for ID: " + id);
////
////        Map<String, Object> reqFlat = flattenSafe(requestJson);
////        Map<String, Object> respFlat = flattenSafe(responseJson);
////
////        // Fetch DB
////        Map<String, Object> dbFlat = fetchDbById(dbTableName, id);
////
////        // Fetch ES if included
////        Map<String, Object> esFlat = excludeElasticCompletely
////                ? new LinkedHashMap<>()
////                : fetchEsById(esIndexName, id);
////
////        // Combine all keys
////        Set<String> allKeys = new LinkedHashSet<>();
////        allKeys.addAll(reqFlat.keySet());
////        allKeys.addAll(respFlat.keySet());
////        allKeys.addAll(dbFlat.keySet());
////        allKeys.addAll(esFlat.keySet());
////
////        Set<String> excludeEverywhere = lowerSet(excludeAllKeys);
////        Set<String> excludeRequestOnly = lowerSet(excludeReqOnly);
////
////        StringBuilder html = new StringBuilder();
////        html.append("<h3>Validation - ID: ").append(id).append("</h3>")
////            .append("<table border='1' style='border-collapse:collapse;width:100%'>")
////            .append("<tr style='background:#d2e0ff;font-weight:bold'>")
////            .append("<td>Key</td><td>Request</td><td>Response</td><td>ES</td><td>DB</td><td>Status</td></tr>");
////
////        boolean allPass = true;
////
////        for (String key : allKeys) {
////            String lk = key.toLowerCase();
////            if (excludeEverywhere.contains(lk)) continue;
////
////            Object rqV = reqFlat.get(lk);
////            Object rpV = respFlat.get(lk);
////            Object esV = esFlat.get(lk);
////            Object dbV = dbFlat.get(lk);
////
////            boolean pass = true;
////            String status = "PASS";
////            if (!excludeRequestOnly.contains(lk) && rqV != null && rpV != null && !equalNorm(rqV, rpV)) {
////                pass = false; status = "Mismatch Req↔Resp";
////            }
////            if (dbV != null && rpV != null && !equalNorm(rpV, dbV)) {
////                pass = false; status = "Mismatch Resp↔DB";
////            }
////            if (!excludeElasticCompletely && esV != null && rpV != null && !equalNorm(rpV, esV)) {
////                pass = false; status = "Mismatch Resp↔ES";
////            }
////
////            if (!pass) allPass = false;
////
////            String row = buildTableRowWithScreenshots(key, rqV, rpV, esV, dbV, status);
////            html.append(row);
////        }
////
////        html.append("</table>");
////
////        if (test != null)
////            test.info(html.toString());
////
////        if (!allPass)
////            throw new AssertionError("Some keys failed validation. See Extent report.");
////    }
////
////    // ==== DB Fetch ====
////    private static Map<String, Object> fetchDbById(String table, int id) {
////        Map<String, Object> out = new LinkedHashMap<>();
////        try (Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
////             PreparedStatement ps = con.prepareStatement(
////                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=? LIMIT 1"
////             )) {
////            ps.setInt(1, id);
////            ResultSet rs = ps.executeQuery();
////            ResultSetMetaData md = rs.getMetaData();
////            if (rs.next()) {
////                for (int i = 1; i <= md.getColumnCount(); i++) {
////                    String col = md.getColumnName(i);
////                    Object val = rs.getObject(i);
////                    out.put(col.toLowerCase(), val);
////                    out.put(toCamel(col).toLowerCase(), val);
////                }
////            }
////        } catch (Exception ignored) {}
////        return out;
////    }
////
////    // ==== ES Fetch ====
////    private static Map<String, Object> fetchEsById(String index, int id) {
////        try {
////            Response esResp = io.restassured.RestAssured
////                    .given()
////                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
////                            "_index/_search?q=id:" + id + "&size=1")
////                    .then().extract().response();
////
////            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
////            if (hits != null && !hits.isEmpty()) {
////                Object sourceObj = hits.get(0).get("_source");
////                if (sourceObj instanceof Map) {
////                    @SuppressWarnings("unchecked")
////                    Map<String, Object> src = (Map<String, Object>) sourceObj;
////                    return flattenSafe(MAPPER.writeValueAsString(src));
////                }
////            }
////        } catch (Exception ignored) {}
////        return new LinkedHashMap<>();
////    }
////
////    // ==== Screenshot for each panel ====
////    private static String buildTableRowWithScreenshots(String key, Object rq, Object rp, Object es, Object db, String status) {
////        String rqS = toStr(rq);
////        String rpS = toStr(rp);
////        String esS = toStr(es);
////        String dbS = toStr(db);
////
////        return "<tr>"
////                + "<td>" + esc(key) + "</td>"
////                + "<td>" + capturePanel("Request", rqS, key) + "</td>"
////                + "<td>" + capturePanel("Response", rpS, key) + "</td>"
////                + "<td>" + capturePanel("Elastic", esS, key) + "</td>"
////                + "<td>" + capturePanel("DB", dbS, key) + "</td>"
////                + "<td style='background:" + (status.equals("PASS") ? "#c7ffd1" : "#ffb6b6") + "'>"
////                + status + "</td></tr>";
////    }
////
////    private static String capturePanel(String title, String body, String key) {
////        try {
////            String html = "<html><body style='font-family:monospace;white-space:pre;font-size:12px'>"
////                    + "<b>" + esc(title) + "</b><br>" + esc(body) + "</body></html>";
////
////            WebDriverManager.chromedriver().setup();
////            ChromeOptions opt = new ChromeOptions();
////            opt.addArguments("--headless=new", "--window-size=1200,600");
////            WebDriver driver = new ChromeDriver(opt);
////            driver.get("data:text/html;base64," +
////                    Base64.getEncoder().encodeToString(html.getBytes()));
////            Thread.sleep(300);
////
////            String b64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
////            driver.quit();
////
////            return "<a href='data:image/png;base64," + b64 + "' target='_blank'>"
////                    + "<img src='data:image/png;base64," + b64
////                    + "' style='width:220px;border:1px solid #aaa'/></a>";
////
////        } catch (Exception e) {
////            return esc(body);
////        }
////    }
////
////    // ==== Utilities ====
////    private static Map<String, Object> flattenSafe(String json) {
////        Map<String, Object> out = new LinkedHashMap<>();
////        if (json == null) return out;
////        try {
////            Map<String, Object> map = MAPPER.readValue(
////                    json,
////                    new TypeReference<Map<String, Object>>() {}
////            );
////            flatten(map, "", out);
////        } catch (Exception ignored) {}
////        return out;
////    }
////
////    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
////        src.forEach((k, v) -> {
////            String full = prefix.isEmpty() ? k : prefix + "." + k;
////            if (v instanceof Map) flatten((Map<String, Object>) v, full, out);
////            else out.put(full.toLowerCase(), v);
////        });
////    }
////
////    private static boolean equalNorm(Object a, Object b) {
////        return normalize(a).equals(normalize(b));
////    }
////
////    private static String normalize(Object v) {
////        if (v == null) return "";
////        return v.toString().trim().replaceAll("[-:T.Z]", "");
////    }
////
////    private static Set<String> lowerSet(String[] arr) {
////        Set<String> s = new HashSet<>();
////        if (arr == null) return s;
////        for (String a : arr) s.add(a.toLowerCase());
////        return s;
////    }
////
////    private static String safe(String s) {
////        return s.replaceAll("[^a-zA-Z0-9_]", "");
////    }
////
////    private static String toCamel(String s) {
////        StringBuilder sb = new StringBuilder();
////        boolean up = false;
////        for (char c : s.toCharArray()) {
////            if (c == '_' || c == '-') up = true;
////            else {
////                sb.append(up ? Character.toUpperCase(c) : c);
////                up = false;
////            }
////        }
////        return sb.toString();
////    }
////
////    private static String toStr(Object o) {
////        return o == null ? "" : o.toString();
////    }
////
////    private static String esc(String s) {
////        return s == null ? "" :
////                s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
////    }
////}
//
//
//
//
//
//
//
//
//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.sql.*;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    // Overloaded - accepts RestAssured Response directly
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response response,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeAllKeys,
//            String[] excludeReqOnly
//    ) throws Exception {
//        String responseJson = response != null ? response.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson, responseJson, id,
//                esIndexName, dbTableName,
//                excludeElasticCompletely,
//                excludeAllKeys, excludeReqOnly
//        );
//    }
//
//    // Main Validator — per-key screenshots, colored rows
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeAllKeys,
//            String[] excludeReqOnly
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//
//        if (test != null)
//            test.info("Cross-validation started for ID: " + id);
//
//        Map<String, Object> reqFlat  = flattenSafe(requestJson);
//        Map<String, Object> respFlat = flattenSafe(responseJson);
//
//        // Fetch DB
//        Map<String, Object> dbFlat = fetchDbById(dbTableName, id);
//
//        // Fetch ES if included
//        Map<String, Object> esFlat = excludeElasticCompletely
//                ? new LinkedHashMap<>()
//                : fetchEsById(esIndexName, id);
//
//        // Combine all keys (case-insensitive)
//        Set<String> allKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
//        allKeys.addAll(reqFlat.keySet());
//        allKeys.addAll(respFlat.keySet());
//        allKeys.addAll(dbFlat.keySet());
//        allKeys.addAll(esFlat.keySet());
//
//        Set<String> excludeEverywhere  = lowerSet(excludeAllKeys);
//        Set<String> excludeRequestOnly = lowerSet(excludeReqOnly);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>Validation - ID: ").append(id).append("</h3>")
//            .append("<table border='1' style='border-collapse:collapse;width:100%'>")
//            .append("<tr style='background:#d2e0ff;font-weight:bold'>")
//            .append("<td>Key</td><td>Request</td><td>Response</td><td>ES</td><td>DB</td><td>Status</td></tr>");
//
//        boolean allPass = true;
//
//        for (String key : allKeys) {
//            String lk = key.toLowerCase();
//
//            boolean isExcludedAll      = excludeEverywhere.contains(lk);
//            boolean isExcludedReqOnly  = excludeRequestOnly.contains(lk);
//
//            Object rqV = reqFlat.get(lk);
//            Object rpV = respFlat.get(lk);
//            Object esV = esFlat.get(lk);
//            Object dbV = dbFlat.get(lk);
//
//            boolean pass   = true;
//            String status;
//
//            if (isExcludedAll) {
//                // Completely excluded key: just display it, do not compare
//                status = "EXCLUDED_ALL";
//            } else {
//                // Compare Request vs Response (unless excluded from request)
//                status = "PASS";
//                if (!isExcludedReqOnly && rqV != null && rpV != null && !equalNorm(rqV, rpV)) {
//                    pass = false;
//                    status = "Mismatch Req↔Resp";
//                }
//
//                // Compare Response vs DB
//                if (dbV != null && rpV != null && !equalNorm(rpV, dbV)) {
//                    pass = false;
//                    status = "Mismatch Resp↔DB";
//                }
//
//                // Compare Response vs ES
//                if (!excludeElasticCompletely && esV != null && rpV != null && !equalNorm(rpV, esV)) {
//                    pass = false;
//                    status = "Mismatch Resp↔ES";
//                }
//
//                if (!pass) {
//                    // fail
//                } else if (isExcludedReqOnly) {
//                    // No mismatches, but excluded from request side only
//                    status = "EXCLUDED_FROM_REQUEST";
//                }
//            }
//
//            if (!isExcludedAll && !pass) {
//                allPass = false;
//            }
//
//            String row = buildTableRowWithScreenshots(
//                    key, rqV, rpV, esV, dbV,
//                    status,
//                    isExcludedAll,
//                    isExcludedReqOnly
//            );
//            html.append(row);
//        }
//
//        html.append("</table>");
//
//        if (test != null)
//            test.info(html.toString());
//
//        if (!allPass)
//            throw new AssertionError("Some keys failed validation. See Extent report.");
//    }
//
//    // ==== DB Fetch ====
//    private static Map<String, Object> fetchDbById(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        try (Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = con.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=? LIMIT 1"
//             )) {
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    // ==== ES Fetch ====
//    private static Map<String, Object> fetchEsById(String index, int id) {
//        try {
//            Response esResp = io.restassured.RestAssured
//                    .given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object sourceObj = hits.get(0).get("_source");
//                if (sourceObj instanceof Map) {
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> src = (Map<String, Object>) sourceObj;
//                    return flattenSafe(MAPPER.writeValueAsString(src));
//                }
//            }
//        } catch (Exception ignored) {}
//        return new LinkedHashMap<>();
//    }
//
//    // ==== Screenshot for each panel + colored rows ====
//    private static String buildTableRowWithScreenshots(
//            String key,
//            Object rq,
//            Object rp,
//            Object es,
//            Object db,
//            String status,
//            boolean excludedAll,
//            boolean excludedReqOnly
//    ) {
//        String rqS = toStr(rq);
//        String rpS = toStr(rp);
//        String esS = toStr(es);
//        String dbS = toStr(db);
//
//        // Row background based on status / exclusion
//        String bgColor;
//        if (excludedAll) {
//            // Grey
//            bgColor = "#e0e0e0";
//        } else if ("EXCLUDED_FROM_REQUEST".equals(status)) {
//            // Yellow
//            bgColor = "#fff9c4";
//        } else if (!"PASS".equals(status)) {
//            // Fail → red
//            bgColor = "#ffb6b6";
//        } else {
//            // Pass → green
//            bgColor = "#c7ffd1";
//        }
//
//        return "<tr style='background:" + bgColor + "'>"
//                + "<td>" + esc(key) + "</td>"
//                + "<td>" + capturePanel("Request",  rqS, key) + "</td>"
//                + "<td>" + capturePanel("Response", rpS, key) + "</td>"
//                + "<td>" + capturePanel("Elastic",  esS, key) + "</td>"
//                + "<td>" + capturePanel("DB",       dbS, key) + "</td>"
//                + "<td>" + esc(status) + "</td>"
//                + "</tr>";
//    }
//
//    private static String capturePanel(String title, String body, String key) {
//        try {
//            String html = "<html><body style='font-family:monospace;white-space:pre;font-size:12px'>"
//                    + "<b>" + esc(title) + "</b><br>" + esc(body) + "</body></html>";
//
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions opt = new ChromeOptions();
//            opt.addArguments("--headless=new", "--window-size=1200,600");
//            WebDriver driver = new ChromeDriver(opt);
//            driver.get("data:text/html;base64," +
//                    Base64.getEncoder().encodeToString(html.getBytes()));
//            Thread.sleep(300);
//
//            String b64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
//            driver.quit();
//
//            return "<a href='data:image/png;base64," + b64 + "' target='_blank'>"
//                    + "<img src='data:image/png;base64," + b64
//                    + "' style='width:220px;border:1px solid #aaa'/></a>";
//
//        } catch (Exception e) {
//            return esc(body);
//        }
//    }
//
//    // ==== Utilities ====
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        if (json == null) return out;
//        try {
//            Map<String, Object> map = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(map, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        src.forEach((k, v) -> {
//            String full = prefix.isEmpty() ? k : prefix + "." + k;
//            if (v instanceof Map) flatten((Map<String, Object>) v, full, out);
//            else out.put(full.toLowerCase(), v);
//        });
//    }
//
//    private static boolean equalNorm(Object a, Object b) {
//        return normalize(a).equals(normalize(b));
//    }
//
//    private static String normalize(Object v) {
//        if (v == null) return "";
//        return v.toString().trim().replaceAll("[-:T.Z]", "");
//    }
//
//    private static Set<String> lowerSet(String[] arr) {
//        Set<String> s = new HashSet<>();
//        if (arr == null) return s;
//        for (String a : arr) {
//            if (a != null) s.add(a.toLowerCase());
//        }
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^a-zA-Z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') up = true;
//            else {
//                sb.append(up ? Character.toUpperCase(c) : c);
//                up = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    private static String toStr(Object o) {
//        return o == null ? "" : o.toString();
//    }
//
//    private static String esc(String s) {
//        return s == null ? "" :
//                s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
//}






//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    // Format for DB timestamps like: 2023-12-01 12:00:00.000000
//    private static final DateTimeFormatter DB_TS_FMT =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSS]");
//
//    // Overloaded - accepts RestAssured Response directly
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response response,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeAllKeys,
//            String[] excludeReqOnly
//    ) throws Exception {
//        String responseJson = response != null ? response.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson, responseJson, id,
//                esIndexName, dbTableName,
//                excludeElasticCompletely,
//                excludeAllKeys, excludeReqOnly
//        );
//    }
//
//    // Main Validator — only mismatch rows get screenshots, colored by status
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeAllKeys,
//            String[] excludeReqOnly
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//
//        if (test != null)
//            test.info("Cross-validation started for ID: " + id);
//
//        Map<String, Object> reqFlat  = flattenSafe(requestJson);
//        Map<String, Object> respFlat = flattenSafe(responseJson);
//
//        // Fetch DB
//        Map<String, Object> dbFlat = fetchDbById(dbTableName, id);
//
//        // Fetch ES if included
//        Map<String, Object> esFlat = excludeElasticCompletely
//                ? new LinkedHashMap<>()
//                : fetchEsById(esIndexName, id);
//
//        // Combine all keys (case-insensitive)
//        Set<String> allKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
//        allKeys.addAll(reqFlat.keySet());
//        allKeys.addAll(respFlat.keySet());
//        allKeys.addAll(dbFlat.keySet());
//        allKeys.addAll(esFlat.keySet());
//
//        Set<String> excludeEverywhere  = lowerSet(excludeAllKeys);
//        Set<String> excludeRequestOnly = lowerSet(excludeReqOnly);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>Validation - ID: ").append(id).append("</h3>")
//            .append("<p><b>Legend:</b> ")
//            .append("<span style='background:#c7ffd1;padding:2px 6px;'>PASS</span> ")
//            .append("<span style='background:#ffb6b6;padding:2px 6px;'>FAIL</span> ")
//            .append("<span style='background:#e0e0e0;padding:2px 6px;'>EXCLUDED_ALL</span> ")
//            .append("<span style='background:#fff9c4;padding:2px 6px;'>EXCLUDED_FROM_REQUEST</span>")
//            .append("</p>")
//            .append("<table border='1' style='border-collapse:collapse;width:100%'>")
//            .append("<tr style='background:#d2e0ff;font-weight:bold'>")
//            .append("<td>Key</td><td>Request</td><td>Response</td><td>ES</td><td>DB</td><td>Status</td></tr>");
//
//        boolean anyFail = false;
//
//        for (String key : allKeys) {
//            String lk = key.toLowerCase();
//
//            boolean isExcludedAll     = excludeEverywhere.contains(lk);
//            boolean isExcludedReqOnly = excludeRequestOnly.contains(lk);
//
//            Object rqV = reqFlat.get(lk);
//            Object rpV = respFlat.get(lk);
//            Object esV = esFlat.get(lk);
//            Object dbV = dbFlat.get(lk);
//
//            boolean pass = true;
//            String status;
//
//            if (isExcludedAll) {
//                // Completely excluded key: show but do not compare anywhere
//                status = "EXCLUDED_ALL";
//            } else {
//                // Start assuming PASS
//                status = "PASS";
//
//                // Request vs Response — only if not excluded-from-request
//                if (!isExcludedReqOnly && rqV != null && rpV != null && !equalNorm(rqV, rpV)) {
//                    pass = false;
//                    status = "Mismatch Req↔Resp";
//                }
//
//                // Response vs DB
//                if (dbV != null && rpV != null && !equalNorm(rpV, dbV)) {
//                    pass = false;
//                    status = "Mismatch Resp↔DB";
//                }
//
//                // Response vs ES (if not excluded)
//                if (!excludeElasticCompletely && esV != null && rpV != null && !equalNorm(rpV, esV)) {
//                    pass = false;
//                    status = "Mismatch Resp↔ES";
//                }
//
//                if (!pass) {
//                    anyFail = true;
//                } else if (isExcludedReqOnly) {
//                    // no mismatches, but this key is only excluded from request
//                    status = "EXCLUDED_FROM_REQUEST";
//                }
//            }
//
//            boolean isMismatchRow = !isExcludedAll
//                    && !status.equals("PASS")
//                    && !status.equals("EXCLUDED_FROM_REQUEST");
//
//            String row = buildTableRow(
//                    key, rqV, rpV, esV, dbV,
//                    status,
//                    isExcludedAll,
//                    isExcludedReqOnly,
//                    isMismatchRow   // only mismatches get screenshot
//            );
//            html.append(row);
//        }
//
//        html.append("</table>");
//
//        if (test != null)
//            test.info(html.toString());
//
//        if (anyFail)
//            throw new AssertionError("Some keys failed validation. See Extent report.");
//    }
//
//    // ==== DB Fetch ====
//    private static Map<String, Object> fetchDbById(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        try (Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = con.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=? LIMIT 1"
//             )) {
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    // ==== ES Fetch ====
//    private static Map<String, Object> fetchEsById(String index, int id) {
//        try {
//            Response esResp = io.restassured.RestAssured
//                    .given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object sourceObj = hits.get(0).get("_source");
//                if (sourceObj instanceof Map) {
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> src = (Map<String, Object>) sourceObj;
//                    return flattenSafe(MAPPER.writeValueAsString(src));
//                }
//            }
//        } catch (Exception ignored) {}
//        return new LinkedHashMap<>();
//    }
//
//    // ==== Build row: only mismatches get screenshots ====
//    private static String buildTableRow(
//            String key,
//            Object rq,
//            Object rp,
//            Object es,
//            Object db,
//            String status,
//            boolean excludedAll,
//            boolean excludedReqOnly,
//            boolean isMismatchRow
//    ) {
//        String rqS = toStr(rq);
//        String rpS = toStr(rp);
//        String esS = toStr(es);
//        String dbS = toStr(db);
//
//        // Row color by status
//        String bgColor;
//        if (excludedAll) {
//            bgColor = "#e0e0e0";        // grey
//        } else if ("EXCLUDED_FROM_REQUEST".equals(status)) {
//            bgColor = "#fff9c4";        // yellow
//        } else if (!"PASS".equals(status)) {
//            bgColor = "#ffb6b6";        // red
//        } else {
//            bgColor = "#c7ffd1";        // green
//        }
//
//        // Only mismatched rows get screenshot panels, others just plain text
//        String reqCell  = isMismatchRow ? capturePanel("Request",  rqS) : esc(rqS);
//        String respCell = isMismatchRow ? capturePanel("Response", rpS) : esc(rpS);
//        String esCell   = isMismatchRow ? capturePanel("Elastic",  esS) : esc(esS);
//        String dbCell   = isMismatchRow ? capturePanel("DB",       dbS) : esc(dbS);
//
//        return "<tr style='background:" + bgColor + "'>"
//                + "<td>" + esc(key) + "</td>"
//                + "<td>" + reqCell + "</td>"
//                + "<td>" + respCell + "</td>"
//                + "<td>" + esCell + "</td>"
//                + "<td>" + dbCell + "</td>"
//                + "<td>" + esc(status) + "</td>"
//                + "</tr>";
//    }
//
//    private static String capturePanel(String title, String body) {
//        try {
//            String html = "<html><body style='font-family:monospace;white-space:pre;font-size:12px'>"
//                    + "<b>" + esc(title) + "</b><br>" + esc(body) + "</body></html>";
//
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions opt = new ChromeOptions();
//            opt.addArguments("--headless=new", "--window-size=1200,600");
//            WebDriver driver = new ChromeDriver(opt);
//            driver.get("data:text/html;base64," +
//                    Base64.getEncoder().encodeToString(html.getBytes()));
//            Thread.sleep(300);
//
//            String b64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
//            driver.quit();
//
//            return "<a href='data:image/png;base64," + b64 + "' target='_blank'>"
//                    + "<img src='data:image/png;base64," + b64
//                    + "' style='width:220px;border:1px solid #aaa'/></a>";
//
//        } catch (Exception e) {
//            return esc(body);
//        }
//    }
//
//    // ==== Utilities: flatten JSON ====
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        if (json == null || json.trim().isEmpty()) return out;
//        try {
//            Map<String, Object> map = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(map, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        if (src == null) return;
//        for (Map.Entry<String, Object> e : src.entrySet()) {
//            String full = prefix.isEmpty() ? e.getKey() : prefix + "." + e.getKey();
//            Object v = e.getValue();
//            if (v instanceof Map) {
//                flatten((Map<String, Object>) v, full, out);
//            } else {
//                out.put(full.toLowerCase(), v);
//            }
//        }
//    }
//
//    // ==== Comparison with date normalization ====
//    private static boolean equalNorm(Object a, Object b) {
//        Object na = normalize(a);
//        Object nb = normalize(b);
//        if (na == null && nb == null) return true;
//        if (na == null || nb == null) return false;
//        return na.equals(nb);
//    }
//
//    /**
//     * Normalize values:
//     * - If looks like date/time, try parse to Instant (epoch millis)
//     * - If numeric, compare as numbers
//     * - Else compare as trimmed strings
//     */
//    private static Object normalize(Object v) {
//        if (v == null) return null;
//        String s = v.toString().trim();
//        if (s.isEmpty()) return "";
//
//        // Try temporal normalization first
//        Instant inst = tryParseInstant(s);
//        if (inst != null) {
//            return inst.toEpochMilli();  // epoch millis for both DB and ISO strings
//        }
//
//        // Try numeric comparison
//        if (isNumeric(s)) {
//            try {
//                return new java.math.BigDecimal(s);
//            } catch (NumberFormatException ignored) {}
//        }
//
//        // Fallback to plain string
//        return s;
//    }
//
//    private static Instant tryParseInstant(String s) {
//        // ISO-style with T...Z or offset
//        try {
//            if (s.contains("T")) {
//                // e.g. 2023-12-01T12:00:00.000+00:00 or 2023-12-01T12:00:00Z
//                return OffsetDateTime.parse(s).toInstant();
//            }
//        } catch (DateTimeParseException ignored) {
//        }
//
//        // DB-style: 2023-12-01 12:00:00.000000 or .000
//        try {
//            if (s.length() >= 19 && s.charAt(4) == '-' && s.charAt(7) == ' ') {
//                LocalDateTime ldt = LocalDateTime.parse(s, DB_TS_FMT);
//                // assume DB time is in UTC (or adjust as needed)
//                return ldt.toInstant(ZoneOffset.UTC);
//            }
//        } catch (DateTimeParseException ignored) {
//        }
//
//        return null;
//    }
//
//    private static boolean isNumeric(String s) {
//        return s != null && s.matches("-?\\d+(\\.\\d+)?");
//    }
//
//    private static Set<String> lowerSet(String[] arr) {
//        Set<String> s = new HashSet<>();
//        if (arr == null) return s;
//        for (String a : arr) {
//            if (a != null) s.add(a.toLowerCase());
//        }
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^a-zA-Z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') up = true;
//            else {
//                sb.append(up ? Character.toUpperCase(c) : Character.toLowerCase(c));
//                up = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    private static String toStr(Object o) {
//        return o == null ? "" : o.toString();
//    }
//
//    private static String esc(String s) {
//        return s == null ? "" :
//                s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
//}





//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.sql.*;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    // Overload for Response input convenience
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response response,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhere,
//            String[] excludeRequestOnly
//    ) throws Exception {
//    	validateKeysAcrosRequestResponseEsDb(
//                requestJson,
//                response != null ? response.getBody().asString() : null,
//                id,
//                esIndexName,
//                dbTableName,
//                excludeElasticCompletely,
//                excludeEverywhere,
//                excludeRequestOnly
//        );
//    }
//
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhere,
//            String[] excludeRequestOnly
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null)
//            test.info("🔍 Cross-Validation Started — ID: " + id);
//
//        Map<String, Object> reqFlat = flattenSafe(requestJson);
//        Map<String, Object> respFlat = flattenSafe(responseJson);
//
//        Map<String, Object> dbFlat = fetchDb(dbTableName, id);
//
//        Map<String, Object> esFlat = excludeElasticCompletely
//                ? new LinkedHashMap<>()
//                : fetchEs(esIndexName, id);
//
//        // Full key set
//        Set<String> keys = new TreeSet<>();
//        keys.addAll(reqFlat.keySet());
//        keys.addAll(respFlat.keySet());
//        keys.addAll(dbFlat.keySet());
//        keys.addAll(esFlat.keySet());
//
//        Set<String> exAll = lowerSet(excludeEverywhere);
//        Set<String> exReq = lowerSet(excludeRequestOnly);
//
//        StringBuilder html =
//                new StringBuilder("<h3>Validation Table (ID: " + id + ")</h3>")
//                        .append("<table border='1' style='border-collapse:collapse;width:100%;font-size:12px'>")
//                        .append("<tr style='background:#cbe2ff;font-weight:bold;'>")
//                        .append("<td>Key</td><td>Request</td><td>Response</td><td>Elastic</td><td>DB</td><td>Status</td></tr>");
//
//        boolean fail = false;
//
//        for (String key : keys) {
//            String norm = key.toLowerCase();
//            if (exAll.contains(norm)) continue;
//
//            Object rq = reqFlat.get(norm);
//            Object rs = respFlat.get(norm);
//            Object es = esFlat.get(norm);
//            Object db = dbFlat.get(norm);
//
//            boolean pass = true;
//            String status = "PASS";
//
//            // Req vs Resp Comparison
//            if (!exReq.contains(norm) && rq != null && rs != null && !equalNormalized(rq, rs)) {
//                pass = false; status = "Mismatch Req ↔ Resp";
//            }
//            // Resp vs DB
//            if (db != null && rs != null && !equalNormalized(rs, db)) {
//                pass = false; status = "Mismatch Resp ↔ DB";
//            }
//            // Resp vs ES
//            if (!excludeElasticCompletely && es != null && rs != null && !equalNormalized(rs, es)) {
//                pass = false; status = "Mismatch Resp ↔ ES";
//            }
//
//            boolean isMismatch = !pass;
//            fail |= !pass;
//
//            html.append(buildRow(key, rq, rs, es, db, status, isMismatch));
//        }
//
//        html.append("</table>");
//
//        if (test != null)
//            test.info(html.toString());
//
//        if (fail)
//            throw new AssertionError("❌ Validation Failed — Check Extent Report");
//    }
//
//    // DB Fetch
//    private static Map<String, Object> fetchDb(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        try (Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = con.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?"
//             )) {
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    Object val = rs.getObject(i);
//                    String col = md.getColumnName(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    // ES Fetch
//    private static Map<String, Object> fetchEs(String index, int id) {
//        try {
//            Response esResp = io.restassured.RestAssured.given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map)
//                    return flattenSafe(MAPPER.writeValueAsString(src));
//            }
//        } catch (Exception ignored) {}
//        return new LinkedHashMap<>();
//    }
//
//    // 🔹 UI TABLE ROW BUILDER
//    private static String buildRow(String key, Object rq, Object rs, Object es, Object db,
//                                   String status, boolean isMismatch) {
//
//        String color =
//                status.contains("PASS") ? "#d4ffdf" :
//                        status.contains("Mismatch") ? "#ffb3b3" : "#ffe8a6";
//
//        return "<tr style='background:" + color + "'>" +
//                td(key) +
//                td(valueCell("Request", rq, key, isMismatch)) +
//                td(valueCell("Response", rs, key, isMismatch)) +
//                td(valueCell("ES", es, key, isMismatch)) +
//                td(valueCell("DB", db, key, isMismatch)) +
//                td(status) +
//                "</tr>";
//    }
//
//    private static String td(String s) {
//        return "<td style='vertical-align:top;word-break:break-all'>" + esc(s) + "</td>";
//    }
//
//    // Screenshot only for mismatched
//    private static String valueCell(String title, Object v, String key, boolean screenshot) {
//        String txt = esc(toStr(v));
//        return screenshot ? screenshotPanel(title, v) + "<div>" + txt + "</div>" : txt;
//    }
//
//    private static String screenshotPanel(String title, Object v) {
//        try {
//            String body = "<html><body style='font-family:monospace;font-size:11px'>" +
//                    "<b>" + esc(title) + "</b><br>" + esc(toStr(v)) + "</body></html>";
//
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions opt = new ChromeOptions();
//            opt.addArguments("--headless=new", "--window-size=900,500");
//            WebDriver driver = new ChromeDriver(opt);
//
//            driver.get("data:text/html;base64," +
//                    Base64.getEncoder().encodeToString(body.getBytes()));
//
//            Thread.sleep(250);
//
//            String img = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
//
//            driver.quit();
//
//            return "<img src='data:image/png;base64," + img + "' width='200'/>";
//        } catch (Exception e) {
//            return "<span style='color:red'>[Screenshot Fail]</span>";
//        }
//    }
//
//    // JSON flatten
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        if (json == null) return out;
//        try {
//            Map<String, Object> raw =
//                    MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
//            flatten(raw, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        src.forEach((k, v) -> {
//            String f = prefix.isEmpty() ? k : prefix + "." + k;
//            if (v instanceof Map) flatten((Map<String, Object>) v, f, out);
//            else out.put(f.toLowerCase(), v);
//        });
//    }
//
//    // Normalized comparison
//    private static boolean equalNormalized(Object a, Object b) {
//        return normalize(toStr(a)).equals(normalize(toStr(b)));
//    }
//
//    // Convert DB vs ISO dates to comparable
//    private static String normalize(String s) {
//        if (s == null) return "";
//        return s.replaceAll("[^0-9]", "").trim();
//    }
//
//    // Utils
//    private static String safe(String s) {
//        return s.replaceAll("[^A-Za-z0-9_]", "");
//    }
//    private static Set<String> lowerSet(String[] arr) {
//        return arr == null ? new HashSet<>() :
//                new HashSet<>(Arrays.asList(Arrays.stream(arr).map(String::toLowerCase).toArray(String[]::new)));
//    }
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') up = true;
//            else sb.append(up ? Character.toUpperCase(c) : c);
//        }
//        return sb.toString();
//    }
//    private static String toStr(Object o) { return o == null ? "" : o.toString(); }
//    private static String esc(String s) {
//        return s == null ? "" :
//                s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
//}






//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//
//import java.sql.*;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response responseObj,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//        String responseJson = responseObj != null ? responseObj.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson, responseJson, id,
//                esIndexName, dbTableName,
//                excludeElasticCompletely,
//                excludeEverywhereArr, excludeRequestOnlyArr,
//                excludeEsOnlyArr
//        );
//    }
//
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null)
//            test.info("🔍 Validation Started (ID: " + id + ")");
//
//        Map<String,Object> req = flattenSafe(requestJson);
//        Map<String,Object> res = flattenSafe(responseJson);
//        Map<String,Object> db = fetchDb(dbTableName, id);
//        Map<String,Object> es = excludeElasticCompletely ? new LinkedHashMap<>() : fetchEs(esIndexName,id);
//
//        Set<String> keys = new TreeSet<>();
//        keys.addAll(req.keySet());
//        keys.addAll(res.keySet());
//        keys.addAll(db.keySet());
//        keys.addAll(es.keySet());
//
//        Set<String> excludeEverywhere = lower(excludeEverywhereArr);
//        Set<String> excludeReqOnly = lower(excludeRequestOnlyArr);
//        Set<String> excludeEsOnly = lower(excludeEsOnlyArr);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>API ↔ ES ↔ DB Key Validation | ID: ").append(id).append("</h3>");
//        html.append("<table border='1' style='border-collapse:collapse;width:100%;font-size:12px'>");
//        html.append("<tr style='background:#d4e3fc;font-weight:bold'>");
//        html.append("<td>Key</td><td>Req SS</td><td>Req Val</td>");
//        html.append("<td>Resp SS</td><td>Resp Val</td>");
//        html.append("<td>ES Val</td><td>ES SS</td>");
//        html.append("<td>DB SS</td><td>DB Val</td><td>Status</td>");
//        html.append("</tr>");
//
//        boolean failOccured = false;
//
//        for (String key : keys) {
//            String normKey = key.toLowerCase();
//
//            if (excludeEverywhere.contains(normKey)) {
//                html.append(excludedRow(key));
//                continue;
//            }
//
//            Object rq = req.get(normKey);
//            Object rp = res.get(normKey);
//            Object esVal = es.get(normKey);
//            Object dbVal = db.get(normKey);
//
//            String rqS = displayValue(rq, excludeReqOnly.contains(normKey) ? "N/A" : null);
//            String rpS = displayValue(rp, null);
//            String esS = displayValue(excludeElasticCompletely || excludeEsOnly.contains(normKey) ? null : esVal,
//                                      (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? "N/A" : null);
//            String dbS = displayValue(dbVal, null);
//
//            boolean pass = true;
//            String status = "PASS";
//            String color = "#d6ffe0";
//
//            if (!excludeReqOnly.contains(normKey) && !equalNorm(rq, rp)) {
//                pass = false; status = "Mismatch Req↔Resp";
//            }
//            if (!equalNorm(rp, dbVal)) {
//                pass = false; status = "Mismatch Resp↔DB";
//            }
//            if (!excludeElasticCompletely && !excludeEsOnly.contains(normKey) && !equalNorm(rp, esVal)) {
//                pass = false; status = "Mismatch Resp↔ES";
//            }
//
//            boolean mismatch = !pass;
//
//            failOccured |= mismatch;
//            if (mismatch) color = "#ffc1b2";
//
//            html.append("<tr style='background:" + color + "'>");
//            html.append(td(key));
//            html.append(td(screenshotCell(rq, mismatch && !excludeReqOnly.contains(normKey))));
//            html.append(td(rqS));
//            html.append(td(screenshotCell(rp, mismatch)));
//            html.append(td(rpS));
//            html.append(td(esS));
//            html.append(td(screenshotCell(excludeElasticCompletely || excludeEsOnly.contains(normKey)?null:esVal, mismatch)));
//            html.append(td(screenshotCell(dbVal, mismatch)));
//            html.append(td(dbS));
//            html.append(td(status));
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//        if (test != null) test.info(html.toString());
//        if (failOccured) throw new AssertionError("❌ Key validation failed. Check report.");
//    }
//
//    /* DB / ES / Helper methods below — unchanged logic except added trim */
//    private static Map<String, Object> fetchDb(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        try(Connection c = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASS);
//            PreparedStatement ps = c.prepareStatement(
//                    "SELECT * FROM `"+DEFAULT_SCHEMA+"`.`"+safe(table)+"` WHERE id=?")) {
//            ps.setInt(1,id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//            if(rs.next()){
//                for(int i=1;i<=md.getColumnCount();i++){
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(),val);
//                    out.put(toCamel(col).toLowerCase(),val);
//                }
//            }
//        } catch(Exception ignored){}
//        return out;
//    }
//
//    private static Map<String,Object> fetchEs(String index,int id){
//        try{
//            Response esResp = io.restassured.RestAssured.given()
//                .get("http://10.10.2.45:9200/piqa_issue-persistence_"+index+"_index/_search?q=id:"+id+"&size=1")
//                .then().extract().response();
//            List<Map<String,Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if(hits!=null && !hits.isEmpty()){
//                Object src = hits.get(0).get("_source");
//                if(src instanceof Map)
//                    return flattenSafe(MAPPER.writeValueAsString(src));
//            }
//        }catch(Exception ignored){}
//        return new LinkedHashMap<>();
//    }
//
//    private static String screenshotCell(Object v, boolean doCapture){
//        if(!doCapture || v==null) return "";
//        try{
//            String html = "<html><body style='font-family:monospace;font-size:11px'>" +
//                    esc(toStr(v)) + "</body></html>";
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions op = new ChromeOptions();
//            op.addArguments("--headless=new","--window-size=800,500");
//            WebDriver d = new ChromeDriver(op);
//            d.get("data:text/html;base64," +
//                    Base64.getEncoder().encodeToString(html.getBytes()));
//            Thread.sleep(200);
//            String img = ((TakesScreenshot)d).getScreenshotAs(OutputType.BASE64);
//            d.quit();
//            return "<img width='180' src='data:image/png;base64," + img + "'>";
//        }catch(Exception ex){
//            return "[Capture Failed]";
//        }
//    }
//
//    private static String displayValue(Object v, String override){
//        if(override!=null) return font(override,"#666");
//        if(v==null) return font("NULL","#999");
//        return esc(toStr(v));
//    }
//
//    private static boolean equalNorm(Object a,Object b){
//        return normalize(a).equals(normalize(b));
//    }
//
//    private static String normalize(Object v){
//        if(v==null) return "";
//        return v.toString().replaceAll("[^0-9]","");
//    }
//
//    private static Map<String,Object> flattenSafe(String json){
//        Map<String,Object> out = new LinkedHashMap<>();
//        if(json==null) return out;
//        try{
//            Map<String,Object> m = MAPPER.readValue(json,new TypeReference<Map<String,Object>>() {});
//            flatten(m,"",out);
//        }catch(Exception ignored){}
//        return out;
//    }
//
//    private static void flatten(Map<String,Object>src,String prefix,Map<String,Object>out){
//        src.forEach((k,v)->{
//            String f = prefix.isEmpty()?k:prefix+"."+k;
//            if(v instanceof Map) flatten((Map)v,f,out);
//            else out.put(f.toLowerCase(),v);
//        });
//    }
//
//    private static String td(String s){
//        return "<td style='vertical-align:top;word-break:break-all'>"+s+"</td>";
//    }
//    private static String font(String s,String c){
//        return "<span style='color:"+c+"'>"+s+"</span>";
//    }
//    private static String esc(String s){
//        return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
//    }
//    private static String toStr(Object o){return o==null?"":o.toString();}
//    private static Set<String> lower(String[]a){
//        if(a==null) return new HashSet<>();
//        Set<String>s=new HashSet<>();
//        for(String x:a)s.add(x.toLowerCase());
//        return s;
//    }
//    private static String toCamel(String s){
//        StringBuilder sb=new StringBuilder();boolean up=false;
//        for(char c:s.toCharArray()){
//            if(c=='_'||c=='-') up=true;
//            else{sb.append(up?Character.toUpperCase(c):c);up=false;}
//        }return sb.toString();
//    }
//    private static String safe(String s){
//        return s.replaceAll("[^A-Za-z0-9_]","");
//    }
//
//    private static String excludedRow(String key){
//        return "<tr style='background:#fff3c6'><td>"+key+
//                "</td><td colspan='8'>Excluded Everywhere</td></tr>";
//    }
//}







//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static WebDriver DRIVER;
//
//    /** PUBLIC ENTRY - Response overload */
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response responseObj,
//            int id,
//            String esIndex,
//            String dbTable,
//            boolean excludeEsCompletely,
//            String[] excludeAll,
//            String[] excludeRequest,
//            String[] excludeEsOnly
//    ) throws Exception {
//        validate(requestJson,
//                responseObj != null ? responseObj.getBody().asString() : null,
//                id, esIndex, dbTable,
//                excludeEsCompletely,
//                excludeAll, excludeRequest, excludeEsOnly);
//    }
//
//    /** Main validation */
//    public static void validate(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndex,
//            String dbTable,
//            boolean excludeEsCompletely,
//            String[] excludeAll,
//            String[] excludeRequest,
//            String[] excludeEsOnly
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null) test.info("🔍 Validating ID = " + id);
//
//        Map<String, Object> req = flattenSafe(requestJson);
//        Map<String, Object> res = flattenSafe(responseJson);
//        Map<String, Object> db = fetchDbRow(dbTable, id);
//        Map<String, Object> es = excludeEsCompletely ?
//                new LinkedHashMap<>() : fetchEsRow(esIndex, id);
//
//        Set<String> allKeys = new TreeSet<>();
//        allKeys.addAll(req.keySet());
//        allKeys.addAll(res.keySet());
//        allKeys.addAll(db.keySet());
//        allKeys.addAll(es.keySet());
//
//        Set<String> excAll = lower(excludeAll);
//        Set<String> excReq = lower(excludeRequest);
//        Set<String> excEs = lower(excludeEsOnly);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>Cross Validation Result (ID: ")
//                .append(id).append(")</h3>");
//        html.append("<table border='1' style='border-collapse:collapse;width:100%;font-size:12px'>");
//        html.append("<tr style='background:#d3e5ff;font-weight:bold'>")
//                .append("<td>Key</td>")
//                .append("<td>Req SS</td><td>Req Value</td>")
//                .append("<td>Resp SS</td><td>Resp Value</td>")
//                .append("<td>ES SS</td><td>ES Value</td>")
//                .append("<td>DB SS</td><td>DB Value</td>")
//                .append("<td>Status</td></tr>");
//
//        boolean failed = false;
//
//        for (String key : allKeys) {
//            String k = key.toLowerCase();
//
//            if (excAll.contains(k)) {
//                html.append(buildExcludedRow(key));
//                continue;
//            }
//
//            Object rq = req.get(k);
//            Object rp = res.get(k);
//            Object esV = es.get(k);
//            Object dbV = db.get(k);
//
//            String reqVal = val(rq, excReq.contains(k) ? "N/A" : null);
//            String resVal = val(rp, null);
//            String esVal = val(
//                    (excludeEsCompletely || excEs.contains(k)) ? null : esV,
//                    (excludeEsCompletely || excEs.contains(k)) ? "N/A" : null
//            );
//            String dbVal = val(dbV, null);
//
//            boolean pass = true;
//            String status = "PASS";
//            String rowColor = "#cbffd5";
//
//            if (!excReq.contains(k) && !eq(rq, rp)) {
//                pass = false; status = "Mismatch Req↔Resp";
//            }
//            if (!eq(rp, dbV)) {
//                pass = false; status = "Mismatch Resp↔DB";
//            }
//            if (!excludeEsCompletely && !excEs.contains(k) && !eq(rp, esV)) {
//                pass = false; status = "Mismatch Resp↔ES";
//            }
//
//            boolean mismatch = !pass;
//            if (mismatch) { failed = true; rowColor = "#ffc7c7"; }
//
//            html.append("<tr style='background:").append(rowColor).append("'>");
//            html.append(td(key));
//            html.append(td(cap(rq, mismatch && !excReq.contains(k))));
//            html.append(td(reqVal));
//            html.append(td(cap(rp, mismatch)));
//            html.append(td(resVal));
//            html.append(td(cap((excludeEsCompletely||excEs.contains(k))?null:esV, mismatch)));
//            html.append(td(esVal));
//            html.append(td(cap(dbV, mismatch)));
//            html.append(td(dbVal));
//            html.append(td(status));
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//
//        if (test != null) test.info(html.toString());
//
//        quitDriver();
//
//        if (failed)
//            throw new AssertionError("❌ Validation failed. Check report.");
//    }
//
//    // DB fetch
//    private static Map<String, Object> fetchDbRow(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = c.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object v = rs.getObject(i);
//                    out.put(col.toLowerCase(), v);
//                    out.put(toCamel(col).toLowerCase(), v);
//                }
//            }
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    // ES fetch
//    private static Map<String, Object> fetchEsRow(String index, int id) {
//        try {
//            Response esResp = io.restassured.RestAssured.given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map)
//                    return flattenSafe(MAPPER.writeValueAsString(src));
//            }
//        } catch (Exception ignored) {}
//        return new LinkedHashMap<>();
//    }
//
//    // screenshot
//    private static String cap(Object v, boolean enable) {
//        if (!enable || v == null) return "";
//        try {
//            initDriver();
//            String h = "<html><body style='font-family:monospace;font-size:11px'>" +
//                    esc(v.toString()) + "</body></html>";
//            String b = Base64.getEncoder().encodeToString(h.getBytes());
//            DRIVER.get("data:text/html;base64," + b);
//            Thread.sleep(200);
//            String img = ((TakesScreenshot) DRIVER).getScreenshotAs(OutputType.BASE64);
//            return "<a target='_blank' href='data:image/png;base64," + img + "'>" +
//                    "<img width='140' src='data:image/png;base64," + img + "'/></a>";
//        } catch (Exception e) {
//            return "[SS Failed]";
//        }
//    }
//
//    // normalize values
//    private static boolean eq(Object a, Object b) {
//        return norm(a).equals(norm(b));
//    }
//
//    private static String norm(Object v) {
//        if (v == null) return "";
//        String s = v.toString().trim();
//        try {
//            // ISO / DB / Zoned → Instant
//            Instant i = Instant.parse(s.replace(" ", "T")
//                    .replaceAll("(\\.\\d{6})$", "$1Z"));
//            return DateTimeFormatter.ISO_INSTANT.format(i);
//        } catch (Exception ignore) {}
//
//        return s.replaceAll("[^0-9A-Za-z]", "").toLowerCase();
//    }
//
//    // html helpers
//    private static String td(String s) {
//        return "<td style='word-break:break-all'>" + s + "</td>";
//    }
//
//    private static String val(Object v, String override) {
//        if (override != null) return "<i>" + override + "</i>";
//        if (v == null) return "<span style='color:gray'>NULL</span>";
//        return esc(v.toString());
//    }
//
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<>();
//        if (json == null) return out;
//        try {
//            Map<String, Object> src = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(src, "", out);
//        } catch (Exception ignore) {}
//        return out;
//    }
//
//    private static void flatten(Map<String, Object> src, String p, Map<String, Object> out) {
//        src.forEach((k, v) -> {
//            String f = p.isEmpty() ? k : p + "." + k;
//            if (v instanceof Map) flatten((Map<String, Object>) v, f, out);
//            else out.put(f.toLowerCase(), v);
//        });
//    }
//
//    private static String buildExcludedRow(String key) {
//        return "<tr style='background:#fff3c4'>" +
//                "<td>" + key + "</td>" +
//                "<td colspan='9'><i>Excluded Everywhere</i></td></tr>";
//    }
//
//    private static String esc(String s) {
//        return s == null ? "" :
//                s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
//
//    private static Set<String> lower(String[] arr) {
//        Set<String> s = new HashSet<>();
//        if (arr != null) for (String x : arr) s.add(x.toLowerCase());
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^A-Za-z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder b = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') up = true;
//            else {
//                b.append(up ? Character.toUpperCase(c) : c);
//                up = false;
//            }
//        }
//        return b.toString();
//    }
//
//    // driver reuse
//    private static void initDriver() {
//        if (DRIVER == null) {
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions o = new ChromeOptions();
//            o.addArguments("--headless=new", "--window-size=1100,700");
//            DRIVER = new ChromeDriver(o);
//        }
//    }
//
//    private static void quitDriver() {
//        try {
//            if (DRIVER != null) {
//                DRIVER.quit();
//                DRIVER = null;
//            }
//        } catch (Exception ignore) {}
//    }
//}





//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static WebDriver DRIVER;
//
//    // ========= PUBLIC ENTRY (with Response) =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response responseObj,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//        String responseJson = (responseObj != null) ? responseObj.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson,
//                responseJson,
//                id,
//                esIndexName,
//                dbTableName,
//                excludeElasticCompletely,
//                excludeEverywhereArr,
//                excludeRequestOnlyArr,
//                excludeEsOnlyArr
//        );
//    }
//
//    // ========= MAIN VALIDATOR =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null) {
//            test.info("🔍 Validation Started (ID: " + id + ")");
//        }
//
//        Map<String, Object> req = flattenSafe(requestJson);
//        Map<String, Object> res = flattenSafe(responseJson);
//        Map<String, Object> db = fetchDb(dbTableName, id);
//        Map<String, Object> es = excludeElasticCompletely
//                ? new LinkedHashMap<String, Object>()
//                : fetchEs(esIndexName, id);
//
//        Set<String> keys = new TreeSet<String>();
//        keys.addAll(req.keySet());
//        keys.addAll(res.keySet());
//        keys.addAll(db.keySet());
//        keys.addAll(es.keySet());
//
//        Set<String> excludeEverywhere = lower(excludeEverywhereArr);
//        Set<String> excludeReqOnly = lower(excludeRequestOnlyArr);
//        Set<String> excludeEsOnly = lower(excludeEsOnlyArr);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>API ↔ ES ↔ DB Key Validation | ID: ").append(id).append("</h3>");
//
//        // Legend
//        html.append("<div style='font-size:12px;margin-bottom:6px'>")
//            .append("<b>Legend:</b> ")
//            .append("<span style='background:#cbffd5;padding:0 10px;margin-right:4px;'>PASS</span>")
//            .append("<span style='background:#f4ffcc;padding:0 10px;margin-right:4px;'>PASS (Req excluded)</span>")
//            .append("<span style='background:#ccfaff;padding:0 10px;margin-right:4px;'>PASS (ES excluded)</span>")
//            .append("<span style='background:#fff3c4;padding:0 10px;margin-right:4px;'>Excluded Everywhere</span>")
//            .append("<span style='background:#ffc7c7;padding:0 10px;'>FAIL</span>")
//            .append("</div>");
//
//        html.append("<table border='1' style='border-collapse:collapse;width:100%;font-size:12px'>");
//        html.append("<tr style='background:#d4e3fc;font-weight:bold'>")
//            .append("<td>Key</td>")
//            .append("<td>Req SS</td><td>Req Val</td>")
//            .append("<td>Resp SS</td><td>Resp Val</td>")
//            .append("<td>ES SS</td><td>ES Val</td>")
//            .append("<td>DB SS</td><td>DB Val</td>")
//            .append("<td>Status</td>")
//            .append("</tr>");
//
//        boolean failOccured = false;
//
//        for (String key : keys) {
//            String normKey = key.toLowerCase();
//
//            // Excluded everywhere → special row
//            if (excludeEverywhere.contains(normKey)) {
//                html.append(excludedRow(key));
//                continue;
//            }
//
//            Object rq = req.get(normKey);
//            Object rp = res.get(normKey);
//            Object esValRaw = es.get(normKey);
//            Object dbValRaw = db.get(normKey);
//
//            // Display strings (NULL / N/A)
//            String rqS = displayValue(rq, excludeReqOnly.contains(normKey) ? "N/A" : null);
//            String rpS = displayValue(rp, null);
//            String esS = displayValue(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? "N/A" : null
//            );
//            String dbS = displayValue(dbValRaw, null);
//
//            boolean pass = true;
//            String status = "PASS";
//
//            // Comparisons
//            if (!excludeReqOnly.contains(normKey) && !equalNorm(rq, rp)) {
//                pass = false;
//                status = "Mismatch Req↔Resp";
//            }
//            if (!equalNorm(rp, dbValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔DB";
//            }
//            if (!excludeElasticCompletely && !excludeEsOnly.contains(normKey) && !equalNorm(rp, esValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔ES";
//            }
//
//            // Row color logic
//            String rowColor;
//            if (!pass) {
//                failOccured = true;
//                rowColor = "#ffc7c7"; // FAIL red
//            } else {
//                // PASS variants
//                if (excludeReqOnly.contains(normKey) && !excludeEsOnly.contains(normKey)
//                        && !excludeElasticCompletely) {
//                    // Req excluded only, ES still used, and everything matches
//                    rowColor = "#f4ffcc"; // yellow-green
//                } else if (!excludeReqOnly.contains(normKey) &&
//                        (excludeEsOnly.contains(normKey) || excludeElasticCompletely)) {
//                    // ES excluded, but Req/Resp/DB all OK
//                    rowColor = "#ccfaff"; // light cyan
//                } else {
//                    // Normal PASS
//                    rowColor = "#cbffd5"; // green
//                }
//            }
//
//            boolean mismatchRow = !pass;
//
//            html.append("<tr style='background:").append(rowColor).append("'>");
//            html.append(td(key));
//
//            // Req screenshot + value
//            html.append(td(screenshotCell(rq, mismatchRow && !excludeReqOnly.contains(normKey))));
//            html.append(td(rqS));
//
//            // Resp screenshot + value
//            html.append(td(screenshotCell(rp, mismatchRow)));
//            html.append(td(rpS));
//
//            // ES screenshot + value (respect global / per-key ES exclusion)
//            html.append(td(screenshotCell(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    mismatchRow && !excludeElasticCompletely && !excludeEsOnly.contains(normKey)
//            )));
//            html.append(td(esS));
//
//            // DB screenshot + value
//            html.append(td(screenshotCell(dbValRaw, mismatchRow)));
//            html.append(td(dbS));
//
//            html.append(td(status));
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//
//        if (test != null) {
//            test.info(html.toString());
//        }
//
//        quitDriver();
//
//        if (failOccured) {
//            throw new AssertionError("❌ Key validation failed. Check Extent report.");
//        }
//    }
//
//    // ========= DB FETCH =========
//    private static Map<String, Object> fetchDb(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = c.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {
//
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= ES FETCH =========
//    private static Map<String, Object> fetchEs(String index, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try {
//            Response esResp = io.restassured.RestAssured.given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map) {
//                    Map<String, Object> srcMap = (Map<String, Object>) src;
//                    return flattenSafe(MAPPER.writeValueAsString(srcMap));
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= SCREENSHOT (clickable) =========
//    private static String screenshotCell(Object v, boolean capture) {
//        if (!capture || v == null) return "";
//        try {
//            initDriver();
//            String html = "<html><body style='font-family:monospace;font-size:11px;white-space:pre-wrap;'>"
//                    + esc(v.toString()) + "</body></html>";
//            String base = Base64.getEncoder().encodeToString(html.getBytes("UTF-8"));
//            DRIVER.get("data:text/html;base64," + base);
//            Thread.sleep(200L);
//            String img = ((TakesScreenshot) DRIVER).getScreenshotAs(OutputType.BASE64);
//
//            return "<a target='_blank' href='data:image/png;base64," + img + "'>" +
//                    "<img width='180' src='data:image/png;base64," + img + "' " +
//                    "style='border:1px solid #666;border-radius:4px'/></a>";
//        } catch (Exception e) {
//            return "[Capture Failed]";
//        }
//    }
//
//    // ========= VALUE DISPLAY & NORMALIZATION =========
//    private static String displayValue(Object v, String override) {
//        if (override != null) return "<i>" + esc(override) + "</i>";
//        if (v == null) return "<span style='color:#999'>NULL</span>";
//        return esc(String.valueOf(v));
//    }
//
//    private static boolean equalNorm(Object a, Object b) {
//        return normalize(a).equals(normalize(b));
//    }
//
//    /**
//     * Normalize values (for date/time and basic scalar types).
//     * Handles:
//     * - 2025-10-31T14:43:48.548439400Z
//     * - 2025-10-31T14:43:48.548+00:00
//     * - 2025-10-31T14:43:48.548
//     * - 2025-10-31 14:43:48.548000
//     */
//    private static String normalize(Object v) {
//        if (v == null) return "";
//        String s = v.toString().trim();
//        if (s.isEmpty()) return "";
//
//        // Try date/time parsing → epoch millis
//        Instant inst = tryParseInstant(s);
//        if (inst != null) {
//            return String.valueOf(inst.toEpochMilli());
//        }
//
//        // Numeric normalization
//        if (s.matches("-?\\d+(\\.\\d+)?")) {
//            try {
//                return new java.math.BigDecimal(s).stripTrailingZeros().toPlainString();
//            } catch (NumberFormatException ignored) {}
//        }
//
//        // Fallback: remove whitespace and casing artifacts
//        return s.replaceAll("\\s+", "").toLowerCase();
//    }
//
//    private static Instant tryParseInstant(String s) {
//        // ISO instant: 2025-10-31T14:43:48.548439400Z, 2025-10-31T14:43:48.548Z, etc.
//        try {
//            return Instant.parse(s);
//        } catch (DateTimeParseException ignored) {}
//
//        // Offset: 2025-10-31T14:43:48.548+00:00
//        try {
//            return OffsetDateTime.parse(s).toInstant();
//        } catch (DateTimeParseException ignored) {}
//
//        // Pure local ISO: 2025-10-31T14:43:48.548
//        try {
//            LocalDateTime ldt = LocalDateTime.parse(s);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        // DB style: 2025-10-31 14:43:48.548000 or 2025-10-31 14:43:48.548
//        try {
//            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSS]");
//            LocalDateTime ldt = LocalDateTime.parse(s, fmt);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        return null;
//    }
//
//    // ========= FLATTEN JSON =========
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        if (json == null || json.trim().isEmpty()) return out;
//        try {
//            Map<String, Object> m = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(m, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        for (Map.Entry<String, Object> e : src.entrySet()) {
//            String k = e.getKey();
//            Object v = e.getValue();
//            String full = prefix.isEmpty() ? k : prefix + "." + k;
//            if (v instanceof Map) {
//                flatten((Map<String, Object>) v, full, out);
//            } else {
//                out.put(full.toLowerCase(), v);
//            }
//        }
//    }
//
//    // ========= HTML HELPERS =========
//    private static String td(String s) {
//        return "<td style='vertical-align:top;word-break:break-all'>" + s + "</td>";
//    }
//
//    private static String esc(String s) {
//        if (s == null) return "";
//        return s.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;");
//    }
//
//    private static String excludedRow(String key) {
//        return "<tr style='background:#fff3c4'><td>" + esc(key) +
//                "</td><td colspan='9'><i>Excluded Everywhere</i></td></tr>";
//    }
//
//    // ========= UTILITIES =========
//    private static Set<String> lower(String[] arr) {
//        Set<String> s = new HashSet<String>();
//        if (arr != null) {
//            for (String x : arr) {
//                if (x != null) s.add(x.toLowerCase());
//            }
//        }
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^A-Za-z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') {
//                up = true;
//            } else {
//                sb.append(up ? Character.toUpperCase(c) : c);
//                up = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    // ========= WEBDRIVER REUSE =========
//    private static void initDriver() {
//        if (DRIVER == null) {
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions op = new ChromeOptions();
//            op.addArguments("--headless=new", "--window-size=1100,700");
//            DRIVER = new ChromeDriver(op);
//        }
//    }
//
//    private static void quitDriver() {
//        try {
//            if (DRIVER != null) {
//                DRIVER.quit();
//            }
//        } catch (Exception ignored) {
//        } finally {
//            DRIVER = null;
//        }
//    }
//}








//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static WebDriver DRIVER;
//
//    // ========= PUBLIC ENTRY (with Response) =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response responseObj,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//        String responseJson = (responseObj != null) ? responseObj.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson,
//                responseJson,
//                id,
//                esIndexName,
//                dbTableName,
//                excludeElasticCompletely,
//                excludeEverywhereArr,
//                excludeRequestOnlyArr,
//                excludeEsOnlyArr
//        );
//    }
//
//    // ========= MAIN VALIDATOR =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null) {
//            test.info("🔍 Validation Started (ID: " + id + ")");
//        }
//
//        Map<String, Object> req = flattenSafe(requestJson);
//        Map<String, Object> res = flattenSafe(responseJson);
//        Map<String, Object> db = fetchDb(dbTableName, id);
//        Map<String, Object> es = excludeElasticCompletely
//                ? new LinkedHashMap<String, Object>()
//                : fetchEs(esIndexName, id);
//
//        Set<String> keys = new TreeSet<String>();
//        keys.addAll(req.keySet());
//        keys.addAll(res.keySet());
//        keys.addAll(db.keySet());
//        keys.addAll(es.keySet());
//
//        Set<String> excludeEverywhere = lower(excludeEverywhereArr);
//        Set<String> excludeReqOnly = lower(excludeRequestOnlyArr);
//        Set<String> excludeEsOnly = lower(excludeEsOnlyArr);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>API ↔ ES ↔ DB Key Validation | ID: ").append(id).append("</h3>");
//
//        // Legend
//        html.append("<div style='font-size:12px;margin-bottom:6px'>")
//            .append("<b>Legend:</b> ")
//            .append("<span style='background:#cbffd5;padding:0 10px;margin-right:4px;'>PASS</span>")
//            .append("<span style='background:#f4ffcc;padding:0 10px;margin-right:4px;'>PASS (Req excluded)</span>")
//            .append("<span style='background:#ccfaff;padding:0 10px;margin-right:4px;'>PASS (ES excluded)</span>")
//            .append("<span style='background:#fff3c4;padding:0 10px;margin-right:4px;'>Excluded Everywhere</span>")
//            .append("<span style='background:#ffc7c7;padding:0 10px;'>FAIL</span>")
//            .append("</div>");
//
//        html.append("<table border='1' style='border-collapse:collapse;width:100%;font-size:12px'>");
//        html.append("<tr style='background:#d4e3fc;font-weight:bold'>")
//            .append("<td>Key</td>")
//            .append("<td>Req SS</td><td>Req Val</td>")
//            .append("<td>Resp SS</td><td>Resp Val</td>")
//            .append("<td>ES SS</td><td>ES Val</td>")
//            .append("<td>DB SS</td><td>DB Val</td>")
//            .append("<td>Status</td>")
//            .append("</tr>");
//
//        boolean failOccured = false;
//
//        for (String key : keys) {
//            String normKey = key.toLowerCase();
//
//            // Excluded everywhere → special row
//            if (excludeEverywhere.contains(normKey)) {
//                html.append(excludedRow(key));
//                continue;
//            }
//
//            Object rq = req.get(normKey);
//            Object rp = res.get(normKey);
//            Object esValRaw = es.get(normKey);
//            Object dbValRaw = db.get(normKey);
//
//            // Display strings (NULL / N/A)
//            String rqS = displayValue(rq, excludeReqOnly.contains(normKey) ? "N/A" : null);
//            String rpS = displayValue(rp, null);
//            String esS = displayValue(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? "N/A" : null
//            );
//            String dbS = displayValue(dbValRaw, null);
//
//            boolean pass = true;
//            String status = "PASS";
//
//            // Comparisons
//            if (!excludeReqOnly.contains(normKey) && !equalNorm(rq, rp)) {
//                pass = false;
//                status = "Mismatch Req↔Resp";
//            }
//            if (!equalNorm(rp, dbValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔DB";
//            }
//            if (!excludeElasticCompletely && !excludeEsOnly.contains(normKey) && !equalNorm(rp, esValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔ES";
//            }
//
//            // 👉 UPDATED ROW COLOR LOGIC
//            String rowColor;
//            if (!pass) {
//                failOccured = true;
//                rowColor = "#ffc7c7"; // FAIL red
//            } else {
//                // PASS variants (priority: ES-excluded > Req-excluded > normal)
//                if (excludeElasticCompletely || excludeEsOnly.contains(normKey)) {
//                    // ES excluded (global OR per key) but other comparisons passed
//                    rowColor = "#ccfaff"; // light cyan
//                } else if (excludeReqOnly.contains(normKey)) {
//                    // Request excluded only, but Resp / ES / DB match
//                    rowColor = "#f4ffcc"; // yellow-green
//                } else {
//                    // Normal PASS (no special exclusions)
//                    rowColor = "#cbffd5"; // green
//                }
//            }
//
//            boolean mismatchRow = !pass;
//
//            html.append("<tr style='background:").append(rowColor).append("'>");
//            html.append(td(key));
//
//            // Req screenshot + value
//            html.append(td(screenshotCell(rq, mismatchRow && !excludeReqOnly.contains(normKey))));
//            html.append(td(rqS));
//
//            // Resp screenshot + value
//            html.append(td(screenshotCell(rp, mismatchRow)));
//            html.append(td(rpS));
//
//            // ES screenshot + value (respect global / per-key ES exclusion)
//            html.append(td(screenshotCell(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    mismatchRow && !excludeElasticCompletely && !excludeEsOnly.contains(normKey)
//            )));
//            html.append(td(esS));
//
//            // DB screenshot + value
//            html.append(td(screenshotCell(dbValRaw, mismatchRow)));
//            html.append(td(dbS));
//
//            html.append(td(status));
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//
//        if (test != null) {
//            test.info(html.toString());
//        }
//
//        quitDriver();
//
//        if (failOccured) {
//            throw new AssertionError("❌ Key validation failed. Check Extent report.");
//        }
//    }
//
//    // ========= DB FETCH =========
//    private static Map<String, Object> fetchDb(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = c.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {
//
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= ES FETCH =========
//    private static Map<String, Object> fetchEs(String index, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try {
//            Response esResp = io.restassured.RestAssured.given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map) {
//                    Map<String, Object> srcMap = (Map<String, Object>) src;
//                    return flattenSafe(MAPPER.writeValueAsString(srcMap));
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= SCREENSHOT (clickable) =========
//    private static String screenshotCell(Object v, boolean capture) {
//        if (!capture || v == null) return "";
//        try {
//            initDriver();
//            String html = "<html><body style='font-family:monospace;font-size:11px;white-space:pre-wrap;'>"
//                    + esc(v.toString()) + "</body></html>";
//            String base = Base64.getEncoder().encodeToString(html.getBytes("UTF-8"));
//            DRIVER.get("data:text/html;base64," + base);
//            Thread.sleep(200L);
//            String img = ((TakesScreenshot) DRIVER).getScreenshotAs(OutputType.BASE64);
//
//            return "<a target='_blank' href='data:image/png;base64," + img + "'>" +
//                    "<img width='180' src='data:image/png;base64," + img + "' " +
//                    "style='border:1px solid #666;border-radius:4px'/></a>";
//        } catch (Exception e) {
//            return "[Capture Failed]";
//        }
//    }
//
//    // ========= VALUE DISPLAY & NORMALIZATION =========
//    private static String displayValue(Object v, String override) {
//        if (override != null) return "<i>" + esc(override) + "</i>";
//        if (v == null) return "<span style='color:#999'>NULL</span>";
//        return esc(String.valueOf(v));
//    }
//
//    private static boolean equalNorm(Object a, Object b) {
//        return normalize(a).equals(normalize(b));
//    }
//
//    /**
//     * Normalize values (for date/time and basic scalar types).
//     * Handles:
//     * - 2025-10-31T14:43:48.548439400Z
//     * - 2025-10-31T14:43:48.548+00:00
//     * - 2025-10-31T14:43:48.548
//     * - 2025-10-31 14:43:48.548000
//     */
//    private static String normalize(Object v) {
//        if (v == null) return "";
//        String s = v.toString().trim();
//        if (s.isEmpty()) return "";
//
//        // Try date/time parsing → epoch millis
//        Instant inst = tryParseInstant(s);
//        if (inst != null) {
//            return String.valueOf(inst.toEpochMilli());
//        }
//
//        // Numeric normalization
//        if (s.matches("-?\\d+(\\.\\d+)?")) {
//            try {
//                return new java.math.BigDecimal(s).stripTrailingZeros().toPlainString();
//            } catch (NumberFormatException ignored) {}
//        }
//
//        // Fallback: remove whitespace and casing artifacts
//        return s.replaceAll("\\s+", "").toLowerCase();
//    }
//
//    private static Instant tryParseInstant(String s) {
//        // ISO instant: 2025-10-31T14:43:48.548439400Z, 2025-10-31T14:43:48.548Z, etc.
//        try {
//            return Instant.parse(s);
//        } catch (DateTimeParseException ignored) {}
//
//        // Offset: 2025-10-31T14:43:48.548+00:00
//        try {
//            return OffsetDateTime.parse(s).toInstant();
//        } catch (DateTimeParseException ignored) {}
//
//        // Pure local ISO: 2025-10-31T14:43:48.548
//        try {
//            LocalDateTime ldt = LocalDateTime.parse(s);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        // DB style: 2025-10-31 14:43:48.548000 or 2025-10-31 14:43:48.548
//        try {
//            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSS]");
//            LocalDateTime ldt = LocalDateTime.parse(s, fmt);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        return null;
//    }
//
//    // ========= FLATTEN JSON =========
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        if (json == null || json.trim().isEmpty()) return out;
//        try {
//            Map<String, Object> m = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(m, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        for (Map.Entry<String, Object> e : src.entrySet()) {
//            String k = e.getKey();
//            Object v = e.getValue();
//            String full = prefix.isEmpty() ? k : prefix + "." + k;
//            if (v instanceof Map) {
//                flatten((Map<String, Object>) v, full, out);
//            } else {
//                out.put(full.toLowerCase(), v);
//            }
//        }
//    }
//
//    // ========= HTML HELPERS =========
//    private static String td(String s) {
//        return "<td style='vertical-align:top;word-break:break-all'>" + s + "</td>";
//    }
//
//    private static String esc(String s) {
//        if (s == null) return "";
//        return s.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;");
//    }
//
//    private static String excludedRow(String key) {
//        return "<tr style='background:#fff3c4'><td>" + esc(key) +
//                "</td><td colspan='9'><i>Excluded Everywhere</i></td></tr>";
//    }
//
//    // ========= UTILITIES =========
//    private static Set<String> lower(String[] arr) {
//        Set<String> s = new HashSet<String>();
//        if (arr != null) {
//            for (String x : arr) {
//                if (x != null) s.add(x.toLowerCase());
//            }
//        }
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^A-Za-z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') {
//                up = true;
//            } else {
//                sb.append(up ? Character.toUpperCase(c) : c);
//                up = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    // ========= WEBDRIVER REUSE =========
//    private static void initDriver() {
//        if (DRIVER == null) {
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions op = new ChromeOptions();
//            op.addArguments("--headless=new", "--window-size=1100,700");
//            DRIVER = new ChromeDriver(op);
//        }
//    }
//
//    private static void quitDriver() {
//        try {
//            if (DRIVER != null) {
//                DRIVER.quit();
//            }
//        } catch (Exception ignored) {
//        } finally {
//            DRIVER = null;
//        }
//    }
//}










//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import reports.ExtentCucumberListener;
//import io.restassured.response.Response;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import io.cucumber.java.en.Then;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Base64;
//
//public class ApiDataValidator {
//
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static WebDriver DRIVER;
//
//    // ========= PUBLIC ENTRY (with Response) =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            Response responseObj,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//        String responseJson = (responseObj != null) ? responseObj.getBody().asString() : null;
//        validateKeysAcrosRequestResponseEsDb(
//                requestJson,
//                responseJson,
//                id,
//                esIndexName,
//                dbTableName,
//                excludeElasticCompletely,
//                excludeEverywhereArr,
//                excludeRequestOnlyArr,
//                excludeEsOnlyArr
//        );
//    }
//
//    // ========= MAIN VALIDATOR =========
//    public static void validateKeysAcrosRequestResponseEsDb(
//            String requestJson,
//            String responseJson,
//            int id,
//            String esIndexName,
//            String dbTableName,
//            boolean excludeElasticCompletely,
//            String[] excludeEverywhereArr,
//            String[] excludeRequestOnlyArr,
//            String[] excludeEsOnlyArr
//    ) throws Exception {
//
//        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
//        if (test != null) {
//            test.info("🔍 Validation Started (ID: " + id + ")");
//        }
//
//        Map<String, Object> req = flattenSafe(requestJson);
//        Map<String, Object> res = flattenSafe(responseJson);
//        Map<String, Object> db = fetchDb(dbTableName, id);
//        Map<String, Object> es = excludeElasticCompletely
//                ? new LinkedHashMap<String, Object>()
//                : fetchEs(esIndexName, id);
//
//        Set<String> keys = new TreeSet<String>();
//        keys.addAll(req.keySet());
//        keys.addAll(res.keySet());
//        keys.addAll(db.keySet());
//        keys.addAll(es.keySet());
//
//        Set<String> excludeEverywhere = lower(excludeEverywhereArr);
//        Set<String> excludeReqOnly = lower(excludeRequestOnlyArr);
//        Set<String> excludeEsOnly = lower(excludeEsOnlyArr);
//
//        StringBuilder html = new StringBuilder();
//        html.append("<h3>API ↔ ES ↔ DB Key Validation | ID: ").append(id).append("</h3>");
//
//        // Legend
//        html.append("<div style='font-size:12px;margin-bottom:6px'>")
//            .append("<b>Legend:</b> ")
//            .append("<span style='background:#cbffd5;padding:0 10px;margin-right:4px;'>PASS</span>")
//            .append("<span style='background:#f4ffcc;padding:0 10px;margin-right:4px;'>PASS (Req excluded)</span>")
//            .append("<span style='background:#ccfaff;padding:0 10px;margin-right:4px;'>PASS (ES excluded)</span>")
//            .append("<span style='background:#fff3c4;padding:0 10px;margin-right:4px;'>Excluded Everywhere</span>")
//            .append("<span style='background:#ffc7c7;padding:0 10px;'>FAIL</span>")
//            .append("</div>");
//
//        // Table with fixed layout so headers stay in one line
//        html.append("<table border='1' ")
//            .append("style='border-collapse:collapse;width:100%;font-size:12px;table-layout:fixed;'>");
//
//        // Header row using <th> and nowrap
//        html.append("<tr style='background:#d4e3fc;font-weight:bold;text-align:center'>")
//            .append(th("Key"))
//            .append(th("Req SS")).append(th("Req Val"))
//            .append(th("Resp SS")).append(th("Resp Val"))
//            .append(th("ES SS")).append(th("ES Val"))
//            .append(th("DB SS")).append(th("DB Val"))
//            .append(th("Status"))
//            .append("</tr>");
//
//        boolean failOccured = false;
//
//        for (String key : keys) {
//            String normKey = key.toLowerCase();
//
//            // Excluded everywhere → special row
//            if (excludeEverywhere.contains(normKey)) {
//                html.append(excludedRow(key));
//                continue;
//            }
//
//            Object rq = req.get(normKey);
//            Object rp = res.get(normKey);
//            Object esValRaw = es.get(normKey);
//            Object dbValRaw = db.get(normKey);
//
//            // Display strings (NULL / N/A)
//            String rqS = displayValue(rq, excludeReqOnly.contains(normKey) ? "N/A" : null);
//            String rpS = displayValue(rp, null);
//            String esS = displayValue(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? "N/A" : null
//            );
//            String dbS = displayValue(dbValRaw, null);
//
//            boolean pass = true;
//            String status = "PASS";
//
//            // Comparisons
//            if (!excludeReqOnly.contains(normKey) && !equalNorm(rq, rp)) {
//                pass = false;
//                status = "Mismatch Req↔Resp";
//            }
//            if (!equalNorm(rp, dbValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔DB";
//            }
//            if (!excludeElasticCompletely && !excludeEsOnly.contains(normKey) && !equalNorm(rp, esValRaw)) {
//                pass = false;
//                status = "Mismatch Resp↔ES";
//            }
//
//            // Row color logic (priority: ES-excluded > Req-excluded > normal)
//            String rowColor;
//            if (!pass) {
//                failOccured = true;
//                rowColor = "#ffc7c7"; // FAIL red
//            } else {
//                if (excludeElasticCompletely || excludeEsOnly.contains(normKey)) {
//                    rowColor = "#ccfaff"; // ES excluded
//                } else if (excludeReqOnly.contains(normKey)) {
//                    rowColor = "#f4ffcc"; // Request excluded only
//                } else {
//                    rowColor = "#cbffd5"; // Normal PASS
//                }
//            }
//
//            boolean mismatchRow = !pass;
//
//            html.append("<tr style='background:").append(rowColor).append("'>");
//            html.append(td(key));
//
//            // Req screenshot + value
//            html.append(td(screenshotCell(rq, mismatchRow && !excludeReqOnly.contains(normKey))));
//            html.append(td(rqS));
//
//            // Resp screenshot + value
//            html.append(td(screenshotCell(rp, mismatchRow)));
//            html.append(td(rpS));
//
//            // ES screenshot + value (respect global / per-key ES exclusion)
//            html.append(td(screenshotCell(
//                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
//                    mismatchRow && !excludeElasticCompletely && !excludeEsOnly.contains(normKey)
//            )));
//            html.append(td(esS));
//
//            // DB screenshot + value
//            html.append(td(screenshotCell(dbValRaw, mismatchRow)));
//            html.append(td(dbS));
//
//            html.append(td(status));
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//
//        if (test != null) {
//            test.info(html.toString());
//        }
//
//        quitDriver();
//
//        if (failOccured) {
//            throw new AssertionError("❌ Key validation failed. Check Extent report.");
//        }
//    }
//
//    // ========= DB FETCH =========
//    private static Map<String, Object> fetchDb(String table, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = c.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {
//
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);
//                    out.put(toCamel(col).toLowerCase(), val);
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= ES FETCH =========
//    private static Map<String, Object> fetchEs(String index, int id) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try {
//            Response esResp = io.restassured.RestAssured.given()
//                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
//                            "_index/_search?q=id:" + id + "&size=1")
//                    .then().extract().response();
//
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object src = hits.get(0).get("_source");
//                if (src instanceof Map) {
//                    Map<String, Object> srcMap = (Map<String, Object>) src;
//                    return flattenSafe(MAPPER.writeValueAsString(srcMap));
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
//
//    // ========= SCREENSHOT (clickable) =========
//    private static String screenshotCell(Object v, boolean capture) {
//        if (!capture || v == null) return "";
//        try {
//            initDriver();
//            String html = "<html><body style='font-family:monospace;font-size:11px;white-space:pre-wrap;'>"
//                    + esc(v.toString()) + "</body></html>";
//            String base = Base64.getEncoder().encodeToString(html.getBytes("UTF-8"));
//            DRIVER.get("data:text/html;base64," + base);
//            Thread.sleep(200L);
//            String img = ((TakesScreenshot) DRIVER).getScreenshotAs(OutputType.BASE64);
//
//            return "<a target='_blank' href='data:image/png;base64," + img + "'>" +
//                    "<img width='180' src='data:image/png;base64," + img + "' " +
//                    "style='border:1px solid #666;border-radius:4px'/></a>";
//        } catch (Exception e) {
//            return "[Capture Failed]";
//        }
//    }
//
//    // ========= VALUE DISPLAY & NORMALIZATION =========
//    private static String displayValue(Object v, String override) {
//        if (override != null) return "<i>" + esc(override) + "</i>";
//        if (v == null) return "<span style='color:#999'>NULL</span>";
//        return esc(String.valueOf(v));
//    }
//
//    private static boolean equalNorm(Object a, Object b) {
//        return normalize(a).equals(normalize(b));
//    }
//
//    /**
//     * Normalize values (for date/time and basic scalar types).
//     * Handles:
//     * - 2025-10-31T14:43:48.548439400Z
//     * - 2025-10-31T14:43:48.548+00:00
//     * - 2025-10-31T14:43:48.548
//     * - 2025-10-31 14:43:48.548000
//     */
//    private static String normalize(Object v) {
//        if (v == null) return "";
//        String s = v.toString().trim();
//        if (s.isEmpty()) return "";
//
//        // Try date/time parsing → epoch millis
//        Instant inst = tryParseInstant(s);
//        if (inst != null) {
//            return String.valueOf(inst.toEpochMilli());
//        }
//
//        // Numeric normalization
//        if (s.matches("-?\\d+(\\.\\d+)?")) {
//            try {
//                return new java.math.BigDecimal(s).stripTrailingZeros().toPlainString();
//            } catch (NumberFormatException ignored) {}
//        }
//
//        // Fallback: remove whitespace and casing artifacts
//        return s.replaceAll("\\s+", "").toLowerCase();
//    }
//
//    private static Instant tryParseInstant(String s) {
//        // ISO instant: 2025-10-31T14:43:48.548439400Z, 2025-10-31T14:43:48.548Z, etc.
//        try {
//            return Instant.parse(s);
//        } catch (DateTimeParseException ignored) {}
//
//        // Offset: 2025-10-31T14:43:48.548+00:00
//        try {
//            return OffsetDateTime.parse(s).toInstant();
//        } catch (DateTimeParseException ignored) {}
//
//        // Pure local ISO: 2025-10-31T14:43:48.548
//        try {
//            LocalDateTime ldt = LocalDateTime.parse(s);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        // DB style: 2025-10-31 14:43:48.548000 or 2025-10-31 14:43:48.548
//        try {
//            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSS]");
//            LocalDateTime ldt = LocalDateTime.parse(s, fmt);
//            return ldt.toInstant(ZoneOffset.UTC);
//        } catch (DateTimeParseException ignored) {}
//
//        return null;
//    }
//
//    // ========= FLATTEN JSON =========
//    private static Map<String, Object> flattenSafe(String json) {
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        if (json == null || json.trim().isEmpty()) return out;
//        try {
//            Map<String, Object> m = MAPPER.readValue(
//                    json,
//                    new TypeReference<Map<String, Object>>() {}
//            );
//            flatten(m, "", out);
//        } catch (Exception ignored) {}
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
//        for (Map.Entry<String, Object> e : src.entrySet()) {
//            String k = e.getKey();
//            Object v = e.getValue();
//            String full = prefix.isEmpty() ? k : prefix + "." + k;
//            if (v instanceof Map) {
//                flatten((Map<String, Object>) v, full, out);
//            } else {
//                out.put(full.toLowerCase(), v);
//            }
//        }
//    }
//
//    // ========= HTML HELPERS =========
//    private static String td(String s) {
//        return "<td style='vertical-align:top;word-break:break-all'>" + s + "</td>";
//    }
//
//    private static String th(String text) {
//        return "<th style='white-space:nowrap;padding:4px 6px;'>" + esc(text) + "</th>";
//    }
//
//    private static String esc(String s) {
//        if (s == null) return "";
//        return s.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;");
//    }
//
//    private static String excludedRow(String key) {
//        return "<tr style='background:#fff3c4'><td>" + esc(key) +
//                "</td><td colspan='9'><i>Excluded Everywhere</i></td></tr>";
//    }
//
//    // ========= UTILITIES =========
//    private static Set<String> lower(String[] arr) {
//        Set<String> s = new HashSet<String>();
//        if (arr != null) {
//            for (String x : arr) {
//                if (x != null) s.add(x.toLowerCase());
//            }
//        }
//        return s;
//    }
//
//    private static String safe(String s) {
//        return s.replaceAll("[^A-Za-z0-9_]", "");
//    }
//
//    private static String toCamel(String s) {
//        StringBuilder sb = new StringBuilder();
//        boolean up = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') {
//                up = true;
//            } else {
//                sb.append(up ? Character.toUpperCase(c) : c);
//                up = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    // ========= WEBDRIVER REUSE =========
//    private static void initDriver() {
//        if (DRIVER == null) {
//            WebDriverManager.chromedriver().setup();
//            ChromeOptions op = new ChromeOptions();
//            op.addArguments("--headless=new", "--window-size=1100,700");
//            DRIVER = new ChromeDriver(op);
//        }
//    }
//
//    private static void quitDriver() {
//        try {
//            if (DRIVER != null) {
//                DRIVER.quit();
//            }
//        } catch (Exception ignored) {
//        } finally {
//            DRIVER = null;
//        }
//    }
//}




// DB order

package validators;

import com.aventstack.extentreports.ExtentTest;
import reports.ExtentCucumberListener;
import io.restassured.response.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Base64;

public class ApiDataValidator {

    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "dhi123";
    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static WebDriver DRIVER;

    // ========= PUBLIC ENTRY (with Response) =========
    public static void validateKeysAcrosRequestResponseEsDb(
            String requestJson,
            Response responseObj,
            int id,
            String esIndexName,
            String dbTableName,
            boolean excludeElasticCompletely,
            String[] excludeEverywhereArr,
            String[] excludeRequestOnlyArr,
            String[] excludeEsOnlyArr
    ) throws Exception {
        String responseJson = (responseObj != null) ? responseObj.getBody().asString() : null;
        validateKeysAcrosRequestResponseEsDb(
                requestJson,
                responseJson,
                id,
                esIndexName,
                dbTableName,
                excludeElasticCompletely,
                excludeEverywhereArr,
                excludeRequestOnlyArr,
                excludeEsOnlyArr
        );
    }

    // ========= MAIN VALIDATOR =========
    public static void validateKeysAcrosRequestResponseEsDb(
            String requestJson,
            String responseJson,
            int id,
            String esIndexName,
            String dbTableName,
            boolean excludeElasticCompletely,
            String[] excludeEverywhereArr,
            String[] excludeRequestOnlyArr,
            String[] excludeEsOnlyArr
    ) throws Exception {

        ExtentTest test = ExtentCucumberListener.getCurrentScenario();
        if (test != null) {
            test.info("🔍 Validation Started (ID: " + id + ")");
        }

        Map<String, Object> req = flattenSafe(requestJson);
        Map<String, Object> res = flattenSafe(responseJson);
        Map<String, Object> db = fetchDb(dbTableName, id);
        Map<String, Object> es = excludeElasticCompletely
                ? new LinkedHashMap<String, Object>()
                : fetchEs(esIndexName, id);

        // ===== DB-FIRST KEY ORDER =====
        // LinkedHashSet preserves insertion order.
        Set<String> keys = new LinkedHashSet<String>();

        // 1) DB keys first, in DB column order
        if (db != null) {
            for (String k : db.keySet()) {
                if (k != null) keys.add(k);
            }
        }

        // 2) Then request keys
        if (req != null) {
            for (String k : req.keySet()) {
                if (k != null) keys.add(k);
            }
        }

        // 3) Then response keys
        if (res != null) {
            for (String k : res.keySet()) {
                if (k != null) keys.add(k);
            }
        }

        // 4) Finally ES keys (if enabled)
        if (!excludeElasticCompletely && es != null) {
            for (String k : es.keySet()) {
                if (k != null) keys.add(k);
            }
        }
        // =================================

        Set<String> excludeEverywhere = lower(excludeEverywhereArr);
        Set<String> excludeReqOnly = lower(excludeRequestOnlyArr);
        Set<String> excludeEsOnly = lower(excludeEsOnlyArr);

        StringBuilder html = new StringBuilder();
        html.append("<h3>API ↔ ES ↔ DB Key Validation | ID: ").append(id).append("</h3>");

        // Legend
        html.append("<div style='font-size:12px;margin-bottom:6px'>")
            .append("<b>Legend:</b> ")
            .append("<span style='background:#cbffd5;padding:0 10px;margin-right:4px;'>PASS</span>")
            .append("<span style='background:#f4ffcc;padding:0 10px;margin-right:4px;'>PASS (Req excluded)</span>")
            .append("<span style='background:#ccfaff;padding:0 10px;margin-right:4px;'>PASS (ES excluded)</span>")
            .append("<span style='background:#fff3c4;padding:0 10px;margin-right:4px;'>Excluded Everywhere</span>")
            .append("<span style='background:#ffc7c7;padding:0 10px;'>FAIL</span>")
            .append("</div>");

        // Table with fixed layout so headers stay in one line
        html.append("<table border='1' ")
            .append("style='border-collapse:collapse;width:100%;font-size:12px;table-layout:fixed;'>");

        // Header row using <th> and nowrap
        html.append("<tr style='background:#d4e3fc;font-weight:bold;text-align:center'>")
            .append(th("Key"))
            .append(th("Req SS")).append(th("Req Val"))
            .append(th("Resp SS")).append(th("Resp Val"))
            .append(th("ES SS")).append(th("ES Val"))
            .append(th("DB SS")).append(th("DB Val"))
            .append(th("Status"))
            .append("</tr>");

        boolean failOccured = false;

        for (String key : keys) {
            String normKey = key.toLowerCase();

            // Excluded everywhere → special row
            if (excludeEverywhere.contains(normKey)) {
                html.append(excludedRow(key));
                continue;
            }

            Object rq = req.get(normKey);
            Object rp = res.get(normKey);
            Object esValRaw = es.get(normKey);
            Object dbValRaw = db.get(normKey);

            // Display strings (NULL / N/A)
            String rqS = displayValue(rq, excludeReqOnly.contains(normKey) ? "N/A" : null);
            String rpS = displayValue(rp, null);
            String esS = displayValue(
                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? "N/A" : null
            );
            String dbS = displayValue(dbValRaw, null);

            boolean pass = true;
            String status = "PASS";

            // Comparisons
            if (!excludeReqOnly.contains(normKey) && !equalNorm(rq, rp)) {
                pass = false;
                status = "Mismatch Req↔Resp";
            }
            if (!equalNorm(rp, dbValRaw)) {
                pass = false;
                status = "Mismatch Resp↔DB";
            }
            if (!excludeElasticCompletely && !excludeEsOnly.contains(normKey) && !equalNorm(rp, esValRaw)) {
                pass = false;
                status = "Mismatch Resp↔ES";
            }

            // Row color logic (priority: ES-excluded > Req-excluded > normal)
            String rowColor;
            if (!pass) {
                failOccured = true;
                rowColor = "#ffc7c7"; // FAIL red
            } else {
                if (excludeElasticCompletely || excludeEsOnly.contains(normKey)) {
                    rowColor = "#ccfaff"; // ES excluded
                } else if (excludeReqOnly.contains(normKey)) {
                    rowColor = "#f4ffcc"; // Request excluded only
                } else {
                    rowColor = "#cbffd5"; // Normal PASS
                }
            }

            boolean mismatchRow = !pass;

            html.append("<tr style='background:").append(rowColor).append("'>");
            html.append(td(key));

            // Req screenshot + value
            html.append(td(screenshotCell(rq, mismatchRow && !excludeReqOnly.contains(normKey))));
            html.append(td(rqS));

            // Resp screenshot + value
            html.append(td(screenshotCell(rp, mismatchRow)));
            html.append(td(rpS));

            // ES screenshot + value (respect global / per-key ES exclusion)
            html.append(td(screenshotCell(
                    (excludeElasticCompletely || excludeEsOnly.contains(normKey)) ? null : esValRaw,
                    mismatchRow && !excludeElasticCompletely && !excludeEsOnly.contains(normKey)
            )));
            html.append(td(esS));

            // DB screenshot + value
            html.append(td(screenshotCell(dbValRaw, mismatchRow)));
            html.append(td(dbS));

            html.append(td(status));
            html.append("</tr>");
        }

        html.append("</table>");

        if (test != null) {
            test.info(html.toString());
        }

        quitDriver();

        if (failOccured) {
            throw new AssertionError("❌ Key validation failed. Check Extent report.");
        }
    }

    // ========= DB FETCH (DB column order preserved) =========
//    private static Map<String, Object> fetchDb(String table, int id) {
//        // LinkedHashMap preserves DB column order as returned by ResultSetMetaData
//        Map<String, Object> out = new LinkedHashMap<String, Object>();
//        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = c.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {
//
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData md = rs.getMetaData();
//
//            if (rs.next()) {
//                for (int i = 1; i <= md.getColumnCount(); i++) {
//                    String col = md.getColumnName(i);     // e.g. "id", "affectedVersions", ...
//                    Object val = rs.getObject(i);
//                    out.put(col.toLowerCase(), val);      // single entry, DB-name-based key
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return out;
//    }
    private static Map<String, Object> fetchDb(String table, int id) {
        Map<String, Object> out = new LinkedHashMap<>();

        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + safe(table) + "` WHERE id=?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            if (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {

                    String col = md.getColumnName(i);
                    Object val;

                    // 🔥 Fix → return BIT/TINYINT as 0/1 instead of boolean false/true
                    switch (md.getColumnType(i)) {
                        case Types.BIT:
                        case Types.TINYINT:
                        case Types.SMALLINT:
                        case Types.INTEGER:
                        case Types.BIGINT:
                            val = rs.getString(i); // ⬅ keeps 0/1 RAW
                            break;

                        default:
                            val = rs.getObject(i); // normal behavior
                    }

                    // Store both snake_case and camelCase key versions
                    out.put(col.toLowerCase(), val);
                    out.put(toCamel(col).toLowerCase(), val);
                }
            }

        } catch (Exception ignored) {}

        return out;
    }

    // ========= ES FETCH =========
    private static Map<String, Object> fetchEs(String index, int id) {
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        try {
            Response esResp = io.restassured.RestAssured.given()
                    .get("http://10.10.2.45:9200/piqa_issue-persistence_" + index +
                            "_index/_search?q=id:" + id + "&size=1")
                    .then().extract().response();

            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
            if (hits != null && !hits.isEmpty()) {
                Object src = hits.get(0).get("_source");
                if (src instanceof Map) {
                    Map<String, Object> srcMap = (Map<String, Object>) src;
                    return flattenSafe(MAPPER.writeValueAsString(srcMap));
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    // ========= SCREENSHOT (clickable) =========
    private static String screenshotCell(Object v, boolean capture) {
        if (!capture || v == null) return "";
        try {
            initDriver();
            String html = "<html><body style='font-family:monospace;font-size:11px;white-space:pre-wrap;'>"
                    + esc(v.toString()) + "</body></html>";
            String base = Base64.getEncoder().encodeToString(html.getBytes("UTF-8"));
            DRIVER.get("data:text/html;base64," + base);
            Thread.sleep(200L);
            String img = ((TakesScreenshot) DRIVER).getScreenshotAs(OutputType.BASE64);

            return "<a target='_blank' href='data:image/png;base64," + img + "'>" +
                    "<img width='180' src='data:image/png;base64," + img + "' " +
                    "style='border:1px solid #666;border-radius:4px'/></a>";
        } catch (Exception e) {
            return "[Capture Failed]";
        }
    }

    // ========= VALUE DISPLAY & NORMALIZATION =========
    private static String displayValue(Object v, String override) {
        if (override != null) return "<i>" + esc(override) + "</i>";
        if (v == null) return "<span style='color:#999'>NULL</span>";
        return esc(String.valueOf(v));
    }

    private static boolean equalNorm(Object a, Object b) {
        return normalize(a).equals(normalize(b));
    }

    /**
     * Normalize values (for date/time and basic scalar types).
     * Handles:
     * - 2025-10-31T14:43:48.548439400Z
     * - 2025-10-31T14:43:48.548+00:00
     * - 2025-10-31T14:43:48.548
     * - 2025-10-31 14:43:48.548000
     */
//    private static String normalize(Object v) {
//        if (v == null) return "";
//        String s = v.toString().trim();
//        if (s.isEmpty()) return "";
//
//        // Try date/time parsing → epoch millis
//        Instant inst = tryParseInstant(s);
//        if (inst != null) {
//            return String.valueOf(inst.toEpochMilli());
//        }
//
//        // Numeric normalization
//        if (s.matches("-?\\d+(\\.\\d+)?")) {
//            try {
//                return new java.math.BigDecimal(s).stripTrailingZeros().toPlainString();
//            } catch (NumberFormatException ignored) {}
//        }
//
//        // Fallback: remove whitespace and casing artifacts
//        return s.replaceAll("\\s+", "").toLowerCase();
//    }
    
    private static String normalize(Object v) {
        if (v == null) return "";
        String s = v.toString().trim();
        if (s.isEmpty()) return "";

        String lower = s.toLowerCase();

        // 🔹 Boolean ↔ 0/1 mapping
        // "true"  -> "1"
        // "false" -> "0"
        if (lower.equals("true")) {
            return "1";
        }
        if (lower.equals("false")) {
            return "0";
        }

        // 🔹 Some JDBC drivers return bit as "b'0'" / "b'1'"
        if (lower.matches("b'[01]'")) {
            return lower.charAt(2) == '1' ? "1" : "0";
        }

        // 🔹 Date/time normalization → epoch millis
        Instant inst = tryParseInstant(s);
        if (inst != null) {
            return String.valueOf(inst.toEpochMilli());
        }

        // 🔹 Numeric normalization (e.g. "5.0" == "5")
        if (s.matches("-?\\d+(\\.\\d+)?")) {
            try {
                return new java.math.BigDecimal(s)
                        .stripTrailingZeros()
                        .toPlainString();
            } catch (NumberFormatException ignored) {}
        }

        // 🔹 Fallback: case/whitespace insensitive comparison
        return s.replaceAll("\\s+", "").toLowerCase();
    }

    

    private static Instant tryParseInstant(String s) {
        // ISO instant: 2025-10-31T14:43:48.548439400Z, 2025-10-31T14:43:48.548Z, etc.
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException ignored) {}

        // Offset: 2025-10-31T14:43:48.548+00:00
        try {
            return OffsetDateTime.parse(s).toInstant();
        } catch (DateTimeParseException ignored) {}

        // Pure local ISO: 2025-10-31T14:43:48.548
        try {
            LocalDateTime ldt = LocalDateTime.parse(s);
            return ldt.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {}

        // DB style: 2025-10-31 14:43:48.548000 or 2025-10-31 14:43:48.548
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSS]");
            LocalDateTime ldt = LocalDateTime.parse(s, fmt);
            return ldt.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {}

        return null;
    }

    // ========= FLATTEN JSON =========
    private static Map<String, Object> flattenSafe(String json) {
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        if (json == null || json.trim().isEmpty()) return out;
        try {
            Map<String, Object> m = MAPPER.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {}
            );
            flatten(m, "", out);
        } catch (Exception ignored) {}
        return out;
    }

    @SuppressWarnings("unchecked")
    private static void flatten(Map<String, Object> src, String prefix, Map<String, Object> out) {
        for (Map.Entry<String, Object> e : src.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            String full = prefix.isEmpty() ? k : prefix + "." + k;
            if (v instanceof Map) {
                flatten((Map<String, Object>) v, full, out);
            } else {
                out.put(full.toLowerCase(), v);
            }
        }
    }

    // ========= HTML HELPERS =========
    private static String td(String s) {
        return "<td style='vertical-align:top;word-break:break-all'>" + s + "</td>";
    }

    private static String th(String text) {
        return "<th style='white-space:nowrap;padding:4px 6px;'>" + esc(text) + "</th>";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String excludedRow(String key) {
        return "<tr style='background:#fff3c4'><td>" + esc(key) +
                "</td><td colspan='9'><i>Excluded Everywhere</i></td></tr>";
    }

    // ========= UTILITIES =========
    private static Set<String> lower(String[] arr) {
        Set<String> s = new HashSet<String>();
        if (arr != null) {
            for (String x : arr) {
                if (x != null) s.add(x.toLowerCase());
            }
        }
        return s;
    }

    private static String safe(String s) {
        return s.replaceAll("[^A-Za-z0-9_]", "");
    }

    private static String toCamel(String s) {
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char c : s.toCharArray()) {
            if (c == '_' || c == '-') {
                up = true;
            } else {
                sb.append(up ? Character.toUpperCase(c) : c);
                up = false;
            }
        }
        return sb.toString();
    }

    // ========= WEBDRIVER REUSE =========
    private static void initDriver() {
        if (DRIVER == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions op = new ChromeOptions();
            op.addArguments("--headless=new", "--window-size=1100,700");
            DRIVER = new ChromeDriver(op);
        }
    }

    private static void quitDriver() {
        try {
            if (DRIVER != null) {
                DRIVER.quit();
            }
        } catch (Exception ignored) {
        } finally {
            DRIVER = null;
        }
    }
}





//@Then("Validate API ES and DB for the ComponentId after {word} request")
//public void validate_api_es_and_db_for_the_ComponentId_after_request(String method) throws Exception {
//    if (ComponentId == 0) {
//        throw new IllegalStateException("No ComponentId available. Run POST/PUT/PATCH first.");
//    }
//    if (response == null) {
//        throw new IllegalStateException("Response is null. Make sure the request step ran.");
//    }
//    if (RequestBodyForValidation == null) {
//        throw new IllegalStateException("Last request body is null. Ensure the scenario used POST/PUT/PATCH step.");
//    }
//
//    ExtentTest current = ExtentCucumberListener.getCurrentScenario();
//    if (current != null) {
//        current.info("🧪 Cross-layer validation triggered from " + method.toUpperCase() + " request");
//    }
//
//    ApiDataValidator.validateKeysAcrosRequestResponseEsDb(
//    		RequestBodyForValidation,
//            response,
//            ComponentId,
//            "components",              // ES index suffix: piqa_issue-persistence_behavior_index
//            "Components",              // DB table name
//            false,                   // false => include ES validation
//            null,                    // excludeEverywhereArr (none)
//            new String[] {"id","deleted"},   // exclude from Request only
//            new String[] {"deleted"}         // exclude from ES only
//    );
//}
