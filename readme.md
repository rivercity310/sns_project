# OOP II 개인 프로젝트


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

---

## [ Data Access Object, DAO ]

### 1. DAO
- DB 접속 기능을 공유하기 위한 Java 클래스
- DB 접속 및 수정 코드를 DAO 클래스로 모두 이관시켜, DB 처리와 HTTP 통신 처리를 분리 => 모듈화
- 일반적으로 테이블당 하나의 DAO 클래스   
- 구조 예
```java
public class UserDAO {
        public boolean insert() { // To do Sth for making INSERT Query to DB }   
        public boolean delete() { // To do sth for making DELETE Query to DB }
}
```

---

## [ File Upload ]

### 1. enctype 속성값
- application/www-form-urlencoded (default)
  - 전송되는 입력 데이터를 URL 형식으로 표현
- multipart/form-data
  - 입력 데이터에 파일이나 이미지가 포함될 경우 이용
- text/plain
  - 별도 인코딩 없이 문자 상태로 전송

### 2. [Apache Commons Library](https://commons.apache.org/)
- multipart/form-data 형식으로 데이터를 전송할 경우, request.getParameter() 함수로 입력값 추출 불가능
- Apache Commons에서 제공하는 FileUpload, IO 라이브러리 다운, WEB-INF/lib에 해당 jar 파일을 저장
- IntelliJ 경우 project structure에서 jar 파일 추가 설정해주기

### 3. ServletFileUpload 클래스
```java
ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
List items = sfu.parseRequest(request);
Iterator it = items.iterator();

while (iter.hasNext()) {
    FileItem item = (FileItem)it.next();
    if (item.isFormField()) {
        // To do Something here for a form-field params   
    }
    else {
        // To do Something here for a non-form-field params
    }
}
```
- parseRequest() : boundary 속성 값을 중심으로 HTTP 요청 메세지의 바디 컨텐츠 분리 후 List type으로 반환
- Object 형태로 반환된 분리된 각각의 사용자 입력 값을 FileItem 클래스의 형태로 저장
- isFormField() : 주어진 요소가 이름-값 쌍으로 구성된 폼 필드인지에 대한 bool 값 반환

### 4. 데이터베이스에 저장
업로드 된 파일을 서버에 저장하기 위한 방법은 크게 두가지가 있다.
- 데이터베이스 내부에 파일을 BLOB 형식으로 저장 (비용 多)  
- 서버에 일반 파일 형태로 저장한 후, 데이터베이스에는 파일의 path만 저장 (주로 이용)
- application.getRealPath() 함수를 통해 현재 수행되고 있는 JSP 모듈의 루트 경로를 얻을 수 있음

