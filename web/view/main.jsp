<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-05
  Time: 오후 3:56
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.*" %>

<%
    out.print(new FeedDAO().getList());
%>