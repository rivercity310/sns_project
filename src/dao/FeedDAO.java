package dao;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.ConnectionPool;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;

public class FeedDAO {
    public boolean insert(String jsonstr) throws NamingException, SQLException, ParseException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            synchronized (this) {
                conn = ConnectionPool.get();

                String sql = "SELECT no FROM feed ORDER BY no DESC LIMIT 1";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                int max = !rs.next() ? 0 : rs.getInt("no");
                pstmt.close();



                JSONObject jsonobj = (JSONObject)(new JSONParser()).parse(jsonstr);
                jsonobj.put("no", max + 1);

                sql = "INSERT INTO feed(no, id, jsonstr) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, max + 1);
                pstmt.setString(2, jsonobj.get("id").toString());
                pstmt.setString(3, jsonobj.toJSONString());

                int count = pstmt.executeUpdate();
                return count == 1;
            }

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public ArrayList<FeedObj> getList() throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM feed ORDER BY ts DESC";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ArrayList<FeedObj> feeds = new ArrayList<FeedObj>();
            while (rs.next()) {
                String id = rs.getString("id");
                String content = rs.getString("content");
                String ts = rs.getString("ts");
                String images = rs.getString("images");

                FeedObj feed = new FeedObj(id, content, ts, images);
                feeds.add(feed);
            }

            return feeds;
        }
        finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
