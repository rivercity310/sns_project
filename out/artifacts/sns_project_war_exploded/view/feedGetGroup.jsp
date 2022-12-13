<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-13
  Time: 오후 9:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.*" %>
<%
  String frids = request.getParameter("frids");
  String maxNo = request.getParameter("maxNo");
  out.print(new FeedDAO().getGroup(frids, maxNo));
%>