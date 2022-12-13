<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-04
  Time: 오후 9:46
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.UserDAO" %>

<%
	request.setCharacterEncoding("utf-8");

	String uid = request.getParameter("id");
	String jsonstr = request.getParameter("jsonstr");

	UserDAO dao = new UserDAO();
	if (dao.exists(uid)) {
		out.print("EX");
		return;
	}

	if (dao.insert(uid, jsonstr)) {
		session.setAttribute("id", uid);
		out.print("OK");
	}
	else {
		out.print("ER");
	}

%>