package dao;

import util.ConnectionPool;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
1. Class.forName("com.mysql.cj.jdbc.Driver")
- Reflection(클래스 파일을 메모리에 올리는 기능) 이 과정을 통해 DriverManager 클래스에 Driver 객체가 등록된다.

2. Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysns", "host", "pw")
- 데이터베이스 네트워크 연결을 생성
- url 구성은 다음과 같다
    - jdbc:mysql : MySQL의 protocol
    - localhost : dbms-ip-address => 개발 단계에서는 localhost
    - 3306 : db-port
    - mysns : db-name

3. Statement stmt = conn.createStatement()
- Statement 클래스는 SQL문을 실행하기 위한 함수를 제공
    - executeQuery(sql) : SELECT 구문, 실행 결과 얻어진 테이블을 ResultSet 형태로 반환
    - executeUpdate(sql) : INSERT, DELETE, UPDATE 구문처리, 실행 결과 변경된 레코드의 수를 int 형식으로 반환

3 - 1. PreparedStatement pstmt = conn.prepareStatement()
- PreparedStatement의 setString(), setInt(), setBlob() 함수를 이용해 SQL문을 사용자 입력 값으로 생성가능
- executeUpdate() 함수 호출시 실행할 SQL을 따로 인자로 전달하지 않아도 됨

4. ResultSet rs = stmt.executeQuery("SELECT id, password FROM user")
- executeQuery() 함수를 통한 SQL 실행 결과는 2차원 테이블 형식으로 전달되는데 Java에서 ResultSet 클래스의 형태로 표현
- ResultSet은 2차원 테이블 형태의 결과 값에 대해 레코드 단위로 접근할 수 있도록 순회 함수를 제공(rs.next())
- 기타 함수 getString(String col), getInt(String col)

5. ResultSet, Statement, Connection 객체를 close() 함수를 이용해 모두 닫아주기
 */

public class UserDAO {
    public boolean insert(String uid, String upass, String uname) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO user(id, password, name) VALUES(?, ?, ?)";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, uid);
            stmt.setString(2, upass);
            stmt.setString(3, uname);

            int count = stmt.executeUpdate();

            return count == 1 ? true : false;

        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean exists(String uid) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id FROM user WHERE id = ?";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, uid);

            rs = stmt.executeQuery();

            return rs.next();

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean delete(String uid) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            String sql = "DELETE FROM user WHERE id = ?";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, uid);

            int count = stmt.executeUpdate();

            return count == 1 ? true : false;

        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public int login(String uid, String upass) throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, password FROM user WHERE id = ?";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, uid);

            rs = stmt.executeQuery();

            if (!rs.next()) return 1;
            if (!upass.equals(rs.getString("password"))) return 2;
            return 0;

        }
        finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public ArrayList<UserObj> getList() throws NamingException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM user ORDER BY ts DESC";

            conn = ConnectionPool.get();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ArrayList<UserObj> users = new ArrayList<UserObj>();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String ts = rs.getString("ts");

                users.add(new UserObj(id, name, ts));
            }

            return users;
        }
        finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
