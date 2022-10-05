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
	String upass = request.getParameter("ps");
	String upass2 = request.getParameter("ps2");
	String uname = request.getParameter("name");

	if (upass.equals(upass2)) {
		UserDAO dao = new UserDAO();
		if (dao.exists(uid)) {
			out.print("이미 가입한 회원입니다.");
			return;
		}

		if (dao.insert(uid, upass, uname)) {
			session.setAttribute("id", uid);
			response.sendRedirect("../main.jsp");
		}
	}
	else {
		out.print("확인 비밀번호가 다릅니다.");
		return;
	}

%>