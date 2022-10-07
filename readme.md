# OOP II 개인 프로젝트

---
## [ JDBC 관련 함수 ]

#### 1. Class.forName("com.mysql.cj.jdbc.Driver")
- Reflection(클래스 파일을 메모리에 올리는 기능) 이 과정을 통해 DriverManager 클래스에 Driver 객체가 등록된다.


#### 2. Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysns", "host", "pw")
- 데이터베이스 네트워크 연결을 생성
- url 구성은 다음과 같다
    - jdbc:mysql : MySQL의 protocol
    - localhost : dbms-ip-address => 개발 단계에서는 localhost
    - 3306 : db-port
    - mysns : db-name


#### 3. Statement stmt = conn.createStatement()
- Statement 클래스는 SQL문을 실행하기 위한 함수를 제공
    - executeQuery(sql) : SELECT 구문, 실행 결과 얻어진 테이블을 ResultSet 형태로 반환
    - executeUpdate(sql) : INSERT, DELETE, UPDATE 구문처리, 실행 결과 변경된 레코드의 수를 int 형식으로 반환


#### 3 - 1. PreparedStatement pstmt = conn.prepareStatement()
- PreparedStatement의 setString(), setInt(), setBlob() 함수를 이용해 SQL문을 사용자 입력 값으로 생성가능
- executeUpdate() 함수 호출시 실행할 SQL을 따로 인자로 전달하지 않아도 됨


#### 4. ResultSet rs = stmt.executeQuery("SELECT id, password FROM user")
- executeQuery() 함수를 통한 SQL 실행 결과는 2차원 테이블 형식으로 전달되는데 Java에서 ResultSet 클래스의 형태로 표현
- ResultSet은 2차원 테이블 형태의 결과 값에 대해 레코드 단위로 접근할 수 있도록 순회 함수를 제공(rs.next())
- 기타 함수 getString(String col), getInt(String col)

#### 5. ResultSet, Statement, Connection 객체를 close() 함수를 이용해 모두 닫아주기

---

## [ Connection Pool ]

- 성능 개선을 위해 일정 개수의 Connection 객체를 미리 생성한 다음 사용자가 요청할 때마다 가용한 객체를 할당
- 톰캣 컨테이너에서 제공하며, javax.sql.DataSource 인터페이스를 통해 접근가능
- DataSource는 JNDI(Java Naming and Directory Interface)를 통해 호출 가능
- 데이터베이스 연동에 필요한 URL이나 커넥션 개수 등의 파라미터들은 context.xml에서 정의

### 구조
```java
Context ctx = new InitialContext();
DataSource ds = (DataSource)ctx.lookup("java:comp/env/");
Connection conn = ds.getConnection();
```
- JNDI에서는 InitialContext 클래스의 lookup() 함수를 통해 <Resource> 태그로 등록된 자원을 찾도록 지원
- 찾고자 하는 자원의 이름 앞에 "java:comp/env/"를 추가하여 lookup() 함수의 파라미터로 전달
- 반환값은 Object 클래스 형태로 전달되므로 캐스팅 필요

### static
- lookup() 함수가 반복적으로 수행되지 않도록 get() 함수가 처음 호출될 때 한해서 함수가 실행되도록 강제
