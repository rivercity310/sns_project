<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-04
  Time: 오후 9:57
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="dao.*" %>

<%
	ArrayList<UserObj> list = new UserDAO().getList();

	String str = "<table align=center>";
	str += "<tr><th colspan=3>가입자 리스트</th></tr>";
	for (UserObj user : list) {
		str += "<tr><td colspan=3><hr/></td></tr>";
		str += "<tr>";
		str += "<td>" + user.getId() + "</td>";
		str += "<td>" + user.getName() + "</td>";
		str += "<td>&nbsp;(" + user.getTs() + ")</td>";
		str += "</tr>";
	}
	str += "</table>"

	out.print(str);
%>