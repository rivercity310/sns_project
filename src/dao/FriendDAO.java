package dao;

import util.ConnectionPool;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
friend 테이블에 레코드를 삽입하고 추출하기 위한 멤버 함수들을 포함하는 DAO 클래스
*/
public class FriendDAO {
    public String insert(String uid, String frid) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id FROM friend WHERE id = ? AND frid = ?";

            conn = ConnectionPool.get();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uid);
            pstmt.setString(2, frid);

            rs = pstmt.executeQuery();
            if (rs.next()) return "EX";

            pstmt.close();

            sql = "INSERT INTO friend VALUES(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uid);
            pstmt.setString(2, frid);

            int count = pstmt.executeUpdate();
            return (count == 1) ? "OK" : "ER";

        }
        finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public String remove(String uid, String frid) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "DELETE FROM friend WHERE id = ? AND frid = ?";

            conn = ConnectionPool.get();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uid);
            pstmt.setString(2, frid);

            int count = pstmt.executeUpdate();
            return (count == 1) ? "OK" : "ER";

        }
        finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public String getList(String uid) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT frid FROM friend WHERE id = ?";

            conn = ConnectionPool.get();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uid);

            rs = pstmt.executeQuery();

            String str = "";
            int cnt = 0;

            while (rs.next()) {
                if (cnt++ > 0) str += ",";
                str += "\"" + rs.getString("frid") + "\"";
            }
            if (cnt == 0) return "[]";

            rs.close();
            pstmt.close();

            sql = "SELECT jsonstr FROM user WHERE id IN (" + str + ")";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            str = "[";
            cnt = 0;

            while (rs.next()) {
                if (cnt++ > 0) str += ", ";
                str += rs.getString("jsonstr");
            }
            str += "]";

            return str;
        }
        finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }
}
