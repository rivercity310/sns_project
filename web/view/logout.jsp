<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-04
  Time: 오후 10:01
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	session.invalidate();

	String str = "<p align=center><br>로그아웃을 완료하였습니다.<br/><br/>";
	str += "<a href='login.html'><button>로그인하기</button></a></p>";
	out.print(str);
%>