package util;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;

/*
Database Connection Pool
- 성능 개선을 위해 일정 개수의 Connection 객체를 미리 생성한 다음 사용자가 요청할 때마다 가용한 객체를 할당
- 톰캣 컨테이너에서 제공하며, javax.sql.DataSource 인터페이스를 통해 접근가능
- DataSource는 JNDI(Java Naming and Directory Interface)를 통해 호출 가능
- 데이터베이스 연동에 필요한 URL이나 커넥션 개수 등의 파라미터들은 context.xml에서 정의

Context ctx = new InitialContext()
DataSource ds = (DataSource)ctx.lookup("java:comp/env/")
Connection conn = ds.getConnection()
- JNDI에서는 InitialContext 클래스의 lookup() 함수를 통해 <Resource> 태그로 등록된 자원을 찾도록 지원
- 찾고자 하는 자원의 이름 앞에 "java:comp/env/"를 추가하여 lookup() 함수의 파라미터로 전달
- 반환값은 Object 클래스 형태로 전달되므로 캐스팅 필요

static
- lookup() 함수가 반복적으로 수행되지 않도록 get() 함수가 처음 호출될 때 한해서 함수가 실행되도록 강제
 */

public class ConnectionPool {
    private static DataSource _ds = null;

    public static Connection get() throws NamingException, SQLException {
        if (_ds == null)
            _ds = (DataSource)(new InitialContext()).lookup("java:comp/env/jdbc/mysns");

        return _ds.getConnection();
    }
}
