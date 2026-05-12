package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DBUtils {

    private static final String URL = "jdbc:mysql://10.10.2.45:3306/issue_persistence_piqa";
    private static final String USER = "root";
    private static final String PASSWORD = "dhi123";

    // ✅ Connection method
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ✅ Fetch multiple columns
    public static Map<String, Object> getBehaviorDetailsById(int id) {
        Map<String, Object> data = new HashMap<>();

        try (Connection conn = getConnection()) {

            String query = "SELECT issueId, projectId, behaviorType FROM Behavior WHERE id = ? AND issueId IS NOT NULL AND deleted = 0;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                data.put("issueId", rs.getInt("issueId"));
//                data.put("projectId", rs.getInt("projectId"));
//                data.put("behaviorType", rs.getString("behaviorType"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // ✅ Optional: main method ONLY for testing
    public static void main(String[] args) {
        Map<String, Object> result = getBehaviorDetailsById(1);
        System.out.println(result);
    }
}