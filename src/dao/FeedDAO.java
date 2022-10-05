package dao;

import util.ConnectionPool;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;

public class FeedDAO {
    public boolean insert(String uid, String ucon, String uimages) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO feed(id, content, images) VALUES(?, ?, ?)";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, uid);
            stmt.setString(2, ucon);
            stmt.setString(3, uimages);

            int count = stmt.executeUpdate();
            return count == 1 ? true : false;

        }
        finally {
            if (stmt != null) stmt.close();
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
