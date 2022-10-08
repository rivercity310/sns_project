<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-09
  Time: 오전 2:02
  To change this template use File | Settings | File Templates.
--%>

<%--
MVC의 경우 JSP 파일에서 session 객체에 바로 접근 가능한 반면, AJAX를 이용할 경우, HTML 파일에서 로그인 여부를 판단해야 한다.
따라서 session 객체의 값을 외부로 제공하는 별도의 JSP 파일이 필요하다.
각 HTML 파일에서는 페이지 시작 전에 AJAX 호출을 통해 이 JSP 파일이 반환한 session 객체 값을 확인한 후 로그인 여부를 판단할 수 있다.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  out.println(session.getAttribute("id"));
%>