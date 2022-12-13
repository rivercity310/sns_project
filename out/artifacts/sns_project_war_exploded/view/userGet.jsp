<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-17
  Time: 오전 2:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.*" %>
<%
    String uid = request.getParameter("id");
    out.print(new UserDAO().get(uid));
%>