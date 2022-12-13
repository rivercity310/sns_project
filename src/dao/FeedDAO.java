package dao;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.ConnectionPool;

import javax.naming.NamingException;
import java.sql.*;

public class FeedDAO {
    public boolean insert(String jsonstr) throws NamingException, SQLException, ParseException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            synchronized (this) {
                conn = ConnectionPool.get();

                /* phase 1. add "no" property */
                String sql = "SELECT no FROM feed ORDER BY no DESC LIMIT 1";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                int max = !rs.next() ? 0 : rs.getInt("no");

                JSONParser parser = new JSONParser();
                JSONObject jsonobj = (JSONObject)parser.parse(jsonstr);
                jsonobj.put("no", max + 1);

                rs.close();
                pstmt.close();

                /* phase 2. add "user" property */
                String uid = jsonobj.get("id").toString();

                sql = "SELECT jsonstr FROM user WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, uid);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String usrstr = rs.getString("jsonstr");
                    JSONObject usrobj = (JSONObject)parser.parse(usrstr);
                    usrobj.remove("password");
                    usrobj.remove("ts");
                    jsonobj.put("user", usrobj);
                }

                pstmt.close();
                rs.close();


                /* phase 3. insert jsonobj to the table */
                sql = "INSERT INTO feed(no, id, jsonstr) VALUES(?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, max + 1);
                pstmt.setString(2, uid);
                pstmt.setString(3, jsonobj.toJSONString());

                int cnt = pstmt.executeUpdate();
                return cnt == 1;

            }

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public String getList() throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT jsonstr FROM feed ORDER BY no DESC";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            String str = "[";
            int cnt = 0;

            while (rs.next()) {
                if (cnt++ > 0) str += ", ";
                str += rs.getString("jsonstr");
            }

            return str + "]";
        }
        finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /* maxNo보다 작은 N개의 레코드를 반환해주는 함수 */
    public String getGroup(String frids, String maxNo) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT jsonstr FROM feed where id IN (" + frids + ")";
            if (maxNo != null)
                sql += " AND no < " + maxNo;
            sql += " ORDER BY no DESC LIMIT 3";


            conn = ConnectionPool.get();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            String str = "[";
            int cnt = 0;

            while (rs.next()) {
                if (cnt++ > 0) str += ", ";
                str += rs.getString("jsonstr");
            }

            return str + "]";
        }
        finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }
}
