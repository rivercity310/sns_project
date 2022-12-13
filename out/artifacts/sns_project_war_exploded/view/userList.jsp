<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-04
  Time: 오후 9:57
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.*" %>

<%
	out.print(new UserDAO().getList());
%>