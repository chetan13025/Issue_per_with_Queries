//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//
//import java.io.IOException;
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ApiValidator {
//
//    // --- CONFIG: change to your environment ---
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    /* ---------------- Public entrypoint: full validator + screenshots ---------------- */
//    public static void validateRequestKeysNullsAndMismatchesWithScreenshots(
//            Response response,
//            String requestJson,
//            int recordId,
//            String esIndexName,
//            String dbTableName,
//            boolean requireEsPresence,
//            long timestampToleranceMs
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//        if (t != null) t.info("🔎 Cross-layer validation (screenshots) started for id=" + recordId);
//
//        // flatten request & response
//        Map<String, Object> reqMap = safeJsonToMap(requestJson);
//        Map<String, Object> reqFlat = new LinkedHashMap<>();
//        flattenJson(reqMap, "", reqFlat);
//
//        Map<String, Object> respMap = safeJsonToMap(response == null ? null : response.getBody().asString());
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(respMap, "", respFlat);
//
//        // fetch ES _source (size=1)
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = io.restassured.RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object _src = hits.get(0).get("_source");
//                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
//            } else if (requireEsPresence && t != null) {
//                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // fetch DB row
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>(); // keep raw column order
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        dbFlat.put(col.toLowerCase(), val);
//                        dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
//                        dbRawOrdered.put(col, val); // keep original col name & order
//                    }
//                } else if (t != null) {
//                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
//                }
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
//        }
//
//        // include [] markers for arrays found in raw JSONs
//        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
//        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
//        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());
//
//        // ---------- Build robust key set (union + helpful variants) ----------
//        Set<String> reqRawKeys = reqFlat.keySet().stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<String> respRawKeys = respFlat.keySet().stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<String> esRawKeys = esFlat.keySet().stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<String> dbRawKeys = dbFlat.keySet().stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
//
//        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
//        Stream.of(reqRawKeys, respRawKeys, esRawKeys, dbRawKeys)
//                .forEach(set -> set.forEach(k -> { if (k != null) keysToCheck.add(k); }));
//
//        // add normalized variants for robust matching (e.g., foo[0] -> foo[], underscore/camel)
//        List<String> additional = new ArrayList<>();
//        for (String k : new ArrayList<>(keysToCheck)) {
//            String norm = normalizeKeyForMatching(k);
//            additional.add(norm);
//            String camel = underscoreToCamel(k);
//            if (camel != null && !camel.equalsIgnoreCase(k)) additional.add(camel);
//            String unders = camelToUnderscore(k);
//            if (unders != null && !unders.equalsIgnoreCase(k)) additional.add(unders);
//        }
//        additional.forEach(a -> { if (a != null) keysToCheck.add(a); });
//
//        // DEBUG: log discovered keys per layer
//        ExtentTest log = log();
//        if (log != null) {
//            log.info("🔍 Discovered keys — Request: " + prettyFormatKeys(reqRawKeys));
//            log.info("🔍 Discovered keys — Response: " + prettyFormatKeys(respRawKeys));
//            log.info("🔍 Discovered keys — Elasticsearch: " + prettyFormatKeys(esRawKeys));
//            log.info("🔍 Discovered keys — DB: " + prettyFormatKeys(dbRawKeys));
//            log.info("🔍 Keys to check (union+variants): " + prettyFormatKeys(keysToCheck));
//        }
//
//        List<String> failures = new ArrayList<>();
//        List<String> failingKeys = new ArrayList<>();
//
//        // Prepare summary HTML builder
//        StringBuilder summaryHtml = new StringBuilder();
//        summaryHtml.append("<html><head><meta charset='utf-8'><style>")
//                .append("body{font-family:Arial,Helvetica,sans-serif;padding:10px} ")
//                .append(".legend{font-size:16px;margin-bottom:10px} .legend span{display:inline-block;padding:6px;border-radius:6px;margin-right:6px} ")
//                .append(".g{background:#d4edda;color:#155724} .y{background:#fff3cd;color:#856404} .r{background:#f8d7da;color:#721c24} .b{background:#e2f0fb;color:#0b4f73} ")
//                .append("table.valTable{border-collapse:collapse;width:100%} table.valTable th, table.valTable td{border:1px solid #ddd;padding:10px;text-align:left;font-size:14px} ")
//                .append("table.valTable th{background:#f2f2f2;font-weight:700;font-size:16px} .mono{font-family:monospace;font-size:13px} ")
//                .append(".pass{background:#d4edda} .nullEs{background:#fff3cd} .fail{background:#f8d7da} .excl{background:#e2f0fb} ")
//                .append("</style></head><body>");
//        summaryHtml.append("<div class='legend'>")
//                .append("<span class='g'>&#x2705; Green = All layers match</span>")
//                .append("<span class='y'>&#9888; Yellow = Null / Missing in Elasticsearch</span>")
//                .append("<span class='r'>&#10060; Red = Missing / Mismatch (DB null or mismatch)</span>")
//                .append("<span class='b'>&#8505; Blue = Not in request but validated</span>")
//                .append("</div>");
//        summaryHtml.append("<table class='valTable'><tr><th>Key</th><th>Request</th><th>Response</th><th>Elasticsearch</th><th>Database (SQL col)</th><th>Status</th></tr>");
//
//        // iterate keys and evaluate — only failing keys will be screenshotted
//        for (String rawKey : keysToCheck) {
//            String key = rawKey == null ? null : rawKey.toLowerCase();
//            Object qObj = findValueCaseInsensitive(reqFlat, key);
//            Object rObj = findValueCaseInsensitive(respFlat, key);
//            Object eObj = findValueCaseInsensitive(esFlat, key);
//            Object dObj = findValueCaseInsensitive(dbFlat, key);
//
//            String qStr = toScalarString(qObj);
//            String rStr = toScalarString(rObj);
//            String eStr = toScalarString(eObj);
//            String dStr = toScalarString(dObj);
//
//            boolean missing = false;
//            boolean mismatch = false;
//
//            // Missing conditions
//            if (rStr == null) {
//                missing = true;
//                failures.add("Missing in Response: " + rawKey);
//            }
//            if (dStr == null) {
//                missing = true;
//                failures.add("Missing in DB: " + rawKey);
//            }
//            if (requireEsPresence && eStr == null) {
//                missing = true;
//                failures.add("Missing in ES: " + rawKey);
//            }
//
//            // Compare values if present
//            if (!missing && qStr != null) {
//                Instant iq = parseInstantOrNull(qStr);
//                Instant ir = parseInstantOrNull(rStr);
//                Instant ie = parseInstantOrNull(eStr);
//                Instant idb = parseInstantOrNull(dStr);
//                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(iq, ir, timestampToleranceMs)
//                            && instantEqualWithin(iq, idb, timestampToleranceMs)
//                            && (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
//                    if (!timesOk) {
//                        mismatch = true;
//                        failures.add("Timestamp mismatch for: " + rawKey + " -> req=" + qStr + ", resp=" + rStr + ", es=" + eStr + ", db=" + dStr);
//                    }
//                } else {
//                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
//                            && qStr.trim().equalsIgnoreCase(dStr.trim())
//                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim())));
//                    if (!allMatch) {
//                        mismatch = true;
//                        failures.add("Value mismatch for: " + rawKey + " -> req=" + qStr + ", resp=" + rStr + ", es=" + eStr + ", db=" + dStr);
//                    }
//                }
//            } else if (!missing && qStr == null) {
//                // No request -> compare response <-> db (and es)
//                Instant ir = parseInstantOrNull(rStr);
//                Instant idb = parseInstantOrNull(dStr);
//                Instant ie = parseInstantOrNull(eStr);
//                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(ir, idb, timestampToleranceMs) && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
//                    if (!timesOk) {
//                        mismatch = true;
//                        failures.add("Timestamp mismatch for (no-request) " + rawKey + " -> resp=" + rStr + ", es=" + eStr + ", db=" + dStr);
//                    }
//                } else {
//                    boolean respDbMatch = rStr.trim().equalsIgnoreCase(dStr.trim());
//                    boolean respEsMatch = !requireEsPresence || rStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim()));
//                    if (!respDbMatch || !respEsMatch) {
//                        mismatch = true;
//                        failures.add("Value mismatch for (no-request) " + rawKey + " -> resp=" + rStr + ", es=" + eStr + ", db=" + dStr);
//                    }
//                }
//            }
//
//            // Evaluate row class & status text
//            String rowClass = "pass";
//            String statusText = "PASS";
//            if (missing) {
//                // Distinguish null-in-es vs others: if ES null but resp/db present -> yellow
//                if (requireEsPresence && eStr == null && rStr != null && dStr != null) {
//                    rowClass = "nullEs";
//                    statusText = "NULL_IN_ES";
//                } else {
//                    rowClass = "fail";
//                    statusText = "MISSING";
//                }
//            } else if (mismatch) {
//                rowClass = "fail";
//                statusText = "VALUE_MISMATCH";
//            }
//
//            // If failed/missing -> capture screenshots (combined)
//            if ("fail".equals(rowClass) || "nullEs".equals(rowClass)) {
//                failingKeys.add(rawKey);
//                try {
//                    captureLayerScreenshotsForKey(rawKey, requestJson, response, esSource, dbRawOrdered, t);
//                } catch (Exception ex) {
//                    if (t != null) t.warning("⚠️ Failed to capture screenshot for key " + rawKey + ": " + ex.getMessage());
//                }
//            } else {
//                if (t != null) t.pass("✅ Key matched: " + rawKey + " -> resp=" + safe(rStr) + " | db=" + safe(dStr) + (eStr == null ? "" : " | es=" + safe(eStr)));
//            }
//
//            // Append summary row (show DB raw column name if present)
//            String dbColDisplay = findDbColumnDisplayName(dbRawOrdered, key);
//            summaryHtml.append("<tr class='").append(rowClass).append("'>")
//                    .append("<td>").append(htmlEscape(rawKey)).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(qStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(rStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(eStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(dbColDisplay))).append("</td>")
//                    .append("<td>").append(statusText).append("</td>")
//                    .append("</tr>");
//        } // end for keys
//
//        summaryHtml.append("</table></body></html>");
//
//        // attach summary to Extent as single screenshot (render HTML & snapshot)
//        if (log != null) {
//            String base64 = captureHtmlHeadlessBase64(summaryHtml.toString(), 1600, 900, 1200); // larger render
//            if (base64 != null) {
//                log.info("📊 Cross-layer summary:").addScreenCaptureFromBase64String(base64, "CrossLayerSummary_" + recordId);
//            } else {
//                // fallback: attach raw html text as info
//                log.info("📊 Cross-layer summary HTML (render failed):\n" + summaryHtml.toString());
//            }
//        }
//
//        // final outcome
//        if (!failures.isEmpty()) {
//            if (log != null) {
//                log.fail("❌ Cross-layer validation failed for id=" + recordId + ". Failing keys: " + failingKeys);
//            }
//            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failingKeys);
//        } else {
//            if (log != null) log.pass("✅ Cross-layer validation passed for id=" + recordId + " (no failing keys)");
//        }
//    }
//
//    // ---------------- Helper & utilities ----------------
//
//    private static ExtentTest log() {
//        return ExtentCucumberListener.getCurrentScenario();
//    }
//
//    private static Map<String, Object> safeJsonToMap(String json) {
//        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
//        try {
//            // try Jackson mapping
//            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
//        } catch (Exception e) {
//            return Collections.emptyMap();
//        }
//    }
//
//    /**
//     * Flatten nested maps/lists into dot/array indexed keys.
//     * Example:
//     * { user: { name: "x", roles: ["a"] } } =>
//     * user.name -> "x"
//     * user.roles[] -> List
//     * user.roles[0] -> "a"
//     */
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
//        if (node == null) return;
//        for (Map.Entry<String, Object> e : node.entrySet()) {
//            String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
//            Object value = e.getValue();
//            if (value instanceof Map) {
//                flattenJson((Map<String, Object>) value, key, out);
//            } else if (value instanceof List) {
//                List<?> list = (List<?>) value;
//                out.put(key + "[]", list);
//                for (int i = 0; i < list.size(); i++) {
//                    Object item = list.get(i);
//                    if (item instanceof Map) {
//                        flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
//                    } else {
//                        out.put(key + "[" + i + "]", item);
//                    }
//                }
//            } else {
//                out.put(key, value);
//            }
//        }
//    }
//
//    /**
//     * If raw JSON text contains `"foo": []` but flattenJson didn't produce foo[] (e.g., empty array),
//     * this helper adds the "foo[]" key into the provided keySet so we can see the empty array marker.
//     */
//    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
//        if (rawJson == null || keySet == null) return;
//        try {
//            // simple regex to find "key" : [ ... ] occurrences (including empty arrays)
//            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE);
//            Matcher m = p.matcher(rawJson);
//            while (m.find()) {
//                String k = m.group(1);
//                keySet.add(k + "[]");
//            }
//        } catch (Exception ignored) {}
//    }
//
//    /**
//     * Robust find: tries exact ci, index-normalized, [] variant, underscore/camel conversions, last resort token contains.
//     */
//    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
//        if (flat == null || key == null) return null;
//
//        // exact case-insensitive
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) return e.getValue();
//        }
//
//        // normalized indices: collapse [digits] -> []
//        String keyIdxNorm = key.replaceAll("\\[\\d+\\]", "[]");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            String ek = e.getKey();
//            if (ek == null) continue;
//            String ekNorm = ek.replaceAll("\\[\\d+\\]", "[]");
//            if (ekNorm.equalsIgnoreCase(keyIdxNorm)) return e.getValue();
//        }
//
//        // try foo vs foo[]
//        String alt = keyIdxNorm.endsWith("[]") ? keyIdxNorm.replace("[]", "") : keyIdxNorm + "[]";
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() != null && e.getKey().equalsIgnoreCase(alt)) return e.getValue();
//        }
//
//        // underscore <-> camel
//        String underscore = camelToUnderscore(key);
//        if (underscore != null) {
//            for (Map.Entry<String, Object> e : flat.entrySet()) {
//                if (e.getKey() != null && e.getKey().equalsIgnoreCase(underscore)) return e.getValue();
//            }
//        }
//        String camel = underscoreToCamel(key);
//        if (camel != null) {
//            for (Map.Entry<String, Object> e : flat.entrySet()) {
//                if (e.getKey() != null && e.getKey().equalsIgnoreCase(camel)) return e.getValue();
//            }
//        }
//
//        // last resort: contains token
//        String token = key.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() == null) continue;
//            String lk = e.getKey().toLowerCase();
//            if (lk.contains(token.toLowerCase())) return e.getValue();
//        }
//
//        return null;
//    }
//
//    private static String normalizeKeyForMatching(String k) {
//        if (k == null) return null;
//        return k.replaceAll("\\[\\d+\\]", "[]");
//    }
//
//    private static String camelToUnderscore(String s) {
//        if (s == null) return null;
//        StringBuilder out = new StringBuilder();
//        for (char c : s.toCharArray()) {
//            if (Character.isUpperCase(c)) {
//                out.append('_').append(Character.toLowerCase(c));
//            } else {
//                out.append(c);
//            }
//        }
//        return out.toString();
//    }
//
//    /**
//     * Convert underscore_case to camelCase (simple).
//     */
//    private static String underscoreToCamel(String s) {
//        if (s == null) return null;
//        StringBuilder sb = new StringBuilder();
//        boolean upper = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') {
//                upper = true;
//            } else {
//                sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
//                upper = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    private static String toScalarString(Object o) {
//        if (o == null) return null;
//        try {
//            if (o instanceof String) return (String) o;
//            if (o instanceof Number) return String.valueOf(o);
//            if (o instanceof Boolean) return String.valueOf(o);
//            if (o instanceof List || o instanceof Map) {
//                return OBJECT_MAPPER.writeValueAsString(o);
//            }
//            return String.valueOf(o);
//        } catch (Exception e) {
//            return String.valueOf(o);
//        }
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try {
//            return Instant.parse(s);
//        } catch (DateTimeParseException ignored) { }
//        try {
//            return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
//        } catch (DateTimeParseException ignored) { }
//        try {
//            Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class);
//            if (d != null) return d.toInstant();
//        } catch (Exception ignored) { }
//        return null;
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
//        return diff <= millisTolerance;
//    }
//
//    private static String htmlEscape(String s) {
//        if (s == null) return "&lt;null&gt;";
//        return s.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\n", "<br/>")
//                .replace("\r", "");
//    }
//
//    private static String safe(String s) {
//        return s == null ? "<null>" : s;
//    }
//
//    private static String prettyFormatKeys(Set<String> keys) {
//        if (keys == null || keys.isEmpty()) return "[]";
//        return keys.stream().collect(Collectors.joining(", "));
//    }
//
//    // ---------------- Screenshot helpers ----------------
//
//    /**
//     * Capture 4-panel comparison for a failing key:
//     * Request | Response
//     * ES      | DB (raw order)
//     */
//    private static void captureLayerScreenshotsForKey(
//            String key,
//            String requestJson,
//            Response response,
//            Map<String,Object> esSource,
//            Map<String,Object> dbRawOrdered, // raw SQL column order mapping
//            ExtentTest t
//    ) throws Exception {
//        String reqPanel = buildJsonPanelHtml(requestJson == null ? "<no request>" : requestJson, key, "Request", 16);
//        String respPanel = buildJsonPanelHtml(response == null ? "<no response>" : response.getBody().asString(), key, "Response", 16);
//        String esPanel = buildDbHtml(esSource == null ? Collections.emptyMap() : esSource, key, "Elasticsearch", true, 14);
//        String dbPanel = buildDbHtml(dbRawOrdered == null ? Collections.emptyMap() : dbRawOrdered, key, "Database (raw)", true, 14);
//
//        String reqBase64 = captureHtmlHeadlessBase64(reqPanel, 900, 900, 900);
//        String respBase64 = captureHtmlHeadlessBase64(respPanel, 900, 900, 900);
//        String esBase64 = captureHtmlHeadlessBase64(esPanel, 900, 900, 900);
//        String dbBase64 = captureHtmlHeadlessBase64(dbPanel, 900, 900, 900);
//
//        String combined = buildCombinedHtmlGrid(key, reqBase64, respBase64, esBase64, dbBase64, 18);
//        String combinedShot = captureHtmlHeadlessBase64(combined, 1600, 1200, 1200);
//
//        if (t != null) {
//            if (combinedShot != null) {
//                t.info("📸 Failure visual for key: " + key)
//                        .addScreenCaptureFromBase64String(combinedShot, "Comparison_" + key.replaceAll("[^A-Za-z0-9_\\-]", "_"));
//            } else {
//                if (reqBase64 != null) t.info("Request (key=" + key + ")").addScreenCaptureFromBase64String(reqBase64);
//                if (respBase64 != null) t.info("Response (key=" + key + ")").addScreenCaptureFromBase64String(respBase64);
//                if (esBase64 != null) t.info("ES (key=" + key + ")").addScreenCaptureFromBase64String(esBase64);
//                if (dbBase64 != null) t.info("DB (key=" + key + ")").addScreenCaptureFromBase64String(dbBase64);
//            }
//        }
//    }
//
//    /**
//     * Build combined HTML grid that embeds base64 images.
//     * fontsize param (px)
//     */
//    private static String buildCombinedHtmlGrid(String key, String reqBase64, String respBase64, String esBase64, String dbBase64, int titleFontPx) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><head><meta charset='utf-8'><title>Compare ").append(htmlEscape(key)).append("</title>");
//        sb.append("<style>body{font-family:Arial;padding:8px} .grid{display:grid;grid-template-columns:1fr 1fr;grid-gap:8px} ");
//        sb.append(".card{border:1px solid #ddd;padding:8px} .title{font-weight:700;margin-bottom:6px;font-size:").append(titleFontPx).append("px} img{max-width:100%;height:auto;display:block}</style>");
//        sb.append("</head><body>");
//        sb.append("<h2 style='font-size:18px;'>Comparison for Key: ").append(htmlEscape(key)).append("</h2>");
//        sb.append("<div class='grid'>");
//        sb.append("<div class='card'><div class='title'>Request</div>");
//        sb.append(reqBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + reqBase64 + "'/>");
//        sb.append("</div>");
//        sb.append("<div class='card'><div class='title'>Response</div>");
//        sb.append(respBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + respBase64 + "'/>");
//        sb.append("</div>");
//        sb.append("<div class='card'><div class='title'>Elasticsearch</div>");
//        sb.append(esBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + esBase64 + "'/>");
//        sb.append("</div>");
//        sb.append("<div class='card'><div class='title'>Database</div>");
//        sb.append(dbBase64 == null ? "<div>&lt;no image&gt;</div>" : "<img src='data:image/png;base64," + dbBase64 + "'/>");
//        sb.append("</div>");
//        sb.append("</div></body></html>");
//        return sb.toString();
//    }
//
//    /**
//     * Headless chrome render of arbitrary HTML -> base64 PNG.
//     * width x height control, and viewportWidth used for window-size arg.
//     */
//    private static String captureHtmlHeadlessBase64(String html, int width, int height, int viewportWidth) {
//        WebDriver driver = null;
//        java.nio.file.Path tmpHtml = null;
//        try {
//            tmpHtml = java.nio.file.Files.createTempFile("av_val_html_", ".html");
//            java.nio.file.Files.write(tmpHtml, html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
//
//            System.setProperty("webdriver.chrome.silentOutput", "true");
//            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
//            options.addArguments("--headless=new");
//            options.addArguments("--disable-gpu");
//            options.addArguments("--no-sandbox");
//            options.addArguments("--window-size=" + viewportWidth + "," + height);
//            options.addArguments("--hide-scrollbars");
//            driver = new ChromeDriver(options);
//
//            driver.get(tmpHtml.toUri().toString());
//            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
//
//            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//            return java.util.Base64.getEncoder().encodeToString(png);
//        } catch (Exception e) {
//            ExtentTest t = log();
//            if (t != null) t.warning("⚠️ captureHtmlHeadlessBase64 failed: " + e.getMessage());
//            return null;
//        } finally {
//            try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
//            try { if (tmpHtml != null) java.nio.file.Files.deleteIfExists(tmpHtml); } catch (Exception ignored) {}
//        }
//    }
//
//    /**
//     * Build JSON panel HTML with strict-ish key highlighting (searches quoted keys or tokens).
//     * fontSize (px) controls visible text size.
//     */
//    private static String buildJsonPanelHtml(String json, String key, String title, int fontSizePx) {
//        String body;
//        if (json == null) json = "<null>";
//        // Pretty-print JSON if possible
//        try {
//            Object parsed = OBJECT_MAPPER.readValue(json, Object.class);
//            String pretty = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
//            body = pretty;
//        } catch (Exception e) {
//            body = json;
//        }
//
//        // Strict-ish highlighting: highlight occurrences of "key" as JSON property names and occurrences of the value token
//        String escaped = htmlEscape(body);
//
//        // highlight quoted property names: "key" :
//        try {
//            Pattern p = Pattern.compile("(?i)(\"\\s*" + Pattern.quote(key) + "\\s*\"\\s*:\\s*)");
//            Matcher m = p.matcher(escaped);
//            StringBuffer sb = new StringBuffer();
//            while (m.find()) {
//                m.appendReplacement(sb, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m.group(1)) + "</mark>");
//            }
//            m.appendTail(sb);
//            escaped = sb.toString();
//        } catch (Exception ignored) {}
//
//        // also highlight occurrences of the key token (unquoted) for non-strict JSON
//        try {
//            Pattern p2 = Pattern.compile("(?i)\\b" + Pattern.quote(key) + "\\b");
//            Matcher m2 = p2.matcher(escaped);
//            StringBuffer sb2 = new StringBuffer();
//            while (m2.find()) {
//                m2.appendReplacement(sb2, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m2.group()) + "</mark>");
//            }
//            m2.appendTail(sb2);
//            escaped = sb2.toString();
//        } catch (Exception ignored) {}
//
//        String html = "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:8px} mark{padding:0 2px;border-radius:3px} .title{font-weight:700;margin-bottom:8px;font-size:" + (fontSizePx+2) + "px}</style></head><body>"
//                + "<div class='title'>" + htmlEscape(title) + "</div>"
//                + "<pre style='font-size:" + fontSizePx + "px;line-height:1.2;'>" + escaped + "</pre></body></html>";
//        return html;
//    }
//
//    /**
//     * Build DB HTML table from Map and highlight the matching column name (case-insensitive).
//     * If preserveRawOrder==true, 'map' should be LinkedHashMap with raw SQL column ordering.
//     */
//    private static String buildDbHtml(Map<String, Object> map, String keyToHighlight, String title, boolean preserveRawOrder, int fontSizePx) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:8px} table{border-collapse:collapse;width:100%} td,th{border:1px solid #ddd;padding:8px;text-align:left;font-size:")
//                .append(fontSizePx).append("px} th{background:#f2f2f2;font-weight:700} .hl{background:#fff3cd} </style></head><body>");
//        sb.append("<div style='font-weight:700;margin-bottom:6px;font-size:").append(fontSizePx+2).append("px;'>").append(htmlEscape(title)).append("</div>");
//        sb.append("<table><tr><th>Column</th><th>Value</th></tr>");
//        if (map == null || map.isEmpty()) {
//            sb.append("<tr><td colspan='2'>&lt;no data&gt;</td></tr>");
//        } else {
//            if (preserveRawOrder && map instanceof LinkedHashMap) {
//                for (Map.Entry<String, Object> e : map.entrySet()) {
//                    String col = e.getKey();
//                    String val = toScalarString(e.getValue());
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            } else {
//                // sort keys for readability (SQL-style upper-case display)
//                List<String> cols = new ArrayList<>(map.keySet());
//                Collections.sort(cols, String.CASE_INSENSITIVE_ORDER);
//                for (String col : cols) {
//                    Object v = map.get(col);
//                    String val = toScalarString(v);
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            }
//        }
//        sb.append("</table></body></html>");
//        return sb.toString();
//    }
//
//    // find raw DB column name to display (exact SQL column name if present)
//    private static String findDbColumnDisplayName(Map<String,Object> dbRawOrdered, String normalizedKey) {
//        if (dbRawOrdered == null || dbRawOrdered.isEmpty()) return "<no row>";
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.equalsIgnoreCase(normalizedKey) || underscoreToCamel(col).equalsIgnoreCase(normalizedKey) || col.replaceAll("\\s","").equalsIgnoreCase(normalizedKey)) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        // fallback: find any column containing token
//        String token = normalizedKey.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.toLowerCase().contains(token.toLowerCase())) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        return "<no col>";
//    }
//    private static String requireSafeIdentifier(String raw) {
//        if (raw == null) throw new IllegalArgumentException("Identifier is null");
//        String cleaned = raw.replaceAll("[^A-Za-z0-9_]", "");
//        if (cleaned.isEmpty()) {
//            throw new IllegalArgumentException("Identifier '" + raw + "' is not a valid SQL identifier after sanitization");
//        }
//        return cleaned;
//    }
//}

//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ApiValidator {
//
//    // --- CONFIG: change to your environment ---
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    /**
//     * Build a plain HTML table summary (no screenshots) and attach it to ExtentReports via t.info(html).
//     *
//     * @param response             REST-assured response (may be null)
//     * @param requestJson          raw request JSON string (may be null)
//     * @param recordId             id used to fetch ES/DB
//     * @param esIndexName          ES index suffix (used in your environment)
//     * @param dbTableName          DB table name
//     * @param requireEsPresence    treat missing ES as failure (if true)
//     * @param timestampToleranceMs tolerance for timestamp comparisons
//     */
//    public static void validateRequestKeysPlainTableHtml(
//            Response response,
//            String requestJson,
//            int recordId,
//            String esIndexName,
//            String dbTableName,
//            boolean requireEsPresence,
//            long timestampToleranceMs
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//
//        // --- Flatten request & response ---
//        Map<String, Object> reqFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(requestJson), "", reqFlat);
//
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(response == null ? null : response.getBody().asString()), "", respFlat);
//
//        // --- Fetch ES _source (size=1) ---
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object _src = hits.get(0).get("_source");
//                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
//            } else if (requireEsPresence && t != null) {
//                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // --- Fetch DB row ---
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>();
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        dbFlat.put(col.toLowerCase(), val);
//                        dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
//                        dbRawOrdered.put(col, val); // keep original col name & order
//                    }
//                } else if (t != null) {
//                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
//                }
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
//        }
//
//        // include [] markers for arrays found in raw JSONs
//        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
//        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
//        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());
//
//        // ---------- Build robust key set (union + variants) ----------
//        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
//        Stream.of(reqFlat.keySet(), respFlat.keySet(), esFlat.keySet(), dbFlat.keySet())
//                .forEach(kset -> kset.forEach(k -> { if (k != null) keysToCheck.add(k); }));
//
//        // add normalized variants for robust matching
//        List<String> additions = new ArrayList<>();
//        for (String k : new ArrayList<>(keysToCheck)) {
//            additions.add(normalizeKeyForMatching(k));
//            String camel = underscoreToCamel(k);
//            if (camel != null && !camel.equalsIgnoreCase(k)) additions.add(camel);
//            String unders = camelToUnderscore(k);
//            if (unders != null && !unders.equalsIgnoreCase(k)) additions.add(unders);
//        }
//        additions.forEach(a -> { if (a != null) keysToCheck.add(a); });
//
//        // DEBUG logging of discovered keys
//        if (t != null) {
//            t.info("🔍 Keys to check: " + prettyFormatKeys(keysToCheck));
//        }
//
//        // ---------- Build HTML table ----------
//        StringBuilder html = new StringBuilder();
//        html.append("<html><head><meta charset='utf-8'><style>")
//                .append("body{font-family:Arial,Helvetica,sans-serif;padding:14px;background:#fff}")
//                .append(".legend{margin-bottom:10px;font-size:15px}")
//                .append(".legend span{display:inline-block;padding:6px;border-radius:6px;margin-right:6px}")
//                .append(".g{background:#d4edda;color:#155724}.y{background:#fff3cd;color:#856404}.r{background:#f8d7da;color:#721c24}")
//                .append("table{border-collapse:collapse;width:100%;table-layout:fixed} th,td{border:1px solid #ddd;padding:10px;text-align:left;font-size:14px;vertical-align:top;word-wrap:break-word}")
//                .append("th{background:#f6f6f6;font-weight:700;font-size:15px}.mono{font-family:monospace;font-size:13px}")
//                .append("</style></head><body>");
//        html.append("<div class='legend'>")
//                .append("<span class='g'>&#x2705; Green = PASS (all layers present & matching)</span>")
//                .append("<span class='y'>&#9888; Yellow = NULL_IN_ES (ES null, resp & db present)</span>")
//                .append("<span class='r'>&#10060; Red = MISSING or VALUE_MISMATCH (DB null or mismatch)</span>")
//                .append("</div>");
//        html.append("<h3>Cross-layer Summary (Record ID: ").append(recordId).append(")</h3>");
//        html.append("<table><tr><th style='width:14%'>Key</th><th style='width:18%'>Request</th><th style='width:18%'>Response</th><th style='width:18%'>Elasticsearch</th><th style='width:20%'>Database (SQL col = value)</th><th style='width:12%'>Status</th></tr>");
//
//        List<String> failures = new ArrayList<>();
//        List<String> failingKeys = new ArrayList<>();
//
//        for (String rawKey : keysToCheck) {
//            String key = rawKey == null ? null : rawKey.toLowerCase();
//
//            Object qObj = findValueCaseInsensitive(reqFlat, key);
//            Object rObj = findValueCaseInsensitive(respFlat, key);
//            Object eObj = findValueCaseInsensitive(esFlat, key);
//            Object dObj = findValueCaseInsensitive(dbFlat, key);
//
//            String qStr = toScalarString(qObj);
//            String rStr = toScalarString(rObj);
//            String eStr = toScalarString(eObj);
//            String dStr = toScalarString(dObj);
//
//            boolean missing = false;
//            boolean mismatch = false;
//
//            // Missing conditions
//            if (rStr == null) { missing = true; failures.add("Missing in Response: " + rawKey); }
//            if (dStr == null) { missing = true; failures.add("Missing in DB: " + rawKey); }
//            if (requireEsPresence && eStr == null) { missing = true; failures.add("Missing in ES: " + rawKey); }
//
//            // Compare values if present
//            if (!missing && qStr != null) {
//                Instant iq = parseInstantOrNull(qStr);
//                Instant ir = parseInstantOrNull(rStr);
//                Instant ie = parseInstantOrNull(eStr);
//                Instant idb = parseInstantOrNull(dStr);
//                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(iq, ir, timestampToleranceMs)
//                            && instantEqualWithin(iq, idb, timestampToleranceMs)
//                            && (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch for: " + rawKey); }
//                } else {
//                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
//                            && qStr.trim().equalsIgnoreCase(dStr.trim())
//                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim())));
//                    if (!allMatch) { mismatch = true; failures.add("Value mismatch for: " + rawKey); }
//                }
//            } else if (!missing && qStr == null) {
//                // No request -> compare response <-> db (and es)
//                Instant ir = parseInstantOrNull(rStr);
//                Instant idb = parseInstantOrNull(dStr);
//                Instant ie = parseInstantOrNull(eStr);
//                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(ir, idb, timestampToleranceMs) && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch (no-request) for: " + rawKey); }
//                } else {
//                    boolean respDbMatch = rStr.trim().equalsIgnoreCase(dStr.trim());
//                    boolean respEsMatch = !requireEsPresence || rStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim()));
//                    if (!respDbMatch || !respEsMatch) { mismatch = true; failures.add("Value mismatch (no-request) for: " + rawKey); }
//                }
//            }
//
//            // Determine status & styling
//            String rowStyle = "";
//            String status = "PASS";
//            if (missing) {
//                if (requireEsPresence && eStr == null && rStr != null && dStr != null) {
//                    rowStyle = "background:#fff3cd"; // yellow
//                    status = "NULL_IN_ES";
//                } else {
//                    rowStyle = "background:#f8d7da"; // red
//                    status = "MISSING";
//                }
//            } else if (mismatch) {
//                rowStyle = "background:#f8d7da"; // red
//                status = "VALUE_MISMATCH";
//            } else {
//                rowStyle = "background:#d4edda"; // green
//                status = "PASS";
//            }
//
//            if ("MISSING".equals(status) || "VALUE_MISMATCH".equals(status) || "NULL_IN_ES".equals(status)) {
//                failingKeys.add(rawKey);
//            }
//
//            // Find DB SQL column name display
//            String dbDisplay = findDbColumnDisplayName(dbRawOrdered, key);
//
//            html.append("<tr style='").append(rowStyle).append("'>")
//                    .append("<td class='mono'>").append(htmlEscape(rawKey)).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(qStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(rStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(eStr))).append("</td>")
//                    .append("<td class='mono'>").append(htmlEscape(safe(dbDisplay))).append("</td>")
//                    .append("<td>").append(status).append("</td>")
//                    .append("</tr>");
//        }
//
//        html.append("</table></body></html>");
//
//        // Attach HTML into Extent (raw HTML; Extent usually renders HTML inside the log)
//        if (t != null) {
//            t.info("📊 Cross-layer plain table summary (HTML):");
//            t.info(html.toString());
//        }
//
//        // Final assertion: fail if there were issues
//        if (!failures.isEmpty()) {
//            if (t != null) t.fail("❌ Cross-layer validation failed for id=" + recordId + ". Failing keys: " + failingKeys);
//            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failingKeys);
//        } else {
//            if (t != null) t.pass("✅ Cross-layer validation passed for id=" + recordId);
//        }
//    }
//
//    // ---------------- Helper & utilities (same as before) ----------------
//
//    private static ExtentTest log() { return ExtentCucumberListener.getCurrentScenario(); }
//
//    private static Map<String, Object> safeJsonToMap(String json) {
//        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
//        try {
//            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
//        } catch (Exception e) {
//            return Collections.emptyMap();
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
//        if (node == null) return;
//        for (Map.Entry<String, Object> e : node.entrySet()) {
//            String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
//            Object value = e.getValue();
//            if (value instanceof Map) {
//                flattenJson((Map<String, Object>) value, key, out);
//            } else if (value instanceof List) {
//                List<?> list = (List<?>) value;
//                out.put(key + "[]", list);
//                for (int i = 0; i < list.size(); i++) {
//                    Object item = list.get(i);
//                    if (item instanceof Map) flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
//                    else out.put(key + "[" + i + "]", item);
//                }
//            } else {
//                out.put(key, value);
//            }
//        }
//    }
//
//    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
//        if (rawJson == null || keySet == null) return;
//        try {
//            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE);
//            Matcher m = p.matcher(rawJson);
//            while (m.find()) {
//                String k = m.group(1);
//                keySet.add(k + "[]");
//            }
//        } catch (Exception ignored) {}
//    }
//
//    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
//        if (flat == null || key == null) return null;
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) return e.getValue();
//        }
//        String keyIdxNorm = key.replaceAll("\\[\\d+\\]", "[]");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            String ek = e.getKey();
//            if (ek == null) continue;
//            String ekNorm = ek.replaceAll("\\[\\d+\\]", "[]");
//            if (ekNorm.equalsIgnoreCase(keyIdxNorm)) return e.getValue();
//        }
//        String underscore = camelToUnderscore(key);
//        if (underscore != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(underscore)) return e.getValue();
//        String camel = underscoreToCamel(key);
//        if (camel != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(camel)) return e.getValue();
//
//        String token = key.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() == null) continue;
//            if (e.getKey().toLowerCase().contains(token.toLowerCase())) return e.getValue();
//        }
//        return null;
//    }
//
//    private static String normalizeKeyForMatching(String k) {
//        if (k == null) return null;
//        return k.replaceAll("\\[\\d+\\]", "[]");
//    }
//
//    private static String camelToUnderscore(String s) {
//        if (s == null) return null;
//        StringBuilder out = new StringBuilder();
//        for (char c : s.toCharArray()) {
//            if (Character.isUpperCase(c)) out.append('_').append(Character.toLowerCase(c));
//            else out.append(c);
//        }
//        return out.toString();
//    }
//
//    private static String underscoreToCamel(String s) {
//        if (s == null) return null;
//        StringBuilder sb = new StringBuilder();
//        boolean upper = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') upper = true;
//            else { sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c)); upper = false; }
//        }
//        return sb.toString();
//    }
//
//    private static String toScalarString(Object o) {
//        if (o == null) return null;
//        try {
//            if (o instanceof String) return (String) o;
//            if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
//            if (o instanceof List || o instanceof Map) return OBJECT_MAPPER.writeValueAsString(o);
//            return String.valueOf(o);
//        } catch (Exception e) {
//            return String.valueOf(o);
//        }
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
//        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(); } catch (DateTimeParseException ignored) {}
//        try { Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class); if (d != null) return d.toInstant(); } catch (Exception ignored) {}
//        return null;
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
//        return diff <= millisTolerance;
//    }
//
//    private static String htmlEscape(String s) {
//        if (s == null) return "&lt;null&gt;";
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\r", "");
//    }
//
//    private static String safe(String s) { return s == null ? "<null>" : s; }
//
//    private static String prettyFormatKeys(Set<String> keys) {
//        if (keys == null || keys.isEmpty()) return "[]";
//        return keys.stream().collect(Collectors.joining(", "));
//    }
//
//    private static String requireSafeIdentifier(String raw) {
//        if (raw == null) throw new IllegalArgumentException("Identifier is null");
//        String cleaned = raw.replaceAll("[^A-Za-z0-9_]", "");
//        if (cleaned.isEmpty()) throw new IllegalArgumentException("Identifier '" + raw + "' is not a valid SQL identifier after sanitization");
//        return cleaned;
//    }
//
//    private static String findDbColumnDisplayName(Map<String,Object> dbRawOrdered, String normalizedKey) {
//        if (dbRawOrdered == null || dbRawOrdered.isEmpty()) return "<no row>";
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.equalsIgnoreCase(normalizedKey) || underscoreToCamel(col).equalsIgnoreCase(normalizedKey) || col.replaceAll("\\s","").equalsIgnoreCase(normalizedKey)) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        String token = normalizedKey == null ? "" : normalizedKey.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.toLowerCase().contains(token.toLowerCase())) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        return "<no col>";
//    }
//
//    // ---------------- Example call ----------------
//    // ApiValidator.validateRequestKeysPlainTableHtml(
//    //     response,
//    //     requestBody,
//    //     IssueId,
//    //     "issue",
//    //     "Issue",
//    //     true,
//    //     5000L
//    // );
//
//}

//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ApiValidator {
//
//    // --- CONFIG: change to your environment ---
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    /**
//     * Validate keys and produce a table with inline thumbnails per key (Request | Response | ES | DB).
//     *
//     * @param response             REST-assured response (may be null)
//     * @param requestJson          raw request JSON (may be null)
//     * @param recordId             id used to fetch ES/DB
//     * @param esIndexName          ES index suffix
//     * @param dbTableName          DB table name
//     * @param requireEsPresence    treat missing ES as failure when true
//     * @param timestampToleranceMs timestamp tolerance ms for date comparisons
//     * @param captureAllKeys       if true capture thumbnails for all keys; if false capture only failing keys
//     */
//    public static void validateRequestKeysWithThumbnails(
//            Response response,
//            String requestJson,
//            int recordId,
//            String esIndexName,
//            String dbTableName,
//            boolean requireEsPresence,
//            long timestampToleranceMs,
//            boolean captureAllKeys
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//        if (t != null) t.info("🔎 Cross-layer validation (thumbnails) started for id=" + recordId);
//
//        // --- Flatten request & response ---
//        Map<String, Object> reqFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(requestJson), "", reqFlat);
//
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(response == null ? null : response.getBody().asString()), "", respFlat);
//
//        // --- Fetch ES _source (size=1) ---
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object _src = hits.get(0).get("_source");
//                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
//            } else if (requireEsPresence && t != null) {
//                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // --- Fetch DB row ---
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>();
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        dbFlat.put(col.toLowerCase(), val);
//                        dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
//                        dbRawOrdered.put(col, val);
//                    }
//                } else if (t != null) {
//                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
//                }
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
//        }
//
//        // include array markers for empty arrays etc.
//        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
//        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
//        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());
//
//        // ---------- Build robust key set ----------
//        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
//        Stream.of(reqFlat.keySet(), respFlat.keySet(), esFlat.keySet(), dbFlat.keySet())
//                .forEach(set -> set.forEach(k -> { if (k != null) keysToCheck.add(k); }));
//
//        // add normalized variants
//        List<String> additions = new ArrayList<>();
//        for (String k : new ArrayList<>(keysToCheck)) {
//            additions.add(normalizeKeyForMatching(k));
//            String camel = underscoreToCamel(k);
//            if (camel != null && !camel.equalsIgnoreCase(k)) additions.add(camel);
//            String unders = camelToUnderscore(k);
//            if (unders != null && !unders.equalsIgnoreCase(k)) additions.add(unders);
//        }
//        additions.forEach(a -> { if (a != null) keysToCheck.add(a); });
//
//        if (t != null) t.info("🔍 Keys to check: " + prettyFormatKeys(keysToCheck));
//
//        List<String> failures = new ArrayList<>();
//        List<String> failingKeys = new ArrayList<>();
//
//        // Build table head (bigger font for clarity)
//        StringBuilder html = new StringBuilder();
//        html.append("<html><head><meta charset='utf-8'><style>")
//                .append("body{font-family:Arial,Helvetica,sans-serif;padding:12px;background:#fff}")
//                .append("table{border-collapse:collapse;width:100%;table-layout:fixed} th,td{border:1px solid #ddd;padding:8px;text-align:left;vertical-align:top;word-wrap:break-word}")
//                .append("th{background:#f6f6f6;font-weight:700;font-size:14px} td.mono{font-family:monospace;font-size:12px}")
//                .append(".pass{background:#d4edda}.nullEs{background:#fff3cd}.fail{background:#f8d7da}")
//                // thumbnail style
//                .append(".thumb{width:200px;height:auto;border:1px solid #ccc;display:block;margin:2px 0} .thumb-wrap{max-width:200px;overflow:hidden;text-align:center}")
//                .append("</style></head><body>");
//        html.append("<h3>Cross-layer Comparison (ID: ").append(recordId).append(")</h3>");
//        html.append("<table><tr><th style='width:12%'>Key</th><th style='width:18%'>Request</th><th style='width:18%'>Response</th><th style='width:18%'>Elasticsearch</th><th style='width:22%'>Database (SQL col = value)</th><th style='width:12%'>Status</th></tr>");
//
//        // Iterate keys
//        for (String rawKey : keysToCheck) {
//            String normKey = rawKey == null ? null : rawKey.toLowerCase();
//            Object qObj = findValueCaseInsensitive(reqFlat, normKey);
//            Object rObj = findValueCaseInsensitive(respFlat, normKey);
//            Object eObj = findValueCaseInsensitive(esFlat, normKey);
//            Object dObj = findValueCaseInsensitive(dbFlat, normKey);
//
//            String qStr = toScalarString(qObj);
//            String rStr = toScalarString(rObj);
//            String eStr = toScalarString(eObj);
//            String dStr = toScalarString(dObj);
//
//            boolean missing = false;
//            boolean mismatch = false;
//
//            // Missing checks
//            if (rStr == null) { missing = true; failures.add("Missing in Response: " + rawKey); }
//            if (dStr == null) { missing = true; failures.add("Missing in DB: " + rawKey); }
//            if (requireEsPresence && eStr == null) { missing = true; failures.add("Missing in ES: " + rawKey); }
//
//            // Compare values
//            if (!missing && qStr != null) {
//                Instant iq = parseInstantOrNull(qStr);
//                Instant ir = parseInstantOrNull(rStr);
//                Instant ie = parseInstantOrNull(eStr);
//                Instant idb = parseInstantOrNull(dStr);
//                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(iq, ir, timestampToleranceMs)
//                            && instantEqualWithin(iq, idb, timestampToleranceMs)
//                            && (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch for: " + rawKey); }
//                } else {
//                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
//                            && qStr.trim().equalsIgnoreCase(dStr.trim())
//                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim())));
//                    if (!allMatch) { mismatch = true; failures.add("Value mismatch for: " + rawKey); }
//                }
//            } else if (!missing && qStr == null) {
//                Instant ir = parseInstantOrNull(rStr);
//                Instant idb = parseInstantOrNull(dStr);
//                Instant ie = parseInstantOrNull(eStr);
//                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(ir, idb, timestampToleranceMs) && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch (no-request) for: " + rawKey); }
//                } else {
//                    boolean respDbMatch = rStr.trim().equalsIgnoreCase(dStr.trim());
//                    boolean respEsMatch = !requireEsPresence || rStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim()));
//                    if (!respDbMatch || !respEsMatch) { mismatch = true; failures.add("Value mismatch (no-request) for: " + rawKey); }
//                }
//            }
//
//            // Determine display status
//            String status = "PASS";
//            String rowClass = "pass";
//            if (missing) {
//                if (requireEsPresence && eStr == null && rStr != null && dStr != null) {
//                    status = "NULL_IN_ES";
//                    rowClass = "nullEs";
//                } else {
//                    status = "MISSING";
//                    rowClass = "fail";
//                }
//            } else if (mismatch) {
//                status = "VALUE_MISMATCH";
//                rowClass = "fail";
//            }
//
//            if (!"PASS".equals(status)) failingKeys.add(rawKey);
//
//            // decide whether to capture thumbnails for this key
//            boolean doCapture = captureAllKeys || !"PASS".equals(status);
//
//            // placeholders for base64 images
//            String reqThumb = null, respThumb = null, esThumb = null, dbThumb = null;
//
//            if (doCapture) {
//                try {
//                    // build small HTML panels that highlight key and capture
//                    String reqPanel = buildJsonPanelHtml(requestJson == null ? "<no request>" : requestJson, rawKey, "Request", 10);
//                    String respPanel = buildJsonPanelHtml(response == null ? "<no response>" : response.getBody().asString(), rawKey, "Response", 10);
//                    String esPanel = buildDbHtml(esSource == null ? Collections.emptyMap() : esSource, rawKey, "Elasticsearch", false, 12);
//                    String dbPanel = buildDbHtml(dbRawOrdered == null ? Collections.emptyMap() : dbRawOrdered, rawKey, "Database (raw)", true, 12);
//
//                    // capture small thumbnails (smaller viewport to get compact images)
//                    reqThumb = captureHtmlHeadlessBase64(reqPanel, 600, 420, 800);
//                    respThumb = captureHtmlHeadlessBase64(respPanel, 600, 420, 800);
//                    esThumb = captureHtmlHeadlessBase64(esPanel, 600, 420, 800);
//                    dbThumb = captureHtmlHeadlessBase64(dbPanel, 600, 420, 800);
//                } catch (Exception ex) {
//                    if (t != null) t.warning("⚠️ Thumbnail capture failed for key " + rawKey + ": " + ex.getMessage());
//                }
//            }
//
//            // determine DB column display text
//            String dbDisplay = findDbColumnDisplayName(dbRawOrdered, normKey);
//
//            // Append row with thumbnail images inline (if available) or fallback to text
//            html.append("<tr class='").append(rowClass).append("'>");
//
//            html.append("<td class='mono'>").append(htmlEscape(rawKey)).append("</td>");
//
//            // Request cell
//            html.append("<td>");
//            if (reqThumb != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' src='data:image/png;base64,").append(reqThumb).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(qStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // Response cell
//            html.append("<td>");
//            if (respThumb != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' src='data:image/png;base64,").append(respThumb).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(rStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // ES cell
//            html.append("<td>");
//            if (esThumb != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' src='data:image/png;base64,").append(esThumb).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(eStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // DB cell
//            html.append("<td>");
//            if (dbThumb != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' src='data:image/png;base64,").append(dbThumb).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(dbDisplay))).append("</div>");
//            }
//            html.append("</td>");
//
//            // Status
//            html.append("<td>").append(status).append("</td>");
//
//            html.append("</tr>");
//        } // end for keys
//
//        html.append("</table></body></html>");
//
//        // Attach HTML to Extent
//        if (t != null) {
//            t.info("📊 Cross-layer summary with thumbnails:");
//            t.info(html.toString());
//        }
//
//        if (!failures.isEmpty()) {
//            if (t != null) t.fail("❌ Cross-layer validation failed for id=" + recordId + ". Failing keys: " + failingKeys);
//            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failingKeys);
//        } else {
//            if (t != null) t.pass("✅ Cross-layer validation passed for id=" + recordId);
//        }
//    }
//
//    // ---------------- Helper & utilities ----------------
//
//    private static ExtentTest log() { return ExtentCucumberListener.getCurrentScenario(); }
//
//    private static Map<String, Object> safeJsonToMap(String json) {
//        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
//        try { return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {}); }
//        catch (Exception e) { return Collections.emptyMap(); }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
//        if (node == null) return;
//        for (Map.Entry<String, Object> e : node.entrySet()) {
//            String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
//            Object value = e.getValue();
//            if (value instanceof Map) {
//                flattenJson((Map<String, Object>) value, key, out);
//            } else if (value instanceof List) {
//                List<?> list = (List<?>) value;
//                out.put(key + "[]", list);
//                for (int i = 0; i < list.size(); i++) {
//                    Object item = list.get(i);
//                    if (item instanceof Map) flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
//                    else out.put(key + "[" + i + "]", item);
//                }
//            } else {
//                out.put(key, value);
//            }
//        }
//    }
//
//    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
//        if (rawJson == null || keySet == null) return;
//        try {
//            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE);
//            Matcher m = p.matcher(rawJson);
//            while (m.find()) { keySet.add(m.group(1) + "[]"); }
//        } catch (Exception ignored) {}
//    }
//
//    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
//        if (flat == null || key == null) return null;
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) return e.getValue();
//        }
//        String keyIdxNorm = key.replaceAll("\\[\\d+\\]", "[]");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            String ek = e.getKey(); if (ek == null) continue;
//            String ekNorm = ek.replaceAll("\\[\\d+\\]", "[]");
//            if (ekNorm.equalsIgnoreCase(keyIdxNorm)) return e.getValue();
//        }
//        String underscore = camelToUnderscore(key);
//        if (underscore != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(underscore)) return e.getValue();
//        String camel = underscoreToCamel(key);
//        if (camel != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(camel)) return e.getValue();
//
//        String token = key.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() == null) continue;
//            if (e.getKey().toLowerCase().contains(token.toLowerCase())) return e.getValue();
//        }
//        return null;
//    }
//
//    private static String normalizeKeyForMatching(String k) { if (k == null) return null; return k.replaceAll("\\[\\d+\\]", "[]"); }
//
//    private static String camelToUnderscore(String s) {
//        if (s == null) return null;
//        StringBuilder out = new StringBuilder();
//        for (char c : s.toCharArray()) {
//            if (Character.isUpperCase(c)) out.append('_').append(Character.toLowerCase(c));
//            else out.append(c);
//        }
//        return out.toString();
//    }
//
//    private static String underscoreToCamel(String s) {
//        if (s == null) return null;
//        StringBuilder sb = new StringBuilder();
//        boolean upper = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') upper = true;
//            else { sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c)); upper = false; }
//        }
//        return sb.toString();
//    }
//
//    private static String toScalarString(Object o) {
//        if (o == null) return null;
//        try {
//            if (o instanceof String) return (String) o;
//            if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
//            if (o instanceof List || o instanceof Map) return OBJECT_MAPPER.writeValueAsString(o);
//            return String.valueOf(o);
//        } catch (Exception e) { return String.valueOf(o); }
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
//        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(); } catch (DateTimeParseException ignored) {}
//        try { Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class); if (d != null) return d.toInstant(); } catch (Exception ignored) {}
//        return null;
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
//        return diff <= millisTolerance;
//    }
//
//    private static String htmlEscape(String s) {
//        if (s == null) return "&lt;null&gt;";
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\r", "");
//    }
//
//    private static String safe(String s) { return s == null ? "<null>" : s; }
//
//    private static String prettyFormatKeys(Set<String> keys) {
//        if (keys == null || keys.isEmpty()) return "[]";
//        return keys.stream().collect(Collectors.joining(", "));
//    }
//
//    private static String requireSafeIdentifier(String raw) {
//        if (raw == null) throw new IllegalArgumentException("Identifier is null");
//        String cleaned = raw.replaceAll("[^A-Za-z0-9_]", "");
//        if (cleaned.isEmpty()) throw new IllegalArgumentException("Identifier '" + raw + "' is not a valid SQL identifier after sanitization");
//        return cleaned;
//    }
//
//    /**
//     * Build JSON panel HTML with strict-ish key highlighting (quoted key name highlight + token highlight).
//     * fontSizePx controls visible text size.
//     */
//    private static String buildJsonPanelHtml(String json, String key, String title, int fontSizePx) {
//        String body;
//        if (json == null) json = "<null>";
//        try {
//            Object parsed = OBJECT_MAPPER.readValue(json, Object.class);
//            body = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
//        } catch (Exception e) {
//            body = json;
//        }
//
//        String escaped = htmlEscape(body);
//
//        // highlight quoted property names: "key" :
//        try {
//            Pattern p = Pattern.compile("(?i)(\"\\s*" + Pattern.quote(key) + "\\s*\"\\s*:\\s*)");
//            Matcher m = p.matcher(escaped);
//            StringBuffer sb = new StringBuffer();
//            while (m.find()) {
//                m.appendReplacement(sb, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m.group(1)) + "</mark>");
//            }
//            m.appendTail(sb);
//            escaped = sb.toString();
//        } catch (Exception ignored) {}
//
//        // highlight bare token occurrences
//        try {
//            Pattern p2 = Pattern.compile("(?i)\\b" + Pattern.quote(key) + "\\b");
//            Matcher m2 = p2.matcher(escaped);
//            StringBuffer sb2 = new StringBuffer();
//            while (m2.find()) {
//                m2.appendReplacement(sb2, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m2.group()) + "</mark>");
//            }
//            m2.appendTail(sb2);
//            escaped = sb2.toString();
//        } catch (Exception ignored) {}
//
//        String html = "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:8px} mark{padding:0 3px;border-radius:3px} .title{font-weight:700;margin-bottom:6px;font-size:" + (fontSizePx+2) + "px}</style></head><body>"
//                + "<div class='title'>" + htmlEscape(title) + "</div>"
//                + "<pre style='font-size:" + fontSizePx + "px;line-height:1.15;'>" + escaped + "</pre></body></html>";
//        return html;
//    }
//
//    /**
//     * Build DB HTML table from Map and highlight matching column if any.
//     * preserveRawOrder: if true keep LinkedHashMap ordering
//     */
//    private static String buildDbHtml(Map<String, Object> map, String keyToHighlight, String title, boolean preserveRawOrder, int fontSizePx) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:8px} table{border-collapse:collapse;width:100%} td,th{border:1px solid #ddd;padding:6px;text-align:left;font-size:")
//                .append(fontSizePx).append("px} th{background:#f2f2f2;font-weight:700} .hl{background:#fff3cd}</style></head><body>");
//        sb.append("<div style='font-weight:700;margin-bottom:6px;font-size:").append(fontSizePx+2).append("px;'>").append(htmlEscape(title)).append("</div>");
//        sb.append("<table><tr><th>Column</th><th>Value</th></tr>");
//        if (map == null || map.isEmpty()) {
//            sb.append("<tr><td colspan='2'>&lt;no data&gt;</td></tr>");
//        } else {
//            if (preserveRawOrder && map instanceof LinkedHashMap) {
//                for (Map.Entry<String, Object> e : map.entrySet()) {
//                    String col = e.getKey();
//                    String val = toScalarString(e.getValue());
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            } else {
//                List<String> cols = new ArrayList<>(map.keySet());
//                Collections.sort(cols, String.CASE_INSENSITIVE_ORDER);
//                for (String col : cols) {
//                    String val = toScalarString(map.get(col));
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            }
//        }
//        sb.append("</table></body></html>");
//        return sb.toString();
//    }
//
//    /**
//     * Headless chrome render of arbitrary HTML -> base64 PNG.
//     * width x height control, viewportWidth controls window-size width argument.
//     */
//    private static String captureHtmlHeadlessBase64(String html, int width, int height, int viewportWidth) {
//        WebDriver driver = null;
//        java.nio.file.Path tmp = null;
//        try {
//            tmp = java.nio.file.Files.createTempFile("av_val_html_", ".html");
//            java.nio.file.Files.write(tmp, html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
//
//            System.setProperty("webdriver.chrome.silentOutput", "true");
//            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
//            options.addArguments("--headless=new");
//            options.addArguments("--disable-gpu");
//            options.addArguments("--no-sandbox");
//            options.addArguments("--window-size=" + viewportWidth + "," + height);
//            options.addArguments("--hide-scrollbars");
//            driver = new ChromeDriver(options);
//
//            driver.get(tmp.toUri().toString());
//            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
//
//            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//            return java.util.Base64.getEncoder().encodeToString(png);
//        } catch (Exception e) {
//            ExtentTest log = log();
//            if (log != null) log.warning("⚠️ captureHtmlHeadlessBase64 failed: " + e.getMessage());
//            return null;
//        } finally {
//            try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
//            try { if (tmp != null) java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignored) {}
//        }
//    }
//
//    private static String findDbColumnDisplayName(Map<String,Object> dbRawOrdered, String normalizedKey) {
//        if (dbRawOrdered == null || dbRawOrdered.isEmpty()) return "<no row>";
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.equalsIgnoreCase(normalizedKey) || underscoreToCamel(col).equalsIgnoreCase(normalizedKey) || col.replaceAll("\\s","").equalsIgnoreCase(normalizedKey)) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        String token = normalizedKey == null ? "" : normalizedKey.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.toLowerCase().contains(token.toLowerCase())) return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//        }
//        return "<no col>";
//    }
//
//    // ---------------- Example call ----------------
//    // ApiValidator.validateRequestKeysWithThumbnails(
//    //     response,
//    //     requestBody,
//    //     IssueId,
//    //     "issue",
//    //     "Issue",
//    //     true,
//    //     5000L,
//    //     false    // capture only failing keys (set true to capture thumbnails for all keys)
//    // );
//}

//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ApiValidator {
//
//    // --- CONFIG: change to your environment ---
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    /**
//     * Validate keys and produce a table with inline thumbnails per key (Request | Response | ES | DB).
//     *
//     * @param response             REST-assured response (may be null)
//     * @param requestJson          raw request JSON (may be null)
//     * @param recordId             id used to fetch ES/DB
//     * @param esIndexName          ES index suffix
//     * @param dbTableName          DB table name
//     * @param requireEsPresence    treat missing ES as failure when true
//     * @param timestampToleranceMs timestamp tolerance ms for date comparisons
//     * @param captureAllKeys       if true capture thumbnails for all keys; if false capture only failing keys
//     */
//    public static void validateRequestKeysWithThumbnails(
//            Response response,
//            String requestJson,
//            int recordId,
//            String esIndexName,
//            String dbTableName,
//            boolean requireEsPresence,
//            long timestampToleranceMs,
//            boolean captureAllKeys
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//        if (t != null) t.info("🔎 Cross-layer validation (thumbnails) started for id=" + recordId);
//
//        // --- Flatten request & response ---
//        Map<String, Object> reqFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(requestJson), "", reqFlat);
//
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(response == null ? null : response.getBody().asString()), "", respFlat);
//
//        // --- Fetch ES _source (size=1) ---
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object _src = hits.get(0).get("_source");
//                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
//            } else if (requireEsPresence && t != null) {
//                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // --- Fetch DB row ---
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>();
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        dbFlat.put(col.toLowerCase(), val);
//                        dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
//                        dbRawOrdered.put(col, val);
//                    }
//                } else if (t != null) {
//                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
//                }
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
//        }
//
//        // include array markers for empty arrays etc.
//        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
//        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
//        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());
//
//        // ---------- Build robust key set ----------
//        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
//        Stream.of(reqFlat.keySet(), respFlat.keySet(), esFlat.keySet(), dbFlat.keySet())
//                .forEach(set -> set.forEach(k -> { if (k != null) keysToCheck.add(k); }));
//
//        // add normalized variants
//        List<String> additions = new ArrayList<>();
//        for (String k : new ArrayList<>(keysToCheck)) {
//            additions.add(normalizeKeyForMatching(k));
//            String camel = underscoreToCamel(k);
//            if (camel != null && !camel.equalsIgnoreCase(k)) additions.add(camel);
//            String unders = camelToUnderscore(k);
//            if (unders != null && !unders.equalsIgnoreCase(k)) additions.add(unders);
//        }
//        additions.forEach(a -> { if (a != null) keysToCheck.add(a); });
//
//        if (t != null) t.info("🔍 Keys to check: " + prettyFormatKeys(keysToCheck));
//
//        List<String> failures = new ArrayList<>();
//        List<String> failingKeys = new ArrayList<>();
//
//        // Build table head (bigger font for clarity)
//        StringBuilder html = new StringBuilder();
//        html.append("<html><head><meta charset='utf-8'><style>")
//                .append("body{font-family:Arial,Helvetica,sans-serif;padding:12px;background:#fff}")
//                .append("table{border-collapse:collapse;width:100%;table-layout:fixed} th,td{border:1px solid #ddd;padding:8px;text-align:left;vertical-align:top;word-wrap:break-word}")
//                .append("th{background:#f6f6f6;font-weight:700;font-size:14px} td.mono{font-family:monospace;font-size:12px}")
//                .append(".pass{background:#d4edda}.nullEs{background:#fff3cd}.fail{background:#f8d7da}")
//                // thumbnail style (clickable)
//                .append(".thumb{width:180px;height:auto;border:1px solid #ccc;display:block;margin:4px auto;cursor:pointer} .thumb-wrap{max-width:200px;overflow:hidden;text-align:center}")
//                .append("</style></head><body>");
//        html.append("<h3>Cross-layer Comparison (ID: ").append(recordId).append(")</h3>");
//        html.append("<table><tr><th style='width:12%'>Key</th><th style='width:18%'>Request (click to enlarge)</th><th style='width:18%'>Response (click to enlarge)</th><th style='width:18%'>Elasticsearch (click to enlarge)</th><th style='width:22%'>Database (SQL col = value) (click to enlarge)</th><th style='width:12%'>Status</th></tr>");
//
//        // Iterate keys
//        for (String rawKey : keysToCheck) {
//            String normKey = rawKey == null ? null : rawKey.toLowerCase();
//            Object qObj = findValueCaseInsensitive(reqFlat, normKey);
//            Object rObj = findValueCaseInsensitive(respFlat, normKey);
//            Object eObj = findValueCaseInsensitive(esFlat, normKey);
//            Object dObj = findValueCaseInsensitive(dbFlat, normKey);
//
//            String qStr = toScalarString(qObj);
//            String rStr = toScalarString(rObj);
//            String eStr = toScalarString(eObj);
//            String dStr = toScalarString(dObj);
//
//            boolean missing = false;
//            boolean mismatch = false;
//
//            // Missing checks
//            if (rStr == null) { missing = true; failures.add("Missing in Response: " + rawKey); }
//            if (dStr == null) { missing = true; failures.add("Missing in DB: " + rawKey); }
//            if (requireEsPresence && eStr == null) { missing = true; failures.add("Missing in ES: " + rawKey); }
//
//            // Compare values
//            if (!missing && qStr != null) {
//                Instant iq = parseInstantOrNull(qStr);
//                Instant ir = parseInstantOrNull(rStr);
//                Instant ie = parseInstantOrNull(eStr);
//                Instant idb = parseInstantOrNull(dStr);
//                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(iq, ir, timestampToleranceMs)
//                            && instantEqualWithin(iq, idb, timestampToleranceMs)
//                            && (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch for: " + rawKey); }
//                } else {
//                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
//                            && qStr.trim().equalsIgnoreCase(dStr.trim())
//                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim())));
//                    if (!allMatch) { mismatch = true; failures.add("Value mismatch for: " + rawKey); }
//                }
//            } else if (!missing && qStr == null) {
//                Instant ir = parseInstantOrNull(rStr);
//                Instant idb = parseInstantOrNull(dStr);
//                Instant ie = parseInstantOrNull(eStr);
//                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(ir, idb, timestampToleranceMs) && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch (no-request) for: " + rawKey); }
//                } else {
//                    boolean respDbMatch = rStr.trim().equalsIgnoreCase(dStr.trim());
//                    boolean respEsMatch = !requireEsPresence || rStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim()));
//                    if (!respDbMatch || !respEsMatch) { mismatch = true; failures.add("Value mismatch (no-request) for: " + rawKey); }
//                }
//            }
//
//            // Determine display status
//            String status = "PASS";
//            String rowClass = "pass";
//            if (missing) {
//                if (requireEsPresence && eStr == null && rStr != null && dStr != null) {
//                    status = "NULL_IN_ES";
//                    rowClass = "nullEs";
//                } else {
//                    status = "MISSING";
//                    rowClass = "fail";
//                }
//            } else if (mismatch) {
//                status = "VALUE_MISMATCH";
//                rowClass = "fail";
//            }
//
//            if (!"PASS".equals(status)) failingKeys.add(rawKey);
//
//            // decide whether to capture thumbnails for this key
//            boolean doCapture = captureAllKeys || !"PASS".equals(status);
//
//            // placeholders for base64 images
//            String reqThumb = null, respThumb = null, esThumb = null, dbThumb = null;
//
//            if (doCapture) {
//                try {
//                    // build small HTML panels that highlight key and capture
//                    String reqPanel = buildJsonPanelHtml(requestJson == null ? "<no request>" : requestJson, rawKey, "Request", 10);
//                    String respPanel = buildJsonPanelHtml(response == null ? "<no response>" : response.getBody().asString(), rawKey, "Response", 10);
//                    String esPanel = buildDbHtml(esSource == null ? Collections.emptyMap() : esSource, rawKey, "Elasticsearch", false, 12);
//                    String dbPanel = buildDbHtml(dbRawOrdered == null ? Collections.emptyMap() : dbRawOrdered, rawKey, "Database (raw)", true, 12);
//
//                    // capture small thumbnails (smaller viewport to get compact images)
//                    reqThumb = captureHtmlHeadlessBase64(reqPanel, 600, 420, 800);
//                    respThumb = captureHtmlHeadlessBase64(respPanel, 600, 420, 800);
//                    esThumb = captureHtmlHeadlessBase64(esPanel, 600, 420, 800);
//                    dbThumb = captureHtmlHeadlessBase64(dbPanel, 600, 420, 800);
//                } catch (Exception ex) {
//                    if (t != null) t.warning("⚠️ Thumbnail capture failed for key " + rawKey + ": " + ex.getMessage());
//                }
//            }
//
//            // determine DB column display text
//            String dbDisplay = findDbColumnDisplayName(dbRawOrdered, normKey);
//
//            // Append row with thumbnail images inline (clickable) or fallback to text
//            html.append("<tr class='").append(rowClass).append("'>");
//
//            html.append("<td class='mono'>").append(htmlEscape(rawKey)).append("</td>");
//
//            // Request cell (clickable)
//            html.append("<td>");
//            if (reqThumb != null) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='data:image/png;base64,").append(reqThumb).append("'>")
//                        .append("<img class='thumb' alt='req-thumb' src='data:image/png;base64,").append(reqThumb).append("'/></a></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(qStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // Response cell
//            html.append("<td>");
//            if (respThumb != null) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='data:image/png;base64,").append(respThumb).append("'>")
//                        .append("<img class='thumb' alt='resp-thumb' src='data:image/png;base64,").append(respThumb).append("'/></a></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(rStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // ES cell
//            html.append("<td>");
//            if (esThumb != null) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='data:image/png;base64,").append(esThumb).append("'>")
//                        .append("<img class='thumb' alt='es-thumb' src='data:image/png;base64,").append(esThumb).append("'/></a></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(eStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // DB cell
//            html.append("<td>");
//            if (dbThumb != null) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='data:image/png;base64,").append(dbThumb).append("'>")
//                        .append("<img class='thumb' alt='db-thumb' src='data:image/png;base64,").append(dbThumb).append("'/></a></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(dbDisplay))).append("</div>");
//            }
//            html.append("</td>");
//
//            // Status
//            html.append("<td>").append(status).append("</td>");
//
//            html.append("</tr>");
//        } // end for keys
//
//        html.append("</table></body></html>");
//
//        // Attach HTML to Extent
//        if (t != null) {
//            t.info("📊 Cross-layer summary with thumbnails:");
//            t.info(html.toString());
//        }
//
//        if (!failures.isEmpty()) {
//            if (t != null) t.fail("❌ Cross-layer validation failed for id=" + recordId + ". Failing keys: " + failingKeys);
//            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failingKeys);
//        } else {
//            if (t != null) t.pass("✅ Cross-layer validation passed for id=" + recordId);
//        }
//    }
//
//    // ---------------- Helper & utilities ----------------
//
//    private static ExtentTest log() { return ExtentCucumberListener.getCurrentScenario(); }
//
//    private static Map<String, Object> safeJsonToMap(String json) {
//        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
//        try { return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {}); }
//        catch (Exception e) { return Collections.emptyMap(); }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
//        if (node == null) return;
//        for (Map.Entry<String, Object> e : node.entrySet()) {
//            String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
//            Object value = e.getValue();
//            if (value instanceof Map) {
//                flattenJson((Map<String, Object>) value, key, out);
//            } else if (value instanceof List) {
//                List<?> list = (List<?>) value;
//                out.put(key + "[]", list);
//                for (int i = 0; i < list.size(); i++) {
//                    Object item = list.get(i);
//                    if (item instanceof Map) flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
//                    else out.put(key + "[" + i + "]", item);
//                }
//            } else {
//                out.put(key, value);
//            }
//        }
//    }
//
//    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
//        if (rawJson == null || keySet == null) return;
//        try {
//            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE);
//            Matcher m = p.matcher(rawJson);
//            while (m.find()) { keySet.add(m.group(1) + "[]"); }
//        } catch (Exception ignored) {}
//    }
//
//    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
//        if (flat == null || key == null) return null;
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) return e.getValue();
//        }
//        String keyIdxNorm = key.replaceAll("\\[\\d+\\]", "[]");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            String ek = e.getKey(); if (ek == null) continue;
//            String ekNorm = ek.replaceAll("\\[\\d+\\]", "[]");
//            if (ekNorm.equalsIgnoreCase(keyIdxNorm)) return e.getValue();
//        }
//        String underscore = camelToUnderscore(key);
//        if (underscore != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(underscore)) return e.getValue();
//        String camel = underscoreToCamel(key);
//        if (camel != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(camel)) return e.getValue();
//
//        String token = key.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() == null) continue;
//            if (e.getKey().toLowerCase().contains(token.toLowerCase())) return e.getValue();
//        }
//        return null;
//    }
//
//    private static String normalizeKeyForMatching(String k) { if (k == null) return null; return k.replaceAll("\\[\\d+\\]", "[]"); }
//
//    private static String camelToUnderscore(String s) {
//        if (s == null) return null;
//        StringBuilder out = new StringBuilder();
//        for (char c : s.toCharArray()) {
//            if (Character.isUpperCase(c)) out.append('_').append(Character.toLowerCase(c));
//            else out.append(c);
//        }
//        return out.toString();
//    }
//
//    private static String underscoreToCamel(String s) {
//        if (s == null) return null;
//        StringBuilder sb = new StringBuilder();
//        boolean upper = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') upper = true;
//            else { sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c)); upper = false; }
//        }
//        return sb.toString();
//    }
//
//    private static String toScalarString(Object o) {
//        if (o == null) return null;
//        try {
//            if (o instanceof String) return (String) o;
//            if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
//            if (o instanceof List || o instanceof Map) return OBJECT_MAPPER.writeValueAsString(o);
//            return String.valueOf(o);
//        } catch (Exception e) { return String.valueOf(o); }
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
//        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(); } catch (DateTimeParseException ignored) {}
//        try { Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class); if (d != null) return d.toInstant(); } catch (Exception ignored) {}
//        return null;
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
//        return diff <= millisTolerance;
//    }
//
//    private static String htmlEscape(String s) {
//        if (s == null) return "&lt;null&gt;";
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\r", "");
//    }
//
//    private static String safe(String s) { return s == null ? "<null>" : s; }
//
//    private static String prettyFormatKeys(Set<String> keys) {
//        if (keys == null || keys.isEmpty()) return "[]";
//        return keys.stream().collect(Collectors.joining(", "));
//    }
//
//    private static String requireSafeIdentifier(String raw) {
//        if (raw == null) throw new IllegalArgumentException("Identifier is null");
//        String cleaned = raw.replaceAll("[^A-Za-z0-9_]", "");
//        if (cleaned.isEmpty()) throw new IllegalArgumentException("Identifier '" + raw + "' is not a valid SQL identifier after sanitization");
//        return cleaned;
//    }
//
//    /**
//     * Build JSON panel HTML with strict-ish key highlighting (quoted key name highlight + token highlight).
//     * fontSizePx controls visible text size.
//     */
//    private static String buildJsonPanelHtml(String json, String key, String title, int fontSizePx) {
//        String body;
//        if (json == null) json = "<null>";
//        try {
//            Object parsed = OBJECT_MAPPER.readValue(json, Object.class);
//            body = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
//        } catch (Exception e) {
//            body = json;
//        }
//
//        String escaped = htmlEscape(body);
//
//        // highlight quoted property names: "key" :
//        try {
//            Pattern p = Pattern.compile("(?i)(\"\\s*" + Pattern.quote(key) + "\\s*\"\\s*:\\s*)");
//            Matcher m = p.matcher(escaped);
//            StringBuffer sb = new StringBuffer();
//            while (m.find()) {
//                m.appendReplacement(sb, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m.group(1)) + "</mark>");
//            }
//            m.appendTail(sb);
//            escaped = sb.toString();
//        } catch (Exception ignored) {}
//
//        // highlight bare token occurrences
//        try {
//            Pattern p2 = Pattern.compile("(?i)\\b" + Pattern.quote(key) + "\\b");
//            Matcher m2 = p2.matcher(escaped);
//            StringBuffer sb2 = new StringBuffer();
//            while (m2.find()) {
//                m2.appendReplacement(sb2, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m2.group()) + "</mark>");
//            }
//            m2.appendTail(sb2);
//            escaped = sb2.toString();
//        } catch (Exception ignored) {}
//
//        String html = "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:8px} mark{padding:0 3px;border-radius:3px} .title{font-weight:700;margin-bottom:6px;font-size:" + (fontSizePx+2) + "px}</style></head><body>"
//                + "<div class='title'>" + htmlEscape(title) + "</div>"
//                + "<pre style='font-size:" + fontSizePx + "px;line-height:1.15;'>" + escaped + "</pre></body></html>";
//        return html;
//    }
//
//    /**
//     * Build DB HTML table from Map and highlight matching column if any.
//     * preserveRawOrder: if true keep LinkedHashMap ordering
//     */
//    private static String buildDbHtml(Map<String, Object> map, String keyToHighlight, String title, boolean preserveRawOrder, int fontSizePx) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:8px} table{border-collapse:collapse;width:100%} td,th{border:1px solid #ddd;padding:6px;text-align:left;font-size:")
//                .append(fontSizePx).append("px} th{background:#f2f2f2;font-weight:700} .hl{background:#fff3cd}</style></head><body>");
//        sb.append("<div style='font-weight:700;margin-bottom:6px;font-size:").append(fontSizePx+2).append("px;'>").append(htmlEscape(title)).append("</div>");
//        sb.append("<table><tr><th>Column</th><th>Value</th></tr>");
//        if (map == null || map.isEmpty()) {
//            sb.append("<tr><td colspan='2'>&lt;no data&gt;</td></tr>");
//        } else {
//            if (preserveRawOrder && map instanceof LinkedHashMap) {
//                for (Map.Entry<String, Object> e : map.entrySet()) {
//                    String col = e.getKey();
//                    String val = toScalarString(e.getValue());
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            } else {
//                List<String> cols = new ArrayList<>(map.keySet());
//                Collections.sort(cols, String.CASE_INSENSITIVE_ORDER);
//                for (String col : cols) {
//                    String val = toScalarString(map.get(col));
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            }
//        }
//        sb.append("</table></body></html>");
//        return sb.toString();
//    }
//
//    /**
//     * Headless chrome render of arbitrary HTML -> base64 PNG.
//     * width x height control, viewportWidth controls window-size width argument.
//     */
//    private static String captureHtmlHeadlessBase64(String html, int width, int height, int viewportWidth) {
//        WebDriver driver = null;
//        java.nio.file.Path tmp = null;
//        try {
//            tmp = java.nio.file.Files.createTempFile("av_val_html_", ".html");
//            java.nio.file.Files.write(tmp, html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
//
//            System.setProperty("webdriver.chrome.silentOutput", "true");
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless=new");
//            options.addArguments("--disable-gpu");
//            options.addArguments("--no-sandbox");
//            options.addArguments("--window-size=" + viewportWidth + "," + height);
//            options.addArguments("--hide-scrollbars");
//            driver = new ChromeDriver(options);
//
//            driver.get(tmp.toUri().toString());
//            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
//
//            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//            return java.util.Base64.getEncoder().encodeToString(png);
//        } catch (Exception e) {
//            ExtentTest log = log();
//            if (log != null) log.warning("⚠️ captureHtmlHeadlessBase64 failed: " + e.getMessage());
//            return null;
//        } finally {
//            try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
//            try { if (tmp != null) java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignored) {}
//        }
//    }
//
//    private static String findDbColumnDisplayName(Map<String,Object> dbRawOrdered, String normalizedKey) {
//        if (dbRawOrdered == null || dbRawOrdered.isEmpty()) return "<no row>";
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.equalsIgnoreCase(normalizedKey) || underscoreToCamel(col).equalsIgnoreCase(normalizedKey) || col.replaceAll("\\s","").equalsIgnoreCase(normalizedKey)) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        String token = normalizedKey == null ? "" : normalizedKey.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.toLowerCase().contains(token.toLowerCase())) return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//        }
//        return "<no col>";
//    }
//}

//package validators;
//
//import com.aventstack.extentreports.ExtentTest;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import reports.ExtentCucumberListener;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//
//import java.nio.file.Path;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import java.sql.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class ApiValidator {
//
//    // --- CONFIG: change to your environment ---
//    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
//    private static final String JDBC_USER = "root";
//    private static final String JDBC_PASS = "dhi123";
//    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    /**
//     * Validate keys and produce a table with inline thumbnails per key (Request | Response | ES | DB).
//     * Clicking a thumbnail opens a full-size PNG stored as a temp file (file://).
//     */
//    public static void validateRequestKeysWithPersistentFullScreenshots(
//            Response response,
//            String requestJson,
//            int recordId,
//            String esIndexName,
//            String dbTableName,
//            boolean requireEsPresence,
//            long timestampToleranceMs,
//            boolean captureAllKeys
//    ) {
//        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
//        if (t != null) t.info("🔎 Cross-layer validation (thumbnails -> persistent PNGs) started for id=" + recordId);
//
//        // flatten request & response
//        Map<String, Object> reqFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(requestJson), "", reqFlat);
//
//        Map<String, Object> respFlat = new LinkedHashMap<>();
//        flattenJson(safeJsonToMap(response == null ? null : response.getBody().asString()), "", respFlat);
//
//        // fetch ES _source
//        Map<String, Object> esSource = Collections.emptyMap();
//        try {
//            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
//                    esIndexName, recordId);
//            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
//            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
//            if (hits != null && !hits.isEmpty()) {
//                Object _src = hits.get(0).get("_source");
//                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
//            } else if (requireEsPresence && t != null) {
//                t.warning("⚠️ ES returned no hits for id=" + recordId + " (requireEsPresence=true)");
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
//        }
//        Map<String, Object> esFlat = new LinkedHashMap<>();
//        flattenJson(esSource, "", esFlat);
//
//        // fetch DB row
//        Map<String, Object> dbFlat = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>();
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
//            ps.setInt(1, recordId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    ResultSetMetaData meta = rs.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String col = meta.getColumnName(i);
//                        Object val = rs.getObject(i);
//                        dbFlat.put(col.toLowerCase(), val);
//                        dbFlat.put(underscoreToCamel(col).toLowerCase(), val);
//                        dbRawOrdered.put(col, val);
//                    }
//                } else if (t != null) {
//                    t.warning("⚠️ No DB row found for id=" + recordId + " (table=" + dbTableName + ")");
//                }
//            }
//        } catch (Exception ex) {
//            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
//        }
//
//        // include [] markers
//        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
//        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
//        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());
//
//        // gather keys-to-check (union)
//        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
//        Stream.of(reqFlat.keySet(), respFlat.keySet(), esFlat.keySet(), dbFlat.keySet())
//                .forEach(set -> set.forEach(k -> { if (k != null) keysToCheck.add(k); }));
//
//        // add normalized variants
//        List<String> additions = new ArrayList<>();
//        for (String k : new ArrayList<>(keysToCheck)) {
//            additions.add(normalizeKeyForMatching(k));
//            String camel = underscoreToCamel(k);
//            if (camel != null && !camel.equalsIgnoreCase(k)) additions.add(camel);
//            String unders = camelToUnderscore(k);
//            if (unders != null && !unders.equalsIgnoreCase(k)) additions.add(unders);
//        }
//        additions.forEach(a -> { if (a != null) keysToCheck.add(a); });
//
//        if (t != null) t.info("🔍 Keys to check: " + prettyFormatKeys(keysToCheck));
//
//        List<String> failures = new ArrayList<>();
//        List<String> failingKeys = new ArrayList<>();
//
//        // Build HTML summary table
//        StringBuilder html = new StringBuilder();
//        html.append("<html><head><meta charset='utf-8'><style>")
//                .append("body{font-family:Arial,Helvetica,sans-serif;padding:12px;background:#fff}")
//                .append("table{border-collapse:collapse;width:100%;table-layout:fixed} th,td{border:1px solid #ddd;padding:8px;text-align:left;vertical-align:top;word-wrap:break-word}")
//                .append("th{background:#f6f6f6;font-weight:700;font-size:14px} td.mono{font-family:monospace;font-size:12px}")
//                .append(".pass{background:#d4edda}.nullEs{background:#fff3cd}.fail{background:#f8d7da}")
//                .append(".thumb{width:200px;height:auto;border:1px solid #ccc;display:block;margin:4px auto;cursor:pointer} .thumb-wrap{max-width:220px;overflow:hidden;text-align:center}")
//                .append("</style></head><body>");
//        html.append("<h3>Cross-layer Comparison (ID: ").append(recordId).append(")</h3>");
//        html.append("<table><tr><th style='width:12%'>Key</th><th style='width:19%'>Request</th><th style='width:19%'>Response</th><th style='width:19%'>Elasticsearch</th><th style='width:19%'>Database</th><th style='width:12%'>Status</th></tr>");
//
//        for (String rawKey : keysToCheck) {
//            String normKey = rawKey == null ? null : rawKey.toLowerCase();
//            Object qObj = findValueCaseInsensitive(reqFlat, normKey);
//            Object rObj = findValueCaseInsensitive(respFlat, normKey);
//            Object eObj = findValueCaseInsensitive(esFlat, normKey);
//            Object dObj = findValueCaseInsensitive(dbFlat, normKey);
//
//            String qStr = toScalarString(qObj);
//            String rStr = toScalarString(rObj);
//            String eStr = toScalarString(eObj);
//            String dStr = toScalarString(dObj);
//
//            boolean missing = false;
//            boolean mismatch = false;
//
//            // Missing checks
//            if (rStr == null) { missing = true; failures.add("Missing in Response: " + rawKey); }
//            if (dStr == null) { missing = true; failures.add("Missing in DB: " + rawKey); }
//            if (requireEsPresence && eStr == null) { missing = true; failures.add("Missing in ES: " + rawKey); }
//
//            // Compare values
//            if (!missing && qStr != null) {
//                Instant iq = parseInstantOrNull(qStr);
//                Instant ir = parseInstantOrNull(rStr);
//                Instant ie = parseInstantOrNull(eStr);
//                Instant idb = parseInstantOrNull(dStr);
//                if (iq != null && ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(iq, ir, timestampToleranceMs)
//                            && instantEqualWithin(iq, idb, timestampToleranceMs)
//                            && (!requireEsPresence || instantEqualWithin(iq, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch for: " + rawKey); }
//                } else {
//                    boolean allMatch = qStr.trim().equalsIgnoreCase(rStr.trim())
//                            && qStr.trim().equalsIgnoreCase(dStr.trim())
//                            && (!requireEsPresence || qStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim())));
//                    if (!allMatch) { mismatch = true; failures.add("Value mismatch for: " + rawKey); }
//                }
//            } else if (!missing && qStr == null) {
//                Instant ir = parseInstantOrNull(rStr);
//                Instant idb = parseInstantOrNull(dStr);
//                Instant ie = parseInstantOrNull(eStr);
//                if (ir != null && idb != null && (!requireEsPresence || ie != null)) {
//                    boolean timesOk = instantEqualWithin(ir, idb, timestampToleranceMs) && (!requireEsPresence || instantEqualWithin(ir, ie, timestampToleranceMs));
//                    if (!timesOk) { mismatch = true; failures.add("Timestamp mismatch (no-request) for: " + rawKey); }
//                } else {
//                    boolean respDbMatch = rStr.trim().equalsIgnoreCase(dStr.trim());
//                    boolean respEsMatch = !requireEsPresence || rStr.trim().equalsIgnoreCase((eStr == null ? "" : eStr.trim()));
//                    if (!respDbMatch || !respEsMatch) { mismatch = true; failures.add("Value mismatch (no-request) for: " + rawKey); }
//                }
//            }
//
//            String status = "PASS";
//            String rowClass = "pass";
//            if (missing) {
//                if (requireEsPresence && eStr == null && rStr != null && dStr != null) {
//                    status = "NULL_IN_ES";
//                    rowClass = "nullEs";
//                } else {
//                    status = "MISSING";
//                    rowClass = "fail";
//                }
//            } else if (mismatch) {
//                status = "VALUE_MISMATCH";
//                rowClass = "fail";
//            }
//
//            if (!"PASS".equals(status)) failingKeys.add(rawKey);
//
//            boolean doCapture = captureAllKeys || !"PASS".equals(status);
//
//            // will store persistent files and base64 thumbnails
//            String reqThumbBase64 = null, respThumbBase64 = null, esThumbBase64 = null, dbThumbBase64 = null;
//            Path reqFullFile = null, respFullFile = null, esFullFile = null, dbFullFile = null;
//
//            if (doCapture) {
//                try {
//                    // build HTML panels (compact font for capturing many keys)
//                    String reqPanel = buildJsonPanelHtml(requestJson == null ? "<no request>" : requestJson, rawKey, "Request", 10);
//                    String respPanel = buildJsonPanelHtml(response == null ? "<no response>" : response.getBody().asString(), rawKey, "Response", 10);
//                    String esPanel = buildDbHtml(esSource == null ? Collections.emptyMap() : esSource, rawKey, "Elasticsearch", false, 12);
//                    String dbPanel = buildDbHtml(dbRawOrdered == null ? Collections.emptyMap() : dbRawOrdered, rawKey, "Database (raw)", true, 12);
//
//                    // heuristic to compute full capture height (based on line count of pretty JSON)
//                    int reqHeight = computeHeightForHtml(reqPanel, 14, 20);
//                    int respHeight = computeHeightForHtml(respPanel, 14, 20);
//                    int esHeight = computeHeightForHtml(esPanel, 14, 18);
//                    int dbHeight = computeHeightForHtml(dbPanel, 14, 18);
//
//                    // capture persistent full PNG files (large height ensures entire content)
//                    reqFullFile = captureHtmlToPngFile(reqPanel, 1200, reqHeight, 1200);
//                    respFullFile = captureHtmlToPngFile(respPanel, 1200, respHeight, 1200);
//                    esFullFile = captureHtmlToPngFile(esPanel, 1200, esHeight, 1200);
//                    dbFullFile = captureHtmlToPngFile(dbPanel, 1200, dbHeight, 1200);
//
//                    // generate base64 thumbnails from full files (fast read)
//                    if (reqFullFile != null && Files.exists(reqFullFile)) reqThumbBase64 = base64FromFile(reqFullFile);
//                    if (respFullFile != null && Files.exists(respFullFile)) respThumbBase64 = base64FromFile(respFullFile);
//                    if (esFullFile != null && Files.exists(esFullFile)) esThumbBase64 = base64FromFile(esFullFile);
//                    if (dbFullFile != null && Files.exists(dbFullFile)) dbThumbBase64 = base64FromFile(dbFullFile);
//
//                } catch (Exception ex) {
//                    if (t != null) t.warning("⚠️ Screenshot capture failed for key " + rawKey + ": " + ex.getMessage());
//                }
//            }
//
//            String dbDisplay = findDbColumnDisplayName(dbRawOrdered, normKey);
//
//            // append row
//            html.append("<tr class='").append(rowClass).append("'>");
//
//            html.append("<td class='mono'>").append(htmlEscape(rawKey)).append("</td>");
//
//            // Request cell: clickable to full file (if file exists) else show thumbnail or text
//            html.append("<td>");
//            if (reqFullFile != null && Files.exists(reqFullFile)) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='file://").append(reqFullFile.toAbsolutePath().toString())
//                        .append("'><img class='thumb' alt='req-thumb' src='data:image/png;base64,").append(safeBase64(reqThumbBase64)).append("'/></a></div>");
//            } else if (reqThumbBase64 != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' alt='req-thumb' src='data:image/png;base64,").append(reqThumbBase64).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(qStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // Response
//            html.append("<td>");
//            if (respFullFile != null && Files.exists(respFullFile)) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='file://").append(respFullFile.toAbsolutePath().toString())
//                        .append("'><img class='thumb' alt='resp-thumb' src='data:image/png;base64,").append(safeBase64(respThumbBase64)).append("'/></a></div>");
//            } else if (respThumbBase64 != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' alt='resp-thumb' src='data:image/png;base64,").append(respThumbBase64).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(rStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // ES
//            html.append("<td>");
//            if (esFullFile != null && Files.exists(esFullFile)) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='file://").append(esFullFile.toAbsolutePath().toString())
//                        .append("'><img class='thumb' alt='es-thumb' src='data:image/png;base64,").append(safeBase64(esThumbBase64)).append("'/></a></div>");
//            } else if (esThumbBase64 != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' alt='es-thumb' src='data:image/png;base64,").append(esThumbBase64).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(eStr))).append("</div>");
//            }
//            html.append("</td>");
//
//            // DB
//            html.append("<td>");
//            if (dbFullFile != null && Files.exists(dbFullFile)) {
//                html.append("<div class='thumb-wrap'><a target='_blank' rel='noopener' href='file://").append(dbFullFile.toAbsolutePath().toString())
//                        .append("'><img class='thumb' alt='db-thumb' src='data:image/png;base64,").append(safeBase64(dbThumbBase64)).append("'/></a></div>");
//            } else if (dbThumbBase64 != null) {
//                html.append("<div class='thumb-wrap'><img class='thumb' alt='db-thumb' src='data:image/png;base64,").append(dbThumbBase64).append("'/></div>");
//            } else {
//                html.append("<div class='mono'>").append(htmlEscape(safe(dbDisplay))).append("</div>");
//            }
//            html.append("</td>");
//
//            // status
//            html.append("<td>").append(status).append("</td>");
//            html.append("</tr>");
//        } // end keys
//
//        html.append("</table></body></html>");
//
//        // attach summary HTML to Extent (as HTML string)
//        if (t != null) t.info("📊 Cross-layer summary with persistent screenshots:").info(html.toString());
//
//        if (!failures.isEmpty()) {
//            if (t != null) t.fail("❌ Cross-layer validation failed for id=" + recordId + ". Failing keys - see screenshots in report.");
//            throw new AssertionError("Cross-layer validation failed. Failing keys: " + failures);
//        } else {
//            if (t != null) t.pass("✅ Cross-layer validation passed for id=" + recordId);
//        }
//    }
//
//    // ----------------------- Utilities / helpers -----------------------
//
//    private static ExtentTest log() { return ExtentCucumberListener.getCurrentScenario(); }
//
//    private static Map<String, Object> safeJsonToMap(String json) {
//        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
//        try { return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {}); }
//        catch (Exception e) { return Collections.emptyMap(); }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
//        if (node == null) return;
//        for (Map.Entry<String, Object> e : node.entrySet()) {
//            String key = (prefix == null || prefix.isEmpty()) ? e.getKey() : (prefix + "." + e.getKey());
//            Object value = e.getValue();
//            if (value instanceof Map) flattenJson((Map<String, Object>) value, key, out);
//            else if (value instanceof List) {
//                List<?> list = (List<?>) value;
//                out.put(key + "[]", list);
//                for (int i = 0; i < list.size(); i++) {
//                    Object item = list.get(i);
//                    if (item instanceof Map) flattenJson((Map<String, Object>) item, key + "[" + i + "]", out);
//                    else out.put(key + "[" + i + "]", item);
//                }
//            } else out.put(key, value);
//        }
//    }
//
//    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
//        if (rawJson == null || keySet == null) return;
//        try {
//            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE);
//            Matcher m = p.matcher(rawJson);
//            while (m.find()) keySet.add(m.group(1) + "[]");
//        } catch (Exception ignored) {}
//    }
//
//    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
//        if (flat == null || key == null) return null;
//        for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) return e.getValue();
//        String keyIdxNorm = key.replaceAll("\\[\\d+\\]", "[]");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            String ek = e.getKey(); if (ek == null) continue;
//            if (ek.replaceAll("\\[\\d+\\]", "[]").equalsIgnoreCase(keyIdxNorm)) return e.getValue();
//        }
//        String underscore = camelToUnderscore(key);
//        if (underscore != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(underscore)) return e.getValue();
//        String camel = underscoreToCamel(key);
//        if (camel != null) for (Map.Entry<String, Object> e : flat.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(camel)) return e.getValue();
//        String token = key.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (Map.Entry<String, Object> e : flat.entrySet()) {
//            if (e.getKey() == null) continue;
//            if (e.getKey().toLowerCase().contains(token.toLowerCase())) return e.getValue();
//        }
//        return null;
//    }
//
//    private static String normalizeKeyForMatching(String k) { if (k == null) return null; return k.replaceAll("\\[\\d+\\]", "[]"); }
//
//    private static String camelToUnderscore(String s) {
//        if (s == null) return null;
//        StringBuilder out = new StringBuilder();
//        for (char c : s.toCharArray()) {
//            if (Character.isUpperCase(c)) out.append('_').append(Character.toLowerCase(c)); else out.append(c);
//        }
//        return out.toString();
//    }
//
//    private static String underscoreToCamel(String s) {
//        if (s == null) return null;
//        StringBuilder sb = new StringBuilder();
//        boolean upper = false;
//        for (char c : s.toCharArray()) {
//            if (c == '_' || c == '-') upper = true;
//            else { sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c)); upper = false; }
//        }
//        return sb.toString();
//    }
//
//    private static String toScalarString(Object o) {
//        if (o == null) return null;
//        try {
//            if (o instanceof String) return (String) o;
//            if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
//            if (o instanceof List || o instanceof Map) return OBJECT_MAPPER.writeValueAsString(o);
//            return String.valueOf(o);
//        } catch (Exception e) { return String.valueOf(o); }
//    }
//
//    private static Instant parseInstantOrNull(String s) {
//        if (s == null) return null;
//        try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
//        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(); } catch (DateTimeParseException ignored) {}
//        try { Date d = OBJECT_MAPPER.readValue("\"" + s + "\"", Date.class); if (d != null) return d.toInstant(); } catch (Exception ignored) {}
//        return null;
//    }
//
//    private static boolean instantEqualWithin(Instant a, Instant b, long millisTolerance) {
//        if (a == null || b == null) return false;
//        long diff = Math.abs(a.toEpochMilli() - b.toEpochMilli());
//        return diff <= millisTolerance;
//    }
//
//    private static String htmlEscape(String s) {
//        if (s == null) return "&lt;null&gt;";
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\r", "");
//    }
//
//    private static String safe(String s) { return s == null ? "<null>" : s; }
//
//    private static String prettyFormatKeys(Set<String> keys) {
//        if (keys == null || keys.isEmpty()) return "[]";
//        return keys.stream().collect(Collectors.joining(", "));
//    }
//
//    private static String requireSafeIdentifier(String raw) {
//        if (raw == null) throw new IllegalArgumentException("Identifier is null");
//        String cleaned = raw.replaceAll("[^A-Za-z0-9_]", "");
//        if (cleaned.isEmpty()) throw new IllegalArgumentException("Identifier '" + raw + "' is not a valid SQL identifier after sanitization");
//        return cleaned;
//    }
//
//    /**
//     * Build JSON panel HTML with strict-ish key highlighting (quoted key name highlight + token highlight).
//     */
//    private static String buildJsonPanelHtml(String json, String key, String title, int fontSizePx) {
//        String body;
//        if (json == null) json = "<null>";
//        try {
//            Object parsed = OBJECT_MAPPER.readValue(json, Object.class);
//            body = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
//        } catch (Exception e) {
//            body = json;
//        }
//
//        String escaped = htmlEscape(body);
//
//        // highlight quoted property names: "key":
//        try {
//            Pattern p = Pattern.compile("(?i)(\"\\s*" + Pattern.quote(key) + "\\s*\"\\s*:\\s*)");
//            Matcher m = p.matcher(escaped);
//            StringBuffer sb = new StringBuffer();
//            while (m.find()) {
//                m.appendReplacement(sb, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m.group(1)) + "</mark>");
//            }
//            m.appendTail(sb);
//            escaped = sb.toString();
//        } catch (Exception ignored) {}
//
//        // highlight token
//        try {
//            Pattern p2 = Pattern.compile("(?i)\\b" + Pattern.quote(key) + "\\b");
//            Matcher m2 = p2.matcher(escaped);
//            StringBuffer sb2 = new StringBuffer();
//            while (m2.find()) {
//                m2.appendReplacement(sb2, "<mark style='background:#fffd80;color:#000;'>" + Matcher.quoteReplacement(m2.group()) + "</mark>");
//            }
//            m2.appendTail(sb2);
//            escaped = sb2.toString();
//        } catch (Exception ignored) {}
//
//        return "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:8px} mark{padding:0 3px;border-radius:3px} .title{font-weight:700;margin-bottom:6px;font-size:" + (fontSizePx+2) + "px}</style></head><body>"
//                + "<div class='title'>" + htmlEscape(title) + "</div>"
//                + "<pre style='font-size:" + fontSizePx + "px;line-height:1.15;'>" + escaped + "</pre></body></html>";
//    }
//
//    /**
//     * Build DB HTML table from Map and highlight matching column if any.
//     */
//    private static String buildDbHtml(Map<String, Object> map, String keyToHighlight, String title, boolean preserveRawOrder, int fontSizePx) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:8px} table{border-collapse:collapse;width:100%} td,th{border:1px solid #ddd;padding:6px;text-align:left;font-size:")
//                .append(fontSizePx).append("px} th{background:#f2f2f2;font-weight:700} .hl{background:#fff3cd}</style></head><body>");
//        sb.append("<div style='font-weight:700;margin-bottom:6px;font-size:").append(fontSizePx+2).append("px;'>").append(htmlEscape(title)).append("</div>");
//        sb.append("<table><tr><th>Column</th><th>Value</th></tr>");
//        if (map == null || map.isEmpty()) {
//            sb.append("<tr><td colspan='2'>&lt;no data&gt;</td></tr>");
//        } else {
//            if (preserveRawOrder && map instanceof LinkedHashMap) {
//                for (Map.Entry<String, Object> e : map.entrySet()) {
//                    String col = e.getKey();
//                    String val = toScalarString(e.getValue());
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            } else {
//                List<String> cols = new ArrayList<>(map.keySet());
//                Collections.sort(cols, String.CASE_INSENSITIVE_ORDER);
//                for (String col : cols) {
//                    String val = toScalarString(map.get(col));
//                    boolean highlight = col != null && col.equalsIgnoreCase(keyToHighlight);
//                    sb.append(highlight ? "<tr class='hl'>" : "<tr>");
//                    sb.append("<td>").append(htmlEscape(col)).append("</td>");
//                    sb.append("<td>").append(htmlEscape(safe(val))).append("</td>");
//                    sb.append("</tr>");
//                }
//            }
//        }
//        sb.append("</table></body></html>");
//        return sb.toString();
//    }
//
//    /**
//     * Capture rendered HTML to a PNG file (full page) and return Path.
//     * This writes a temp PNG file and returns its path (deletedOnExit()).
//     */
//    private static Path captureHtmlToPngFile(String html, int viewportWidth, int viewportHeight, int viewportWidthArg) throws Exception {
//        WebDriver driver = null;
//        Path tmpHtml = null;
//        Path tmpPng = null;
//        try {
//            tmpHtml = Files.createTempFile("av_val_html_", ".html");
//            Files.write(tmpHtml, html.getBytes(java.nio.charset.StandardCharsets.UTF_8), StandardOpenOption.WRITE);
//
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless=new");
//            options.addArguments("--disable-gpu");
//            options.addArguments("--no-sandbox");
//            options.addArguments("--window-size=" + viewportWidthArg + "," + viewportHeight);
//            options.addArguments("--hide-scrollbars");
//            driver = new ChromeDriver(options);
//
//            driver.get(tmpHtml.toUri().toString());
//            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
//
//            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//
//            tmpPng = Files.createTempFile("av_val_img_", ".png");
//            Files.write(tmpPng, png, StandardOpenOption.WRITE);
//            tmpPng.toFile().deleteOnExit();
//            tmpHtml.toFile().deleteOnExit();
//            return tmpPng;
//        } finally {
//            try { if (driver != null) driver.quit(); } catch (Exception ignored) {}
//            // don't delete files here - we want them for clicking from report; JVM will remove on exit
//        }
//    }
//
//    private static String base64FromFile(Path p) {
//        try {
//            byte[] bytes = Files.readAllBytes(p);
//            return java.util.Base64.getEncoder().encodeToString(bytes);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private static String safeBase64(String s) { return s == null ? "" : s; }
//
//    /**
//     * Compute a viewport height heuristic based on number of <br/> in HTML (rough lines).
//     * fontPx & lineHeightPx influence computation.
//     */
//    private static int computeHeightForHtml(String html, int fontPx, int lineHeightPx) {
//        if (html == null) return 800;
//        int lines = Math.max(10, html.split("<br/>").length + html.split("\n").length);
//        int estimated = Math.min(15000, Math.max(600, lines * lineHeightPx + 200));
//        return estimated;
//    }
//
//    private static String findDbColumnDisplayName(Map<String,Object> dbRawOrdered, String normalizedKey) {
//        if (dbRawOrdered == null || dbRawOrdered.isEmpty()) return "<no row>";
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.equalsIgnoreCase(normalizedKey) || underscoreToCamel(col).equalsIgnoreCase(normalizedKey) || col.replaceAll("\\s","").equalsIgnoreCase(normalizedKey)) {
//                return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//            }
//        }
//        String token = normalizedKey == null ? "" : normalizedKey.replaceAll("\\[\\d+\\]", "").replace("[]", "");
//        for (String col : dbRawOrdered.keySet()) {
//            if (col.toLowerCase().contains(token.toLowerCase())) return col + " = " + safe(toScalarString(dbRawOrdered.get(col)));
//        }
//        return "<no col>";
//    }
//}

package validators;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import reports.ExtentCucumberListener;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiValidator {

    private static final String JDBC_URL = "jdbc:mysql://10.10.2.45:3306";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "dhi123";
    private static final String DEFAULT_SCHEMA = "issue_persistence_piqa";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void validateRequestKeysWithSeparateScreenshotColumns(
            Response response,
            String requestJson,
            int recordId,
            String esIndexName,
            String dbTableName,
            boolean requireEsPresence,
            long timestampToleranceMs,
            boolean captureAllKeys
    ) {
        ExtentTest t = ExtentCucumberListener.getCurrentScenario();
        if (t != null)
            t.info("🔎 Cross-layer validation (separate screenshot columns - in-memory) started for id=" + recordId);

        Map<String, Object> reqFlat = new LinkedHashMap<>();
        flattenJson(safeJsonToMap(requestJson), "", reqFlat);

        Map<String, Object> respFlat = new LinkedHashMap<>();
        flattenJson(safeJsonToMap(response == null ? null : response.getBody().asString()), "", respFlat);

        // Fetch ES (optional)
        Map<String, Object> esSource = Collections.emptyMap();
        try {
            String esUrl = String.format("http://10.10.2.45:9200/piqa_issue-persistence_%s_index/_search?q=id:%d&size=1",
                    esIndexName, recordId);
            Response esResp = RestAssured.given().get(esUrl).then().extract().response();
            List<Map<String, Object>> hits = esResp.jsonPath().getList("hits.hits");
            if (hits != null && !hits.isEmpty()) {
                Object _src = hits.get(0).get("_source");
                if (_src instanceof Map) esSource = (Map<String, Object>) _src;
            } else if (requireEsPresence && t != null) {
                t.warning("⚠️ ES returned no hits for id=" + recordId);
            }
        } catch (Exception ex) {
            if (t != null) t.warning("⚠️ ES fetch failed: " + ex.getMessage());
        }
        Map<String, Object> esFlat = new LinkedHashMap<>();
        flattenJson(esSource, "", esFlat);

        // Fetch DB row
        Map<String, Object> dbFlat = new LinkedHashMap<>();
        LinkedHashMap<String, Object> dbRawOrdered = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM `" + DEFAULT_SCHEMA + "`.`" + requireSafeIdentifier(dbTableName) + "` WHERE id = ? LIMIT 1")) {
            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String col = meta.getColumnName(i);
                        Object v = rs.getObject(i);
                        dbFlat.put(col.toLowerCase(), v);
                        dbFlat.put(underscoreToCamel(col).toLowerCase(), v);
                        dbRawOrdered.put(col, v);
                    }
                } else if (t != null) {
                    t.warning("⚠️ No DB row found for id=" + recordId);
                }
            }
        } catch (Exception ex) {
            if (t != null) t.warning("⚠️ DB fetch failed: " + ex.getMessage());
        }

        includeArrayKeysExplicitly(requestJson, reqFlat.keySet());
        includeArrayKeysExplicitly(response == null ? null : response.getBody().asString(), respFlat.keySet());
        includeArrayKeysExplicitly(esSource == null ? null : esSource.toString(), esFlat.keySet());

        LinkedHashSet<String> keysToCheck = new LinkedHashSet<>();
        Stream.of(reqFlat.keySet(), respFlat.keySet(), esFlat.keySet(), dbFlat.keySet())
                .forEach(set -> set.forEach(k -> {
                    if (k != null) keysToCheck.add(k);
                }));

        if (t != null) t.info("🔍 Keys to check: " + prettyFormatKeys(keysToCheck));

        List<String> failures = new ArrayList<>();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='utf-8'><style>")
                .append("body{font-family:Arial;padding:12px;background:#fff}")
                .append("table{border-collapse:collapse;width:100%;table-layout:fixed}")
                .append("th,td{border:1px solid #ddd;padding:8px;vertical-align:top;word-wrap:break-word}")
                .append(".thumb{width:220px;height:auto;border:1px solid #ccc;cursor:pointer}")
                .append(".pass{background:#eaf6ec}.fail{background:#fdecec}.warn{background:#fff7e0}")
                .append("</style></head><body>");
        html.append("<h3>Cross-layer Comparison (ID: ").append(recordId).append(")</h3>");
        html.append("<table><tr><th>Key</th><th>Request</th><th>Response</th><th>Elasticsearch</th><th>Database</th><th>Status</th></tr>");

        for (String rawKey : keysToCheck) {
            String normKey = rawKey == null ? null : rawKey.toLowerCase();
            Object qObj = findValueCaseInsensitive(reqFlat, normKey);
            Object rObj = findValueCaseInsensitive(respFlat, normKey);
            Object eObj = findValueCaseInsensitive(esFlat, normKey);
            Object dObj = findValueCaseInsensitive(dbFlat, normKey);

            String qStr = toScalarString(qObj);
            String rStr = toScalarString(rObj);
            String eStr = toScalarString(eObj);
            String dStr = toScalarString(dObj);

            String status = "PASS";
            String rowClass = "pass";

            boolean doCapture = captureAllKeys || !"PASS".equals(status);
            String reqBase64 = null, respBase64 = null, esBase64 = null, dbBase64 = null;

            if (doCapture) {
                try {
                    String reqPanel = buildJsonPanelHtml(requestJson, rawKey, "Request", 11);
                    String respPanel = buildJsonPanelHtml(response == null ? "" : response.getBody().asString(), rawKey, "Response", 11);
                    String esPanel = buildDbHtml(esSource, rawKey, "Elasticsearch", false, 12);
                    String dbPanel = buildDbHtml(dbRawOrdered, rawKey, "Database", true, 12);

                    int reqH = computeHeightForHtml(reqPanel, 12, 20);
                    int respH = computeHeightForHtml(respPanel, 12, 20);
                    int esH = computeHeightForHtml(esPanel, 12, 18);
                    int dbH = computeHeightForHtml(dbPanel, 12, 18);

                    reqBase64 = Base64.getEncoder().encodeToString(captureHtmlToPngBytes(reqPanel, 1100, reqH, 1200));
                    respBase64 = Base64.getEncoder().encodeToString(captureHtmlToPngBytes(respPanel, 1100, respH, 1200));
                    esBase64 = Base64.getEncoder().encodeToString(captureHtmlToPngBytes(esPanel, 1100, esH, 1200));
                    dbBase64 = Base64.getEncoder().encodeToString(captureHtmlToPngBytes(dbPanel, 1100, dbH, 1200));
                } catch (Exception ex) {
                    if (t != null) t.warning("Screenshot capture failed for " + rawKey + ": " + ex.getMessage());
                }
            }

            html.append("<tr class='").append(rowClass).append("'><td>").append(htmlEscape(rawKey)).append("</td>");
            html.append(addImageCell(reqBase64, qStr));
            html.append(addImageCell(respBase64, rStr));
            html.append(addImageCell(esBase64, eStr));
            html.append(addImageCell(dbBase64, dStr));
            html.append("<td>").append(status).append("</td></tr>");
        }

        html.append("</table></body></html>");

        if (t != null) t.info("📊 Cross-layer summary (separate columns - in-memory):").info(html.toString());
    }

    private static String addImageCell(String base64, String text) {
        if (base64 != null)
            return "<td><a target='_blank' href='data:image/png;base64," + base64 + "'><img class='thumb' src='data:image/png;base64," + base64 + "'/></a></td>";
        return "<td><div>" + htmlEscape(text) + "</div></td>";
    }

    // 🔹 HTML-to-PNG (in-memory)
    private static byte[] captureHtmlToPngBytes(String html, int viewportWidth, int viewportHeight, int viewportWidthArg) throws Exception {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-gpu", "--window-size=" + viewportWidthArg + "," + viewportHeight);
            driver = new ChromeDriver(options);
            String base64Html = Base64.getEncoder().encodeToString(html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            driver.get("data:text/html;base64," + base64Html);
            Thread.sleep(600);
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // 🔹 JSON/DB panel renderers
    private static String buildJsonPanelHtml(String json, String key, String title, int fontSizePx) {
        if (json == null) json = "<null>";
        String body;
        try {
            Object parsed = OBJECT_MAPPER.readValue(json, Object.class);
            body = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
        } catch (Exception e) {
            body = json;
        }

        String escaped = htmlEscape(body);
        try {
            Pattern p = Pattern.compile("(?i)(\"\\s*" + Pattern.quote(key) + "\\s*\"\\s*:\\s*)");
            Matcher m = p.matcher(escaped);
            StringBuffer sb = new StringBuffer();
            while (m.find()) m.appendReplacement(sb, "<mark style='background:#fffd80'>" + m.group(1) + "</mark>");
            m.appendTail(sb);
            escaped = sb.toString();
        } catch (Exception ignored) {
        }

        return "<html><head><meta charset='utf-8'><style>body{font-family:monospace;white-space:pre-wrap;padding:8px;font-size:" + fontSizePx + "px}</style></head><body><b>" + title + "</b><br/>" + escaped + "</body></html>";
    }

    private static String buildDbHtml(Map<String, Object> map, String keyToHighlight, String title, boolean preserveRawOrder, int fontSizePx) {
        StringBuilder sb = new StringBuilder("<html><head><meta charset='utf-8'><style>body{font-family:Arial;font-size:")
                .append(fontSizePx)
                .append("px;} table{border-collapse:collapse;width:100%} th,td{border:1px solid #ccc;padding:5px}</style></head><body>")
                .append("<b>").append(title).append("</b><table><tr><th>Column</th><th>Value</th></tr>");
        if (map != null) {
            for (Map.Entry<String, Object> e : map.entrySet()) {
                boolean highlight = e.getKey().equalsIgnoreCase(keyToHighlight);
                sb.append("<tr").append(highlight ? " style='background:#fff3cd'" : "").append("><td>")
                        .append(htmlEscape(e.getKey())).append("</td><td>").append(htmlEscape(String.valueOf(e.getValue()))).append("</td></tr>");
            }
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    // 🔹 Helper utilities
    private static void flattenJson(Map<String, Object> node, String prefix, Map<String, Object> out) {
        if (node == null) return;
        for (Map.Entry<String, Object> e : node.entrySet()) {
            String key = prefix.isEmpty() ? e.getKey() : prefix + "." + e.getKey();
            Object v = e.getValue();
            if (v instanceof Map) flattenJson((Map<String, Object>) v, key, out);
            else out.put(key, v);
        }
    }

    private static void includeArrayKeysExplicitly(String rawJson, Set<String> keySet) {
        if (rawJson == null) return;
        Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[", Pattern.MULTILINE).matcher(rawJson);
        while (m.find()) keySet.add(m.group(1) + "[]");
    }

    private static Map<String, Object> safeJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyMap();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static Object findValueCaseInsensitive(Map<String, Object> flat, String key) {
        if (flat == null || key == null) return null;
        for (Map.Entry<String, Object> e : flat.entrySet())
            if (e.getKey().equalsIgnoreCase(key)) return e.getValue();
        return null;
    }

    private static String toScalarString(Object o) {
        if (o == null) return null;
        try {
            if (o instanceof String) return (String) o;
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

    private static String htmlEscape(String s) {
        if (s == null) return "&lt;null&gt;";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String prettyFormatKeys(Set<String> keys) {
        return String.join(", ", keys);
    }

    private static int computeHeightForHtml(String html, int fontPx, int lineHeightPx) {
        int lines = html.split("\n").length;
        return Math.min(15000, Math.max(600, lines * lineHeightPx + 200));
    }

    private static String underscoreToCamel(String s) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : s.toCharArray()) {
            if (c == '_' || c == '-') upper = true;
            else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    private static String requireSafeIdentifier(String raw) {
        return raw.replaceAll("[^A-Za-z0-9_]", "");
    }
}
