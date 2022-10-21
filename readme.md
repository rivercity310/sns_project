# OOP II 개인 프로젝트
### 삼육대학교 컴퓨터공학부 Object Oriented Programming II 수업에서 배운 내용을 토대로 간단한 웹 SNS를 구현해본 클론 프로젝트입니다.

---

## 사용 기술
###### - HTML, CSS, JavaScript, JavaEE, MySQL
###### - MVC 패턴을 구현해본 후, jQuery AJAX를 통해 서버 중심 처리방식의 단점 개선
###### - Apache Commons Library (File upload 관련)
###### - JSON.simple Library (JSON 처리 관련)

---

## 프로젝트 구조
<img src="web\images\화면 캡처 2022-10-21 215157.jpg" style="float:left; width: 40%; margin-left: 10%">
<img src="web\images\화면 캡처 2022-10-21 215237.jpg" style="float: left; width: 40%; margin-right: 10%">


---

## 목차
### [1. JDBC 관련 함수](#[-JDBC-관련-함수-])
### [2. Connection Pool](#[-Connection-Pool-])
### [3. DAO, DTO, VO](#[-DAO,-DTO,-VO-])
### [4. File Upload](#[-File-Upload-])
### [5. MVC와 AJAX](#[-MVC-패턴과-AJAX-])
### [6. JSON](#[-JSON-])
### [7. 상호 배제](#[-상호 배제-(Mutual-Exclusion)-])
### [8. Storage & Data Caching](#[-Storage-])

---

# 개념 정리

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
- 데이터베이스 연동에 필요한 URL이나 커넥션 개수 등의 파라미터들은 context.xml(META-INF)에서 정의

### 1. 구조
```java
public class ConnectionPool {
  private static DataSource _ds = null;

  public static Connection get() throws NamingException, SQLException {
    if (_ds == null)
      _ds = (DataSource)(new InitialContext()).lookup("java:comp/env/jdbc/mysns");

    return _ds.getConnection();
  }
}
```
- JNDI에서는 InitialContext 클래스의 lookup() 함수를 통해 <Resource> 태그로 등록된 자원을 찾도록 지원
- 찾고자 하는 자원의 이름 앞에 "java:comp/env/"를 추가하여 lookup() 함수의 파라미터로 전달
- 반환값은 Object 클래스 형태로 전달되므로 캐스팅 필요

### 2. static
- lookup() 함수가 반복적으로 수행되지 않도록 get() 함수가 처음 호출될 때 한해서 함수가 실행되도록 강제

---

## [ DAO, DTO, VO ]

### 1. DAO (Data Access Object)
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

### 2. DTO (Data Transfer Object)
- 계층간 데이터 전달 목적으로 사용 (Client <-> Controller <-> Service)
- 별도 로직을 갖지 않는 순수한 데이터 객체 (getter/setter만 갖는다.)
- 불변성을 위해 setter 대신 생성자로 데이터를 넣어주도록 하면 변조 불가능

### 3. VO (Value Object)
- DTO와 반대로 로직 포함 가능
- 불변성을 위해 setter 대신 생성자 사용
- VO는 서로 다른 인스턴스라도 모든 속성 값이 같다면 두 인스턴스는 같은 객체인 것이 핵심

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

---

## [ MVC 패턴과 AJAX ]

### 1. MVC
- 사용자 인터페이스로부터 비즈니스 로직을 분리하여 모듈간 결합도를 낮추기 위한 디자인 패턴
- Model: 사용자 요청을 처리하기 위한 로직 (Business Logic, DB Logic), JSP or Java
- View: 사용자 인터페이스 담당, JSP
- Controller: 데이터와 비즈니스 로직 사이 상호 동작 관리 (필터링, 연결 제어), Servlet 

### 2. MVC 패턴의 단점
- 서버 중심 처리 방식 -> 서버 과부하
- AJAX와 JSON을 이용해 서버에서 이루어지던 일을 클라이언트로 이관
- 비즈니스 로직(Model)과 결과 페이지 생성(View) 부분을 클라이언트에서 스크립트 언어로 처리

### 3. AJAX (Asynchronous Java and XML)
- JAVA나 XML 형식의 데이터를 비동기식으로 전송하기 위한 기술
- URL을 동일하게 유지하면서 내부적으로 여러 개의 HTTP 요청, 응답을 전송할 수 있도록 지원

### 4. AJAX 유의점
- html 파일의 form 태그 제거 (required 속성 작동 안함) 
- submit의 onclick 속성값으로 ajax 함수 등록 
- 별도의 입력값 검사 로직을 작성해주어야 함
- 결과 코드 생성 및 메세지 출력 부분을 클라이언트(JS)에서 처리하므로, JSP 파일에서는 out.print() 함수를 통해 서버 측 처리 결과를 간단한 코드 형태로 출력
  (out.print()의 내용이 ajax success에 등록된 함수의 파라미터 msg로 전달)
---

## [ jQuery AJAX ]

### 1. jQuery.ajax()
```javascript
const AJAX = {
  call: (url, params, func, isfd) => {
    const callobj = {
      url: url,
      type: "post",
      data: params,
      dataType: "text",
      success: func,
      error: (xhr, status, error) => {
        if (xhr.status == 0)
          alert("네트워크 접속이 원활하지 않습니다.");
        else {
          console.log(xhr.responseText);
          alert("에러 발생!");
        }
      }
    };

    /*
    파일 업로드를 위해 multipart/form-data 형식으로 파라미터를 전송
    - processData : 주어진 파라미터가 이름-값 쌍의 리스트로 표현된 쿼리 스트링 형식으로 전달, default는 True
    - contentType : HTTP 요청의 인코딩 타입 설정, default는 True이며 이 경우 'application/www-form-urlencoded'로 설정
     */
    if (isfd) {
      callobj.processData = false;
      callobj.contentType = false;
    }

    jQuery.ajax(callobj);
  },
};
```
- Params
  - url : AJAX를 통해 호출될 URL 지정
  - type : HTTP 전송 방식
  - data : URL의 쿼리 스트링에 해당하는 부분을 지정
  - dataType : 서버로부터 전송되는 데이터의 포멧 (JSON, TEXT 등)
  - success : AJAX 호출이 성공했을 때, 서버로부터 결과값을 받기 위한 함수, 함수 인자인 msg에는 서버로부터 out.print()된 출력 결과가 텍스트 형식으로 전달됨
  - error : AJAX 호출이 실패했을 때, 에러 코드나 상태 정보를 받기 위한 함수

---

## [ JSON ]
### 1. XML에 비해 쉬운 문법과 빠른 처리 속도로 인해 클라이언트와 서버 간 전송되는 메세지를 표현하는데 적합

### 2. [JSON.simple](https://code.google.com/archive/p/json-simple/downloads) 
- 라이브러리를 이용해서 Java에서 데이터를 JSON 형식으로 인코딩(서버 측), 디코딩(클라이언트 측)
- UserDAO 클래스의 getList() 함수에서 ArrayList는 JSONArray로, UserObj는 JSONObject로 대체
- 클라이언트 측에서는 JSON.parse()를 통해 userList.jsp로부터 AJAX 전달된 데이터를 Javascript의 JSON 객체로 변환 (디코딩)
```java
JSONArray users = new JSONArray();
while (rs.next()) {
    JSONObject obj = new JSONObject();

    obj.put("id", rs.getString("id"));
    obj.put("name", rs.getString("name"));
    obj.put("ts", rs.getString("ts"));

    users.add(obj);
}

  // 현재 배열의 내용을 String 형태로 변환한 후 리턴
  return users.toJSONString();
}
```

### 3. DB에 JSON 객체를 통합하여 저장 (jsonstr)
- JSON 라이브러리를 이용하지 않으므로 메모리 절약, 수행 속도 향상
- 새로운 속성 값이 추가되더라도 테이블 구조에 영향이 없으므로 코드 수정이 필요 없음
```java
String str = "[";
int cnt = 0;
while (rs.next()) {
    if (cnt++ > 0) str += ", ";
    str += rs.getString("jsonstr");
}

return str + "]";
```

### 4. JSONParser
- rs.getString()으로 얻어진 스트링을 JSON 객체 형식(JSONObject)으로 변환
- parse 함수는 예외시 ParseException을 던짐

```java
String jsonstr = rs.getString("jsonstr");
JSONObject obj = (JSONObject)(new JSONParser()).parse(jsonstr);
String password = obj.get("password").toString();
```

--- 

## [ 상호 배제 (Mutual Exclusion) ]
두 사용자가 작성글을 동시에 업로드하는 경우 feedAdd.jsp 스레드가 동시에 실행,   
만약 이 과정중 feedDAO.insert()가 병렬적으로 수행된다면 레코드의 no 값이 똑같아질 수 있는 위험이 있음   
오류, 버그의 원인이므로 synchronized(this) 구문으로 묶어 한 스레드가 점거하는 동안 다른 스레드의 접근을 막아야 함   
   
```java
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
    // ...
}
```

---

## [ Storage ]
기존에는 클라이언트 측에 데이터를 저장하기 위해 캐시(Cache)를 사용 (4kb)   
부족한 저장 공간과 HTTP 요청시 서버에 함께 전달된다는 단점때문에 불필요한 네트워크 전송을 야기함
   
HTML5에서는 이러한 문제를 해결하기 위해 이름-값 쌍의 형태로 데이터를 저장, 추출할 수 있는
Local Storage와 Session Storage를 제공
   
- Cache
  - 저장용량: 4KB
  - 이름-값 형태로 데이터 저장, 추출
  - HTTP 요청시 내용이 함께 전달됨
- Local Storage
  - 저장용량: 5MB 이상
  - 이름-값 형태로 데이터 저장, 추출
  - 사용자가 지우지 않는 한 계속 유지됨
- Session Storage
  - 저장용량: 5MB 이상
  - 이름-값 형태로 데이터 저장, 추출
  - 브라우저를 닫을 때 삭제됨

### 1. Storage의 목적
- 데이터 캐싱: 하나의 HTML 페이지에서 데이터를 일정 시간동안 저장 => 해당 시간 내에는
서버로부터 데이터를 가져오지 않고, 임시 저장된 데이터를 이용
- 데이터 공유: 2개 이상의 HTML 페이지에서 데이터를 공유 => 하나의 페이지에서 다른 페이지를 호출할 때 공유할 데이터를 파라미터 형식으로 전달하도록 구현

### 2. 구현
데이터 임시 저장이라는 측면에서 볼때 세션 스토리지를 이용하는 것이 바람직하다.   
JavaScript의 sessionStorage 객체를 통해 접근 가능하며 
setItem(), getItem(), removeItem() 메서드를 이용하여 데이터 저장, 접근 및 삭제 가능
   
```javascript
/* 
세션 스토리지를 이용할 경우 항상 스트링 형태로 입력되어야 한다.
따라서 JSON 객체 형식으로 데이터를 저장하고 반환할 수 있도록 다음과 같이 구성한다.
*/ 
const SessionStore = {
    set: function(name, val) {
        sessionStorage.setItem(name, JSON.stringify(val));
    },
    
    get: function(name) {
        const str = sessionStorage.getItem(name);
        return (str == null || str == "null") ? null : JSON.parse(str);
    },
    
    remove: function(name) {
        sessionStorage.removeItem(name);
    }
}
```

### 3. 데이터 캐싱
위에서 작성한 SessionStore 객체를 이용하여, 세션 스토리지에 저장되는 데이터의
유효 기간을 설정하고 체크하기 위한 로직을 추가한 DataCache 객체

```javascript
const DataCache = {
    set: function(name, data) {
        const obj = { ts: Date.now(), data: data };
        SessionStore.set(name, obj);
    },

    get: function(name) {
        const obj = SessionStore.get(name);
        if (obj == null)
            return null;

        const diff = (Date.now() - obj.ts) / 60000;
        if (diff > 10) {
            SessionStore.remove(name);
            return null;
        }

        return obj.data;
    }
}
```
