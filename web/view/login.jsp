<%@ page contentType="text/html" pageEncoding="utf-8" %>
<%@ page import="dao.UserDAO" %>
<%
	request.setCharacterEncoding("utf-8");

	String uid = request.getParameter("id");
	String upass = request.getParameter("ps");

	UserDAO dao = new UserDAO();

	int code = dao.login(uid, upass);

	if (code == 1) out.print("NE");
	else if (code == 2) out.print("PE");
	else {
		session.setAttribute("id", uid);
		out.print("OK");
	}
%>